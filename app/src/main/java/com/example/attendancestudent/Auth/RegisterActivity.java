package com.example.attendancestudent.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.Auth.Model.ModelProfessor;
import com.example.attendancestudent.Auth.Model.ModelStudent;
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityRegisterBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding registerBinding;
    String Title = "", Gender = "";

    FirebaseDatabase database;
    DatabaseReference UserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());

        database = FirebaseDatabase.getInstance();
        UserReference = database.getReference("Users");


        registerBinding.btnGoToLogin.setOnClickListener(v -> {
            onBackPressed();
        });

        registerBinding.btnNext.setOnClickListener(v -> {
            //Extract the data from edit texts
            //Get complete phone number
            String _getUserEnteredPhoneNumber = Objects.requireNonNull(registerBinding.etPhone.getText()).toString().trim();
            if (TextUtils.isEmpty(_getUserEnteredPhoneNumber)) {
                registerBinding.etPhone.setError("" + getResources().getString(R.string.phone_is_required));
                registerBinding.etPhone.setFocusable(true);
                registerBinding.etPhone.requestFocus();
                return;
            } else {
                //Remove first zero if entered!
                if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
                    _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
                }
            }
            //Complete phone number
            String UserPhoneNumber = "+" + "20" + _getUserEnteredPhoneNumber;

            if (UserPhoneNumber.length() < 11) {
                registerBinding.etPhone.setError("" + getResources().getString(R.string.please_enter_number));
                registerBinding.etPhone.setFocusable(true);
                registerBinding.etPhone.requestFocus();
                return;
            }

            String UserEmail = Objects.requireNonNull(registerBinding.etEmailAddress.getText()).toString().trim();
            if (UserEmail.isEmpty()) {
                registerBinding.etEmailAddress.setError("" + getResources().getString(R.string.email_required));
                registerBinding.etEmailAddress.setFocusable(true);
                registerBinding.etEmailAddress.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
                registerBinding.etEmailAddress.setError("" + getResources().getString(R.string.please_enter_email));
                registerBinding.etEmailAddress.setFocusable(true);
                registerBinding.etEmailAddress.requestFocus();
                return;
            }

            String fullName = Objects.requireNonNull(registerBinding.etFullName.getText()).toString().trim();
            if (fullName.isEmpty()) {
                registerBinding.etFullName.setError("" + getResources().getString(R.string.username_is_required));
                registerBinding.etFullName.setFocusable(true);
                registerBinding.etFullName.requestFocus();
                return;
            }

            RadioButton selectedJopTitle;
            if (registerBinding.radioGroupJob.getCheckedRadioButtonId() == -1) {
                Toasty.info(RegisterActivity.this, "" + getResources().getString(R.string.select_gender), Toast.LENGTH_SHORT).show();
                return;
            } else {
                selectedJopTitle = findViewById(registerBinding.radioGroupJob.getCheckedRadioButtonId());
                if (selectedJopTitle.getId() == R.id.student) {
                    Title = "Student";
                } else {
                    Title = "Professor";
                }
            }

            RadioButton selectedGender;
            if (registerBinding.radioGroupGender.getCheckedRadioButtonId() == -1) {
                Toasty.info(RegisterActivity.this, "" + getResources().getString(R.string.select_gender), Toast.LENGTH_SHORT).show();
                return;
            } else {
                selectedGender = findViewById(registerBinding.radioGroupGender.getCheckedRadioButtonId());
                if (selectedGender.getId() == R.id.male) {
                    Gender = "Male";
                } else {
                    Gender = "Female";
                }
            }

            try {
                // Check UserPhone Number Is Found or not
                UserReference.child(UserPhoneNumber).child("JobTitle").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String JopTitle = snapshot.getValue(String.class);
                        if (JopTitle == null) {
                            //Now data is validated
                            if (Title == "Student") {

                                ModelStudent student = new ModelStudent();
                                student.setsKey(UserPhoneNumber);
                                student.setsPhoneNumber(UserPhoneNumber);
                                student.setsEmail(UserEmail);
                                student.setsFullName(fullName);
                                student.setsJopTitle(Title);
                                student.setsGender(Gender);
                                Intent sIntent = new Intent(RegisterActivity.this, RegisterStudentActivity.class);
                                sIntent.putExtra("Student", student);
                                startActivity(sIntent);

                            } else if (Title == "Professor") {

                                ModelProfessor professor = new ModelProfessor();
                                professor.setpKey(UserPhoneNumber);
                                professor.setpPhoneNumber(UserPhoneNumber);
                                professor.setpEmail(UserEmail);
                                professor.setpFullName(fullName);
                                professor.setpJopTitle(Title);
                                professor.setpGender(Gender);
                                Intent pIntent = new Intent(RegisterActivity.this, RegisterProfessorActivity.class);
                                pIntent.putExtra("Professor", professor);
                                startActivity(pIntent);
                            }
                            finish();
                        } else {
                            Toasty.info(RegisterActivity.this, "" + getResources().getString(R.string.phone_is_found), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("", error.getMessage());
                    }
                });
            } catch (NullPointerException e) {
                Log.d("", e.getMessage());
            }

        });
    }
}