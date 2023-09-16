package com.example.capstoneproject;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.example.capstoneproject.databinding.ActivityMainBinding;
import com.example.capstoneproject.databinding.ActivityTestingCertificateDocumentBinding;

public class MainActivity extends AppCompatActivity {
    ClientDB clientDB;
    ListView clientListView;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);
        clientListView = findViewById(R.id.clientListView);

        clientDB = new ClientDB(this);
        ArrayList<HashMap<String, String>> clientList = clientDB.GetClients();

        CustomListViewAdapter adapter = new CustomListViewAdapter(MainActivity.this, clientList, R.layout.list_entry, new String[]{"address"}, new int[]{R.id.address});
        clientListView.setAdapter(adapter);
        clientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                HashMap<String, String> selectedEntry = (HashMap<String, String>) adapterView.getItemAtPosition(position);
                String address = selectedEntry.get("address");
                Intent intent = new Intent(MainActivity.this, ClientJobDetailsActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    private class CustomListViewAdapter extends SimpleAdapter {
        public CustomListViewAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Button deleteButton = view.findViewById(R.id.deleteJobButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteEntryPressed(position);
                }
            });
            return view;
        }
    }

    public void addJobButtonPressed(View view) {
        Intent intent = new Intent(this, addJobActivity.class);
        startActivity(intent);
    }


    public void deleteEntryPressed(int position) {
        HashMap<String, String> selectedEntry = (HashMap<String, String>) clientListView.getItemAtPosition(position);
        String entryID = selectedEntry.get("id");
        Log.d("EntryId", "EntryID: " + entryID);
        clientDB.deleteClientEntry(entryID);
        refreshClientListView();
    }

    private void refreshClientListView() {
        ArrayList<HashMap<String, String>> clientList = clientDB.GetClients();
        ListAdapter adapter = new CustomListViewAdapter(MainActivity.this, clientList, R.layout.list_entry, new String[]{"address"}, new int[]{R.id.address});
        clientListView.setAdapter(adapter);
    }
}