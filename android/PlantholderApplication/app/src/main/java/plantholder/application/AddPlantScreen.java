package plantholder.application;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant_screen);
        firebase = new Firebase();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        back = (Button) findViewById(R.id.buttonBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPlantScreen.this,HomeScreen.class);
                startActivity(intent);
            }
        });

        editID = (EditText) (findViewById(R.id.editTextID));
        editColumn = (EditText) (findViewById(R.id.editTextColumn));
        editRow = (EditText) (findViewById(R.id.editTextRow));

        //create spinner for health
        spinnerPlantHealth = (Spinner) (findViewById(R.id.spinnerPlantHealth));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.healthStatus, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPlantHealth.setAdapter(adapter);

        // create spinner for type
        spinnerPlantType = (Spinner) (findViewById(R.id.spinnerPlantType));

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.plantType, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPlantType.setAdapter(adapter2);

        //create new plant by pressing save
        Button save = findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedHealth = spinnerPlantHealth.getSelectedItem().toString();
                selectedType = spinnerPlantType.getSelectedItem().toString();
                addedID = editID.getText().toString();
                addedColumn = Integer.parseInt(editColumn.getText().toString());
                addedRow = Integer.parseInt(editRow.getText().toString());

                if (addedID.length() < 10 ) {
                    Toast.makeText(AddPlantScreen.this, "Plant ID needs to be 10 digits.", Toast.LENGTH_SHORT).show();
                } else {
                    firebase.writeNewPlant(addedID, selectedType, addedRow, addedColumn, selectedHealth);
                    Toast.makeText(AddPlantScreen.this, "Plant created!", Toast.LENGTH_SHORT).show();

                    //clean input
                    editID.setText("");
                    editColumn.setText("");
                    editRow.setText("");
                    spinnerPlantType.setAdapter(adapter2);
                    spinnerPlantHealth.setAdapter(adapter);
                }
            }
        });
    }
}
