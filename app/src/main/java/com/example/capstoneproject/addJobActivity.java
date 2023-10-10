package com.example.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class addJobActivity extends AppCompatActivity {

    EditText clientAddressEditText;
    private Button addJobBtn;
    public ClientDB clientDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        clientAddressEditText = findViewById(R.id.clientAddressEditText);
        addJobBtn = findViewById(R.id.addJobBtn);

        clientDB = new ClientDB(this);

        addJobBtn.setOnClickListener(view -> {
            String clientAddress = clientAddressEditText.getText().toString();

            if (clientAddress.isEmpty()) {
                Toast.makeText(addJobActivity.this, "Please enter data into all fields",
                        Toast.LENGTH_SHORT).show();
            }
            clientDB.addNewJobCard(clientAddress);
            Toast.makeText(addJobActivity.this, "Job Card has been added", Toast.LENGTH_SHORT).show();
            AddClientDetails(view);
        });
    }

    public void AddClientDetails (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}