package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ClientJobDetailsActivity extends AppCompatActivity {

    private TextView addressTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_job_details);

        addressTextview = findViewById(R.id.addressTextView);


        Intent intent = getIntent();
        String address = intent.getExtras().getString("address");
        addressTextview.setText(address);



    }

    public void testingCertificateOnPressed(View view) {
        Intent intent = new Intent(ClientJobDetailsActivity.this, TestingCertificateDocumentActivity.class);
        startActivity(intent);
    }

    public void visualInspectionOnPressed(View view) {
        Intent intent = new Intent(ClientJobDetailsActivity.this, VisualInspectionDocumentActivity.class);
        startActivity(intent);
    }

    public void incidentReportOnPressed(View view) {
        Intent intent = new Intent(ClientJobDetailsActivity.this, IncidentReportDocumentActivity.class);
        startActivity(intent);
    }


//    public void siteSpecificOnPressed(View view) {
//        Intent intent = new Intent(ClientJobDetailsActivity.this, SiteSpecificRiskAssessmentActivity.class);
//        startActivity(intent);
//    }
}