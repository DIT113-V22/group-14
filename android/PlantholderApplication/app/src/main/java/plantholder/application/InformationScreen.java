package plantholder.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    private RecyclerView recyclerView;
    private PlantsAdapter adapter;
    private ArrayList<Plants> plantList;
    private DatabaseReference database;
    private SearchView searchView;
    private Button plantStatsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_screen);

        database= FirebaseDatabase.getInstance().getReference("Plants");

        //All initializations
        recyclerView = findViewById(R.id.showPlants);
        searchView = findViewById(R.id.searchView);
        plantStatsBtn = findViewById(R.id.plantStats);
        Button back = findViewById(R.id.backMain);
        plantStatsBtn = findViewById(R.id.plantStats);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        adapter = new PlantsAdapter(plantList);

        recyclerView.setAdapter(adapter);
        searchView.setQueryHint("Status of the plant");

        //hide decor view
        getWindow().getDecorView().getWindowInsetsController().hide(android.view.WindowInsets.Type.statusBars());

        //Sends back to the previous page when press back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Sends to the statistics page
        plantStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationScreen.this,PlantStatistics.class);
                startActivity(intent);
            }
        });
    }

    //Populate table with data from the database
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
                        PlantsAdapter  adapter1 = new PlantsAdapter(plantList);
                        recyclerView.setAdapter(adapter1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(InformationScreen.this, "No Data found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //if search view is empty populate the table with all plants infor
        if (searchView== null){
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Plants plant = dataSnapshot.getValue(Plants.class);
                        plantList.add(plant);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(InformationScreen.this, "No Data found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //if search view has something entered retrieve the info from that reference
        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText!= null )
                    {
                        if (newText.isEmpty()){
                            onStart();
                        }
                        else{
                            search(newText);
                        }
                    }
                    return true;
                }
            });
        }
    }

    //Method that goes through the plants and set the results on the table
    private void search(String str){
        ArrayList<Plants> newPlantList = new ArrayList<>();

        for (Plants plant : plantList) {
            if (plant.getHealth().toLowerCase().equalsIgnoreCase(str.toLowerCase()) || plant.getSpecies().toLowerCase().equalsIgnoreCase(str.toLowerCase())) {
                newPlantList.add(plant);
            }
        }
        PlantsAdapter adapter2 = new PlantsAdapter(newPlantList);
        recyclerView.setAdapter(adapter2);
    }
}