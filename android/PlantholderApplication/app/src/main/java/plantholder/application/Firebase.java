package plantholder.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Firebase extends AppCompatActivity {

    private DatabaseReference myDatabase;



    public Task<Void> addPlantTask(Plants plant) {
      return myDatabase.push().setValue(plant);
    }

    public Task<Void> updatePlantTask(String key, HashMap<String, Object> hashMap) {
        return myDatabase.child(key).updateChildren(hashMap);
    }


    //old below
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

    }

    public void getDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://planty-9e09f-default-rtdb.europe-west1.firebasedatabase.app/");
        myDatabase = database.getReference("Plants");
    }

    public void getPlantInformation(String ID){

       // DatabaseReference database = FirebaseDatabase.getInstance("https://planty-9e09f-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Plants");
DatabaseReference database = FirebaseDatabase.getInstance().getReference("Plants");
        Query getType = myDatabase.orderByChild("id").equalTo(ID);
        Query getType = myDatabase()
        //Query query=users.child(auth.getCurrentUser().getUid()).orderByChild("email");
        getType.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String plantType = snapshot.child(ID).child("species").getValue(String.class);
                    String plantStatus = snapshot.child(ID).child("health").getValue(String.class);
                    int plantRow= snapshot.child(ID).child("row").getValue(int.class);
                    int plantColumn = snapshot.child(ID).child("column").getValue(int.class);


                    /*Intent intent = new Intent(getApplicationContext(),Plants.class);;
                    intent.putExtra("ID", ID);
                    intent.putExtra("species", plantType);
                    intent.putExtra("row", plantRow);
                    intent.putExtra("column", plantColumn);
                    intent.putExtra("health", plantStatus);
                    TextView plantIdText = (TextView)findViewById(R.id.plantIdTextView);
                    plantIdText.setText("ID: " + plantStatus);

                    startActivity(intent);

                     */


                    TextView plantTypeText = (TextView)findViewById(R.id.plantTypeTextView);
                    plantTypeText.setText(String.format("Type: %s", plantType));

                    TextView plantStatusText = (TextView)findViewById(R.id.plantStatusTextView);
                    plantStatusText.setText(String.format("Status: %s", plantStatus));

                    TextView plantRowText = (TextView)findViewById(R.id.plantRowTextView);
                    plantRowText.setText(String.format("Row: %s", plantRow));

                    TextView plantColumnText = (TextView)findViewById(R.id.plantColumnTextView);
                    plantColumnText.setText(String.format("Column: %s", plantColumn));

                } else {
                    TextView plantIdText = (TextView)findViewById(R.id.plantIdTextView);
                    String noSuchThing = "ID does not exist";
                    plantIdText.setText(noSuchThing);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // Do not delete method, we will use it later
    public void writeNewPlant(String ID,String species, int row, int column, String health){
        getDatabase();

        Plants plant = new Plants(ID,species,row,column,health);
        myDatabase.child(ID).setValue(plant);

    }

    public void updatePlantHealth(String ID, String status){
        getDatabase();
        myDatabase.child(ID).child("health").setValue(status);

    }

}
