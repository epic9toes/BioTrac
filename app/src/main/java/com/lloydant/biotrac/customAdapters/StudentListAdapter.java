package com.lloydant.biotrac.customAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lloydant.biotrac.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private ArrayList<String> names;
    private  ArrayList<String> department;
    private ArrayList<String> regNos;
    private OnStudentListener mOnStudentListener;


    public StudentListAdapter(ArrayList<String> names, ArrayList<String> department,
                              ArrayList<String> regNos, OnStudentListener onStudentListener) {
        this.names = names;
        this.department = department;
        this.regNos = regNos;
        this.mOnStudentListener = onStudentListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.std_list_layout,
                parent,false);
        return new ViewHolder(v, mOnStudentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(names.get(position));
        holder.department.setText(department.get(position));
        holder.regNo.setText(regNos.get(position));
        holder.userImg.setImageResource(R.drawable.profile_img);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatImageView userImg;
        TextView username;
        TextView department;
        TextView regNo;

        OnStudentListener onStudentListener;

        public ViewHolder(@NonNull View itemView, OnStudentListener onStudentListener) {
            super(itemView);
            userImg  = itemView.findViewById(R.id.userImg);
            username = itemView.findViewById(R.id.username);
            department  = itemView.findViewById(R.id.department);
            regNo = itemView.findViewById(R.id.regNo);
            this.onStudentListener = onStudentListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onStudentListener.onStudentClick(view, getAdapterPosition());
        }
    }

    public interface OnStudentListener{
        void onStudentClick(View view, int position);
    }

}
