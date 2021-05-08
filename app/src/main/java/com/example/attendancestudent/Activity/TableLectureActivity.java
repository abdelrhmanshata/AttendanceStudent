package com.example.attendancestudent.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.attendancestudent.R;
import com.example.attendancestudent.databinding.ActivityTableLectureBinding;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class TableLectureActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ActivityTableLectureBinding lectureBinding;
    String[] Departments = {"D1", "D2", "D3", "D4", "D5"};
    String Year = "", Department = "";
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Lectures_Table");
    Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("Lectures_Table");
    PhotoView photoView;
    ProgressBar Loading;
    private Handler mHandler;
    private Runnable mUpdateTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lectureBinding = ActivityTableLectureBinding.inflate(getLayoutInflater());
        setContentView(lectureBinding.getRoot());

        // Permissions to open Camera
        ActivityCompat.requestPermissions(TableLectureActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mHandler = new Handler();
        mUpdateTimeTask = new Runnable() {
            @SuppressLint("SetTextI18n")
            public void run() {
                mHandler.postDelayed(this, 1000);
                lectureBinding.textViewDate.setText("" + getCurrentData().trim());
                lectureBinding.textViewDay.setText("" + getCurrentDay().trim());
                lectureBinding.textViewTime.setText("" + getCurrentTime().trim());
            }
        };

        photoView = findViewById(R.id.photo_view);
        Loading = findViewById(R.id.loading);

        showDialogSelectTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (IntroActivity.USERisADMIN.toLowerCase().contains("true"))
            getMenuInflater().inflate(R.menu.table_menu_admin, menu);
        else
            getMenuInflater().inflate(R.menu.table_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.addPhoto:
                showDialogSelectImageTable();
                break;
            case R.id.refresh:
                showDialogSelectTable();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(TableLectureActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toasty.normal(TableLectureActivity.this, "Permission denied to camera", Toast.LENGTH_SHORT).show();
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
                    Picasso.get().load(filePath).into(photoView);
                } catch (Exception e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Error", error.getMessage());
            }
        }
    }

    // UploadImage method
    private void uploadImage(Uri filePath) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child(Year + "/" + Department + "/" +
                            "Table_" + Year + "_" + Department
                    //System.currentTimeMillis()
            );
            // adding listeners on upload
            // or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (!databaseReference.onDisconnect().cancel().isSuccessful()) {
                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            Toasty.success(TableLectureActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            databaseReference.child(Year).child(Department).setValue(uri.toString());
                        } else {
                            Toasty.error(TableLectureActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(TableLectureActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Failure Error", e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
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

    public void showDialogSelectTable() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.table_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupAcademicYear);
        Spinner spinner = dialogView.findViewById(R.id.spinner);
        Button button = dialogView.findViewById(R.id.done);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button.setOnClickListener(v -> {

            int selectedAcademicYear = radioGroup.getCheckedRadioButtonId();
            if (selectedAcademicYear == R.id.year1) {
                Year = "Year1";
            } else if (selectedAcademicYear == R.id.year2) {
                Year = "Year2";
            } else if (selectedAcademicYear == R.id.year3) {
                Year = "Year3";
            } else if (selectedAcademicYear == R.id.year4) {
                Year = "Year4";
            }

            try {
                Loading.setVisibility(View.VISIBLE);
                databaseReference.child(Year).child(Department).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String uriImage = snapshot.getValue(String.class);
                        if (uriImage != null) {
                            Picasso.get().load(uriImage).into(photoView);
                        } else {
                            Toasty.warning(TableLectureActivity.this, "Table Not Found", Toast.LENGTH_SHORT).show();
                        }
                        Loading.setVisibility(View.GONE);
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Table", error.getMessage());
                        Loading.setVisibility(View.GONE);
                        alertDialog.dismiss();
                    }
                });
            } catch (NullPointerException e) {
                Log.d("Table", e.getMessage());
                Loading.setVisibility(View.GONE);
                alertDialog.dismiss();
            }
        });
    }

    public void showDialogSelectImageTable() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.table_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        //
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupAcademicYear);
        Spinner spinner = dialogView.findViewById(R.id.spinner);
        Button button = dialogView.findViewById(R.id.done);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button.setOnClickListener(v -> {

            int selectedAcademicYear = radioGroup.getCheckedRadioButtonId();
            if (selectedAcademicYear == R.id.year1) {
                Year = "Year1";
            } else if (selectedAcademicYear == R.id.year2) {
                Year = "Year2";
            } else if (selectedAcademicYear == R.id.year3) {
                Year = "Year3";
            } else if (selectedAcademicYear == R.id.year4) {
                Year = "Year4";
            }
            SelectImage();
            alertDialog.dismiss();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Department = Departments[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}