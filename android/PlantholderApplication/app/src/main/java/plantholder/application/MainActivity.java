package plantholder.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.io.FileOutputStream;
import java.io.IOException;

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
    //New variable to save the last speed selected (turtle slower, rabbit faster or normal). The initial speed is the normal (30)
    private int speedMode = MOVEMENT_SPEED;

    //Bitmap that saves the picture that was taken from the video
    private final Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

    //Path to where the image gets stored after pressed take picture on the Main screen
    private static String imagesPath = "/storage/emulated/0/Download/PlantImage.png";

    private static String decodedPlantId;

    //Gets the path of the image
    public static String getSavedImagePath(){
        return imagesPath;
    }

    public static String getDecodedPlantId(){
        return decodedPlantId;
    }

    private MqttClient mMqttClient;
    private boolean isConnected = false;
    private QrCodeProcessing QrCodeProcessing;
    private ImageView mCameraView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMqttClient = new MqttClient(getApplicationContext(), MQTT_SERVER, TAG);
        mCameraView = findViewById(R.id.imageView);

        //Hide decor view
        getWindow().getDecorView().getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars());

        connectToMqttBroker();
        QrCodeProcessing = new QrCodeProcessing();

        //Initialize all the buttons
        ImageView turtleButton = findViewById(R.id.turtleButton);
        ImageView rabbitButton = findViewById(R.id.rabbitButton);
        ImageButton leftArrow = findViewById(R.id.left_arrow);
        ImageButton rightArrow = findViewById(R.id.right_arrow);
        ImageButton forwardArrow = findViewById(R.id.forward_arrow);
        ImageButton backwardArrow = findViewById(R.id.backward_arrow);
        Button autoPilot = findViewById(R.id.auto_mode);
        Button menuButton = findViewById(R.id.menuButton);
        Button infoScreen = findViewById(R.id.information);
        Button takePicture = findViewById(R.id.takePicture);

        //Drives forward without pressing forward button. It drives slower or faster if the speed mode buttons are selected.
        //Otherwise drives in the normal speed.
        autoPilot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    if(turtleButton.isSelected() || rabbitButton.isSelected()) {
                        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    }else{
                        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    }
                    view.setSelected(true);
                    view.setBackgroundColor(GREEN);
                }else{
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setSelected(false);
                    view.setBackgroundColor(Color.BLACK);
                }
            }
        });

        //Sends to the previous page
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Sends to the infor screen page
        infoScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InformationScreen.class);
                startActivity(intent);
            }
        });

        //Navigation button going forward. Moves forward only when is pressed. Deactivates Auto button when pressed
        forwardArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    view.setPressed(true);
                    autoPilot.setSelected(false);
                    autoPilot.setBackgroundColor(Color.BLACK);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        //Navigation button going backwards. Moves backwards only when is pressed. Deactivates Auto button when pressed
        backwardArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(-speedMode, STRAIGHT_ANGLE, "Moving backward");
                    view.setPressed(true);
                    autoPilot.setSelected(false);
                    autoPilot.setBackgroundColor(Color.BLACK);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        //Navigation button going left. Moves left only when is pressed. Deactivates Auto button when pressed
        leftArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(TURNING_SPEED, -STEERING_ANGLE, "Moving forward left");
                    view.setPressed(true);
                    autoPilot.setSelected(false);
                    autoPilot.setBackgroundColor(Color.BLACK);
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        //Navigation button going right. Moves right only when is pressed. Deactivates Auto button when pressed
        rightArrow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    drive(TURNING_SPEED, STEERING_ANGLE, "Moving forward right");
                    view.setPressed(true);
                    autoPilot.setSelected(false);
                    autoPilot.setBackgroundColor(Color.BLACK);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
                    view.setPressed(false);
                }
                return true;
            }
        });

        //Set speed to lower speed when turtle button is clicked. Deselect rabbit button when turtle is selected.
        //Changes the speed if auto button is selected.
        turtleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    if(autoPilot.isSelected()){
                        speedMode = LOWER_MOVEMENT_SPEED;
                        view.setSelected(true);
                        rabbitButton.setSelected(false);
                        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    }else{
                        speedMode = LOWER_MOVEMENT_SPEED;
                        view.setSelected(true);
                        rabbitButton.setSelected(false);
                    }
                }else{
                    if(autoPilot.isSelected()){
                        speedMode = MOVEMENT_SPEED;
                        view.setSelected(false);
                        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    }
                    view.setSelected(false);
                    speedMode = MOVEMENT_SPEED;
                }
            }
        });

        //Set speed to faster speed when rabbit button is clicked. Deselect turtle button when rabbit is selected.
        //Changes the speed if auto button is selected.
        rabbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    if(autoPilot.isSelected()){
                        speedMode = FASTER_MOVEMENT_SPEED;
                        view.setSelected(true);
                        turtleButton.setSelected(false);
                        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    }else{
                        speedMode = FASTER_MOVEMENT_SPEED;
                        view.setSelected(true);
                        turtleButton.setSelected(false);
                    }
                }else{
                    if(autoPilot.isSelected()){
                        speedMode = MOVEMENT_SPEED;
                        view.setSelected(false);
                        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
                    }
                    view.setSelected(false);
                    speedMode = MOVEMENT_SPEED;
                }
            }
        });

        //Button takes a picture of the video and sends the picture to the status screen
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

    //Connection to mqtt broker
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
