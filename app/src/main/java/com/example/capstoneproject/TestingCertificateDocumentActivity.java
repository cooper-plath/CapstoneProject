package com.example.capstoneproject;

import static com.example.capstoneproject.DatePickerHelper.initDateButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstoneproject.databinding.ActivityTestingCertificateDocumentBinding;

public class TestingCertificateDocumentActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> signatureActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int result = activityResult.getResultCode();
                    if (result == RESULT_OK) {
                        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();

                        if (signatureBitmap != null) {
                            binding.signatureImageView.setVisibility(View.VISIBLE);

                        }
                    }
                }
            });

    private ActivityTestingCertificateDocumentBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private DocumentOutputToPDF documentOutputToPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapSingleton.getInstance().clearMultipleSignatureBitmaps();
        binding = ActivityTestingCertificateDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        documentOutputToPDF = new DocumentOutputToPDF(this);

        initDateButton(binding.chooseDateBtn1, this);
        initDateButton(binding.chooseDateBtn2, this);

        setRegulationBold();


        binding.submitDocumentBtn.setOnClickListener(view -> validateUserInputData());



    }

    private void validateUserInputData() {
        String nameTitle = binding.nameTitleEditText.getText().toString();
        String nameGivenName = binding.nameGivenNameEditText.getText().toString();
        String nameSurname = binding.nameSurnameEditText.getText().toString();
        String addressStreet = binding.addressStreetEditText.getText().toString();
        String addressSuburb = binding.addressSuburbEditText.getText().toString();
        String addressPostCode = binding.addressPostCodeEditText.getText().toString();
        String workCompleted = binding.workCompletedEditText.getText().toString();
        String contractorLicenseNumber = binding.contractorLicenseNumberEditText.getText().toString();
        String contractorLicenseName = binding.contractorLicenseNameEditText.getText().toString();
        String contractorLicenseMobile = binding.contractorLicenseMobileEditText.getText().toString();
        String contractorFinalName = binding.contractorFinalNameEditText.getText().toString();
        String dateBtn1 = binding.chooseDateBtn1.getText().toString();
        String dateBtn2 = binding.chooseDateBtn2.getText().toString();

        if (nameTitle.isEmpty() || nameGivenName.isEmpty() || nameSurname.isEmpty() ||
                addressStreet.isEmpty() || addressSuburb.isEmpty() || addressPostCode.isEmpty() ||
                workCompleted.isEmpty() || contractorLicenseNumber.isEmpty() || contractorLicenseName.isEmpty() ||
                contractorLicenseMobile.isEmpty() || contractorFinalName.isEmpty() || dateBtn1.equals("Date") || dateBtn2.equals("Date") || binding.signatureImageView.getVisibility() == View.GONE) {

            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        } else {
            documentOutputToPDF.testingAndComplianceOutputToPDF(nameTitle, nameGivenName, nameSurname, addressStreet, addressSuburb,
                    addressPostCode, workCompleted, contractorLicenseNumber,
                    contractorLicenseName, contractorLicenseMobile, contractorFinalName,
                    dateBtn1, dateBtn2, binding.testingAndComplianceCheckbox.isChecked(), binding.testingAndSafetyCheckbox.isChecked());
        }
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    //Set bold indentations for specific regulation text
    private void setRegulationBold() {
        String formattedRegulationText = getString(R.string.testCertificateInformation);
        binding.regulationInformationTextView.setText(Html.fromHtml(formattedRegulationText));
    }


    public void digitalSignatureOnPressed(View view) {
        Intent intent = new Intent(TestingCertificateDocumentActivity.this, digitalSignatureView.class);
        signatureActivityResultLauncher.launch(intent);
    }

}

