package com.example.attendancestudent.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding loginBinding;
    FirebaseDatabase database;
    DatabaseReference UserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());


        database = FirebaseDatabase.getInstance();
        UserReference = database.getReference("Users");

        loginBinding.btnLogin.setOnClickListener(v -> {

            //Get complete phone number
            String _getUserEnteredPhoneNumber = Objects.requireNonNull(loginBinding.loginPhone.getText()).toString().trim();
            if (TextUtils.isEmpty(_getUserEnteredPhoneNumber)) {
                loginBinding.loginPhone.setError("" + getResources().getString(R.string.phone_is_required));
                loginBinding.loginPhone.setFocusable(true);
                loginBinding.loginPhone.requestFocus();
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
                loginBinding.loginPhone.setError("" + getResources().getString(R.string.please_enter_number));
                loginBinding.loginPhone.setFocusable(true);
                loginBinding.loginPhone.requestFocus();
                return;
            }

            try {
                // Check UserPhone Number Is Found or not
                UserReference.child(UserPhoneNumber).child("JobTitle").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String JopTitle = snapshot.getValue(String.class);
                        if (JopTitle != null) {
                            Intent intent = new Intent(LoginActivity.this, LoginOTPActivity.class);

                            intent.putExtra("UserPhoneNumber", UserPhoneNumber);
                            intent.putExtra("JopTitle", JopTitle);
                            startActivity(intent);
                            finish();
                        } else {
                            Toasty.info(LoginActivity.this, "" + getResources().getString(R.string.phone_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("login", error.getMessage());
                    }
                });
            } catch (NullPointerException e) {
                Log.d("login", e.getMessage());
            }
        });

        loginBinding.btnGOregister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

    }
}