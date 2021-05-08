package com.example.attendancestudent.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.Auth.Model.ModelStudent;
import com.example.attendancestudent.R;

import java.util.Objects;

public class RegisterStudentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText etAddress, etsNational_ID;
    String[] Departments = {"D1", "D2", "D3", "D4", "D5"};
    String Year = "", Department = "";
    Button register;
    ModelStudent student;
    RadioGroup radioGroup;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        student = (ModelStudent) getIntent().getSerializableExtra("Student");

        etAddress = findViewById(R.id.register_address);
        etsNational_ID = findViewById(R.id.register_national_id);
        //
        radioGroup = findViewById(R.id.radioGroupAcademicYear);
        spinner = findViewById(R.id.spinner);

        register = findViewById(R.id.btn_register);

        spinner.setOnItemSelectedListener(RegisterStudentActivity.this);
        ArrayAdapter adapter = new ArrayAdapter(RegisterStudentActivity.this, android.R.layout.simple_spinner_item, Departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extracting

                String address = Objects.requireNonNull(etAddress.getText()).toString().trim();
                if (address.isEmpty()) {
                    etAddress.setError("Your Address is Required");
                    etAddress.setFocusable(true);
                    etAddress.requestFocus();
                    return;
                }

                String nationalId = Objects.requireNonNull(etsNational_ID.getText()).toString().trim();
                if (nationalId.isEmpty()) {
                    etsNational_ID.setError("Your National ID is Required");
                    etsNational_ID.setFocusable(true);
                    etsNational_ID.requestFocus();
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

                student.setsAddress(address);
                student.setsNational_ID(nationalId);
                student.setsAcademicYear(Year);
                student.setsDepartment(Department);

                Intent intent = new Intent(RegisterStudentActivity.this, OTPActivity.class);
                intent.putExtra("ModelStudent", student);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Department = Departments[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}