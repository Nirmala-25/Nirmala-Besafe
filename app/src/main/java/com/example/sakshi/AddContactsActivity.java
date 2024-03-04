package com.example.sakshi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddContactsActivity extends AppCompatActivity {
    private static final int CONTACT_PICKER_RESULT = 1001;

    EditText editTextContactName, editTextContactPhone;
    Button btnSave, btnChooseContact;

    FirebaseDatabase database;
    DatabaseReference contactsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        editTextContactName = findViewById(R.id.editTextContactName);
        editTextContactPhone = findViewById(R.id.editTextContactPhone);
        btnSave = findViewById(R.id.btnSave);
        btnChooseContact = findViewById(R.id.btnChooseContact);

        database = FirebaseDatabase.getInstance();
        contactsReference = database.getReference("EmergencyContacts");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContact();
            }
        });

        btnChooseContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
            }
        });
    }

    private void saveContact() {
        String contactName = editTextContactName.getText().toString().trim();
        String contactPhone = editTextContactPhone.getText().toString().trim();

        if (!contactName.isEmpty() && !contactPhone.isEmpty()) {
            // Retrieve the current user's UID
            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Create a reference to the user's specific node in the database
            DatabaseReference userContactsReference = contactsReference.child(currentUserUid);

            // Save the contact under the user's node
            Contact contactInfo = new Contact(contactName, contactPhone);
            userContactsReference.child(contactName).setValue(contactInfo);

            Toast.makeText(this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }


    private void pickContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == RESULT_OK) {
            if (data != null) {
                // Retrieve selected contact details
                String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String contactPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    // Set the retrieved contact details to EditText fields
                    editTextContactName.setText(contactName);
                    editTextContactPhone.setText(contactPhone);

                    cursor.close();
                }
            }
        }
    }

    private void clearFields() {
        editTextContactName.getText().clear();
        editTextContactPhone.getText().clear();
    }
}
