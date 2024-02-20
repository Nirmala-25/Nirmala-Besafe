package com.example.sakshi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DashBoardActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        // Find the "Add Contacts" CardView by its ID
        CardView addContactsCardView = findViewById(R.id.addContactsCardView);

        // Set OnClickListener for the "Add Contacts" CardView
        addContactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the AddContactsActivity when the "Add Contacts" CardView is clicked
                Intent intent = new Intent(DashBoardActivity.this, AddContactsActivity.class);
                startActivity(intent);
            }
        });

        // Check SMS permission and set up "Alert" CardView
        setupAlertCard();
    }

    private void setupAlertCard() {
        // Find the "Alert" CardView by its ID
        CardView alertCardView = findViewById(R.id.alertCardView);

        // Set OnClickListener for the "Alert" CardView
        alertCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for SMS permission
                if (ContextCompat.checkSelfPermission(DashBoardActivity.this,
                        Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    // Permission already granted, send emergency message
                    sendEmergencyMessage();
                } else {
                    // Request SMS permission
                    ActivityCompat.requestPermissions(DashBoardActivity.this,
                            new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
                }
            }
        });

        // Optionally, disable or hide the "Alert" CardView based on permission status
        boolean hasSmsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        alertCardView.setEnabled(hasSmsPermission);
        alertCardView.setVisibility(hasSmsPermission ? View.VISIBLE : View.GONE);
    }
    private void sendEmergencyMessage() {
        // Get the contacts and other necessary information
        String emergencyMessage = "Emergency! Help needed.";

        // Get registered contacts from your data source
        String contact1 = "1234567890";
        String contact2 = "9876543210";

        // Send SMS to registered contacts
        sendSMS(contact1, emergencyMessage);
        sendSMS(contact2, emergencyMessage);

        // Display a notification to the user
        displayEmergencyNotification();
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void displayEmergencyNotification() {
        // Implement the logic to display a notification to the user
        // You can use NotificationManager or any other notification library
        // to show a notification indicating that the emergency message has been sent.
    }

    // Handle the result of the SMS permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send emergency message
                sendEmergencyMessage();
            } else {
                // Permission denied, inform the user
                // You may want to display a message or take appropriate action
            }
        }
    }
}
