package edu.gatech.seclass.fastpass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Kevin on 11/8/2016.
 */

public class DatabaseHelper {
    final SQLiteDatabase gdb;

    public DatabaseHelper(Context context)
    {


        gdb = context.openOrCreateDatabase("fastPassDatabase",context.MODE_PRIVATE,null);
        gdb.execSQL("PRAGMA foreign_keys=ON;");
        createDatabaseTables();
    };

    private void createDatabaseTables() {
        String createFormTableSQL = "CREATE TABLE if not exists forms (" +
                " formID Integer Primary key AUTOINCREMENT, " +
                " firstName String (50)," +
                " lastName String (50))";


        gdb.execSQL(createFormTableSQL);

    };

    public void insertForm(form formToInsert) {
        gdb.execSQL("Insert into forms (firstName, lastName) VALUES (?,?);",
                new String[] {formToInsert.firstName, formToInsert.lastName});
    }

    public ArrayList<form> getAllLists() {
        ArrayList<form> allLists = new ArrayList<>();
        String selectQuery = "Select * from forms";

        Cursor cur= gdb.rawQuery(selectQuery,new String[] {});
        cur.moveToFirst();
        while (cur.isAfterLast() == false)
        {
            form myNewItem = new form();
            myNewItem.formID = cur.getInt(0);
            myNewItem.firstName = cur.getString(1);
            myNewItem.lastName = cur.getString(2);
            allLists.add(myNewItem);
            cur.moveToNext();
        }
        return allLists;
    }

    public void editForm(form formToEdit) {
        gdb.execSQL("UPDATE forms SET firstName = ?, lastName = ? where formID = ?;",
                new String[] {formToEdit.firstName, formToEdit.lastName, String.valueOf(formToEdit.formID)});
    }

    public void deleteForm(form formToDelete) {
        gdb.execSQL("Delete from forms where formID = ?;",
                new String[] {String.valueOf(formToDelete.formID)});
    }

};
