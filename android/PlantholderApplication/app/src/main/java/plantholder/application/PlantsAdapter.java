package plantholder.application;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Plants> list;

    public PlantsAdapter(Context context, ArrayList<Plants> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.show_plants,parent,false);
        return new ViewHolder(v);

        //return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_plants,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plants plant = list.get(position);
        holder.ID.setText(plant.getID());
        holder.species.setText(plant.getSpecies());
        holder.health.setText(plant.getHealth());
        holder.row.setText(plant.getRow()+"");
        holder.column.setText(plant.getColumn()+"");

       // holder.column.setText(plant.getColumn());



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ID;
        TextView species;
        TextView row;
        TextView column;
        TextView health;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ID = itemView.findViewById(R.id.plantID);
            species = itemView.findViewById(R.id.plantSpecies);
            health = itemView.findViewById(R.id.plantHealth);
            row = itemView.findViewById(R.id.plantRow);
            column = itemView.findViewById(R.id.plantColumn);


        }
    }
}
