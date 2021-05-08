package com.example.attendancestudent.Activity.Professor;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.attendancestudent.Adapter.Adapter_All_Student;
import com.example.attendancestudent.Auth.Model.ModelAttend;
import com.example.attendancestudent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AttendingStudentsActivity extends AppCompatActivity implements Adapter_All_Student.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    static int NumOfLecture = 0;

    Adapter_All_Student adapterAllStudent;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference professorRef = firebaseDatabase.getReference("Professor");
    DatabaseReference reference = firebaseDatabase.getReference("Attendees");
    ProgressBar Loading;
    SwipeRefreshLayout refreshLayout;
    private List<ModelAttend> mAllStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_students);


        refreshLayout = findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(AttendingStudentsActivity.this);
        recyclerView = findViewById(R.id.studentRecyclerView);
        Loading = findViewById(R.id.loading);
        recyclerView.setLayoutManager(new LinearLayoutManager(AttendingStudentsActivity.this));
        recyclerView.setHasFixedSize(true);
        mAllStudent = new ArrayList<>();
        getNumOfLecture();
        Loading.setVisibility(View.VISIBLE);
        Thread thread = new Thread(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                adapterAllStudent = new Adapter_All_Student(AttendingStudentsActivity.this, mAllStudent, NumOfLecture);
                recyclerView.setAdapter(adapterAllStudent);
                getAllStudentAttending();
                adapterAllStudent.setOnItemClickListener(AttendingStudentsActivity.this);
                Loading.setVisibility(View.GONE);
            });
        });
        thread.start();
    }

    void getAllStudentAttending() {
        Loading.setVisibility(View.VISIBLE);
        reference.child(Objects.requireNonNull(firebaseUser.getPhoneNumber())).orderByChild("StudentName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mAllStudent.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ModelAttend attend = snapshot.getValue(ModelAttend.class);
                            if (attend != null) {
                                mAllStudent.add(attend);
                            }
                        }
                        Loading.setVisibility(View.GONE);
                        adapterAllStudent.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Error", error.getMessage());
                        Loading.setVisibility(View.GONE);
                    }
                });
    }

    private void getNumOfLecture() {
        try {
            professorRef.child(Objects.requireNonNull(firebaseUser.getPhoneNumber())).child("pNumLecture")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int NumLecture = snapshot.getValue(Integer.class);
                            if (NumLecture != 0) {
                                NumOfLecture = NumLecture;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Error", error.getMessage());
                        }
                    });
        } catch (NullPointerException e) {
            Log.d("Error", e.getMessage());
        }
    }

    @Override
    public void onItem_Student_Click(int position) {
        Toasty.info(this, "" + mAllStudent.get(position).getStudentName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNumOfLecture();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            refreshLayout.setRefreshing(false);
            getNumOfLecture();
            adapterAllStudent = new Adapter_All_Student(AttendingStudentsActivity.this, mAllStudent, NumOfLecture);
            recyclerView.setAdapter(adapterAllStudent);
            getAllStudentAttending();
        }, 500);
    }

}