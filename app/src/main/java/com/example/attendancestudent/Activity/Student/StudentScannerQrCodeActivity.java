package com.example.attendancestudent.Activity.Student;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.attendancestudent.Activity.Professor.AttendingStudentsActivity;
import com.example.attendancestudent.Adapter.Adapter_All_Student;
import com.example.attendancestudent.Auth.Model.ModelAttend;
import com.example.attendancestudent.Auth.Model.ModelStudent;
import com.example.attendancestudent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class StudentScannerQrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler , SwipeRefreshLayout.OnRefreshListener {

    static public String studentName = "";
    static public String StudentImageUri = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("Attendees");
    DatabaseReference studentReference = firebaseDatabase.getReference("Student");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    ProgressBar loading;
    // QRCode
    private ZXingScannerView mScannerView;
    SwipeRefreshLayout refreshLayout;
    ViewGroup contentFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_scanner_qr_code);

        // Permissions to open Camera
        ActivityCompat.requestPermissions(StudentScannerQrCodeActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        refreshLayout = findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this);

        loading = findViewById(R.id.loading);

        studentReference.child(Objects.requireNonNull(firebaseUser.getPhoneNumber()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelStudent student = snapshot.getValue(ModelStudent.class);
                        if (student != null) {
                            studentName = student.getsFullName();
                            StudentImageUri = student.getsImageUri();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Scanner", error.getMessage());
                    }
                });

        // link View to AlertDialog in Activity
        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        mScannerView.removeAllViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toasty.normal(StudentScannerQrCodeActivity.this, "" + getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void handleResult(Result result) {
        String Code = result.getText().trim();
        attendStudent(Code);
    }

    void attendStudent(String code) {
        if (code.contains(":")) {
            loading.setVisibility(View.VISIBLE);
            String[] outPutCode = code.split(":");
            String ProgessorID = outPutCode[0];
            String SubjectName = outPutCode[1];
            String Date = outPutCode[2];
            reference.child(ProgessorID).child(firebaseUser.getPhoneNumber())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelAttend attend = snapshot.getValue(ModelAttend.class);
                            if (attend != null) {
                                if (attend.getDate().equals(Date)) {
                                    Toasty.info(StudentScannerQrCodeActivity.this, "" + getResources().getString(R.string.you_have_already), Toast.LENGTH_SHORT).show();
                                } else {
                                    int numOfLectuer = attend.getNumOfAttend();
                                    snapshot.getRef().child("StudentName").setValue(studentName);
                                    snapshot.getRef().child("StudentImageUri").setValue(StudentImageUri);
                                    snapshot.getRef().child("Date").setValue(Date);
                                    snapshot.getRef().child("NumOfAttend").setValue(numOfLectuer + 1);

                                    Toasty.success(StudentScannerQrCodeActivity.this, "" + getResources().getString(R.string.register_Done), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                snapshot.getRef().child("StudentName").setValue(studentName);
                                snapshot.getRef().child("StudentImageUri").setValue(StudentImageUri);
                                snapshot.getRef().child("Date").setValue(Date);
                                snapshot.getRef().child("NumOfAttend").setValue(1);

                                Toasty.success(StudentScannerQrCodeActivity.this, "" + getResources().getString(R.string.register_Done), Toast.LENGTH_SHORT).show();
                            }
                            loading.setVisibility(View.GONE);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loading.setVisibility(View.GONE);
                            Log.d("Scanner", error.getMessage());
                        }
                    });
        }else{
            Toasty.error(this, ""+getResources().getString(R.string.codeNotVaild), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            refreshLayout.setRefreshing(false);
            onResume();
        }, 500);
    }
}