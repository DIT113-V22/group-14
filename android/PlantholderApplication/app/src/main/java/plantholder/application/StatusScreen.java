package plantholder.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatusScreen extends AppCompatActivity {

    private Firebase firebase;

    String scannedPlantId = "1846973667";
    String scannedTypeText = "bad query";
    String scannedStatusText = "bad query";
    String scannedRowText = "Row: x";
    String scannedColumnText = "Column: x";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_screen);
        firebase = new Firebase();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
);
        TextView plantIdText = (TextView)findViewById(R.id.plantIdTextView);
        plantIdText.setText(String.format("ID: %s", scannedPlantId));
        firebase.getPlantInformation(scannedPlantId);
/*
        TextView plantIdText = (TextView)findViewById(R.id.plantIdTextView);
        plantIdText.setText(String.format("ID: %s", scannedPlantId));

        TextView plantTypeText = (TextView)findViewById(R.id.plantTypeTextView);
        plantTypeText.setText(String.format("Type: %s", scannedTypeText));

        TextView plantStatusText = (TextView)findViewById(R.id.plantStatusTextView);
        plantStatusText.setText(String.format("Status: %s", scannedStatusText));

        TextView plantRowText = (TextView)findViewById(R.id.plantRowTextView);
        plantRowText.setText(String.format("Row: %s", scannedRowText));

        TextView plantColumnText = (TextView)findViewById(R.id.plantColumnTextView);
        plantColumnText.setText(String.format("Column: %s", scannedColumnText));

 */



        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatusScreen.this,MainActivity.class);
                startActivity(intent);
            }
        });

        Button unhealthy = findViewById(R.id.unhealthy);
        unhealthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth("1846973667","Unhealthy");
            }
        });

        Button healthy = findViewById(R.id.healthy);
        healthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth("1846973667","Healthy");
            }
        });

        Button keepTrackOn = findViewById(R.id.keepTrack);
        keepTrackOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth("1846973667","Keep Track On");
            }
        });

        Button ripe = findViewById(R.id.ripe);
        ripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth("1846973667","Ripe");
            }
        });

    }
}