package plantholder.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Plantholder";
    private static final String EXTERNAL_MQTT_BROKER = "aerostun.dev";
    private static final String LOCALHOST = "10.0.2.2";  //"192.168.0.45"
    private static final String MQTT_SERVER = "tcp://" + LOCALHOST + ":1883";
    private static final String THROTTLE_CONTROL = "/smartcar/control/throttle";
    private static final String STEERING_CONTROL = "/smartcar/control/steering";
    private static final int MOVEMENT_SPEED = 30;  //initial speed
    private static final int LOWER_MOVEMENT_SPEED = 10;
    private static final int FASTER_MOVEMENT_SPEED = 50;
    private static final int IDLE_SPEED = 0;
    private static final int TURNING_SPEED = 0;    //new variable for turning in tank mode
    private static final int STRAIGHT_ANGLE = 0;
    private static final int STEERING_ANGLE = 50;
    private static final int QOS = 1;
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;
    private static final int GREEN = Color.parseColor("#759d4b");


    final Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);


    private static String imagesPath = "/storage/emulated/0/Download/PlantImage.png";

    public static String getSavedImagePath(){
        return imagesPath;
    }


    private static String decodedPlantId;

    public static String getDecodedPlantId(){
        return decodedPlantId;
    }

    private MqttClient mMqttClient;
    private boolean isConnected = false;
    private QrCodeProcessing QrCodeProcessing;
    private ImageView mCameraView;

    //New variable to save the last speed selected (turtle slower, rabbit faster or normal). The initial speed is the normal (30)
    private int speedMode = MOVEMENT_SPEED;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMqttClient = new MqttClient(getApplicationContext(), MQTT_SERVER, TAG);
        mCameraView = findViewById(R.id.imageView);

        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );

        connectToMqttBroker();
        QrCodeProcessing = new QrCodeProcessing();

        ImageButton turtleButton = findViewById(R.id.turtleButton);
        ImageButton rabbitButton = findViewById(R.id.rabbitButton);
        ImageButton leftArrow = findViewById(R.id.left_arrow);
        ImageButton rightArrow = findViewById(R.id.right_arrow);
        ImageButton forwardArrow = findViewById(R.id.forward_arrow);
        ImageButton backwardArrow = findViewById(R.id.backward_arrow);
        Button autoPilot = findViewById(R.id.auto_mode);
        Button menuButton = (Button) findViewById(R.id.menuButton);

        autoPilot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    view.setSelected(true);
                    view.setBackgroundColor(GREEN);
                }else{
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setSelected(false);
                    view.setBackgroundColor(Color.BLACK);
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button infoScreen = findViewById(R.id.information);
        infoScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InformationScreen.class);
                startActivity(intent);
            }
        });

        forwardArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    view.setPressed(true);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        backwardArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(-speedMode, STRAIGHT_ANGLE, "Moving backward");
                    view.setPressed(true);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(TURNING_SPEED, -STEERING_ANGLE, "Moving forward left");
                    view.setPressed(true);
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(TURNING_SPEED, STEERING_ANGLE, "Moving forward right");
                    view.setPressed(true);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        //action when the turtle button is clicked
        turtleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    speedMode = LOWER_MOVEMENT_SPEED;
                    view.setSelected(true);
                    rabbitButton.setSelected(false);
                }else{
                    view.setSelected(false);
                    speedMode = MOVEMENT_SPEED;
                }
            }
        });

        //action when the rabbit button for faster is activated
        rabbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    speedMode = FASTER_MOVEMENT_SPEED;
                    view.setSelected(true);
                    turtleButton.setSelected(false);
                }else{
                    view.setSelected(false);
                    speedMode = MOVEMENT_SPEED;
                }
            }
        });

        Button takePicture = findViewById(R.id.takePicture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (FileOutputStream out = new FileOutputStream(imagesPath)) {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                decodedPlantId = (QrCodeProcessing.decodeQRImage(imagesPath));
                Intent intent = new Intent(MainActivity.this, StatusScreen.class);
                intent.putExtra("key", decodedPlantId);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        connectToMqttBroker();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mMqttClient.disconnect(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.i(TAG, "Disconnected from broker");
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
            }
        });
    }

    private void connectToMqttBroker() {
        if (!isConnected) {
            mMqttClient.connect(TAG, "", new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnected = true;

                    final String successfulConnection = "Connected to MQTT broker";
                    Log.i(TAG, successfulConnection);
                    Toast.makeText(getApplicationContext(), successfulConnection, Toast.LENGTH_SHORT).show();

                    mMqttClient.subscribe("/smartcar/ultrasound/front", QOS, null);
                    mMqttClient.subscribe("/smartcar/camera", QOS, null);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    final String failedConnection = "Failed to connect to MQTT broker";
                    Log.e(TAG, failedConnection);
                    Toast.makeText(getApplicationContext(), failedConnection, Toast.LENGTH_SHORT).show();
                }
            }, new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    isConnected = false;

                    final String connectionLost = "Connection to MQTT broker lost";
                    Log.w(TAG, connectionLost);
                    Toast.makeText(getApplicationContext(), connectionLost, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    if (topic.equals("/smartcar/camera")) {
                        final byte[] payload = message.getPayload();
                        final int[] colors = new int[IMAGE_WIDTH * IMAGE_HEIGHT];

                        for (int ci = 0; ci < colors.length; ci++) {
                            final int r = payload[3 * ci] & 0xFF;
                            final int g = payload[3 * ci + 1] & 0xFF;
                            final int b = payload[3 * ci + 2] & 0xFF;
                            colors[ci] = Color.rgb(r, g, b);
                        }
                        bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
                        mCameraView.setImageBitmap(bm);
                    } else {
                        Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Message delivered");
                }
            });
        }
    }

    void drive(int throttleSpeed, int steeringAngle, String actionDescription) {
        if (!isConnected) {
            final String notConnected = "Not connected (yet)";
            Log.e(TAG, notConnected);
            Toast.makeText(getApplicationContext(), notConnected, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, actionDescription);
        mMqttClient.publish(THROTTLE_CONTROL, Integer.toString(throttleSpeed), QOS, null);
        mMqttClient.publish(STEERING_CONTROL, Integer.toString(steeringAngle), QOS, null);
    }
}
