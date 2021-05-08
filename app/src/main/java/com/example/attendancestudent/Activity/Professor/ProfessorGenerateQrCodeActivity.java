package com.example.attendancestudent.Activity.Professor;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancestudent.Auth.Model.ModelProfessor;
import com.example.attendancestudent.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfessorGenerateQrCodeActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    EditText codeText;
    FloatingActionButton actionButton;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("Professor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_generate_qr_code);

        imageView = findViewById(R.id.iv_output);
        progressBar = findViewById(R.id.progress);
        codeText = findViewById(R.id.editText);
        actionButton = findViewById(R.id.doneCode);
        getUserCode();

        actionButton.setOnClickListener(v -> doneQrCode());

    }

    void getUserCode() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            // Check UserPhone Number Is Found or not
            reference.child(firebaseUser.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelProfessor professor = snapshot.getValue(ModelProfessor.class);

                    if (professor != null) {

                        String ProfessorID = professor.getpPhoneNumber();
                        String Subject_Name = professor.getpSubject_Name();
                        String Code = ProfessorID + ":" + Subject_Name + ":" + getCurrentData();
                        codeText.setText(Code);
                        String sText = codeText.getText().toString().trim();
                        generateQRCode(sText);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfessorGenerateQrCodeActivity.this, "Cancelled" + error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(ProfessorGenerateQrCodeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    void generateQRCode(String textCode) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(textCode, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            imageView.setImageBitmap(bitmap);
            InputMethodManager manager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );
            manager.hideSoftInputFromWindow(codeText.getApplicationWindowToken(), 0);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    void doneQrCode() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            // Check UserPhone Number Is Found or not
            reference.child(firebaseUser.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelProfessor professor = snapshot.getValue(ModelProfessor.class);

                    if (professor != null) {
                        int numLecture = professor.getpNumLecture();
                        snapshot.getRef().child("pNumLecture").setValue(numLecture + 1);
                    }
                    progressBar.setVisibility(View.GONE);
                    onBackPressed();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfessorGenerateQrCodeActivity.this, "Cancelled" + error, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(ProfessorGenerateQrCodeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentData() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/M/dd", Locale.ENGLISH);
        String strDate = mdformat.format(calendar.getTime());
        return strDate;
    }
}
