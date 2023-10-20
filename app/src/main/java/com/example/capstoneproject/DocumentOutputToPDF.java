package com.example.capstoneproject;

import android.content.Context;
import android.content.res.Resources;
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
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.viewbinding.ViewBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DocumentOutputToPDF {
    private final Context context;

    public  DocumentOutputToPDF(Context context) {
        this.context = context;
    }

    //Set Height and Width of Document to A4
    private static final int DOCUMENT_HEIGHT = 842;
    private static final int DOCUMENT_WIDTH = 595;
    private static final int BORDER_MARGIN = 40;
    private static final int DRAWABLE_WIDTH = 16;
    private static final int DRAWABLE_HEIGHT = 16;

    public void testingAndComplianceOutputToPDF(String nameTitle, String nameGivenName, String nameSurname, String addressStreet, String addressSuburb,
                                                String addressPostCode, String workCompleted, String contractorLicenseNumber,
                                                String contractorLicenseName, String contractorLicenseMobile, String contractorFinalName,
                                                String dateBtn1, String dateBtn2, boolean isTestingAndComplianceChecked, boolean isTestingAndSafetyChecked) {

        PdfDocument testingCertificateDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(DOCUMENT_WIDTH, DOCUMENT_HEIGHT, 1).create();

        PdfDocument.Page documentPage = testingCertificateDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();

        //Set black margin border dimensions and colour around document
        float borderLeft = 40;
        float borderRight = DOCUMENT_WIDTH - BORDER_MARGIN;
        float borderBottom = DOCUMENT_HEIGHT - BORDER_MARGIN;
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(2);

        //Set border margin to document
        RectF borderRect = new RectF(borderLeft, BORDER_MARGIN, borderRight, borderBottom);
        canvas.drawRect(borderRect, borderPaint);

        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pinnacle_banner);
        Rect pinnacleBannerRect = new Rect(BORDER_MARGIN + 2, BORDER_MARGIN + 2, DOCUMENT_WIDTH - BORDER_MARGIN - 2, 150);
        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);

        //Set line stroke under banner
        RectF lineStokeRectf = new RectF(BORDER_MARGIN + 25, 151, DOCUMENT_WIDTH - BORDER_MARGIN - 25, 151);
        canvas.drawRect(lineStokeRectf, borderPaint);

        //Set dimensions of checkmark drawable
        Drawable checkedDrawable = AppCompatResources.getDrawable(context, R.drawable.baseline_check_circle_outline_24);
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
        if (isTestingAndComplianceChecked) {
            assert checkedDrawable != null;
            checkedDrawable.setBounds((int) (checkboxTextX + boldPaint.measureText("Testing and Compliance") + 5), 165, (int) (checkboxTextX + boldPaint.measureText("Testing and Compliance") + 10) + drawableWidth, 170 + drawableHeight);
            checkedDrawable.draw(canvas);
        }
        if (isTestingAndSafetyChecked) {
            assert checkedDrawable != null;
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

        Bitmap regulationInfoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.electrical_information);
        Rect regulationInfoRect = new Rect(-2, 240, documentPageInfo.getPageWidth() - 150, 340);
        canvas.drawBitmap(regulationInfoBitmap, null, regulationInfoRect, null);

        canvas.drawText("Name: " + contractorFinalName, 0, 385, textPaint);
        canvas.drawText("Date: " + dateBtn2, 0, 405, textPaint);
        canvas.drawText("Digital Signature: ", 270, 385, textPaint);

        //Retrieve digital signature bitmap and set dimensions
        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
        Rect signatureRect = new Rect(375, 350, 445, 430);
        canvas.drawBitmap(signatureBitmap, null, signatureRect, null);
        //Clear bitmap from list
        BitmapSingleton.getInstance().clearSignatureBitmap();

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
            Toast.makeText(context, "Outputted to PDF", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDF Generation Error", "Error while generating PDF", e);
        }

    }
}
