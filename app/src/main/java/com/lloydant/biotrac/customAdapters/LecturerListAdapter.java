package com.lloydant.biotrac.customAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lloydant.biotrac.R;
import com.lloydant.biotrac.models.Lecturer;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class LecturerListAdapter extends RecyclerView.Adapter<LecturerListAdapter.ViewHolder> {

    private ArrayList<Lecturer> mLecturers;
    private OnLecturerListener mOnLecturerListener;


    public LecturerListAdapter(ArrayList<Lecturer> lecturers, OnLecturerListener onLecturerListener) {
        this.mLecturers = lecturers;
        this.mOnLecturerListener = onLecturerListener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lecturer_list_layout,
                parent,false);
        return new ViewHolder(v, mOnLecturerListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(mLecturers.get(position).getName());
        holder.email.setText(mLecturers.get(position).getEmail());
        holder.phoneNo.setText(mLecturers.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return mLecturers.size();
    }

    public void filterList(ArrayList<Lecturer> lecturers) {
        this.mLecturers = lecturers;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AppCompatImageView userImg;
        TextView username;
        TextView email;
        TextView phoneNo;

        OnLecturerListener onLecturerListener;

        public ViewHolder(@NonNull View itemView, OnLecturerListener onLecturerListener) {
            super(itemView);
            userImg  = itemView.findViewById(R.id.userImg);
            username = itemView.findViewById(R.id.username);
            email  = itemView.findViewById(R.id.email);
            phoneNo = itemView.findViewById(R.id.phoneNo);
            this.onLecturerListener = onLecturerListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onLecturerListener.onLecturerClick(view, getAdapterPosition());
        }
    }

    public interface OnLecturerListener {
        void onLecturerClick(View view, int position);
    }

}
