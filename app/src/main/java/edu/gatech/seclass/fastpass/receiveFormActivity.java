package edu.gatech.seclass.fastpass;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Kevin on 11/11/2016.
 */

public class receiveFormActivity extends AppCompatActivity {
    NfcAdapter mNfcAdapter;
    private DatabaseHelper DBHelper;
    Button saveFormButton, cancelButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_form);
        saveFormButton = (Button) findViewById(R.id.saveForm);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        DBHelper = new DatabaseHelper(getApplicationContext());

        saveFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveForm();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(listIntent);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    public void saveForm() {
        form myForm = new form();

        EditText firstName, lastName, dob, insurer, phone;

        firstName = (EditText) findViewById(R.id.firstNameInput);
        lastName = (EditText) findViewById(R.id.lastNameInput);
        dob = (EditText) findViewById(R.id.dateOfBirthInput);
        insurer = (EditText) findViewById(R.id.insurerInput);
        phone = (EditText) findViewById(R.id.phoneInput);

        myForm.firstName = firstName.getText().toString();
        myForm.lastName = lastName.getText().toString();
        myForm.dateOfBirth = dob.getText().toString();
        myForm.insurer = insurer.getText().toString();
        myForm.phone = phone.getText().toString();

        DBHelper.insertForm(myForm);

        Intent intent = new Intent(receiveFormActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    void processIntent(Intent intent) {
        try {
            String formJsonString;
            JSONObject formJson;
            form myForm = new form();
            EditText firstName, lastName, dob, insurer, phone;

            firstName = (EditText) findViewById(R.id.firstNameInput);
            lastName = (EditText) findViewById(R.id.lastNameInput);
            dob = (EditText) findViewById(R.id.dateOfBirthInput);
            insurer = (EditText) findViewById(R.id.insurerInput);
            phone = (EditText) findViewById(R.id.phoneInput);

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];

            // record 0 contains the MIME type, record 1 is the AAR, if present
            formJsonString = new String(msg.getRecords()[0].getPayload());
            formJson = new JSONObject(formJsonString);

            myForm.firstName = formJson.getString("firstName");
            myForm.lastName = formJson.getString("lastName");
            myForm.dateOfBirth = formJson.getString("dateOfBirth");
            myForm.insurer = formJson.getString("insurer");
            myForm.phone = formJson.getString("phone");

            firstName.setText(myForm.firstName);
            lastName.setText(myForm.lastName);
            dob.setText(myForm.dateOfBirth);
            insurer.setText(myForm.insurer);
            phone.setText(myForm.phone);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
