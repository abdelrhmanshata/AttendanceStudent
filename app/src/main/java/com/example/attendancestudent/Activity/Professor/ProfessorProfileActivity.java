package com.example.attendancestudent.Activity.Professor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.attendancestudent.Activity.IntroActivity;
import com.example.attendancestudent.Auth.Model.ModelProfessor;
import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityProfessorProfileBinding;
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

public class ProfessorProfileActivity extends AppCompatActivity {

    ActivityProfessorProfileBinding professorProfileBinding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = firebaseDatabase.getReference("Professor");
    String Year = "";

    Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("ProfessorImage");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        professorProfileBinding = ActivityProfessorProfileBinding.inflate(getLayoutInflater());
        setContentView(professorProfileBinding.getRoot());

        // Permissions to open Camera
        ActivityCompat.requestPermissions(ProfessorProfileActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        getCurrentUser();

        professorProfileBinding.updata.setOnClickListener(v -> {

            String fullName = Objects.requireNonNull(professorProfileBinding.userName.getText()).toString().trim();
            if (fullName.isEmpty()) {
                professorProfileBinding.userName.setError("" + getResources().getString(R.string.username_is_required));
                professorProfileBinding.userName.setFocusable(true);
                professorProfileBinding.userName.requestFocus();
                return;
            }

            String UserEmail = Objects.requireNonNull(professorProfileBinding.userEmail.getText()).toString().trim();
            if (UserEmail.isEmpty()) {
                professorProfileBinding.userEmail.setError("" + getResources().getString(R.string.email_required));
                professorProfileBinding.userEmail.setFocusable(true);
                professorProfileBinding.userEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
                professorProfileBinding.userEmail.setError("" + getResources().getString(R.string.please_enter_email));
                professorProfileBinding.userEmail.setFocusable(true);
                professorProfileBinding.userEmail.requestFocus();
                return;
            }

            String address = Objects.requireNonNull(professorProfileBinding.subjectName.getText()).toString().trim();
            if (address.isEmpty()) {
                professorProfileBinding.subjectName.setError("" + getResources().getString(R.string.sbject_name_required));
                professorProfileBinding.subjectName.setFocusable(true);
                professorProfileBinding.subjectName.requestFocus();
                return;
            }

            int selectedAcademicYear = professorProfileBinding.radioGroupAcademicYear.getCheckedRadioButtonId();
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

        professorProfileBinding.userImage.setOnClickListener(v -> SelectImage());
    }

    void getCurrentUser() {
        professorProfileBinding.progressCircle.setVisibility(View.VISIBLE);
        reference.child(Objects.requireNonNull(user.getPhoneNumber()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ModelProfessor professor = snapshot.getValue(ModelProfessor.class);
                        if (professor != null) {

                            Picasso.get().load(professor.getpImageUri().trim())
                                    .into(professorProfileBinding.userImage);

                            professorProfileBinding.userPhoneNumber.setText(professor.getpPhoneNumber());
                            professorProfileBinding.userPhoneNumber.setEnabled(false);

                            professorProfileBinding.userName.setText(professor.getpFullName());
                            professorProfileBinding.userEmail.setText(professor.getpEmail());
                            professorProfileBinding.subjectName.setText(professor.getpSubject_Name());

                            String yearAcadmic = professor.getpAcademicYear();
                            switch (yearAcadmic) {
                                case "Year1":
                                    professorProfileBinding.year1.setChecked(true);
                                    break;
                                case "Year2":
                                    professorProfileBinding.year2.setChecked(true);
                                    break;
                                case "Year3":
                                    professorProfileBinding.year3.setChecked(true);
                                    break;
                                case "Year4":
                                    professorProfileBinding.year4.setChecked(true);
                                    break;
                            }
                        }
                        professorProfileBinding.progressCircle.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        professorProfileBinding.progressCircle.setVisibility(View.GONE);
                        Log.d("ProfessorProfile", error.getMessage());
                    }
                });
    }

    private void updataValue() {

        reference.child(Objects.requireNonNull(user.getPhoneNumber())).child("pFullName").setValue(professorProfileBinding.userName.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("pEmail").setValue(professorProfileBinding.userEmail.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("pSubject_Name").setValue(professorProfileBinding.subjectName.getText().toString().trim());

        reference.child(user.getPhoneNumber()).child("pAcademicYear").setValue(Year);

        Toasty.success(ProfessorProfileActivity.this, "Success Updata", Toast.LENGTH_SHORT).show();
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
                .start(ProfessorProfileActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toasty.normal(ProfessorProfileActivity.this, ""+getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
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
                    Picasso.get().load(filePath).into(professorProfileBinding.userImage);
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
            progressDialog.setTitle(""+getResources().getString(R.string.uploading));
            progressDialog.show();

            StorageReference ref = storageReference.child(user.getPhoneNumber() + "/ProfessorImage");
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    if (!reference.onDisconnect().cancel().isSuccessful()) {
                        progressDialog.dismiss();
                        Toasty.success(ProfessorProfileActivity.this, ""+getResources().getString(R.string.image_uploading), Toast.LENGTH_SHORT).show();
                        reference.child(Objects.requireNonNull(user.getPhoneNumber())).child("pImageUri").setValue(uri.toString());
                    } else {
                        Toasty.error(ProfessorProfileActivity.this, ""+getResources().getString(R.string.faild), Toast.LENGTH_SHORT).show();
                    }
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Log.d("Failure Error", e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(""+getResources().getString(R.string.upload) + (int) progress + "%");
            });
        }
    }

}