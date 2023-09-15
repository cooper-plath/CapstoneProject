package com.example.capstoneproject;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.icu.lang.UCharacter.JoiningGroup.PE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.databinding.ActivityMainBinding;
import com.example.capstoneproject.databinding.ActivityTestingCertificateDocumentBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

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

    private DatePickerDialog datePickerDialog;

    private ActivityTestingCertificateDocumentBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestingCertificateDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDateButton(binding.chooseDateBtn1);
        initDateButton(binding.chooseDateBtn2);

        setRegulationBold();

        binding.submitDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDocumentPermissions()) {
                    generatePDF();
                } else {
                    requestDocumentPermission();
                }

            }
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


        int documentHeight = 842;
        int documentWidth = 595;
        int borderMargin = 24;
        float marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderMargin, getResources().getDisplayMetrics());

        PdfDocument testingCertificateDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = testingCertificateDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();

        //Set black margin border dimensions and colour around document
        float borderLeft = documentWidth - marginPx;
        float borderRight = documentWidth -marginPx;
        float borderTop = marginPx;
        float borderBottom = marginPx;
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(5);

        //Set border to document
        RectF borderRect = new RectF(borderLeft, borderTop, borderRight, borderBottom);
        canvas.drawRect(borderRect, borderPaint);

        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pinnacle_banner);
        Rect pinnacleBannerRect = new Rect(0, 0, documentPageInfo.getPageWidth(), 150);
        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);

        Drawable checkedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_circle_outline_24);
        int drawableWidth = 10;
        int drawableHeight= 12;

        //Set text size and colour for strings
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        float checkboxTextX = 225;

        canvas.drawText("CERTIFICATE OF: ", 100, 200, paint);
        canvas.drawText("Testing and Safety", checkboxTextX, 220, paint);
        canvas.drawText("Testing and Compliance", checkboxTextX, 180, paint);


        //Add checkboxes to top of document
        if (binding.testingAndComplianceCheckbox.isChecked()) {
            checkedDrawable.setBounds((int) (checkboxTextX + paint.measureText("Testing and Compliance") + 5), 170, (int) (checkboxTextX + paint.measureText("Testing and Compliance") + 10) + drawableWidth, 170 + drawableHeight);
            checkedDrawable.draw(canvas);
        }
        if (binding.testingAndSafetyCheckbox.isChecked()) {
            checkedDrawable.setBounds((int) (checkboxTextX + paint.measureText("Testing and Safety") + 5), 210, (int) (checkboxTextX + paint.measureText("Testing and Safety") + 10) + drawableWidth, 210 + drawableHeight);
            checkedDrawable.draw(canvas);
        }

        canvas.drawText("Work performed by:", 110, 280, paint);

        canvas.drawText("Name: " + nameTitle + " " + nameGivenName + " " + nameSurname, 115, 300, paint);



        testingCertificateDocument.finishPage(documentPage);

        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "TestingDocument.pdf";
        File file = new File(fileDir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            testingCertificateDocument.writeTo(fileOutputStream);
            testingCertificateDocument.close();
            fileOutputStream.close();
            Toast.makeText(this, "Outputted to PDF", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.e("FileLog", "Error writing to PDF: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("FileLog", "IOException: " + e.toString());
            e.printStackTrace();

        }
    }

    private boolean checkDocumentPermissions() {
        int writePermission = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int readPermission = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestDocumentPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PE);
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

    private void setRegulationBold() {
        String formattedRegulationText = getString(R.string.testCertificateInformation);
        binding.regulationInformationTextView.setText(Html.fromHtml(formattedRegulationText));
    }

    private void initDateButton(final Button button) {
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = " " + day + " / " + month + " / " + year;
                button.setText(date);
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int style = AlertDialog.THEME_HOLO_LIGHT;
                datePickerDialog = new DatePickerDialog(TestingCertificateDocumentActivity.this, style, dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void digitalSignatureOnPressed(View view) {
        Intent intent = new Intent(TestingCertificateDocumentActivity.this, digitalSignatureView.class);
        signatureActivityResultLauncher.launch(intent);
    }


}

