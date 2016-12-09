package pl.lodz.p.navapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.lodz.p.navapp.domain.ClassInfo;
import pl.lodz.p.navapp.domain.Classes;
import pl.lodz.p.navapp.domain.Classroom;
import pl.lodz.p.navapp.domain.Group;

/**
 * Created by Łukasz Świtoń on 24.11.2016.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ClassesViewHolder>{
    private Group group;
    private List<Classes> classes;


    public RVAdapter(Group group){
        this.group = group;
        this.classes=group.getClassesList();
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
        String str;
        Classes classInfo =classes.get(position);
        Classroom classroom = group.getClassroom();
        holder.nazwaPrzedmiotu.setText(classInfo.getName());
        holder.prowadzacy.setText(classInfo.getLecturerList().get(0).getFullName());
        holder.budynek.setText(classroom.getBuilding().getTitle());
        String from = String.valueOf(classInfo.getStartHour());
        str = new StringBuilder(from).insert(from.length()-2, ":").toString();
        holder.godzinaOd.setText(str);
        String to = String.valueOf(classInfo.getEndHour());
        str = new StringBuilder(to).insert(to.length()-2, ":").toString();
        holder.godzinaDo.setText(str);
        holder.klasa.setText(classroom.getName());
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ClassesViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView nazwaPrzedmiotu;
        TextView prowadzacy;
        TextView budynek;
        TextView godzinaOd;
        TextView godzinaDo;
        TextView klasa;

        ClassesViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            nazwaPrzedmiotu = (TextView)itemView.findViewById(R.id.className);
            prowadzacy = (TextView) itemView.findViewById(R.id.lecturerTextView);
            budynek = (TextView)itemView.findViewById(R.id.buildingName);
            godzinaOd = (TextView)itemView.findViewById(R.id.from);
            godzinaDo = (TextView)itemView.findViewById(R.id.to);
            klasa = (TextView) itemView.findViewById(R.id.classroomTextView);

        }
    }

}