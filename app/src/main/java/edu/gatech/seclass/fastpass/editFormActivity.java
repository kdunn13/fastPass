package edu.gatech.seclass.fastpass;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonWriter;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_form);
        DBHelper = new DatabaseHelper(getApplicationContext());


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

        String formIDAsString = getIntent().getExtras().getString("formID");
        int formID = Integer.parseInt(formIDAsString);

        form selectedForm = DBHelper.getFormById(formID);

        JSONObject dataToSend = new JSONObject();
        try {
            dataToSend.put("firstName", selectedForm.firstName);
            dataToSend.put("lastName", selectedForm.lastName);
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
}
