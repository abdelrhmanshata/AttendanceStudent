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
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityLoginOTPBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class LoginOTPActivity extends AppCompatActivity {

    private static final String TAG = "MainTAG";
    ActivityLoginOTPBinding otpBinding;
    FirebaseAuth firebaseAuth;
    String UserPhoneNumber;
    String JopTitle;
    // if code send Faild will used to resend Code
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId; // Will hold OTP Verification Code
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding = ActivityLoginOTPBinding.inflate(getLayoutInflater());
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

            UserPhoneNumber = getIntent().getStringExtra("UserPhoneNumber");
            JopTitle = getIntent().getStringExtra("JopTitle");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // runOnUiThread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (UserPhoneNumber != null && JopTitle != null) {
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
                                Toast.makeText(LoginOTPActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG + "2", e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                Log.d(TAG + "3", "onCodeSent: " + verificationId);
                                mVerificationId = verificationId;
                                mForceResendingToken = forceResendingToken;
                                pd.dismiss();
                                Toasty.info(LoginOTPActivity.this, "" + getResources().getString(R.string.verification_send), Toast.LENGTH_SHORT).show();
                            }
                        };

                        startPhoneNumberVerification(UserPhoneNumber);

                        otpBinding.resendTv.setOnClickListener(v -> {
                            resendPhoneNumberVerificationCode(UserPhoneNumber, mForceResendingToken);
                        });

                        otpBinding.verifyCodeBtn.setOnClickListener(v -> {
                            String code = otpBinding.otpTextView.getText().toString().trim();
                            if (TextUtils.isEmpty(code)) {
                                Toasty.info(LoginOTPActivity.this, "" + getResources().getString(R.string.enter_verification_code), Toast.LENGTH_SHORT).show();
                            } else {
                                verificationPhoneNumberWithCode(mVerificationId, code);
                            }
                        });
                    } else {
                        Toasty.error(LoginOTPActivity.this, "" + getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        thread.start();
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

                    Toast.makeText(this, "" + JopTitle, Toast.LENGTH_SHORT).show();

                    if (JopTitle.contains("Student")) {
                        startActivity(new Intent(LoginOTPActivity.this, StudentActivity.class));
                    } else if (JopTitle.contains("Professor")) {
                        startActivity(new Intent(LoginOTPActivity.this, ProfessorActivity.class));
                    }
                    pd.dismiss();
                    finish();
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