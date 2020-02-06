package com.lloydant.biotrac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lloydant.biotrac.customAdapters.StudentListAdapter;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StudentSearchActivity extends AppCompatActivity implements StudentListAdapter.OnStudentListener {

    private ArrayList<String> names;
    private  ArrayList<String> department;
    private ArrayList<String> regNos;


    RecyclerView mRecyclerView;
    StudentListAdapter mListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);

        names = new ArrayList<>();
        names.add("Onwe Sunday .C");
        names.add("Otis Henry .C");
        names.add("Ejike Ezekwunem");
        names.add("Ugochukwu Anike");
        names.add("William Uchemba");
        names.add("John Nsikan .F");
        names.add("King Ifechukwu .D");
        names.add("David Tagba");
        names.add("Chukwuka Innocent");
        names.add("Mbah Chisom");


        department = new ArrayList<>();
        department.add("Biochemistry");
        department.add("Micro Biology");
        department.add("Chemical Engineering");
        department.add("Software Engineering");
        department.add("English Lt.");
        department.add("Computer Science");
        department.add("industrial Arts");
        department.add("Micro Biology");
        department.add("Chemical Engineering");
        department.add("Software Engineering");

        regNos = new ArrayList<>();
        regNos.add("NAU/2015/045864");
        regNos.add("NAU/2018/203578");
        regNos.add("NAU/2014/0354868");
        regNos.add("NAU/2016/2545482");
        regNos.add("NAU/2015/0324876");
        regNos.add("NAU/2018/3651289");
        regNos.add("NAU/2014/0802456");
        regNos.add("NAU/2016/0365845");
        regNos.add("NAU/2020/0324876");
        regNos.add("NAU/2020/3651289");


        mListAdapter = new StudentListAdapter(names,department,regNos, this);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mListAdapter);

    }


    @Override
    public void onStudentClick(View view, int position) {
        String name = names.get(position);
        Toast.makeText(this, "onStudentClick: clicked " + name, Toast.LENGTH_SHORT).show();
       startActivity(new Intent(StudentSearchActivity.this, EnrollFingerprintActivity.class));
    }
}
