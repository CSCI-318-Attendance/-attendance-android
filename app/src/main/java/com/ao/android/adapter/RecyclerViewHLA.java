package com.ao.android.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ao.android.R;
import com.ao.android.data.Classroom;

import java.util.List;

public class RecyclerViewHLA extends RecyclerView.Adapter<RecyclerViewHLA.ClassroomViewHolder> {

    private List<Classroom> classroomList;
    private Context context;

    public RecyclerViewHLA(List<Classroom> classroomList, Context context){
        this.classroomList = classroomList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the layout file
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_layout, parent, false);
        return new ClassroomViewHolder(groceryProductView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, final int position) {
        Classroom classroom = classroomList.get(position);
        holder.cl_i_view.setImageDrawable(context.getDrawable(R.drawable.ic_description_black_24dp));
        holder.cl_title_view.setText(classroom.getFullTitle());
        holder.cl_course_view.setText(classroom.getClassType().toString());
        holder.cl_course_time.setText(classroom.getTimeDuration());
        holder.cl_course_location.setText(classroom.getLocation());
    }

    @Override
    public int getItemCount() {
        return classroomList.size();
    }

    class ClassroomViewHolder extends RecyclerView.ViewHolder {
        ImageView cl_i_view;
        TextView cl_title_view;
        TextView cl_course_view;
        TextView cl_course_time;
        TextView cl_course_location;
        ClassroomViewHolder(View view) {
            super(view);
            cl_i_view = view.findViewById(R.id.cl_i_view);
            cl_title_view = view.findViewById(R.id.cl_title_view);
            cl_course_view = view.findViewById(R.id.cl_course_view);
            cl_course_time = view.findViewById(R.id.cl_course_time);
            cl_course_location = view.findViewById(R.id.cl_course_location);
        }
    }
}
