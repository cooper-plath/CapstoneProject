package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.capstoneproject.databinding.ActivityIncidentReportDocumentBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IncidentReportDocumentActivity extends AppCompatActivity {

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

    private ActivityIncidentReportDocumentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIncidentReportDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatePickerHelper.initDateButton(binding.incidentDateBtn1, this);
        DatePickerHelper.initDateButton(binding.incidentDateBtn2, this);

        binding.digitalSignatureBtn.setOnClickListener(view -> {
            Intent intent = new Intent(IncidentReportDocumentActivity.this, digitalSignatureView.class);
            signatureActivityResultLauncher.launch(intent);
        });

        binding.submitDocumentBtn.setOnClickListener(view -> generatePDF());
    }

    private void generatePDF() {
        //Set Height and Width of Document to A4
        int documentHeight = 842;
        int documentWidth = 595;
        int borderMargin = 40;

        //Set text size and colour for regular strings
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);

        //Set text and size for bold text
        Paint boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setTextSize(13);
        Typeface boldTypeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        boldPaint.setTypeface(boldTypeFace);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(0.5f);

        PdfDocument incidentReportDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = incidentReportDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();
        //Input User data to document

        float textInitialX = borderMargin + 5;
        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pinnacle_banner);
        Rect pinnacleBannerRect = new Rect(borderMargin + 2, 10, documentWidth - borderMargin - 2, 130);
        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);


        canvas.drawText("Incident Report", (documentWidth / 2) - 50, 145, boldPaint);
        PDFBorderRectHelper.drawLinedRect(canvas, 0.5f, 147, 255);

        canvas.drawText("Date: " + binding.incidentDateBtn1.getText().toString(), textInitialX, 160, textPaint);
        canvas.drawText("Name: " + binding.nameEditText.getText().toString(), textInitialX, 180, textPaint);
        canvas.drawText("Role: " + binding.roleEditText.getText().toString(), textInitialX, 200, textPaint);
        canvas.drawText("Signature: ", textInitialX, 230, textPaint);

        //Retrieve digital signature bitmap and set dimensions
        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
        Rect signatureRect = new Rect((int) (textInitialX + textPaint.measureText("Signature: ")), 205, (documentWidth / 2) - 65, 248);
        canvas.drawBitmap(signatureBitmap, null, signatureRect, null);

        canvas.drawText("Incident", textInitialX, 280, boldPaint);
        PDFBorderRectHelper.drawLinedRect(canvas, 0.5f, 282, 480);

        canvas.drawText("Date and Time of incident: " + binding.dateTimeEditText.getText().toString(), textInitialX, 295, textPaint);
        PDFBorderRectHelper.drawStokedLine(canvas, 0.5f, 305);

        canvas.drawText("Name/s of person/s involved in the incident: ", textInitialX, 320, textPaint);


        TextPaint workCompletedPaint = new TextPaint();
        StaticLayout incidentStaticLayout = PDFBorderRectHelper.textBoxLayout(binding.personsInvolvedEditText.getText().toString(),
                workCompletedPaint, 500);
        canvas.translate(textInitialX, 325);
        canvas.save();
        incidentStaticLayout.draw(canvas);
        canvas.restore();


        canvas.drawLine(-5, 50, 510, 52, borderPaint);

        canvas.drawText("Description of incident:", 0, 62, textPaint);
        StaticLayout descriptionStaticLayout = PDFBorderRectHelper.textBoxLayout(binding.descriptionIncidentEditText.getText().toString(),
                workCompletedPaint, 500);
        canvas.translate(0, 65);
        canvas.save();
        descriptionStaticLayout.draw(canvas);
        canvas.restore();


        canvas.drawRect(-5, 110, 510, 185, borderPaint);

        canvas.drawText("Witnesses (include contact details):", 0, 122, textPaint);
        StaticLayout witnessStaticLayout = PDFBorderRectHelper.textBoxLayout(binding.witnessEditText.getText().toString(),
                workCompletedPaint, 500);
        canvas.translate(0, 127);
        canvas.save();
        witnessStaticLayout.draw(canvas);
        canvas.restore();


        canvas.drawRect(-5, 80, 510, 163, borderPaint);
        canvas.drawText("Reporting of the incident", 0, 75, boldPaint);
        canvas.drawText("Incident Reported to: (Please tick/circle)", 0, 93, textPaint);

        Drawable checkedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_24);
        Drawable unCheckedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_outline_blank_24);

        if (binding.pinnacleOfficeCheckbox.isChecked()) {
            checkedDrawable.setBounds(0, 100, 15, 115);
            checkedDrawable.draw(canvas);
            canvas.drawText("Pinnacle Office - Dave/Admin", 16, 113, textPaint);

            unCheckedDrawable.setBounds(0, 120, 15, 135);
            unCheckedDrawable.draw(canvas);
            canvas.drawText("Electrical Safety Office", 16, 133, textPaint);
        }

        if (binding.electricalSafetyOfficeCheckbox.isChecked()) {
            unCheckedDrawable.setBounds(0, 100, 15, 115);
            unCheckedDrawable.draw(canvas);
            canvas.drawText("Pinnacle Office - Dave/Admin", 16, 113, textPaint);

            checkedDrawable.setBounds(0, 120, 15, 135);
            checkedDrawable.draw(canvas);
            canvas.drawText("Electrical Safety Office", 16, 133, textPaint);
        }
        canvas.drawLine(-5, 140, 510, 140, borderPaint);

        canvas.drawLine((documentWidth / 2) + 40, 80, (documentWidth / 2) + 40, 140, borderPaint);


        canvas.drawText("Date: " + binding.incidentDateBtn2.getText().toString(), (documentWidth / 2) + 45, 116, textPaint);

        canvas.drawText("How (this form, in person, email, phone):" + binding.communicateMethodEditText.getText().toString(), 0, 155, textPaint);

        canvas.drawRect(-5, 190, 510, 280, borderPaint);
        canvas.drawText("Follow Up Action", 0, 185, boldPaint);

        canvas.drawText("Was treatment received following the injury/illness?", 0, 200, textPaint);
        canvas.drawText(binding.treatmentEditText.getText().toString(), 0, 218, textPaint);

        canvas.drawText("Description of actions to be taken:", 0, 240, textPaint);
        StaticLayout actionsTakenStaticLayout = PDFBorderRectHelper.textBoxLayout(binding.actionsTakenEditText.getText().toString(),
                workCompletedPaint, 500);
        canvas.translate(0, 248);
        canvas.save();
        actionsTakenStaticLayout.draw(canvas);
        canvas.restore();


        incidentReportDocument.finishPage(documentPage);
        //Output PDF
        String fileName = "IncidentReport.pdf";
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(fileDir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            incidentReportDocument.writeTo(fileOutputStream);
            incidentReportDocument.close();
            fileOutputStream.close();
            Toast.makeText(this, "Outputted to PDF", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}