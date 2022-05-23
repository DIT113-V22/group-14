package plantholder.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class PlantStatistics extends AppCompatActivity {

    private TextView plantTotal;
    private TextView healthyPlant;
    private TextView unHealthyPlant;
    private TextView ripePlant;
    private TextView trackPlant;
    private TextView tomatoPlant;
    private TextView otherPlant;
    private TextView grapePlant;

    private int plantCount = 0;
    private int healthy = 0;
    private int unhealthy = 0;
    private int ripe = 0;
    private int track = 0;
    private int tomato = 0;
    private int grape = 0;
    private int other = 0;

    private long tomatoPercentage = 0;
    private long otherPercentage = 0;
    private long grapePercentage = 0;
    private long healthyPercentage = 0;
    private long unhealthyPercentage = 0;
    private long ripePercentage = 0;
    private long trackPercentage = 0;

    private Button backToInfoBtn;
    private PieChart chart;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_statistics);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

       //get the total number of plants registered in the database
        plantTotal = (TextView) findViewById(R.id.plantsumAmnt);
        chart = findViewById(R.id.pie_chart);

        backToInfoBtn = findViewById(R.id.backInfoButton);
        backToInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlantStatistics.this,InformationScreen.class);
                startActivity(intent);
            }
        });

        getWindow().getDecorView().getWindowInsetsController().hide(
                android.view.WindowInsets.Type.statusBars()
        );


        databaseReference = FirebaseDatabase.getInstance().getReference("Plants");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot totalSnapshot) {
                plantCount= (int) totalSnapshot.getChildrenCount();
                plantTotal.setText(Integer.toString(plantCount) + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get the percentages of different plant health statuses
        healthyPlant = (TextView) findViewById(R.id.healthyPlant);
        unHealthyPlant = (TextView) findViewById(R.id.unhealthyPlant);
        ripePlant = (TextView) findViewById(R.id.ripePlant);
        trackPlant = (TextView) findViewById(R.id.trackPlant);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot healthSnapshot : snapshot.getChildren()) {

                  switch(healthSnapshot.child("health").getValue(String.class)){

                      case "Healthy":
                            ++healthy;
                             break;
                      case "Unhealthy":
                            ++unhealthy;
                              break;
                      case "Ripe":
                            ++ripe;
                            break;
                      case "Keep Track":
                            ++track;
                            break;
                  }

                healthyPercentage = (healthy * 100L)/ plantCount;
                unhealthyPercentage = (unhealthy* 100L)/ plantCount;
                ripePercentage = (ripe * 100L)/ plantCount;
                trackPercentage = (track * 100L)/ plantCount;

                healthyPlant.setText(Long.toString(healthyPercentage) + "%");
                unHealthyPlant.setText(Long.toString(unhealthyPercentage) + "%");
                ripePlant.setText(Long.toString(ripePercentage) + "%");
                trackPlant.setText(Long.toString(trackPercentage) + "%");
                }

             chart.addPieSlice(new PieModel("Healthy", healthyPercentage, Color.parseColor("#26912B")));
             chart.addPieSlice(new PieModel("Unhealthy", unhealthyPercentage, Color.parseColor("#704C17")));
             chart.addPieSlice(new PieModel("Ripe", ripePercentage, Color.parseColor("#AF2D2A")));
             chart.addPieSlice(new PieModel("Track", trackPercentage, Color.parseColor("#CC9D12")));


             chart.startAnimation();



         }

                  @Override
                public void onCancelled(@NonNull DatabaseError error) {

                     }
                }
            );

// get species population in %
        tomatoPlant = (TextView) findViewById(R.id.tomatoPlants);
        grapePlant = (TextView) findViewById(R.id.grapePlants);
        otherPlant = (TextView) findViewById(R.id.otherPlants);
        databaseReference = FirebaseDatabase.getInstance().getReference("Plants");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot speciesSnapshot : snapshot.getChildren()) {

                    switch(speciesSnapshot.child("species").getValue(String.class)){
                        case "Tomato":
                            ++tomato;
                            break;
                        case "Grape":
                            ++grape;
                            break;
                        default:
                            ++other;
                            break;
                    }
                    tomatoPercentage = (tomato* 100L)/ plantCount;
                    grapePercentage = (grape* 100L)/ plantCount;
                    otherPercentage = (other* 100L)/ plantCount;

                    tomatoPlant.setText(Long.toString(tomatoPercentage) + "%");
                    grapePlant.setText(Long.toString(grapePercentage) + "%");
                    otherPlant.setText(Long.toString(otherPercentage) + "%" );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}