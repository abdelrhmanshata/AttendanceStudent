package com.example.attendancestudent.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.Activity.Professor.ProfessorActivity;
import com.example.attendancestudent.Activity.Student.StudentActivity;
import com.example.attendancestudent.Auth.Model.ModelProfessor;
import com.example.attendancestudent.Auth.Model.ModelStudent;
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityOTPBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class OTPActivity extends AppCompatActivity {
    private static final String TAG = "MainTAG";
    static ModelProfessor modelProfessor = null;
    static ModelStudent modelStudent = null;
    ActivityOTPBinding otpBinding;
    FirebaseAuth firebaseAuth;
    String UserPhoneNumber;
    // if code send Faild will used to resend Code
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId; // Will hold OTP Verification Code
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding = ActivityOTPBinding.inflate(getLayoutInflater());
        setContentView(otpBinding.getRoot());

        setSupportActionBar(otpBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        String text = "" + getResources().getString(R.string.didn_t_get_otp_resend);
        if (text.contains("Resend")) {
            SpannableString spannableString = new SpannableString(text);
            UnderlineSpan underlineSpan = new UnderlineSpan();
            ForegroundColorSpan mBlue = new ForegroundColorSpan(Color.BLUE);
            spannableString.setSpan(underlineSpan, 16, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(mBlue, 16, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            otpBinding.resendTv.setText(spannableString);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);

        Thread thread = new Thread(() -> {

            modelProfessor = (ModelProfessor) getIntent().getSerializableExtra("ModelProfessor");
            if (modelProfessor != null)
                UserPhoneNumber = modelProfessor.getpPhoneNumber();

            modelStudent = (ModelStudent) getIntent().getSerializableExtra("ModelStudent");
            if (modelStudent != null)
                UserPhoneNumber = modelStudent.getsPhoneNumber();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // runOnUiThread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (modelProfessor != null || modelStudent != null) {
                        pd.setTitle(getResources().getString(R.string.plaese_wait));
                        pd.setCanceledOnTouchOutside(false);

                        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                pd.dismiss();
                                Toast.makeText(OTPActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG + "2", e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                Log.d(TAG + "3", "onCodeSent: " + verificationId);
                                mVerificationId = verificationId;
                                mForceResendingToken = forceResendingToken;
                                pd.dismiss();
                                Toasty.info(OTPActivity.this, "" + getResources().getString(R.string.verification_send), Toast.LENGTH_SHORT).show();
                            }
                        };

                        startPhoneNumberVerification(UserPhoneNumber);

                        otpBinding.resendTv.setOnClickListener(v -> {
                            resendPhoneNumberVerificationCode(UserPhoneNumber, mForceResendingToken);
                        });

                        otpBinding.verifyCodeBtn.setOnClickListener(v -> {
                            String code = otpBinding.otpTextView.getText().toString().trim();
                            if (TextUtils.isEmpty(code)) {
                                Toasty.info(OTPActivity.this, "" + getResources().getString(R.string.enter_verification_code), Toast.LENGTH_SHORT).show();
                            } else {
                                verificationPhoneNumberWithCode(mVerificationId, code);
                            }
                        });
                    } else {
                        Toasty.error(OTPActivity.this, "" + getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        thread.start();
    }

    public void RegisterUser(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference UserReference = database.getReference("Users");
        DatabaseReference reference;
        if (modelProfessor != null) {
            reference = database.getReference("Professor");
            // Add Uid
            modelProfessor.setpKey(uid);
            if (modelProfessor.getpGender().equals("Male")) {
                modelProfessor.setpImageUri("https://firebasestorage.googleapis.com/v0/b/attendancestudent-a9050.appspot.com/o/pmale.png?alt=media&token=684ff232-655c-4158-926d-7230e299d075");
            } else {
                modelProfessor.setpImageUri("https://firebasestorage.googleapis.com/v0/b/attendancestudent-a9050.appspot.com/o/pfemale.png?alt=media&token=67c37646-70fd-44f5-833b-12c602e1d53a");
            }
            modelProfessor.setpNumLecture(0);
            // add info in dataBase
            reference.child(UserPhoneNumber).setValue(modelProfessor).addOnSuccessListener(aVoid -> {
                HashMap<String, String> JobTitle = new HashMap<>();
                JobTitle.put("JobTitle", "Professor");
                JobTitle.put("Admin", "False");
                UserReference.child(UserPhoneNumber).setValue(JobTitle);
                Toasty.success(OTPActivity.this, "" + getResources().getString(R.string.user_registered), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                startActivity(new Intent(this, ProfessorActivity.class));
                finish();
            });
        }

        if (modelStudent != null) {
            reference = database.getReference("Student");
            // Add Uid
            modelStudent.setsKey(uid);
            if (modelStudent.getsGender().equals("Male")) {
                modelStudent.setsImageUri("https://firebasestorage.googleapis.com/v0/b/attendancestudent-a9050.appspot.com/o/male.png?alt=media&token=1f3f7204-4ed7-42e8-8f3e-2a131b643bdd");
            } else {
                modelStudent.setsImageUri("https://firebasestorage.googleapis.com/v0/b/attendancestudent-a9050.appspot.com/o/female.png?alt=media&token=467ca0ae-a0b0-442c-99b6-e704bfd48b32");
            }

            // add info in dataBase
            reference.child(UserPhoneNumber).setValue(modelStudent).addOnSuccessListener(aVoid -> {
                HashMap<String, String> JobTitle = new HashMap<>();
                JobTitle.put("JobTitle", "Student");
                JobTitle.put("Admin", "False");
                UserReference.child(UserPhoneNumber).setValue(JobTitle);
                Toasty.success(OTPActivity.this, "User Registered Successfull", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                startActivity(new Intent(this, StudentActivity.class));
                finish();
            });
        }
    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("" + getResources().getString(R.string.verifying_phone_number));
        pd.show();
        PhoneAuthOptions optionsl = PhoneAuthOptions
                .newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(optionsl);
    }

    private void resendPhoneNumberVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("" + getResources().getString(R.string.resending_code));
        pd.show();
        PhoneAuthOptions optionsl = PhoneAuthOptions
                .newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(optionsl);
    }

    private void verificationPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("" + getResources().getString(R.string.verifying_code));
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("" + getResources().getString(R.string.login_succec));
        pd.show();

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    RegisterUser(firebaseAuth.getUid());
                }).addOnFailureListener(e -> {
            // failed Sigining In
            pd.dismiss();
            Log.d(TAG, e.getMessage());
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}