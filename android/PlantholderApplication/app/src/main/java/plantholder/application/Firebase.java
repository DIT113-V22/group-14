package plantholder.application;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class Firebase extends AppCompatActivity {

    private DatabaseReference myDatabase;
    private DatabaseReference userDatabase;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

    }

    public void getDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://planty-9e09f-default-rtdb.europe-west1.firebasedatabase.app/");
        myDatabase = database.getReference("Plants");
        userDatabase = database.getReference("Users");
    }

    public void writeNewUser(String userName, String email, String password){
        getDatabase();
        //String userId = userDatabase.push().getKey();
        User users = new User(userName, email, password);
        userDatabase.setValue(users);
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
