package com.example.capstoneproject;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.icu.lang.UCharacter.JoiningGroup.PE;
import static com.example.capstoneproject.DatePickerHelper.initDateButton;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.capstoneproject.databinding.ActivityTestingCertificateDocumentBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestingCertificateDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDateButton(binding.chooseDateBtn1, this);
        initDateButton(binding.chooseDateBtn2, this);

        setRegulationBold();

        binding.submitDocumentBtn.setOnClickListener(view -> {
//            if (checkDocumentPermissions()) {
//                generatePDF();
//            } else {
//                requestDocumentPermission();
//            }
            generatePDF();

        });
    }

    private void generatePDF() {

        //Retrieve string data from editTexts
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

        //Set Height and Width of Document to A4
        int documentHeight = 842;
        int documentWidth = 595;
        int borderMargin = 40;

        PdfDocument testingCertificateDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = testingCertificateDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();

        //Set black margin border dimensions and colour around document
        float borderLeft = 40;
        float borderRight = documentWidth - borderMargin;
        float borderBottom = documentHeight - borderMargin;
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(2);

        //Set border margin to document
        RectF borderRect = new RectF(borderLeft, borderMargin, borderRight, borderBottom);
        canvas.drawRect(borderRect, borderPaint);

        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pinnacle_banner);
        Rect pinnacleBannerRect = new Rect(borderMargin + 2, borderMargin +  2, documentWidth - borderMargin - 2, 150);
        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);

        //Set line stroke under banner
        RectF lineStokeRectf = new RectF(borderMargin + 25, 151, documentWidth - borderMargin - 25, 151);
        canvas.drawRect(lineStokeRectf, borderPaint);

        //Set dimensions of checkmark drawable
        Drawable checkedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_circle_outline_24);
        int drawableWidth = 16;
        int drawableHeight = 16;

        //Set text size and colour for regular strings
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);

        //Set text and size for bold text
        Paint boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setTextSize(18);
        Typeface boldTypeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        boldPaint.setTypeface(boldTypeFace);


        float checkboxTextX = 250;
        float textX = 80;

        canvas.drawText("CERTIFICATE OF: ", 80, 200, boldPaint);
        canvas.drawText("Testing and Safety", checkboxTextX, 220, boldPaint);
        canvas.drawText("Testing and Compliance", checkboxTextX, 180, boldPaint);


        //Add checkboxes to top of document
        if (binding.testingAndComplianceCheckbox.isChecked()) {
            checkedDrawable.setBounds((int) (checkboxTextX + boldPaint.measureText("Testing and Compliance") + 5), 165, (int) (checkboxTextX + boldPaint.measureText("Testing and Compliance") + 10) + drawableWidth, 170 + drawableHeight);
            checkedDrawable.draw(canvas);
        }
        if (binding.testingAndSafetyCheckbox.isChecked()) {
            checkedDrawable.setBounds((int) (checkboxTextX + boldPaint.measureText("Testing and Safety") + 5), 205, (int) (checkboxTextX + boldPaint.measureText("Testing and Safety") + 10) + drawableWidth, 210 + drawableHeight);
            checkedDrawable.draw(canvas);
        }

        canvas.drawText("Work performed by:", textX, 250, textPaint);

        canvas.drawText("Name: " + nameTitle + " " + nameGivenName + " " + nameSurname, textX + 5, 280, textPaint);

        canvas.drawText("Address: " + addressStreet + " " + addressSuburb + " " + addressPostCode, textX + 5, 310, textPaint);

        canvas.drawText("*Electrical installation / equipment tested", textX, 340, textPaint);

        //Create text wrapping which places line breaks so text doesn't print off the document

        TextPaint workCompletedPaint = new TextPaint();
        StaticLayout staticLayout = new StaticLayout(workCompleted, workCompletedPaint, documentPageInfo.getPageWidth() - 150,
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.translate(textX, 360);
        canvas.save();
        staticLayout.draw(canvas);
        canvas.restore();

        int staticLayoutHeight = staticLayout.getHeight();
        int staticLayoutWidth = staticLayout.getWidth();
        Log.d("StaticHeight", "StaticHeight: " + staticLayoutHeight);
        Log.d("StaticWidth", "StaticWidth: " + staticLayoutWidth);


        canvas.drawText("Date of Test: " + dateBtn1, 0, 150, textPaint);
        canvas.drawText("Electrical Contractor License Number: " + contractorLicenseNumber, textPaint.measureText("Date of Test: " + dateBtn1) + 50, 150, textPaint);

        canvas.drawText("Name on Contractor License: " + contractorLicenseName, 0, 180, textPaint);

        canvas.drawText("Electrical contractor phone number: " + contractorLicenseMobile, 0, 210, textPaint);

        Bitmap regulationInfoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.electrical_information);
        Rect regulationInfoRect = new Rect(-2, 240, documentPageInfo.getPageWidth() - 150, 340);
        canvas.drawBitmap(regulationInfoBitmap, null, regulationInfoRect, null);

        canvas.drawText("Name: " + contractorFinalName, 0, 385, textPaint);
        canvas.drawText("Date: " + dateBtn2, 0, 405, textPaint);
        canvas.drawText("Digital Signature: ", 270, 385, textPaint);

        //Retrieve digital signature bitmap and set dimensions
        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
        Rect signatureRect = new Rect(375, 350, 445, 430);
        canvas.drawBitmap(signatureBitmap, null, signatureRect, null);

        testingCertificateDocument.finishPage(documentPage);

        //Output PDF
        String fileName = "TestingDocument.pdf";
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(fileDir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            testingCertificateDocument.writeTo(fileOutputStream);
            testingCertificateDocument.close();
            fileOutputStream.close();
            Toast.makeText(this, "Outputted to PDF", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
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

