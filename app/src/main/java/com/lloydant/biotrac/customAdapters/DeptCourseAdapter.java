package com.lloydant.biotrac.customAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lloydant.biotrac.R;
import com.lloydant.biotrac.models.DepartmentalCourses;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeptCourseAdapter extends RecyclerView.Adapter<DeptCourseAdapter.ViewHolder> {

    private ArrayList<DepartmentalCourses> mDepartmentalCoursesArrayList;
    private OnDepartmentalCourseListener mOnDepartmentalCourseListener;

    public DeptCourseAdapter(ArrayList<DepartmentalCourses> departmentalCoursesArrayList,
                             OnDepartmentalCourseListener onDepartmentalCourseListener) {
        this.mDepartmentalCoursesArrayList = departmentalCoursesArrayList;
        this.mOnDepartmentalCourseListener = onDepartmentalCourseListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.departmental_courses_template,
                parent, false);
        return new ViewHolder(v, mOnDepartmentalCourseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.txtCode.setText(mDepartmentalCoursesArrayList.get(position).getCode());
            holder.txtLevel.setText(mDepartmentalCoursesArrayList.get(position).getLevel());
            holder.txtSemester.setText(mDepartmentalCoursesArrayList.get(position).getSemester());
            holder.txtTitle.setText(mDepartmentalCoursesArrayList.get(position).getTitle());
    }


    @Override
    public int getItemCount() {
        return mDepartmentalCoursesArrayList.size();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        OnDepartmentalCourseListener mOnDepartmentalCourseListener;
        TextView txtCode, txtTitle, txtLevel, txtSemester;

        public ViewHolder(@NonNull View itemView, OnDepartmentalCourseListener onDepartmentalCourseListener) {
            super(itemView);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            txtSemester = itemView.findViewById(R.id.txtSemester);

            this.mOnDepartmentalCourseListener = onDepartmentalCourseListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        mOnDepartmentalCourseListener.onDepartmentalCourseClick(view, getAdapterPosition());
        }
    }


    public interface OnDepartmentalCourseListener {
        void onDepartmentalCourseClick(View view, int position);
    }
}
