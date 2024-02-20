package com.example.sakshi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddContactsActivity extends AppCompatActivity {
    private static final String PREF_NAME = "EmergencyContacts";
    private static final String KEY_NAME = "contactName";
    private static final String KEY_PHONE = "contactPhone";

    private EditText edtContactName;
    private EditText edtContactPhone;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        edtContactName = findViewById(R.id.editTextContactName);
        edtContactPhone = findViewById(R.id.editTextContactPhone);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEmergencyContact();
            }
        });
    }

    private void saveEmergencyContact() {
        String contactName = edtContactName.getText().toString().trim();
        String contactPhone = edtContactPhone.getText().toString().trim();

        if (!contactName.isEmpty() && !contactPhone.isEmpty()) {
            // Save the contacts in SharedPreferences
            SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_NAME, contactName);
            editor.putString(KEY_PHONE, contactPhone);
            editor.apply();

            // Inform the user that the contact is saved
            Toast.makeText(this, "Emergency contact saved successfully", Toast.LENGTH_SHORT).show();

            // Clear the input fields
            edtContactName.setText("");
            edtContactPhone.setText("");
        } else {
            // Display a message if any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
