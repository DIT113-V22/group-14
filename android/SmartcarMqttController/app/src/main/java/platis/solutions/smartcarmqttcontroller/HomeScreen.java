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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        firebase = new Firebase();

        //firebase.updatePlantHealth("7522007913");
     /* firebase.writeNewPlant("7522007913","Tomato",4, 65,"Unhealthy");
        firebase.writeNewPlant("1305167209","Tomato",1, 23,"Healthy");
        firebase.writeNewPlant("1846973667","Tomato",10, 14,"KeepTrackOn");
        firebase.writeNewPlant("4770449838","Tomato",2, 32,"Ripe");


        //This will add new plants each time we run the project if not commented out.
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