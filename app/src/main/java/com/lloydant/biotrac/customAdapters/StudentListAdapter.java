package com.lloydant.biotrac.customAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lloydant.biotrac.R;
import com.lloydant.biotrac.models.Student;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {

    private ArrayList<Student> mStudents;
    private OnStudentListener mOnStudentListener;


    public StudentListAdapter(ArrayList<Student> students, OnStudentListener onStudentListener) {
        this.mStudents = students;
        this.mOnStudentListener = onStudentListener;

    }

    public void filterList(ArrayList<Student> students) {
        this.mStudents = students;
        notifyDataSetChanged();
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
        holder.username.setText(mStudents.get(position).getName());
        holder.department.setText(mStudents.get(position).getDepartment().getName());
        holder.regNo.setText(mStudents.get(position).getReg_no());
        holder.userImg.setImageResource(R.drawable.avatar);
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
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
