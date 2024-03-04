package com.example.sakshi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private EditText fullNameEditText, addressEditText, stateEditText, countryEditText, bloodGroupEditText, policeStationEditText;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        profileImageView = view.findViewById(R.id.profileImageView);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        countryEditText = view.findViewById(R.id.countryEditText);
        bloodGroupEditText = view.findViewById(R.id.bloodGroupEditText);
        policeStationEditText = view.findViewById(R.id.policeStationEditText);
        saveButton = view.findViewById(R.id.saveButton);

        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save or update user profile data
                saveUserProfile();
            }
        });

        return view;
    }

    private void saveUserProfile() {
        // Implement the logic to save or update user profile data
        // You can use SharedPreferences, SQLite database, Firebase, etc.
        // For simplicity, let's use SharedPreferences in this example

        // Get user input
        String fullName = fullNameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String country = countryEditText.getText().toString();
        String bloodGroup = bloodGroupEditText.getText().toString();
        String policeStation = policeStationEditText.getText().toString();

        // Save data using SharedPreferences (you can replace this with your preferred storage method)
        saveToSharedPreferences(fullName, address, state, country, bloodGroup, policeStation);
    }

    private void saveToSharedPreferences(String fullName, String address, String state, String country, String bloodGroup, String policeStation) {
        // Use SharedPreferences to save the user profile data
        // You can customize the preference name as needed
        String prefName = "UserProfilePrefs";
        Context context = requireContext();
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
        editor.putString("fullName", fullName)
                .putString("address", address)
                .putString("state", state)
                .putString("country", country)
                .putString("bloodGroup", bloodGroup)
                .putString("policeStation", policeStation)
                .apply();
    }
}
