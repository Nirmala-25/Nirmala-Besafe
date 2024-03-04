package com.example.sakshi;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingActivity extends AppCompatActivity {
    private Button pickUpLocationButton;
    private Button dropOffLocationButton;
    private Button bookRideButton;

    private LatLng pickUpLatLng;
    private LatLng dropOffLatLng;

    private FirebaseAuth mAuth;
    private DatabaseReference mRidesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        mAuth = FirebaseAuth.getInstance();
        mRidesDatabase = FirebaseDatabase.getInstance().getReference("rides");

        pickUpLocationButton = findViewById(R.id.pickUpLocationButton);
        dropOffLocationButton = findViewById(R.id.dropOffLocationButton);
        bookRideButton = findViewById(R.id.bookRideButton);

        pickUpLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Google Maps for picking up location
                openGoogleMaps();
            }
        });

        dropOffLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Google Maps for dropping off location
                openGoogleMaps();
            }
        });

        bookRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookRide();
            }
        });
    }

    private void openGoogleMaps() {
        // Implementation to open Google Maps omitted for brevity
    }

    private void bookRide() {
        if (pickUpLatLng != null && dropOffLatLng != null) {
            String rideId = mRidesDatabase.push().getKey();
            Ride ride = new Ride(rideId, mAuth.getCurrentUser().getUid(),
                    pickUpLatLng.latitude, pickUpLatLng.longitude,
                    dropOffLatLng.latitude, dropOffLatLng.longitude);
            mRidesDatabase.child(rideId).setValue(ride)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(BookingActivity.this, "Ride booked successfully", Toast.LENGTH_SHORT).show();
                                // You can perform additional actions after successful booking here
                            } else {
                                Toast.makeText(BookingActivity.this, "Failed to book ride", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please select pick-up and drop-off locations", Toast.LENGTH_SHORT).show();
        }
    }
}
