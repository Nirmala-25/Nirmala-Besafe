package com.example.sakshi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendVoiceActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private boolean isRecording = false;
    private String audioFilePath;
    private Button startBtn;
    private Button stopBtn;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_voice);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);
        sendBtn = findViewById(R.id.sendBtn);

        if (checkPermission()) {
            initializeMediaRecorder();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }
        outputFile = getExternalFilesDir(null).getAbsolutePath() + "/voice_note.3gp";

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                }
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopRecording();
                retrieveUserAudioFiles(getCurrentUserId());
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRecording();
            }
        });
    }

    private void initializeMediaRecorder() {
        File directory = new File(getExternalCacheDir().getAbsolutePath());
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory if it doesn't exist
        }
        // Initialize the MediaRecorder and set output file path
        outputFile = getExternalCacheDir().getAbsolutePath() + "/voice_note.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
    }

    private void startRecording() {
        if (checkPermission()) {
            initializeMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
                isRecording = true; // Update recording status
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error starting recording", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaRecorder = null;
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
                isRecording = false; // Update recording status
                handleAudioFile(outputFile);
            }
        }
    }

    private void handleAudioFile(String audioFilePath) {
        // Implement the logic to handle the recorded audio file
        // You can upload it to Firebase, display it to the user, or perform any other required actions
        // In this example, I'm assuming you have a method to send the audio to registered contacts
        sendAudioToContacts(getCurrentUserId());
    }

    private void sendAudioToContacts(String userId) {
        firestore.collection("usersCollection")
                .document(userId)
                .collection("contacts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Iterate through the contacts for the user
                        for (DocumentSnapshot contactDoc : task.getResult()) {
                            String contactPhone = contactDoc.getString("phone");
                            // Send audio file to this contact using contactPhone
                            sendAudioFileToContact(outputFile, contactPhone);
                        }
                    } else {
                        // Handle error
                        Toast.makeText(SendVoiceActivity.this, "Error retrieving contacts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendAudioFileToContact(String audioFilePath, String contactPhone) {
        // Implement the logic to send the audio file to the specified contact
        // You can use SMS, Firebase Cloud Messaging (FCM), or any other method
        // For simplicity, let's assume you are sending an SMS
        // You need to replace this with your actual implementation
        String message = "You have received an audio message. Check your app!";
        sendSms(contactPhone, message);
    }

    private void sendSms(String phoneNumber, String message) {
        // Implement the logic to send an SMS
        // You can use SmsManager or any other SMS sending method
        // For simplicity, let's assume you are using SmsManager
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private String getCurrentUserId() {
        if (firebaseAuth.getCurrentUser() != null) {
            return firebaseAuth.getCurrentUser().getUid();
        }
        return null;
    }

    private void sendRecording() {
        if (isRecording) {
            stopRecording(); // Stop recording before sending
        }

        // Rest of your sendRecording() logic...
    }

    private void retrieveUserAudioFiles(String userId) {
        firestore.collection("audioCollection")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Iterate through the audio files for the user
                        for (DocumentSnapshot document : task.getResult()) {
                            // Handle each audio file document
                            String fileName = document.getString("fileName");
                            // Process the audio file as needed
                            // You can store these file names or download the audio data
                        }
                    } else {
                        // Handle error
                        Toast.makeText(SendVoiceActivity.this, "Error retrieving audio files", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    startRecording();
                } else {
                    // Permission denied
                    Toast.makeText(this, "Permission denied. App cannot record audio.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
