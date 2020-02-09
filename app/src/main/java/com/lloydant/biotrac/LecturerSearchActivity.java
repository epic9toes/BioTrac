package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lloydant.biotrac.Repositories.implementations.LecturerSearchRepo;
import com.lloydant.biotrac.customAdapters.LecturerListAdapter;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.presenters.LecturerSearchActivityPresenter;
import com.lloydant.biotrac.views.LecturerSearchActivityView;

import java.util.ArrayList;

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
    private EditText editTextSearch;
    private ImageView closeBtn;

    public  static  final String LecturerActivity = "LecturerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_search);

        mLoading = findViewById(R.id.loading);
        mNotFound = findViewById(R.id.notFound);
        errorMsg = mNotFound.findViewById(R.id.errorMsg);
        mRecyclerView = findViewById(R.id.recyclerView);
        editTextSearch = findViewById(R.id.editTextSearch);
        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(view -> finish());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mPresenter = new LecturerSearchActivityPresenter(this, new LecturerSearchRepo());
        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        String token = mPreferences.getString("token", "Token not found!");
        mPresenter.GetLecturers(token);


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterRecycler(editable.toString());
            }
        });

    }

    private void filterRecycler(String text) {
        //new array list that will hold the filtered data
        ArrayList<Lecturer> lecturers = new ArrayList<>();

        //looping through existing elements
        for (Lecturer lecturer : mLecturerArrayList) {
            //if the existing elements contains the search input
            if (lecturer.getEmail().toLowerCase().contains(text.toLowerCase()) || lecturer.getName().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                lecturers.add(lecturer);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        mListAdapter.filterList(lecturers);
    }

    @Override
    public void onLecturerClick(View view, int position) {
        String name = mLecturerArrayList.get(position).getName();
        String id = mLecturerArrayList.get(position).getId();
        Intent intent = new Intent(LecturerSearchActivity.this, EnrollFingerprintActivity.class);
        intent.putExtra("LecturerName", name);
        intent.putExtra("LecturerID",id);
        intent.putExtra(LecturerActivity, LecturerActivity);
        startActivity(intent);
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
