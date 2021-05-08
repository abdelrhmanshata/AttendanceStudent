package com.example.attendancestudent.Activity.Student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.attendancestudent.Activity.AppInfoActivity;
import com.example.attendancestudent.Activity.TableLectureActivity;
import com.example.attendancestudent.Auth.LoginActivity;
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityStudentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ActivityStudentBinding studentBinding;
    private Handler mHandler;
    private Runnable mUpdateTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentBinding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(studentBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {
            @SuppressLint("SetTextI18n")
            public void run() {
                mHandler.postDelayed(this, 1000);
                studentBinding.textViewDate.setText("" + getCurrentData().trim());
                studentBinding.textViewDay.setText("" + getCurrentDay().trim());
                studentBinding.textViewTime.setText("" + getCurrentTime().trim());
            }
        };

        studentBinding.layoutQR.setOnClickListener(v -> startActivity(new Intent(StudentActivity.this, StudentScannerQrCodeActivity.class)));
        studentBinding.layoutTableLectur.setOnClickListener(v -> startActivity(new Intent(StudentActivity.this, TableLectureActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mUpdateTimeTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.profile:
                startActivity(new Intent(StudentActivity.this, StudentProfileActivity.class));
                break;
            case R.id.info:
                startActivity(new Intent(StudentActivity.this, AppInfoActivity.class));
                break;
            case R.id.logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(StudentActivity.this, LoginActivity.class));
        finish();
    }

    public String getCurrentData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/M/dd", Locale.getDefault());
        return mdformat.format(calendar.getTime());
    }

    public String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("EEEE", Locale.getDefault());
        return mdformat.format(calendar.getTime());
    }

    public String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return mdformat.format(calendar.getTime());
    }
}