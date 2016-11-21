package edu.gatech.seclass.fastpass;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.nfc.NdefRecord.createMime;

/**
 * Created by Kevin on 11/11/2016.
 */

public class editFormActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{
    private DatabaseHelper DBHelper;
    NfcAdapter mNfcAdapter;
    private form formDetails;
    boolean newForm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_form);
        DBHelper = new DatabaseHelper(getApplicationContext());

        // Determine if the form is new.
        newForm = getIntent().getExtras().getBoolean("createNew");
        hide_delete_if_new();

        // Read the form information from the database.
        initialize_form(Integer.parseInt(getIntent().getExtras().getString("formID")));

        // Auto populate fields from retrieved database values.
        autopopulate_form();

        // Set the listener events for the buttons on the page.
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigation code.
                Intent listIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(listIntent);
            }
        });

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper.deleteForm(formDetails);
                Intent listIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(listIntent);
            }
        });

        findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstName, lastName, dob, insurer, phone;

                firstName = (EditText) findViewById(R.id.FirstNameEdit);
                lastName = (EditText) findViewById(R.id.LastNameEdit);
                dob = (EditText) findViewById(R.id.DOBEdit);
                insurer = (EditText) findViewById(R.id.InsurerEdit);
                phone = (EditText) findViewById(R.id.PhoneEdit);

                formDetails.firstName = firstName.getText().toString();
                formDetails.lastName = lastName.getText().toString();
                formDetails.dateOfBirth = dob.getText().toString();
                formDetails.insurer = insurer.getText().toString();
                formDetails.phone = phone.getText().toString();

                if (newForm) {
                    DBHelper.insertForm(formDetails);
                }
                else {
                    DBHelper.editForm(formDetails);
                }

                Intent listIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(listIntent);
            }
        });

        // Set up NFC.
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = null;

        JSONObject dataToSend = new JSONObject();
        try {
            dataToSend.put("firstName", formDetails.firstName);
            dataToSend.put("lastName", formDetails.lastName);
            dataToSend.put("dateOfBirth", formDetails.dateOfBirth);
            dataToSend.put("insurer", formDetails.insurer);
            dataToSend.put("phone",  formDetails.phone);
            text = dataToSend.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.com.example.android.beam", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    private void initialize_form(int formID)
    {
        if (formID == -1) {
            formDetails = new form();
            return;
        }
        formDetails = DBHelper.getFormById(formID);
    }

    private void autopopulate_form()
    {
        EditText firstName, lastName, dob, insurer, phone;

        firstName = (EditText) findViewById(R.id.FirstNameEdit);
        lastName = (EditText) findViewById(R.id.LastNameEdit);
        dob = (EditText) findViewById(R.id.DOBEdit);
        insurer = (EditText) findViewById(R.id.InsurerEdit);
        phone = (EditText) findViewById(R.id.PhoneEdit);

        firstName.setText(formDetails.firstName);
        lastName.setText(formDetails.lastName);
        dob.setText(formDetails.dateOfBirth);
        insurer.setText(formDetails.insurer);
        phone.setText(formDetails.phone);
    }

    private void hide_delete_if_new()
    {
        if (newForm) {
            Button delButton = (Button) findViewById(R.id.deleteButton);

            delButton.setVisibility(View.INVISIBLE);
        }

        return;
    }
}
