package com.example.capstoneproject;

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
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.capstoneproject.databinding.ActivityVisualInspectionDocumentBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VisualInspectionDocumentActivity extends AppCompatActivity {

    private ActivityVisualInspectionDocumentBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVisualInspectionDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatePickerHelper.initDateButton(binding.visualDateBtn, this);
        binding.submitDocumentBtn.setOnClickListener(view -> generatePDF());

    }

    private void generatePDF() {

        if (binding.visualDateBtn.equals("Date") || binding.addressEditText.getText().toString().isEmpty()) {

            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }


        int documentHeight = 842;
        int documentWidth = 595;
        int borderMargin = 40;

        PdfDocument visualInspectionDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = visualInspectionDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();


        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pinnaclebannerabn);
        Rect pinnacleBannerRect = new Rect(30, 10, documentWidth - 30, 160);
        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);


        //Set dimensions of blank checkmark drawable
        Drawable checkedBlankDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_outline_blank_24);

        //Set dimensions of blank checkmark drawable
        Drawable checkedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_24);

        //Set text size and colour for regular strings
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(10);

        //Set text and size for bold text
        Paint boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setTextSize(12);
        Typeface boldTypeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        boldPaint.setTypeface(boldTypeFace);

        //Set black margin border dimensions and colour around document
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(0.5F);


        canvas.drawText("Address: " + binding.addressEditText.getText().toString(), 40, 180, textPaint);
        canvas.drawText("Date: " + binding.visualDateBtn.getText().toString(), 450, 180, textPaint);

        float tableRowHeight = 25;
        float tableRowInitialY = 215;
        float columnInitialY = 190;

        float columnLine1X = 450;
        float columnLine2X = 485;
        float columnLine3X = 520;


        //Draw rect table row for inspection activity and checkbox answers
        RectF lineStokeRectf = new RectF(borderMargin, 190, documentWidth - borderMargin, 190 + tableRowHeight);
        canvas.drawRect(lineStokeRectf, borderPaint);


        //Establish headers for table rows
        canvas.drawText("INSPECTION ACTIVITY", 160, 205, boldPaint);
        canvas.drawText("Yes", columnLine1X + 7, 205, boldPaint);
        canvas.drawText("No", columnLine2X + 7, 205, boldPaint);
        canvas.drawText("NA", columnLine3X + 7, 205, boldPaint);

        //Create row with data from user input

        int checkedCheckboxCount = 0;
        for (int i = 0; i < 19; i++) {

            View rowView = binding.tableLayoutCheckboxes.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                int checkedCheckboxIndex = -1;

                for (int a = 1; a < row.getChildCount(); a++) {
                    View rowChildView = row.getChildAt(a);
                    if (rowChildView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) rowChildView;


                        boolean isChecked = checkBox.isChecked();
                        if (isChecked) {
                            checkedCheckboxIndex = a - 1;
                            checkedCheckboxCount++;
                            break;
                        }

                    }

                }

                if (checkedCheckboxIndex >= 0 && checkedCheckboxIndex <= 2) {
                    TextView textView = (TextView) row.getChildAt(0);
                    String text = textView.getText().toString();
                    Log.d("Testing", "Question: " + text + " Checked Checkbox Index: " + checkedCheckboxIndex);
                    //Print checked drawable dependent on which checkbox is pressed
                    canvas.drawText(text, borderMargin + 5, tableRowInitialY + 15, textPaint);
                    switch (checkedCheckboxIndex) {

                        //Yes is pressed
                        case 0:
                            assert checkedDrawable != null;
                            checkedDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialY + 2), (int) (columnLine2X - 5), (int) (tableRowInitialY + 23));
                            checkedDrawable.draw(canvas);

                            assert checkedBlankDrawable != null;
                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialY + 2), (int) (columnLine3X - 5), (int) (tableRowInitialY + 23));
                            checkedBlankDrawable.draw(canvas);

                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialY + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialY + 23));
                            checkedBlankDrawable.draw(canvas);
                            break;

                        //No is pressed
                        case 1:
                            assert checkedDrawable != null;
                            checkedDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialY + 2), (int) (columnLine3X - 5), (int) (tableRowInitialY + 23));
                            checkedDrawable.draw(canvas);

                            assert checkedBlankDrawable != null;
                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialY + 2), (int) (columnLine2X - 5), (int) (tableRowInitialY + 23));
                            checkedBlankDrawable.draw(canvas);

                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialY + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialY + 23));
                            checkedBlankDrawable.draw(canvas);
                            break;

                        //NA is pressed
                        case 2:
                            assert checkedDrawable != null;
                            checkedDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialY + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialY + 23));
                            checkedDrawable.draw(canvas);

                            assert checkedBlankDrawable != null;
                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialY + 2), (int) (columnLine2X - 5), (int) (tableRowInitialY + 23));
                            checkedBlankDrawable.draw(canvas);

                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialY + 2), (int) (columnLine3X - 5), (int) (tableRowInitialY + 23));
                            checkedBlankDrawable.draw(canvas);
                            break;
                    }


                }


                //Draw left line of table row
                canvas.drawLine(borderMargin, tableRowInitialY, borderMargin, tableRowInitialY + tableRowHeight, borderPaint);
                //Draw right line of table row
                canvas.drawLine(documentWidth - borderMargin, tableRowInitialY, documentWidth - borderMargin, tableRowInitialY + tableRowHeight, borderPaint);
                //Draw bottom line of table row
                canvas.drawLine(borderMargin, tableRowInitialY + tableRowHeight, documentWidth - borderMargin, tableRowInitialY + tableRowHeight, borderPaint);


                //Draw columns
                canvas.drawLine(columnLine1X, columnInitialY, columnLine1X, columnInitialY + tableRowHeight, borderPaint);
                canvas.drawLine(columnLine2X, columnInitialY, columnLine2X, columnInitialY + tableRowHeight, borderPaint);
                canvas.drawLine(columnLine3X, columnInitialY, columnLine3X, columnInitialY + tableRowHeight, borderPaint);

                tableRowInitialY += tableRowHeight;
                columnInitialY += 25;
            }
        }


        //Draw last set of column lines for final entry
        canvas.drawLine(columnLine1X, columnInitialY, columnLine1X, columnInitialY + tableRowHeight, borderPaint);
        canvas.drawLine(columnLine2X, columnInitialY, columnLine2X, columnInitialY + tableRowHeight, borderPaint);
        canvas.drawLine(columnLine3X, columnInitialY, columnLine3X, columnInitialY + tableRowHeight, borderPaint);

        visualInspectionDocument.finishPage(documentPage);


        //Start page 2 to display further user input
        PdfDocument.PageInfo documentPageInfo2 = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 2).create();
        PdfDocument.Page documentPage2 = visualInspectionDocument.startPage(documentPageInfo2);

        Canvas canvas2 = documentPage2.getCanvas();

        //Set Pinnacle Power banner at top of document
        canvas2.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);

        float tableRowInitialYPage2 = 215;
        float columnInitialYPage2 = 190;

        //Establish headers for table rows
        canvas2.drawText("ROOM BY ROOM INSPECTION", 150, 205, boldPaint);
        canvas2.drawText("Yes", columnLine1X + 7, 205, boldPaint);
        canvas2.drawText("No", columnLine2X + 7, 205, boldPaint);
        canvas2.drawText("NA", columnLine3X + 7, 205, boldPaint);

        canvas2.drawRect(lineStokeRectf, borderPaint);


        for (int i = 20; i < 37; i++) {

            View rowView = binding.tableLayoutCheckboxes.getChildAt(i);
            if (rowView instanceof TableRow) {
                TableRow row = (TableRow) rowView;
                int checkedCheckboxIndex = -1;

                for (int a = 1; a < row.getChildCount(); a++) {
                    View rowChildView = row.getChildAt(a);
                    if (rowChildView instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) rowChildView;


                        boolean isChecked = checkBox.isChecked();
                        if (isChecked) {
                            checkedCheckboxIndex = a - 1;
                            checkedCheckboxCount++;
                            break;
                        }

                    }

                }

                if (checkedCheckboxIndex >= 0 && checkedCheckboxIndex <= 2) {
                    TextView textView = (TextView) row.getChildAt(0);
                    String text = textView.getText().toString();
                    //Print checked drawable dependent on which checkbox is pressed
                    canvas2.drawText(text, borderMargin + 5, tableRowInitialYPage2 + 15, textPaint);
                    switch (checkedCheckboxIndex) {

                        //Yes is pressed
                        case 0:
                            assert checkedDrawable != null;
                            checkedDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine2X - 5), (int) (tableRowInitialYPage2 + 23));
                            checkedDrawable.draw(canvas2);

                            assert checkedBlankDrawable != null;
                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine3X - 5), (int) (tableRowInitialYPage2 + 23));
                            checkedBlankDrawable.draw(canvas2);

                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialYPage2 + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialYPage2 + 23));
                            checkedBlankDrawable.draw(canvas2);
                            break;

                        //No is pressed
                        case 1:
                            assert checkedDrawable != null;
                            checkedDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine3X - 5), (int) (tableRowInitialYPage2 + 23));
                            checkedDrawable.draw(canvas2);

                            assert checkedBlankDrawable != null;
                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine2X - 5), (int) (tableRowInitialYPage2 + 23));
                            checkedBlankDrawable.draw(canvas2);

                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialYPage2 + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialYPage2 + 23));
                            checkedBlankDrawable.draw(canvas2);
                            break;

                        //NA is pressed
                        case 2:
                            assert checkedDrawable != null;
                            checkedDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialYPage2 + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialYPage2 + 23));
                            checkedDrawable.draw(canvas2);

                            assert checkedBlankDrawable != null;
                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine2X - 5), (int) (tableRowInitialYPage2 + 23));
                            checkedBlankDrawable.draw(canvas2);

                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine3X - 5), (int) (tableRowInitialYPage2 + 23));
                            checkedBlankDrawable.draw(canvas2);
                            break;
                    }


                }

                //Draw left line of table row
                canvas2.drawLine(borderMargin, tableRowInitialYPage2, borderMargin, tableRowInitialYPage2 + tableRowHeight, borderPaint);
                //Draw right line of table row
                canvas2.drawLine(documentWidth - borderMargin, tableRowInitialYPage2, documentWidth - borderMargin, tableRowInitialYPage2 + tableRowHeight, borderPaint);
                //Draw bottom line of table row
                canvas2.drawLine(borderMargin, tableRowInitialYPage2 + tableRowHeight, documentWidth - borderMargin, tableRowInitialYPage2 + tableRowHeight, borderPaint);


                //Draw columns
                canvas2.drawLine(columnLine1X, columnInitialYPage2, columnLine1X, columnInitialYPage2 + tableRowHeight, borderPaint);
                canvas2.drawLine(columnLine2X, columnInitialYPage2, columnLine2X, columnInitialYPage2 + tableRowHeight, borderPaint);
                canvas2.drawLine(columnLine3X, columnInitialYPage2, columnLine3X, columnInitialYPage2 + tableRowHeight, borderPaint);

                tableRowInitialYPage2 += tableRowHeight;
                columnInitialYPage2 += 25;
            }
        }
        Log.d("CheckboxCount", "generatePDF: " + checkedCheckboxCount);

        // Check if user hasn't pressed a checkbox in each row and display message
        if (checkedCheckboxCount != 36) {
            Toast.makeText(this, "Please click a checkbox for every entry.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Draw last set of column lines for final entry
        canvas2.drawLine(columnLine1X, columnInitialYPage2, columnLine1X, columnInitialYPage2 + tableRowHeight, borderPaint);
        canvas2.drawLine(columnLine2X, columnInitialYPage2, columnLine2X, columnInitialYPage2 + tableRowHeight, borderPaint);
        canvas2.drawLine(columnLine3X, columnInitialYPage2, columnLine3X, columnInitialYPage2 + tableRowHeight, borderPaint);


        visualInspectionDocument.finishPage(documentPage2);

        //Output PDF
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        String fileName = "VisualInspectionDocument.pdf";
        File file = new File(fileDir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            visualInspectionDocument.writeTo(fileOutputStream);
            visualInspectionDocument.close();
            fileOutputStream.close();
//            Toast.makeText(this, "Outputted to PDF", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        PdfRenderer pdfRenderer;
        PdfRenderer.Page pdfPage;
        int pageNumber = 1;


        try {
            // Open the PDF file
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            int totalPages = pdfRenderer.getPageCount();
            for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                pdfPage = pdfRenderer.openPage(pageIndex);

                // Create a Bitmap to render the page
                Bitmap bitmap = Bitmap.createBitmap(pdfPage.getWidth(), pdfPage.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvasBitmap = new Canvas(bitmap);
                canvasBitmap.drawColor(Color.WHITE);
                canvasBitmap.drawBitmap(bitmap, 0, 0, null);

                // Render the PDF page onto the Bitmap
                pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);
                pdfPage.close();

                // Save the Bitmap as a JPEG image
                File jpgFileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String jpgFileName = "VisualInspectionDocument_" + pageNumber + ".jpg";
                File jpgFile = new File(jpgFileDir, jpgFileName);
                FileOutputStream jpgOutputStream = new FileOutputStream(jpgFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, jpgOutputStream);
                jpgOutputStream.close();
                pageNumber++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Outputted to Documents and Photo Album", Toast.LENGTH_SHORT).show();
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
}