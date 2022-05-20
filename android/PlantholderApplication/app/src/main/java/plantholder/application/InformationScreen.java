package plantholder.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InformationScreen extends AppCompatActivity  {
    RecyclerView recyclerView;
    PlantsAdapter adapter;
    ArrayList<Plants> plantList;
    DatabaseReference database;
    SearchView searchView;
    Dialog myDialog;
    Button plantStatsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_screen);

        database= FirebaseDatabase.getInstance().getReference("Plants");
        recyclerView = findViewById(R.id.showPlants);
        searchView = findViewById(R.id.searchView);
        plantStatsBtn = findViewById(R.id.plantStats);
        myDialog = new Dialog(this);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        plantList = new ArrayList<>();
        adapter = new PlantsAdapter(plantList);
        recyclerView.setAdapter(adapter);

        /* Button back = findViewById(R.id.backMain);
         back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoScreen.this,HomeScreen.class);
                startActivity(intent);
            }
        });
*/
       plantStatsBtn = findViewById(R.id.plantStats);

        plantStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationScreen.this,PlantStatistics.class);
                startActivity(intent);
            }
        });

    }

// populate table with data from the database
    protected void onStart(){
        super.onStart();
        if(database != null){
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        plantList = new ArrayList<>();

                        for(DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Plants plant = dataSnapshot.getValue(Plants.class);
                            plantList.add(plant);
                        }

                        adapter = new PlantsAdapter(plantList);
                        recyclerView.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(InformationScreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            }
            );
        }
    }

    private void search(String str){

        ArrayList<Plants> newPlantList = new ArrayList<>();

        for (Plants plant : plantList) {
            if(plant.getHealth().toLowerCase().equalsIgnoreCase(str.toLowerCase()) ||plant.getSpecies().toLowerCase().equalsIgnoreCase(str.toLowerCase()) ){
                newPlantList.add(plant);
            }
            
        }
        adapter = new PlantsAdapter(newPlantList);
        recyclerView.setAdapter(adapter);
    }

}