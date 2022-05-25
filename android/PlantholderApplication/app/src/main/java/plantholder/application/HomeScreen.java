package plantholder.application;

import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    private Button scanButton;
    private Button infoButton;
    private Button buttonAddPlant;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        firebase = new Firebase();

        //hide decor view
        getWindow().getDecorView().getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars());

        //Initialize buttons
        scanButton = (Button) findViewById(R.id.scanButton);
        infoButton = (Button) findViewById(R.id.toInfoButton);
        buttonAddPlant = (Button) findViewById(R.id.buttonAddPlant);

        //Send it to the main screen with the camera and drive controls
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Sends to the screen with the table with information of all plants
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, InformationScreen.class);
                startActivity(intent);
            }
        });

        //Sends to the screen that adds plants manually
        buttonAddPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, AddPlantScreen.class);
                startActivity(intent);
            }
        });
    }
}