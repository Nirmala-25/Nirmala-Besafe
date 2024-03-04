package com.example.sakshi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CaptureImageActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;

    private ImageView imageView;
    private Button btnCapturePhoto;
    private Uri imageUri; // Store the captured image URI

    private FirebaseAuth mAuth;
    private DatabaseReference mUserImagesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        mAuth = FirebaseAuth.getInstance();
        mUserImagesDatabase = FirebaseDatabase.getInstance().getReference("user_images");

        imageView = findViewById(R.id.imageView);
        btnCapturePhoto = findViewById(R.id.btnCapturePhoto);

        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndCapture();
            }
        });
    }

    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            // Convert Bitmap to Uri
            imageUri = getImageUri(this, imageBitmap);

            // Now, you can upload the image to Firebase Storage and send the download URL to emergency contacts
            uploadImageAndSendToEmergencyContacts();
        }
    }

    // Convert Bitmap to Uri
    private Uri getImageUri(CaptureImageActivity context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void uploadImageAndSendToEmergencyContacts() {
        if (imageUri != null) {
            // Define the database reference for storing images
            DatabaseReference imagesRef = FirebaseDatabase.getInstance().getReference().child("user_images");

            // Get current user's UID
            String uid = mAuth.getCurrentUser().getUid();

            // Define a unique key for the image
            String imageKey = imagesRef.push().getKey();

            // Store the image URL under the user's UID and image key
            imagesRef.child(uid).child(imageKey).setValue(imageUri.toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CaptureImageActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                // Now, you can send the alert to emergency contacts
                                retrieveEmergencyContactsAndSendAlert();
                            } else {
                                Toast.makeText(CaptureImageActivity.this, "Failed to save image to database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void retrieveEmergencyContactsAndSendAlert() {
        // Get the current user's UID
        String uid = mAuth.getCurrentUser().getUid();

        // Reference to the emergency contacts database
        DatabaseReference emergencyContactsRef = FirebaseDatabase.getInstance().getReference("emergency_contacts").child(uid);

        // Retrieve emergency contacts
        emergencyContactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // List to store emergency contact numbers
                List<String> emergencyContactNumbers = new ArrayList<>();
                // Iterate through each emergency contact number
                for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                    String contactNumber = contactSnapshot.getValue(String.class);
                    // Add the contact number to the list
                    emergencyContactNumbers.add(contactNumber);
                }
                // Send the image to each emergency contact
                sendImageToEmergencyContacts(emergencyContactNumbers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(CaptureImageActivity.this, "Failed to retrieve emergency contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendImageToEmergencyContacts(List<String> emergencyContactNumbers) {
        if (imageUri != null) {
            try {
                // Convert the Uri to Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Convert Bitmap to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                // Send the image via SMS to each emergency contact
                SmsManager smsManager = SmsManager.getDefault();
                for (String contactNumber : emergencyContactNumbers) {
                    smsManager.sendDataMessage(contactNumber, null, (short) 1234, imageData, null, null);
                }
                // Inform the user that the image has been sent to emergency contacts
                Toast.makeText(this, "Image sent to emergency contacts", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send image to emergency contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, capture photo
                dispatchTakePictureIntent();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied. Cannot capture photo.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
