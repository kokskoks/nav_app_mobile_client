package pl.lodz.p.navapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Łukasz Świtoń on 24.11.2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ClassesViewHolder>{
    private List<ClassInfo> classes;


    public RVAdapter(List<ClassInfo> classes){
        this.classes = classes;
    }
    @Override
    public ClassesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row, parent, false);

        return new ClassesViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(ClassesViewHolder holder, int position) {
        holder.nazwaPrzedmiotu.setText(classes.get(position).getName());
        holder.budynek.setText(classes.get(position).getBuilding());
        holder.godzinaOd.setText(classes.get(position).getFrom());
        holder.godzinaDo.setText(classes.get(position).getTo());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ClassesViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView nazwaPrzedmiotu;
        TextView budynek;
        TextView godzinaOd;
        TextView godzinaDo;

        ClassesViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            nazwaPrzedmiotu = (TextView)itemView.findViewById(R.id.className);
            budynek = (TextView)itemView.findViewById(R.id.buildingName);
            godzinaOd = (TextView)itemView.findViewById(R.id.from);
            godzinaDo = (TextView)itemView.findViewById(R.id.to);

        }
    }

}