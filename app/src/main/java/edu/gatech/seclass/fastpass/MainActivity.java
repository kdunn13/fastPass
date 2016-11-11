package edu.gatech.seclass.fastpass;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.SQLException;
import java.util.ArrayList;

import edu.gatech.seclass.fastpass.form;

import static android.nfc.NdefRecord.createMime;


public class MainActivity extends AppCompatActivity  {

    private Button addFormButton;
    private Button recieveFileButton;
    ArrayList<form> listOfForms;
    ArrayAdapter<form> formListAdapter;
    private ListView formListView;
    private DatabaseHelper DBHelper;
    NfcAdapter mNfcAdapter;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listOfForms = new ArrayList<form>();
        formListAdapter = new ArrayAdapter<form>(MainActivity.this, android.R.layout.simple_list_item_1, listOfForms);
        setupViews();
        DBHelper = new DatabaseHelper(getApplicationContext());

        listOfForms = DBHelper.getAllLists();

        formListAdapter = new ArrayAdapter<form>(MainActivity.this, android.R.layout.simple_list_item_1, listOfForms);
        formListView.setAdapter(formListAdapter);
//
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (mNfcAdapter == null) {
//            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//        // Register callback
//        mNfcAdapter.setNdefPushMessageCallback(this, this);
//
//        performTagOperations(getIntent());


    }

//    @Override
//    public NdefMessage createNdefMessage(NfcEvent event) {
//        String text = ("Beam me up, Android!\n\n" +
//                "Beam Time: " + System.currentTimeMillis());
//        NdefMessage msg = new NdefMessage(
//                new NdefRecord[] { createMime(
//                        "application/vnd.com.example.android.beam", text.getBytes())
//                        /**
//                         * The Android Application Record (AAR) is commented out. When a device
//                         * receives a push with an AAR in it, the application specified in the AAR
//                         * is guaranteed to run. The AAR overrides the tag dispatch system.
//                         * You can add it back in to guarantee that this
//                         * activity starts when receiving a beamed message. For now, this code
//                         * uses the tag dispatch system.
//                        */
//                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
//                });
//        return msg;
//    }


    private void setupViews() {
        addFormButton = (Button) findViewById(R.id.addFormButton);
        formListView = (ListView) findViewById(R.id.formList);
        formListView.setAdapter(formListAdapter);
        recieveFileButton = (Button) findViewById(R.id.recieveFile);


        addFormButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewFormDialog();
            }
        });

        recieveFileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recieveFile();
            }
        });


        formListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                Intent listIntent = new Intent(getApplicationContext(), editFormActivity.class);
                listIntent.putExtra("formID",listOfForms.get(position).formID);
                startActivity(listIntent);

                // editForm(position);
            }
        });

        formListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id)
            {
                return transferForm(pos);
            }
        });
    }

    private Boolean transferForm(final int position) {
        final form myForm = listOfForms.get(position);

        Toast.makeText(MainActivity.this, "Transfer file code goes here",
                Toast.LENGTH_SHORT).show();


        return true;
    };

    private void editForm(final int position) {
        final form myForm = listOfForms.get(position);




        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        final View promptsView = li.inflate(R.layout.create_or_edit_form, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText firstName = (EditText) promptsView.findViewById(R.id.firstNameInput);
        firstName.setText(myForm.firstName);

        final EditText lastName = (EditText) promptsView.findViewById(R.id.lastNameInput);
        lastName.setText(myForm.lastName);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .setNeutralButton("Delete Form",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // get user input and set it to result
                                // edit text
                                listOfForms.remove(position);
                                DBHelper.deleteForm(myForm);
                                formListAdapter.notifyDataSetChanged();
                            }
                        })


                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog editListDialog = alertDialogBuilder.create();


        editListDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {

            @Override
            public void onShow(DialogInterface dialog)
            {

                Button b = editListDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        myForm.firstName = (firstName).getText().toString();
                        myForm.lastName = (lastName).getText().toString();

                        DBHelper.editForm(myForm);

                        formListAdapter.notifyDataSetChanged();
                        editListDialog.dismiss();

                    }
                });
            }
        });
        // show it
        editListDialog.show();

    }

    private void recieveFile() {
        Toast.makeText(MainActivity.this, "Transfer file code goes here",
                Toast.LENGTH_SHORT).show();
    }

    private void createNewFormDialog() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        final View promptsView = li.inflate(R.layout.create_or_edit_form, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setView(promptsView);
        final EditText editTextFirstName = (EditText) promptsView.findViewById(R.id.firstNameInput);
        final EditText editTextLastName = (EditText) promptsView.findViewById(R.id.lastNameInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();



        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        form myForm = new form();
                        String firstName = editTextFirstName.getText().toString();
                        String lastName = editTextLastName.getText().toString();

                        myForm.firstName = firstName;
                        myForm.lastName = lastName;
                        listOfForms.add(myForm);
                        DBHelper.insertForm(myForm);
                        formListAdapter = new ArrayAdapter<form>(MainActivity.this, android.R.layout.simple_list_item_1, listOfForms);
                        formListView.setAdapter(formListAdapter);

                        //formListAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
            }
        });


        // show it
        alertDialog.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textView.setText(new String(msg.getRecords()[0].getPayload()));
    }



//    private void performTagOperations(Intent intent){
//        String action = intent.getAction();
//        if(action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
//                action.equals(NfcAdapter.ACTION_TECH_DISCOVERED) ){
//        }
//    }

}
