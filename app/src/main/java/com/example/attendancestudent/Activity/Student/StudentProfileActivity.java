package com.example.attendancestudent.Activity.Student;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.attendancestudent.Auth.Model.ModelStudent;
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityStudentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class StudentProfileActivity extends AppCompatActivity {

    ActivityStudentProfileBinding studentProfileBinding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("Student");
    String Year = "";

    Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("StudentImage");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentProfileBinding = ActivityStudentProfileBinding.inflate(getLayoutInflater());
        setContentView(studentProfileBinding.getRoot());

        // Permissions to open Camera
        ActivityCompat.requestPermissions(StudentProfileActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        getCurrentUser();

        studentProfileBinding.updata.setOnClickListener(v -> {

            String fullName = Objects.requireNonNull(studentProfileBinding.userName.getText()).toString().trim();
            if (fullName.isEmpty()) {
                studentProfileBinding.userName.setError("" + getResources().getString(R.string.username_is_required));
                studentProfileBinding.userName.setFocusable(true);
                studentProfileBinding.userName.requestFocus();
                return;
            }


            String UserEmail = Objects.requireNonNull(studentProfileBinding.userEmail.getText()).toString().trim();
            if (UserEmail.isEmpty()) {
                studentProfileBinding.userEmail.setError("" + getResources().getString(R.string.email_required));
                studentProfileBinding.userEmail.setFocusable(true);
                studentProfileBinding.userEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
                studentProfileBinding.userEmail.setError("" + getResources().getString(R.string.please_enter_email));
                studentProfileBinding.userEmail.setFocusable(true);
                studentProfileBinding.userEmail.requestFocus();
                return;
            }

            String address = Objects.requireNonNull(studentProfileBinding.userAddress.getText()).toString().trim();
            if (address.isEmpty()) {
                studentProfileBinding.userAddress.setError("Your Address is Required");
                studentProfileBinding.userAddress.setFocusable(true);
                studentProfileBinding.userAddress.requestFocus();
                return;
            }

            String nationalId = Objects.requireNonNull(studentProfileBinding.userNationalID.getText()).toString().trim();
            if (nationalId.isEmpty()) {
                studentProfileBinding.userNationalID.setError("Your National ID is Required");
                studentProfileBinding.userNationalID.setFocusable(true);
                studentProfileBinding.userNationalID.requestFocus();
                return;
            }

            int selectedAcademicYear = studentProfileBinding.radioGroupAcademicYear.getCheckedRadioButtonId();
            if (selectedAcademicYear == R.id.year1) {
                Year = "Year1";
            } else if (selectedAcademicYear == R.id.year2) {
                Year = "Year2";
            } else if (selectedAcademicYear == R.id.year3) {
                Year = "Year3";
            } else if (selectedAcademicYear == R.id.year4) {
                Year = "Year4";
            }
            updataValue();
        });

        studentProfileBinding.userImage.setOnClickListener(v -> SelectImage());

    }


    void getCurrentUser() {
        studentProfileBinding.progressCircle.setVisibility(View.VISIBLE);
        reference.child(Objects.requireNonNull(user.getPhoneNumber()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ModelStudent student = snapshot.getValue(ModelStudent.class);
                        if (student != null) {

                            Picasso.get().load(student.getsImageUri().trim())
                                    .into(studentProfileBinding.userImage);

                            studentProfileBinding.userPhoneNumber.setText(student.getsPhoneNumber());
                            studentProfileBinding.userPhoneNumber.setEnabled(false);

                            studentProfileBinding.userName.setText(student.getsFullName());
                            studentProfileBinding.userEmail.setText(student.getsEmail());
                            studentProfileBinding.userAddress.setText(student.getsAddress());
                            studentProfileBinding.userNationalID.setText(student.getsNational_ID());

                            studentProfileBinding.userDepartment.setText(student.getsDepartment());
                            studentProfileBinding.userDepartment.setEnabled(false);

                            String yearAcadmic = student.getsAcademicYear();
                            switch (yearAcadmic) {
                                case "Year1":
                                    studentProfileBinding.year1.setChecked(true);
                                    break;
                                case "Year2":
                                    studentProfileBinding.year2.setChecked(true);
                                    break;
                                case "Year3":
                                    studentProfileBinding.year3.setChecked(true);
                                    break;
                                case "Year4":
                                    studentProfileBinding.year4.setChecked(true);
                                    break;
                            }
                        }
                        studentProfileBinding.progressCircle.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        studentProfileBinding.progressCircle.setVisibility(View.GONE);
                        Log.d("StudentProfile", error.getMessage());
                    }
                });
    }

    private void updataValue() {

        reference.child(Objects.requireNonNull(user.getPhoneNumber())).child("sFullName").setValue(studentProfileBinding.userName.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("sEmail").setValue(studentProfileBinding.userEmail.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("sAddress").setValue(studentProfileBinding.userAddress.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("sNational_ID").setValue(studentProfileBinding.userNationalID.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("sAcademicYear").setValue(Year);

        Toasty.success(StudentProfileActivity.this, "Success Updata", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(StudentProfileActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toasty.normal(StudentProfileActivity.this, "" + getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                uploadImage(filePath);
                try {
                    Picasso.get().load(filePath).into(studentProfileBinding.userImage);
                } catch (Exception e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Error", error.getMessage());
            }
        }
    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {

            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("" + getResources().getString(R.string.uploading));
            progressDialog.show();

            StorageReference ref = storageReference.child(user.getPhoneNumber() + "/StudentImage");
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!reference.onDisconnect().cancel().isSuccessful()) {
                            progressDialog.dismiss();
                            Toasty.success(StudentProfileActivity.this, "" + getResources().getString(R.string.image_uploading), Toast.LENGTH_SHORT).show();
                            reference.child(user.getPhoneNumber()).child("sImageUri").setValue(uri.toString());
                        } else {
                            Toasty.error(StudentProfileActivity.this, "" + getResources().getString(R.string.faild), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.d("Failure Error", e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("" + getResources().getString(R.string.upload) + (int) progress + "%");
            });
        }
    }

}