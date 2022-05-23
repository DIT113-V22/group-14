package plantholder.application;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

// reference for the bitmap creation from
// https://stackoverflow.com/questions/16804404/create-a-bitmap-drawable-from-file-path
// by CodeShadow

public class StatusScreen extends AppCompatActivity {

    Firebase firebase;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;
    String scannedPlantId = MainActivity.getDecodedPlantId();
    String takenPicturePath = MainActivity.getSavedImagePath();
    private ImageView viewTakenPicture;
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebase = new Firebase();

        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );

        //Grabs any incoming sent with the intent, if it exists changes a variable
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scannedPlantId = extras.getString("key");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_screen);
        viewTakenPicture = findViewById(R.id.taken_picture_view);
        Bitmap myBitmap = BitmapFactory.decodeFile(takenPicturePath, bmOptions);
        viewTakenPicture.setImageBitmap(myBitmap);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
 
        //set id text to variable
        TextView textViewID = findViewById(R.id.plantIdTextView);
        textViewID.setText(scannedPlantId);

        //gets database snapshot and check if the variable id exists
        firebaseDatabase =  FirebaseDatabase.getInstance();

       try {
           firebaseReference = (firebaseDatabase.getReference("Plants").child(scannedPlantId).child("id"));
       } catch (
        Exception e) {
        e.printStackTrace();
        }

       verifyPlantExistence();

        //back button that brings you to the MainActivity screen
        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatusScreen.this,MainActivity.class);
                startActivity(intent);
            }
        });
        //Button to change plants health to unhealthy
        Button unhealthy = findViewById(R.id.unhealthy);
        unhealthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth(scannedPlantId,"Unhealthy");
            }
        });

        //Button to change plants health to Healthy
        Button healthy = findViewById(R.id.healthy);
        healthy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth(scannedPlantId,"Healthy");
            }
        });

        //Button to change plants health to Keep Track
        Button keepTrackOn = findViewById(R.id.keepTrack);
        keepTrackOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth(scannedPlantId,"Keep Track");
            }
        });

        //Button to change plants health to Ripe
        Button ripe = findViewById(R.id.ripe);
        ripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebase.updatePlantHealth(scannedPlantId,"Ripe");
            }
        });
    }

    //gets the value from the database and sets the given textView as that value, strings only
    private void getdata(TextView textView) {
        firebaseDatabase =  FirebaseDatabase.getInstance();
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
    //gets the value from the database and sets the given textView as that value, longs only
    private void getdataLong(TextView textView) {
        firebaseDatabase =  FirebaseDatabase.getInstance();
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

    //checks if a plant exists, if it does all the textViews are updated with that plants information
    private void verifyPlantExistence() {
        firebaseReference = firebaseDatabase.getReference("Plants").child(scannedPlantId).child("id");
        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value != null) {
                    //Set health text
                    firebaseReference = firebaseDatabase.getReference("Plants").child(scannedPlantId).child("health");
                    TextView textViewHealth = findViewById(R.id.plantStatusTextView);
                    getdata(textViewHealth);

                    //set type text
                    firebaseReference = firebaseDatabase.getReference("Plants").child(scannedPlantId).child("species");
                    TextView textViewSpecies = findViewById(R.id.plantTypeTextView);
                    getdata(textViewSpecies);

                    //set row text
                    firebaseReference = firebaseDatabase.getReference("Plants").child(scannedPlantId).child("row");
                    TextView textViewRow = findViewById(R.id.plantRowTextView);
                    getdataLong(textViewRow);

                    //set column text
                    firebaseReference = firebaseDatabase.getReference("Plants").child(scannedPlantId).child("column");
                    TextView textViewColumn = findViewById(R.id.plantColumnTextView);
                    getdataLong(textViewColumn);
                } else if(scannedPlantId.equals("Unreadable QR code")) {
                    Toast.makeText(StatusScreen.this, "Unreadable QR code", Toast.LENGTH_SHORT).show();
                } else {
                    changeToAddPlantScreen();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatusScreen.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //When entering the screen, if the plantID does not exists it asks if you want to go to the addPlantScreen to add it
    public void changeToAddPlantScreen() {

        AlertDialog.Builder builder = new AlertDialog.Builder(StatusScreen.this); // (getActivity();)
        builder.setTitle("The scanned ID does not exist ");
        builder.setMessage("Do you want to add this plant?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(StatusScreen.this, AddPlantScreen.class);
                //Sends the plant ID as a extra bundle with intent
                intent.putExtra("key", scannedPlantId);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(StatusScreen.this, "The scanned plant does not exist.", Toast.LENGTH_LONG).show();
            }
        });
        builder.create();
        builder.show();
    }
}