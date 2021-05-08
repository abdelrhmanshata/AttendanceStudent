package com.example.attendancestudent.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.Activity.Professor.ProfessorActivity;
import com.example.attendancestudent.Activity.Student.StudentActivity;
import com.example.attendancestudent.Auth.LoginActivity;
import com.example.attendancestudent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class IntroActivity extends AppCompatActivity {

    public static String USERisADMIN = "False";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UserReference = database.getReference("Users");
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {

        Thread intro = new Thread(() -> {
            // Sleep UI 4 Seconds
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check User is found
            if (firebaseAuth.getCurrentUser() != null) {
                //data is valid
                getUserIsAdmin();
                try {
                    // Check UserPhone Number Is Found or not
                    UserReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getPhoneNumber())).child("JobTitle").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String JopTitle = snapshot.getValue(String.class);
                            if (JopTitle.contains("Student")) {
                                startActivity(new Intent(IntroActivity.this, StudentActivity.class));
                            } else if (JopTitle.contains("Professor")) {
                                startActivity(new Intent(IntroActivity.this, ProfessorActivity.class));
                            }
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Intro", error.getMessage());
                        }
                    });
                } catch (NullPointerException e) {
                    Log.d("Intro", e.getMessage());
                }
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });
        intro.start();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    void getUserIsAdmin() {
        try {
            UserReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser().getPhoneNumber())).child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String admin = snapshot.getValue(String.class);
                    if (admin != null) {
                        USERisADMIN = admin;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Intro", error.getMessage());
                }
            });
        } catch (NullPointerException e) {
            Log.d("Intro", e.getMessage());
        }
    }
}