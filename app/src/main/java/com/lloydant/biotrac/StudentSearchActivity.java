package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydant.biotrac.Repositories.implementations.StudentSearchRepo;
import com.lloydant.biotrac.customAdapters.LecturerListAdapter;
import com.lloydant.biotrac.customAdapters.StudentListAdapter;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.presenters.LecturerSearchActivityPresenter;
import com.lloydant.biotrac.presenters.StudentSearchActivityPresenter;
import com.lloydant.biotrac.views.StudentSearchActivityView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;

public class StudentSearchActivity extends AppCompatActivity implements StudentListAdapter.OnStudentListener, StudentSearchActivityView {

    private SharedPreferences mPreferences;
    StudentSearchActivityPresenter mPresenter;
    private View mLoading, mNotFound;
    private TextView errorMsg;
    private EditText editTextSearch;
    private ImageView closeBtn;

    private Button btnRetry;

    ArrayList<Student> mStudents;
    RecyclerView mRecyclerView;
    StudentListAdapter mListAdapter;
    String token, dept_id, username;
    int level;

    public  static  final String StudentActivity = "StudentActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        mLoading = findViewById(R.id.loading);
        mNotFound = findViewById(R.id.notFound);
        errorMsg = mNotFound.findViewById(R.id.errorMsg);
        btnRetry = mNotFound.findViewById(R.id.btnRetry);
        mRecyclerView = findViewById(R.id.recyclerView);
        editTextSearch = findViewById(R.id.editTextSearch);
        mRecyclerView = findViewById(R.id.recyclerView);
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(view -> finish());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mPresenter = new StudentSearchActivityPresenter(this, new StudentSearchRepo());
        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        token = mPreferences.getString("token", "Token not found!");
        dept_id = mPreferences.getString("dept_id", "Depertment not found!");
        level = mPreferences.getInt("level", 0);
        mPresenter.GetStudentsByDepartment(token, dept_id,level );

        btnRetry.setOnClickListener(view ->{
            mLoading.setVisibility(View.VISIBLE);
            mPresenter.GetStudentsByDepartment(token, dept_id,level );
        });

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
        ArrayList<Student> students = new ArrayList<>();

        //looping through existing elements
        for (Student student: mStudents) {
            //if the existing elements contains the search input
            if (student.getName().toLowerCase().contains(text.toLowerCase()) || student.getReg_no().toLowerCase()
                    .contains(text.toLowerCase())) {
                //adding the element to filtered list
                students.add(student);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        mListAdapter.filterList(students);
    }

    @Override
    public void onStudentClick(View view, int position) {
        String name = mStudents.get(position).getName();
        String id = mStudents.get(position).getId();
        Intent intent = new Intent(StudentSearchActivity.this, EnrollFingerprintActivity.class);
        intent.putExtra("StudentName", name);
        intent.putExtra("StudentID",id);
        intent.putExtra(StudentActivity, StudentActivity);
        startActivity(intent);
    }

    @Override
    public void OnGetStudents(ArrayList<Student> studentArrayList) {
        if (studentArrayList.size() > 0){
            mLoading.setVisibility(View.GONE);
            mNotFound.setVisibility(View.GONE);
            mStudents = studentArrayList;
            mListAdapter = new StudentListAdapter(mStudents, this);
            mRecyclerView.setAdapter(mListAdapter);
        } else {
            mLoading.setVisibility(View.GONE);
            errorMsg.setText("No unregistered Student currently exist in your stdDepartment, please try again later.");
            btnRetry.setVisibility(View.GONE);
            mNotFound.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void OnGetNullDataResponse() {
        mLoading.setVisibility(View.GONE);
        errorMsg.setText("Something went wrong!");
        mNotFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnFailure(Throwable e) {
        mLoading.setVisibility(View.GONE);
        errorMsg.setText("Error: " + e.getMessage() + ", turn on the internet and try again.");
        mNotFound.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.GetStudentsByDepartment(token, dept_id,level );
        if (mStudents != null){
            if (mStudents.size() < 1 && mNotFound.getVisibility() != View.VISIBLE){
                errorMsg.setText("No unregistered Student currently exist in your stdDepartment, please try again later.");
                mNotFound.setVisibility(View.VISIBLE);

            }
        }

    }
}
