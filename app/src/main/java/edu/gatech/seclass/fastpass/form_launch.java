package edu.gatech.seclass.fastpass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class form_launch extends AppCompatActivity {

    private Button addFormButton;
    //private Button recieveFileButton;
    ArrayList<form> listOfForms;
    ArrayAdapter<form> formListAdapter;
    private ListView formListView;
    private DatabaseHelper DBHelper;
    // NfcAdapter mNfcAdapter;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_launch);
        listOfForms = new ArrayList<form>();
        formListAdapter = new ArrayAdapter<form>(form_launch.this, android.R.layout.simple_list_item_1, listOfForms);
        setupViews();
        DBHelper = new DatabaseHelper(getApplicationContext());

        listOfForms = DBHelper.getAllLists();

        formListAdapter = new ArrayAdapter<form>(form_launch.this, android.R.layout.simple_list_item_1, listOfForms);
        formListView.setAdapter(formListAdapter);
    }

    private void setupViews() {
        addFormButton = (Button) findViewById(R.id.addFormButton);
        formListView = (ListView) findViewById(R.id.formList);
        formListView.setAdapter(formListAdapter);
        //recieveFileButton = (Button) findViewById(R.id.recieveFile);


        addFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigation code.
                Intent listIntent = new Intent(getApplicationContext(), editFormActivity.class);
                listIntent.putExtra("formID", "-1");
                listIntent.putExtra("createNew", true);
                startActivity(listIntent);
            }
        });

        /*
        recieveFileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recieveFile();
            }
        });
        */

        formListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                // Navigation code.
                Intent listIntent = new Intent(getApplicationContext(), editFormActivity.class);
                listIntent.putExtra("formID", Integer.toString(listOfForms.get(position).formID));
                listIntent.putExtra("createNew", false);
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

        Toast.makeText(form_launch.this, "Transfer file code goes here",
                Toast.LENGTH_SHORT).show();


        return true;
    };

    private void editForm(final int position) {

        final form myForm = listOfForms.get(position);

        LayoutInflater li = LayoutInflater.from(form_launch.this);
        final View promptsView = li.inflate(R.layout.create_or_edit_form, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(form_launch.this);

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

    /*
    private void recieveFile() {
        Toast.makeText(MainActivity.this, "Transfer file code goes here",
                Toast.LENGTH_SHORT).show();
    }*/

    private void createNewFormDialog() {
        LayoutInflater li = LayoutInflater.from(form_launch.this);
        final View promptsView = li.inflate(R.layout.create_or_edit_form, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(form_launch.this);

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
                        formListAdapter = new ArrayAdapter<form>(form_launch.this, android.R.layout.simple_list_item_1, listOfForms);
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

}
