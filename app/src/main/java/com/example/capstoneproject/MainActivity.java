package com.example.capstoneproject;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private Button addJobButton;
    private ListView clientListView;

    ClientDB clientDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addJobButton = findViewById(R.id.addJobButton);
        clientListView = findViewById(R.id.clientListView);

        clientDB = new ClientDB(this);
        ArrayList<HashMap<String, String>> clientList = clientDB.GetClients();
        ListAdapter adapter = new SimpleAdapter(MainActivity.this, clientList, R.layout.list_entry, new String[]{"address"},
                new int[]{R.id.address});
        clientListView.setAdapter(adapter);
        clientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String listViewEntry = adapterView.getItemAtPosition(position).toString();
                //Split listview address into two strings split by '='
                String[] address = listViewEntry.split("=");
                //Toast.makeText(MainActivity.this, "Item " + address, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ClientJobDetailsActivity.class);
                intent.putExtra("address", address[1]);
                startActivity(intent);
            }
        });

    }

    public void addJobButtonPressed(View view) {
        Intent intent = new Intent(this, addJobActivity.class);
        startActivity(intent);
    }


}