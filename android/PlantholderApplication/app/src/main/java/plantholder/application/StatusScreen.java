package plantholder.application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class StatusScreen extends AppCompatActivity {

    Firebase firebase;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;


    String scannedPlantId = "1846973667"; //placeholder value for the qr code


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebase = new Firebase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //set id text
        TextView textViewID = findViewById(R.id.plantIdTextView);
        textViewID.setText(scannedPlantId);

        //Set health text
        firebaseDatabase =  FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference("Plants").child("1846973667").child("health");
        TextView textViewHealth = findViewById(R.id.plantStatusTextView);
        getdata(textViewHealth);

        //set type text
        firebaseReference = firebaseDatabase.getReference("Plants").child("1846973667").child("species");
        TextView textViewSpecies = findViewById(R.id.plantTypeTextView);
        getdata(textViewSpecies);

        //set row text
        firebaseReference = firebaseDatabase.getReference("Plants").child("1846973667").child("row");
        TextView textViewRow = findViewById(R.id.plantRowTextView);
        getdataLong(textViewRow);

        //set column text
        firebaseReference = firebaseDatabase.getReference("Plants").child("1846973667").child("column");
        TextView textViewColumn = findViewById(R.id.plantColumnTextView);
        getdataLong(textViewColumn);



        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
);




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
                firebase.updatePlantHealth("1846973667","Keep Track");
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

    private void getdata(TextView textView) {
        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                textView.setText(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatusScreen.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }

        });

    }
        //gets a textview and updates it with current references data
    private void getdataLong(TextView textView) {
        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long value = snapshot.getValue(Long.class);
                String valueString = Long.toString(value);
                textView.setText(valueString);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatusScreen.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }

        });

    }
}