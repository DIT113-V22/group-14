package plantholder.application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Firebase extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private DatabaseReference userDatabase;
    private FirebaseDatabase firebaseDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    //Import the database data through the link and stores in an instance of the FirebaseDatase
    public void getDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://planty-9e09f-default-rtdb.europe-west1.firebasedatabase.app/");
        myDatabase = database.getReference("Plants");
        userDatabase = database.getReference("Users");
    }

    //Create new user method
    public void writeNewUser(String userName, String email, String password){
        getDatabase();
        User users = new User(userName, email, password);
        userDatabase.child(userName).setValue(users);
    }

    //Creates new plants method
    public void writeNewPlant(String ID,String species, int row, int column, String selectedHealth){
        getDatabase();
        Plants plant = new Plants(ID,species,row,column,selectedHealth);
        myDatabase.child(ID).setValue(plant);
    }

    //Updated the status of the plant. Method called after picture taken from the QR code on the status screen
    public void updatePlantHealth(String ID, String status){

        firebaseDatabase =  FirebaseDatabase.getInstance();
        myDatabase = firebaseDatabase.getReference("Plants").child(ID).child("id");

        myDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                if (value != null) {
                    myDatabase = firebaseDatabase.getReference("Plants");
                    myDatabase.child(ID).child("health").setValue(status);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
