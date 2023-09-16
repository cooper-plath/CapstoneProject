package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientDB extends SQLiteOpenHelper {

    private static final String DB_NAME = "jobcarddb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myjobcard";
    private static final String ID_COL = "id";
    private static final String CLIENT_NAME = "name";
    private static final String CLIENT_EMAIL = "email";
    private static final String CLIENT_ADDRESS = "address";

    public ClientDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = " CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CLIENT_NAME + " TEXT,"
                + CLIENT_ADDRESS + " TEXT)";
        db.execSQL(query);
    }

    public void addNewJobCard(String clientName, String clientAddress) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CLIENT_NAME, clientName);
        values.put(CLIENT_ADDRESS, clientAddress);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> GetClients() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();

        String query = "SELECT " + ID_COL + ", " + CLIENT_ADDRESS + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            HashMap<String,String> client = new HashMap<>();

            long clientID = cursor.getLong(cursor.getColumnIndex(ID_COL));
            String clientAddress = cursor.getString(cursor.getColumnIndex(CLIENT_ADDRESS));

            client.put("id", String.valueOf(clientID));
            client.put("address", clientAddress);
            clientList.add(client);
        }
        return clientList;
    }

    public boolean deleteClientEntry(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_NAME, ID_COL + " = ?", new String[]{String.valueOf(id)});
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

}
