package platis.solutions.smartcarmqttcontroller;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {
    private static final String TAG = "HomeScreen";

    Button manualButton;
    private Firebase firebase;
    // private QrCodeTest qrCodeTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        firebase = new Firebase();
        // qrCodeTest = new QrCodeTest();
        // qrCodeTest.decodeQRImage();

     /* firebase.writeNewPlant("7522007913","Tomato",4, 65,"Unhealthy");

        This is an example of how to add new plants add new plants.
*/

        manualButton = (Button) findViewById(R.id.manualButton);

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}