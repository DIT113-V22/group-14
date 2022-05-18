package plantholder.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_screen);

        recyclerView = findViewById(R.id.showPlants);
        database= FirebaseDatabase.getInstance().getReference("Plants");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        plantList = new ArrayList<>();
        adapter = new PlantsAdapter(this,plantList);
        recyclerView.setAdapter(adapter);



        /* Button back = findViewById(R.id.backMain);
         back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoScreen.this,HomeScreen.class);
                startActivity(intent);
            }
        });

        Button statistics = findViewById(R.id.statisticsScreen);

          statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoScreen.this,PlantStatistics.class);
                startActivity(intent);
            }
        });
*/

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

            }
        });





    }
}