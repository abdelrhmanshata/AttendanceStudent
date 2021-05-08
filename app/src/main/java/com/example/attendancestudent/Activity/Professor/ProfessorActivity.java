package com.example.attendancestudent.Activity.Professor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.attendancestudent.Activity.AppInfoActivity;
import com.example.attendancestudent.Activity.TableLectureActivity;
import com.example.attendancestudent.Auth.LoginActivity;
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityProfessorBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfessorActivity extends AppCompatActivity {

    ActivityProfessorBinding professorBinding;

    private Handler mHandler;
    private Runnable mUpdateTimeTask;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        professorBinding = ActivityProfessorBinding.inflate(getLayoutInflater());
        setContentView(professorBinding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {
            @SuppressLint("SetTextI18n")
            public void run() {
                mHandler.postDelayed(this, 1000);
                professorBinding.textViewDate.setText("" + getCurrentData().trim());
                professorBinding.textViewDay.setText("" + getCurrentDay().trim());
                professorBinding.textViewTime.setText("" + getCurrentTime().trim());
            }
        };

        professorBinding.layoutQR.setOnClickListener(v -> {
            startActivity(new Intent(ProfessorActivity.this, ProfessorGenerateQrCodeActivity.class));
        });

        professorBinding.layoutTableLectur.setOnClickListener(v -> {
            startActivity(new Intent(ProfessorActivity.this, TableLectureActivity.class));
        });

        professorBinding.layoutAttending.setOnClickListener(v -> {
            startActivity(new Intent(ProfessorActivity.this, AttendingStudentsActivity.class));
        });

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
                startActivity(new Intent(ProfessorActivity.this, ProfessorProfileActivity.class));
                break;
            case R.id.info:
                startActivity(new Intent(ProfessorActivity.this, AppInfoActivity.class));
                break;
            case R.id.logout:
                Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(ProfessorActivity.this, LoginActivity.class));
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