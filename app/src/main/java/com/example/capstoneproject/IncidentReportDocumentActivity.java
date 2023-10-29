package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstoneproject.databinding.ActivityIncidentReportDocumentBinding;

public class IncidentReportDocumentActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> signatureActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int result = activityResult.getResultCode();
                    if (result == RESULT_OK) {
                        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
                        //Clear bitmap from list
                        if (signatureBitmap != null) {
                            binding.signatureImageView.setVisibility(View.VISIBLE);

                        }
                    }
                }
            });

    private DocumentOutputToPDF documentOutputToPDF;

    private ActivityIncidentReportDocumentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapSingleton.getInstance().clearMultipleSignatureBitmaps();

        binding = ActivityIncidentReportDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        documentOutputToPDF = new DocumentOutputToPDF(this);

        DatePickerHelper.initDateButton(binding.incidentDateBtn1, this);
        DatePickerHelper.initDateButton(binding.incidentDateBtn2, this);

        binding.digitalSignatureBtn.setOnClickListener(view -> {
            Intent intent = new Intent(IncidentReportDocumentActivity.this, digitalSignatureView.class);
            signatureActivityResultLauncher.launch(intent);
        });

        binding.submitDocumentBtn.setOnClickListener(view -> validateUserData());
    }

    private void validateUserData() {

        if (binding.nameEditText.getText().toString().isEmpty() || binding.roleEditText.getText().toString().isEmpty() || binding.dateTimeEditText.getText().toString().isEmpty() || binding.personsInvolvedEditText.getText().toString().isEmpty()
                || binding.descriptionIncidentEditText.getText().toString().isEmpty() || binding.witnessEditText.getText().toString().isEmpty() || binding.incidentDateBtn1.equals("Date") || binding.incidentDateBtn2.equals("Date") || binding.signatureImageView.getVisibility() == View.GONE
                || binding.communicateMethodEditText.getText().toString().isEmpty() || binding.treatmentEditText.getText().toString().isEmpty() || binding.actionsTakenEditText.getText().toString().isEmpty()) {

            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        } else {
            documentOutputToPDF.incidentReportToPDF(binding.nameEditText.getText().toString(), binding.roleEditText.getText().toString(), binding.dateTimeEditText.getText().toString(), binding.personsInvolvedEditText.getText().toString(),
                    binding.descriptionIncidentEditText.getText().toString(), binding.witnessEditText.getText().toString(), binding.incidentDateBtn1.getText().toString(), binding.incidentDateBtn2.getText().toString(),
                    binding.communicateMethodEditText.getText().toString(), binding.treatmentEditText.getText().toString(), binding.actionsTakenEditText.getText().toString(), binding.pinnacleOfficeCheckbox.isChecked(), binding.electricalSafetyOfficeCheckbox.isChecked());

        }
    }

}