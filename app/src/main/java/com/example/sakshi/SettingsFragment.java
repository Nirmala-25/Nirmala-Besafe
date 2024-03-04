package com.example.sakshi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;

public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Switch switchSafetyFeature;
    private SeekBar seekBarSettings;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize UI components
        switchSafetyFeature = view.findViewById(R.id.switchSafetyFeature);
        seekBarSettings = view.findViewById(R.id.seekBarSettings);

        // Add listeners to handle UI component actions
        switchSafetyFeature.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle switch state change
            if (isChecked) {
                // Enable safety feature logic
            } else {
                // Disable safety feature logic
            }
        });

        seekBarSettings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle seek bar progress change
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Handle when user starts tracking touch on seek bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Handle when user stops tracking touch on seek bar
            }
        });

        // Add your logic for handling settings changes
        // For example, you can set listeners to these components and perform actions accordingly

        return view;
    }
}