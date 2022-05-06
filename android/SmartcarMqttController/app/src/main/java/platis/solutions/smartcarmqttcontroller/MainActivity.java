package platis.solutions.smartcarmqttcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SmartcarMqttController";
    private static final String EXTERNAL_MQTT_BROKER = "aerostun.dev";
    private static final String LOCALHOST = "10.0.2.2";  //"192.168.0.45"
    private static final String MQTT_SERVER = "tcp://" + LOCALHOST + ":1883";  
    private static final String THROTTLE_CONTROL = "/smartcar/control/throttle";
    private static final String STEERING_CONTROL = "/smartcar/control/steering";
    private static final int MOVEMENT_SPEED = 30;
    private static final int LOWER_MOVEMENT_SPEED = 10;
    private static final int FASTER_MOVEMENT_SPEED = 50;
    private static final int IDLE_SPEED = 0;
    private static final int TURNING_SPEED = 0;    //new variable for turning in tank mode
    private static final int STRAIGHT_ANGLE = 0;
    private static final int STEERING_ANGLE = 50;
    private static final int QOS = 1;
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;

    private MqttClient mMqttClient;
    private boolean isConnected = false;
    private ImageView mCameraView;

    //New variables to control the speed mode (turtle slower, rabbit faster or normal)
    private boolean slowModeActive = false;
    private boolean fastModeActive = false;
    private int speedMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMqttClient = new MqttClient(getApplicationContext(), MQTT_SERVER, TAG);
        mCameraView = findViewById(R.id.imageView);


        connectToMqttBroker();

        ImageButton turtleButton = findViewById(R.id.turtleButton);
        ImageButton rabbitButton = findViewById(R.id.rabbitButton);

        //action when the turtle button is clicked
        turtleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    speedMode = LOWER_MOVEMENT_SPEED;
                    //THROTTLE_CONTROL = String.valueOf(LOWER_MOVEMENT_SPEED);
                    view.setSelected(true);
                    rabbitButton.setSelected(false);
                    slowModeActive = true;
                    drive(speedMode, STRAIGHT_ANGLE, "Lower speed");
                }else{
                    view.setSelected(false);
                    slowModeActive = false;
                    speedMode = MOVEMENT_SPEED;
                    //THROTTLE_CONTROL = String.valueOf(MOVEMENT_SPEED);
                    drive(speedMode, STRAIGHT_ANGLE, "Normal speed");
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
                    //THROTTLE_CONTROL = String.valueOf(FASTER_MOVEMENT_SPEED);
                    view.setSelected(true);
                    turtleButton.setSelected(false);
                    fastModeActive = true;
                    drive(speedMode, STRAIGHT_ANGLE, "Faster speed");
                }else{
                    view.setSelected(false);
                    fastModeActive = false;
                    speedMode = MOVEMENT_SPEED;
                    //THROTTLE_CONTROL = String.valueOf(MOVEMENT_SPEED);
                    drive(speedMode, STRAIGHT_ANGLE, "Normal speed");
                }
            }
        });

        Button takePicture = findViewById(R.id.takePicture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,StatusScreen.class);
                startActivity(intent);
            }
        });

        //Experiment to get the speed value on the screen. Not working properly yet
        TextView textView = (TextView) findViewById(R.id.realSpeed);
        textView.setText(Integer.toString(speedMode));
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
                        final Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

                        final byte[] payload = message.getPayload();
                        final ByteBuffer colors = ByteBuffer.allocate(IMAGE_WIDTH * IMAGE_HEIGHT * 4);
                        for (int ci = 0; ci < colors.capacity(); ci += 3) {
                            colors.put(payload[ci]);
                            colors.put(payload[ci + 1]);
                            colors.put(payload[ci + 2]);
                            colors.put((byte)255);
                        }
                        bm.copyPixelsFromBuffer(colors);
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
        //check if any speed mode is selected and change the speed
        if(slowModeActive){
            speedMode = LOWER_MOVEMENT_SPEED;
        }else if(fastModeActive){
            speedMode = FASTER_MOVEMENT_SPEED;
        }else{
            speedMode = MOVEMENT_SPEED;
        }
        //apply the driving direction to the speed mode that is selected
        int speed;
        if(actionDescription == "Moving backward"){
            speed = -speedMode;
        }else if (actionDescription == "Stopping"){
            speed = IDLE_SPEED;
        }else if (actionDescription == "Moving forward"){
            speed = speedMode;
        }else if (actionDescription == "Moving forward left"){
            speed = TURNING_SPEED;
        }else if (actionDescription == "Moving forward right"){
            speed = TURNING_SPEED;
        }else{
            speed = speedMode;
        }

        throttleSpeed = speed;

        Log.i(TAG, actionDescription);
        mMqttClient.publish(THROTTLE_CONTROL, Integer.toString(throttleSpeed), QOS, null);
        mMqttClient.publish(STEERING_CONTROL, Integer.toString(steeringAngle), QOS, null);
    }

    public void moveForward(View view) {
        drive(speedMode, STRAIGHT_ANGLE, "Moving forward");
    }

    public void turnLeft(View view) {
        drive(TURNING_SPEED, -STEERING_ANGLE, "Moving forward left");
    }

    public void stop(View view) {
        drive(IDLE_SPEED, STRAIGHT_ANGLE, "Stopping");
    }

    public void turnRight(View view) {
        drive(TURNING_SPEED, STEERING_ANGLE, "Moving forward right");
    }

    public void moveBackward(View view) {
        drive(-speedMode, STRAIGHT_ANGLE, "Moving backward");
    }
}