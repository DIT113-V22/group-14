package plantholder.application;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddPlantScreen extends AppCompatActivity {
    private Firebase firebase;
    private String selectedHealth;
    private String selectedType;
    private String addedID;
    private int addedRow;
    private int addedColumn;

    private Spinner spinnerPlantHealth;
    private Spinner spinnerPlantType;
    private EditText editColumn;
    private EditText editRow;
    private EditText editID;

    private Button back;

    public ArrayAdapter<CharSequence> adapter;
    public ArrayAdapter<CharSequence> adapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant_screen);
        firebase = new Firebase();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // hide decorview
        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );

        back = (Button) findViewById(R.id.buttonBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPlantScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        editID = (EditText) (findViewById(R.id.editTextID));
        editColumn = (EditText) (findViewById(R.id.editTextColumn));
        editRow = (EditText) (findViewById(R.id.editTextRow));

        //checks if intent sent over a key to input as ID
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            editID.setText(value);
        }

        //create spinner for health
        spinnerPlantHealth = (Spinner) (findViewById(R.id.spinnerPlantHealth));

        adapter = ArrayAdapter.createFromResource(this, R.array.healthStatus, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPlantHealth.setAdapter(adapter);

        // create spinner for type
        spinnerPlantType = (Spinner) (findViewById(R.id.spinnerPlantType));

        adapter2 = ArrayAdapter.createFromResource(this, R.array.plantType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPlantType.setAdapter(adapter2);

        //create new plant by pressing save and go through some conditions
        Button save = findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedHealth = spinnerPlantHealth.getSelectedItem().toString();
                selectedType = spinnerPlantType.getSelectedItem().toString();
                addedID = editID.getText().toString();
                if (editColumn.getText().toString().isEmpty()) {
                    addedColumn = -1;
                } else {
                    addedColumn = Integer.parseInt(editColumn.getText().toString());
                }
                if (editRow.getText().toString().isEmpty()) {
                    addedRow = -1;
                } else {
                    addedRow = Integer.parseInt(editRow.getText().toString());
                }

                if (addedID.length() < 10) {
                    Toast.makeText(AddPlantScreen.this, "Plant ID needs to be 10 digits.", Toast.LENGTH_SHORT).show();
                } else if (addedRow < 0) {
                    Toast.makeText(AddPlantScreen.this, "Please enter a row.", Toast.LENGTH_SHORT).show();
                } else if (addedColumn < 0) {
                    Toast.makeText(AddPlantScreen.this, "Please enter a column.", Toast.LENGTH_SHORT).show();
                } else {

                    verifyPlantExistence(addedID);

                }
            }
        });

    }

    //give the user an Alert with action if the inserted id already exists
    public void idExistAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddPlantScreen.this); // (getActivity();)
        builder.setTitle("The inserted ID already exists! ");
        builder.setMessage("Do you want to continue and update this plant?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                createPlant();
                toastUpdate();
            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(AddPlantScreen.this, "Plant not updated.", Toast.LENGTH_LONG).show();
            }
        });

        builder.create();
        builder.show();
    }

    public void toastUpdate() {
        Toast.makeText(this, "Plant updated!", Toast.LENGTH_SHORT).show();
    }

    public void toastCreate() {
        Toast.makeText(this, "Plant created!", Toast.LENGTH_SHORT).show();
    }

    //creates a new plant in the database
    void createPlant() {
        firebase.writeNewPlant(addedID, selectedType, addedRow, addedColumn, selectedHealth);

        //clean input in layout
        editID.setText("");
        editColumn.setText("");
        editRow.setText("");
        spinnerPlantType.setAdapter(adapter2);
        spinnerPlantHealth.setAdapter(adapter);

    }

    //This checks if the plant id already exists in the database
    private void verifyPlantExistence(String addedID) {
        DatabaseReference plantRef = FirebaseDatabase.getInstance().getReference("Plants/" + addedID);
        plantRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    idExistAlert();
                } else {
                    createPlant();
                    toastCreate();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}





