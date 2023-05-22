package com.example.projectfinal_alpha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class new_teacher extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    RadioGroup radioGroup;
    Spinner gradeSpinner;
    Spinner classSpinner;

    String[] grades = {"", "ז", "ח", "ט", "י", "יא", "יב"};
    int[] gradesnums = {0, 7, 8, 9, 10, 11, 12};
    String[] classes = {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};

    int teacherGrade = -1;
    int teacherClass = -1;

    ArrayAdapter<String> adp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teacher);

        radioGroup = findViewById(R.id.radioGroup);
        gradeSpinner = findViewById(R.id.grade_spinner);
        classSpinner = findViewById(R.id.class_spinner);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, grades);
        gradeSpinner.setAdapter(adp);

        adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, classes);
        classSpinner.setAdapter(adp);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.teacherRadioA) {
                    gradeSpinner.setEnabled(false);
                    classSpinner.setEnabled(false);
                } else {
                    gradeSpinner.setEnabled(true);
                    classSpinner.setEnabled(true);
                }
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            teacherGrade = -1;
            teacherClass = -1;

        } else {
            if (adapterView == gradeSpinner) {
                teacherGrade = gradesnums[i];
            } else if (adapterView == classSpinner) {
                teacherClass = Integer.parseInt(classes[i]);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
