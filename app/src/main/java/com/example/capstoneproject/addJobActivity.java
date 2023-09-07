package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class addJobActivity extends AppCompatActivity {

    EditText clientNameEditText, clientAddressEditText;
    private Button addJobBtn;
    public ClientDB clientDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        clientNameEditText = findViewById(R.id.clientNameEditText);
        clientAddressEditText = findViewById(R.id.clientAddressEditText);
        addJobBtn = findViewById(R.id.addJobBtn);

        clientDB = new ClientDB(this);

        addJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientName = clientNameEditText.getText().toString();
                String clientAddress = clientAddressEditText.getText().toString();

                if (clientName.isEmpty() && clientAddress.isEmpty()) {
                    Toast.makeText(addJobActivity.this, "Please enter data into all fields",
                            Toast.LENGTH_SHORT).show();
                }
                clientDB.addNewJobCard(clientName, clientAddress);
                Toast.makeText(addJobActivity.this, "Job Card has been added", Toast.LENGTH_SHORT).show();
                AddClientDetails(view);
            }
        });
    }

    public void AddClientDetails (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}