package plantholder.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import android.widget.AdapterView;
import android.widget.Toast;

public class AddPlantScreen extends AppCompatActivity {
    String selectedHealth;
    private Firebase firebase;
    Spinner spinnerPlantHealth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant_screen);
        firebase = new Firebase();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        spinnerPlantHealth = (Spinner) (findViewById(R.id.spinnerPlantHealth));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.healthStatus, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPlantHealth.setAdapter(adapter);

        Button save = findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedHealth = spinnerPlantHealth.getSelectedItem().toString();
                firebase.writeNewPlant("9999999988","potato",4,5, selectedHealth);
            }
        });

    }
    }
