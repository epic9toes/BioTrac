package com.lloydant.biotrac.customAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lloydant.biotrac.R;
import com.lloydant.biotrac.models.Course;
import com.lloydant.biotrac.models.DepartmentalCourse;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeptCourseAdapter extends RecyclerView.Adapter<DeptCourseAdapter.ViewHolder> {

    private ArrayList<Course> mDepartmentalCoursesArrayList;
    private OnDepartmentalCourseListener mOnDepartmentalCourseListener;

    public DeptCourseAdapter(ArrayList<Course> departmentalCoursesArrayList,
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
            holder.txtCreditUnit.setText("Credit Unit: " + mDepartmentalCoursesArrayList.get(position).getCredit_unit());
            holder.txtSemester.setText(mDepartmentalCoursesArrayList.get(position).getSemester());
            holder.txtTitle.setText(mDepartmentalCoursesArrayList.get(position).getTitle());
    }


    @Override
    public int getItemCount() {
        return mDepartmentalCoursesArrayList.size();
    }

    public void filterList(ArrayList<Course> courses) {
        this.mDepartmentalCoursesArrayList = courses;
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        OnDepartmentalCourseListener mOnDepartmentalCourseListener;
        TextView txtCode, txtTitle, txtCreditUnit, txtSemester;

        public ViewHolder(@NonNull View itemView, OnDepartmentalCourseListener onDepartmentalCourseListener) {
            super(itemView);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtCreditUnit = itemView.findViewById(R.id.txtCreditUnit);
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
