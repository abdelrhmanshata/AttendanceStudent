package com.example.attendancestudent.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.Auth.Model.ModelProfessor;
import com.example.attendancestudent.R;

import java.util.Objects;

public class RegisterProfessorActivity extends AppCompatActivity {

    EditText subjectName;
    Button register;
    RadioGroup radioGroup;
    String Year = "";
    ModelProfessor professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_professor);

        professor = (ModelProfessor) getIntent().getSerializableExtra("Professor");

        subjectName = findViewById(R.id.subject_name_prof);
        register = findViewById(R.id.btn_register_prof);
        radioGroup = findViewById(R.id.radioGroupAcademicYear);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extracting

                String subject = Objects.requireNonNull(subjectName.getText()).toString().trim();
                if (subject.isEmpty()) {
                    subjectName.setError("Your Subject Name is Required");
                    subjectName.setFocusable(true);
                    subjectName.requestFocus();
                    return;
                }


                int selectedAcademicYear = radioGroup.getCheckedRadioButtonId();
                if (selectedAcademicYear == R.id.year1) {
                    Year = "Year1";
                } else if (selectedAcademicYear == R.id.year2) {
                    Year = "Year2";
                } else if (selectedAcademicYear == R.id.year3) {
                    Year = "Year3";
                } else if (selectedAcademicYear == R.id.year4) {
                    Year = "Year4";
                }

                professor.setpSubject_Name(subject);
                professor.setpAcademicYear(Year);

                Intent intent = new Intent(RegisterProfessorActivity.this, OTPActivity.class);
                intent.putExtra("ModelProfessor", professor);
                startActivity(intent);
                finish();
            }
        });


    }
}