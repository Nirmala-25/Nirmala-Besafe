package com.example.sakshi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.ImageButton;

public class DashBoardActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SMS = 1;
    private static final int PERMISSION_REQUEST_LOCATION = 2;
    private DrawerLayout drawer;
    private DrawerLayout drawerLayout;
    private ImageButton btnOpenNavbar;
    private ActionBarDrawerToggle toggle;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Set up the toolbar

        // Find the toolbar button
        btnOpenNavbar = findViewById(R.id.btnOpenNavbar);

        // Set up the navigation drawer button click listener
        btnOpenNavbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the navigation drawer state
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

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
        CardView BookrideCardView = findViewById(R.id.Bookridecard);
        BookrideCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DashBoardActivity.this, BookingActivity.class);
                startActivity(intent);
            }
        });
        CardView Cameracardview = findViewById(R.id.Cameracardview);

        // Set OnClickListener for the "Add Contacts" CardView
        Cameracardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the AddContactsActivity when the "Add Contacts" CardView is clicked
                Intent intent = new Intent(DashBoardActivity.this, CaptureImageActivity.class);
                startActivity(intent);
            }
        });

        // Find the "SOS" CardView by its ID
        CardView sosCardView = findViewById(R.id.alertCardView);

        // Set OnClickListener for the "SOS" CardView
        sosCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSOSMessages();
            }
        });

        // Find the "Send Voice Note" CardView by its ID
        CardView voiceNoteCardView = findViewById(R.id.voiceCardView);

        // Set OnClickListener for the "Send Voice Note" CardView
        voiceNoteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, SendVoiceActivity.class);
                startActivity(intent);
            }
        });
        CardView searchHelpCardView = findViewById(R.id.findway);
        searchHelpCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNearby();
            }
        });
    }
    private void searchNearby() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Initialize LocationManager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Check if location services are enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Retrieve the user's last known location
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();

                    // Form the search query for police stations and hospitals near the user's location
                    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=police+stations+hospitals");

                    // Create an Intent for Google Maps
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    // Check if there's an app that can handle this Intent
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        // Start Google Maps
                        startActivity(mapIntent);
                    } else {
                        // If Google Maps is not available, notify the user
                        Toast.makeText(this, "Google Maps app not found. Opening in browser.", Toast.LENGTH_SHORT).show();

                        // Open the maps query in a browser as an alternative
                        Uri mapsUrl = Uri.parse(gmmIntentUri.toString());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, mapsUrl);
                        startActivity(browserIntent);
                    }
                } else {
                    Toast.makeText(this, "Location not available. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Location services are not enabled. Please enable GPS.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }



    private void sendSOSMessages() {
        // Check if SMS permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, check for location permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Location permission is granted, proceed to send SOS messages
                retrieveContactsAndSendSOS();
            } else {
                // Request location permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            }
        } else {
            // Request SMS permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, check for location permission
                sendSOSMessages();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied. Cannot send SOS messages.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, proceed to send SOS messages
                retrieveContactsAndSendSOS();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission denied. Cannot send SOS messages.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveContactsAndSendSOS() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference emergencyContactsRef = FirebaseDatabase.getInstance().getReference("EmergencyContacts").child(currentUserId);

            emergencyContactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // List to store emergency contact phone numbers
                        List<String> emergencyContactPhones = new ArrayList<>();

                        for (DataSnapshot contactSnapshot : dataSnapshot.getChildren()) {
                            String contactPhone = contactSnapshot.child("phone").getValue(String.class);
                            if (contactPhone != null) {
                                emergencyContactPhones.add(contactPhone);
                            }
                        }

                        // Send SOS messages to emergency contacts
                        sendSOSMessagesToContacts(emergencyContactPhones);
                    } else {
                        Toast.makeText(DashBoardActivity.this, "No emergency contacts found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DashBoardActivity.this, "Failed to retrieve emergency contacts", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User is not logged in
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSOSMessagesToContacts(List<String> contactPhones) {
        // Implement the logic to send SOS messages to the retrieved emergency contacts
        // You can use SmsManager or any other preferred method for sending messages
        // For simplicity, I'm using SmsManager in this example

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" +
                        lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

                String SOS_Message = "SOS Alert: My current location is available at " + mapsUrl + ". Please help!";
                SmsManager smsManager = SmsManager.getDefault();

                for (String contactPhone : contactPhones) {
                    smsManager.sendTextMessage(contactPhone, null, SOS_Message, null, null);
                }

                Toast.makeText(this, "SOS messages sent to emergency contacts", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location not available. Cannot send SOS messages.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
