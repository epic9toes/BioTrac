package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydant.biotrac.Repositories.implementations.LecturerSearchRepo;
import com.lloydant.biotrac.customAdapters.LecturerListAdapter;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.presenters.LecturerSearchActivityPresenter;
import com.lloydant.biotrac.views.LecturerSearchActivityView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;


public class LecturerSearchActivity extends AppCompatActivity implements LecturerListAdapter.OnLecturerListener, LecturerSearchActivityView {

    private SharedPreferences mPreferences;
    private ArrayList<Lecturer> mLecturerArrayList;
    LecturerListAdapter mListAdapter;
    RecyclerView mRecyclerView;
    LecturerSearchActivityPresenter mPresenter;
    private View mLoading, mNotFound;
    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_search);

        mLoading = findViewById(R.id.loading);
        mNotFound = findViewById(R.id.notFound);
        errorMsg = mNotFound.findViewById(R.id.errorMsg);
        mRecyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mPresenter = new LecturerSearchActivityPresenter(this, new LecturerSearchRepo());
        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        String token = mPreferences.getString("token", "Token not found!");
        mPresenter.GetLecturers(token);

        mLecturerArrayList = new ArrayList<>();
        mLecturerArrayList.add(new Lecturer("123","Prof. John Nsika",
                "08032643363","johndoe@gmail.com",""));
        mListAdapter = new LecturerListAdapter(mLecturerArrayList, this);
        mRecyclerView.setAdapter(mListAdapter);
    }

    @Override
    public void onLecturerClick(View view, int position) {
        String name = mLecturerArrayList.get(position).getName();
        Toast.makeText(this, "Lecturer: clicked " + name, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LecturerSearchActivity.this, EnrollFingerprintActivity.class));

    }

    @Override
    public void OnGetLecturers(ArrayList<Lecturer> lecturerArrayList) {
        mLecturerArrayList = lecturerArrayList;
        mListAdapter = new LecturerListAdapter(mLecturerArrayList, this);
        mRecyclerView.setAdapter(mListAdapter);
        mLoading.setVisibility(View.GONE);
        mNotFound.setVisibility(View.GONE);
    }

    @Override
    public void OnGetEmptyLecturerList() {
        mLoading.setVisibility(View.GONE);
        mNotFound.setVisibility(View.VISIBLE);
        errorMsg.setText("No Lecturer currently exist on the system, please try again later.");
    }

    @Override
    public void OnFailure(Throwable e) {
        mLoading.setVisibility(View.GONE);
        mNotFound.setVisibility(View.VISIBLE);
        errorMsg.setText("Error: " + e.getMessage() + ", please check your internet connection.");
    }
}
