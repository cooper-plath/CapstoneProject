package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstoneproject.databinding.ActivitySiteSpecficRiskAssessmentBinding;

public class SiteSpecificRiskAssessmentActivity extends AppCompatActivity {

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

    ActivitySiteSpecficRiskAssessmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteSpecficRiskAssessmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Remove Hazard Container
        binding.enterHazardContainerLayout.removeAllViews();
        binding.enterTradesmenContainerLayout.removeAllViews();

        DatePickerHelper.initDateButton(binding.chooseDateBtn1, this);

        binding.tradesmenCountEditText.setVisibility(View.INVISIBLE);
        binding.submitDocumentBtn.setVisibility(View.INVISIBLE);
        binding.certifyCheckbox.setVisibility(View.INVISIBLE);

        //Add text listener for when user inputs number of hazards for site
        binding.hazardCountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int hazardCount = 0;
                try {
                    hazardCount = Integer.parseInt(binding.hazardCountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Integer Input only", Toast.LENGTH_SHORT).show();
                }

                int hazardTitleInteger = 1;
                for (int a = 0; a < hazardCount; a++) {
                    View hazardContainerLayout = getLayoutInflater().inflate(R.layout.hazard_entry, null);
                    TextView hazardTitle = hazardContainerLayout.findViewById(R.id.hazardTitleTextView);
                    hazardTitle.setText("Hazard " + hazardTitleInteger);
                    binding.enterHazardContainerLayout.addView(hazardContainerLayout);
                    hazardTitleInteger++;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tradesmenCountEditText.setVisibility(View.VISIBLE);
                binding.certifyCheckbox.setVisibility(View.VISIBLE);
            }
        });

        binding.tradesmenCountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int employeeCount = 0;
                try {
                    employeeCount = Integer.parseInt(binding.tradesmenCountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Integer Input only", Toast.LENGTH_SHORT).show();
                }
                int employeeTitleInteger = 1;
                for (int b = 0; b < employeeCount; b++) {
                    View employeeContainerLayout = getLayoutInflater().inflate(R.layout.tradesmen_entry, null);
                    TextView employeeTitle = employeeContainerLayout.findViewById(R.id.employeeTextView);
                    Button employeeDateButton = employeeContainerLayout.findViewById(R.id.chooseDateBtn);
                    DatePickerHelper.initDateButton(employeeDateButton, SiteSpecificRiskAssessmentActivity.this);
                    employeeTitle.setText("Employee " + employeeTitleInteger);
                    binding.enterTradesmenContainerLayout.addView(employeeContainerLayout);
                    employeeTitleInteger++;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.submitDocumentBtn.setVisibility(View.VISIBLE);
            }
        });
        
        binding.submitDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF();
            }
        });


    }

    private void generatePDF() {


        int documentHeight = 842;
        int documentWidth = 595;
        int borderMargin = 40;

        PdfDocument SiteRiskAssessmentDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = SiteRiskAssessmentDocument.startPage(documentPageInfo);


        Canvas canvas = documentPage.getCanvas();
        canvas.rotate(90);


//        //Set Pinnacle Power banner at top of document
//        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.banner_abn);
//        Rect pinnacleBannerRect = new Rect(30, 10, documentWidth - 30, 160);
//        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);
//
//
//        //Set dimensions of blank checkmark drawable
//        Drawable checkedBlankDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_outline_blank_24);
//
//        //Set dimensions of blank checkmark drawable
//        Drawable checkedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_24);
//
//        //Set text size and colour for regular strings
//        Paint textPaint = new Paint();
//        textPaint.setColor(Color.BLACK);
//        textPaint.setTextSize(10);
//
//        //Set text and size for bold text
//        Paint boldPaint = new Paint();
//        boldPaint.setColor(Color.BLACK);
//        boldPaint.setTextSize(12);
//        Typeface boldTypeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
//        boldPaint.setTypeface(boldTypeFace);
//
//        //Set black margin border dimensions and colour around document
//        Paint borderPaint = new Paint();
//        borderPaint.setStyle(Paint.Style.STROKE);
//        borderPaint.setColor(Color.BLACK);
//        borderPaint.setStrokeWidth(0.5F);
//
//
//        canvas.drawText("Address: " + binding.addressEditText.getText().toString(), 40, 180, textPaint);
//        canvas.drawText("Date: " + binding.visualDateBtn.getText().toString(), 450, 180, textPaint);
//
//        float tableRowHeight = 25;
//        float tableRowInitialY = 215;
//        float columnInitialY = 190;
//
//        float columnLine1X = 450;
//        float columnLine2X = 485;
//        float columnLine3X = 520;
//
//
//        //Draw rect table row for inspection activity and checkbox answers
//        RectF lineStokeRectf = new RectF(borderMargin, 190, documentWidth - borderMargin, 190 + tableRowHeight);
//        canvas.drawRect(lineStokeRectf, borderPaint);
//
//
//        //Establish headers for table rows
//        canvas.drawText("INSPECTION ACTIVITY", 160, 205, boldPaint);
//        canvas.drawText("Yes", columnLine1X + 7, 205, boldPaint);
//        canvas.drawText("No", columnLine2X + 7, 205, boldPaint);
//        canvas.drawText("NA", columnLine3X + 7, 205, boldPaint);
//
//        //Create row with data from user input
//
//        for (int i = 0; i < 19; i++) {
//
//            View rowView = binding.tableLayoutCheckboxes.getChildAt(i);
//            if (rowView instanceof TableRow) {
//                TableRow row = (TableRow) rowView;
//                int checkedCheckboxIndex = -1;
//
//                for (int a = 1; a < row.getChildCount(); a++) {
//                    View rowChildView = row.getChildAt(a);
//                    if (rowChildView instanceof CheckBox) {
//                        CheckBox checkBox = (CheckBox) rowChildView;
//
//
//                        boolean isChecked = checkBox.isChecked();
//                        if (isChecked) {
//                            checkedCheckboxIndex = a - 1;
//                            break;
//                        }
//
//                    }
//
//                }
//
//                if (checkedCheckboxIndex >= 0 && checkedCheckboxIndex <= 2) {
//                    TextView textView = (TextView) row.getChildAt(0);
//                    String text = textView.getText().toString();
//                    Log.d("Testing", "Question: " + text + " Checked Checkbox Index: " + checkedCheckboxIndex);
//                    //Print checked drawable dependent on which checkbox is pressed
//                    canvas.drawText(text, borderMargin + 5, tableRowInitialY + 15, textPaint);
//                    switch (checkedCheckboxIndex) {
//
//                        //Yes is pressed
//                        case 0:
//                            checkedDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialY + 2), (int) (columnLine2X - 5), (int) (tableRowInitialY + 23));
//                            checkedDrawable.draw(canvas);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialY + 2), (int) (columnLine3X - 5), (int) (tableRowInitialY + 23));
//                            checkedBlankDrawable.draw(canvas);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialY + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialY + 23));
//                            checkedBlankDrawable.draw(canvas);
//                            break;
//
//                        //No is pressed
//                        case 1:
//                            checkedDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialY + 2), (int) (columnLine3X - 5), (int) (tableRowInitialY + 23));
//                            checkedDrawable.draw(canvas);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialY + 2), (int) (columnLine2X - 5), (int) (tableRowInitialY + 23));
//                            checkedBlankDrawable.draw(canvas);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialY + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialY + 23));
//                            checkedBlankDrawable.draw(canvas);
//                            break;
//
//                        //NA is pressed
//                        case 2:
//                            checkedDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialY + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialY + 23));
//                            checkedDrawable.draw(canvas);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialY + 2), (int) (columnLine2X - 5), (int) (tableRowInitialY + 23));
//                            checkedBlankDrawable.draw(canvas);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialY + 2), (int) (columnLine3X - 5), (int) (tableRowInitialY + 23));
//                            checkedBlankDrawable.draw(canvas);
//                            break;
//                    }
//
//
//                }
//
//                //Draw left line of table row
//                canvas.drawLine(borderMargin, tableRowInitialY, borderMargin, tableRowInitialY + tableRowHeight, borderPaint);
//                //Draw right line of table row
//                canvas.drawLine(documentWidth - borderMargin, tableRowInitialY, documentWidth - borderMargin, tableRowInitialY + tableRowHeight, borderPaint);
//                //Draw bottom line of table row
//                canvas.drawLine(borderMargin, tableRowInitialY + tableRowHeight, documentWidth - borderMargin, tableRowInitialY + tableRowHeight, borderPaint);
//
//
//                //Draw columns
//                canvas.drawLine(columnLine1X, columnInitialY, columnLine1X, columnInitialY + tableRowHeight, borderPaint);
//                canvas.drawLine(columnLine2X, columnInitialY, columnLine2X, columnInitialY + tableRowHeight, borderPaint);
//                canvas.drawLine(columnLine3X, columnInitialY, columnLine3X, columnInitialY + tableRowHeight, borderPaint);
//
//                tableRowInitialY += tableRowHeight;
//                columnInitialY += 25;
//            }
//        }
//        //Draw last set of column lines for final entry
//        canvas.drawLine(columnLine1X, columnInitialY, columnLine1X, columnInitialY + tableRowHeight, borderPaint);
//        canvas.drawLine(columnLine2X, columnInitialY, columnLine2X, columnInitialY + tableRowHeight, borderPaint);
//        canvas.drawLine(columnLine3X, columnInitialY, columnLine3X, columnInitialY + tableRowHeight, borderPaint);
//
//        visualInspectionDocument.finishPage(documentPage);
//
//
//        //Start page 2 to display further user input
//        PdfDocument.PageInfo documentPageInfo2 = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 2).create();
//        PdfDocument.Page documentPage2 = visualInspectionDocument.startPage(documentPageInfo2);
//
//        Canvas canvas2 = documentPage2.getCanvas();
//
//        //Set Pinnacle Power banner at top of document
//        canvas2.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);
//
//        float tableRowInitialYPage2 = 215;
//        float columnInitialYPage2 = 190;
//
//        //Establish headers for table rows
//        canvas2.drawText("ROOM BY ROOM INSPECTION", 150, 205, boldPaint);
//        canvas2.drawText("Yes", columnLine1X + 7, 205, boldPaint);
//        canvas2.drawText("No", columnLine2X + 7, 205, boldPaint);
//        canvas2.drawText("NA", columnLine3X + 7, 205, boldPaint);
//
//        canvas2.drawRect(lineStokeRectf, borderPaint);
//
//
//        for (int i = 20; i < 37; i++) {
//
//            View rowView = binding.tableLayoutCheckboxes.getChildAt(i);
//            if (rowView instanceof TableRow) {
//                TableRow row = (TableRow) rowView;
//                int checkedCheckboxIndex = -1;
//
//                for (int a = 1; a < row.getChildCount(); a++) {
//                    View rowChildView = row.getChildAt(a);
//                    if (rowChildView instanceof CheckBox) {
//                        CheckBox checkBox = (CheckBox) rowChildView;
//
//
//                        boolean isChecked = checkBox.isChecked();
//                        if (isChecked) {
//                            checkedCheckboxIndex = a - 1;
//                            break;
//                        }
//
//                    }
//
//                }
//
//                if (checkedCheckboxIndex >= 0 && checkedCheckboxIndex <= 2) {
//                    TextView textView = (TextView) row.getChildAt(0);
//                    String text = textView.getText().toString();
//                    //Print checked drawable dependent on which checkbox is pressed
//                    canvas2.drawText(text, borderMargin + 5, tableRowInitialYPage2 + 15, textPaint);
//                    switch (checkedCheckboxIndex) {
//
//                        //Yes is pressed
//                        case 0:
//                            checkedDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine2X - 5), (int) (tableRowInitialYPage2 + 23));
//                            checkedDrawable.draw(canvas2);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine3X - 5), (int) (tableRowInitialYPage2 + 23));
//                            checkedBlankDrawable.draw(canvas2);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialYPage2 + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialYPage2 + 23));
//                            checkedBlankDrawable.draw(canvas2);
//                            break;
//
//                        //No is pressed
//                        case 1:
//                            checkedDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine3X - 5), (int) (tableRowInitialYPage2 + 23));
//                            checkedDrawable.draw(canvas2);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine2X - 5), (int) (tableRowInitialYPage2 + 23));
//                            checkedBlankDrawable.draw(canvas2);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialYPage2 + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialYPage2 + 23));
//                            checkedBlankDrawable.draw(canvas2);
//                            break;
//
//                        //NA is pressed
//                        case 2:
//                            checkedDrawable.setBounds((int) (columnLine3X + 5), (int) (tableRowInitialYPage2 + 2), documentWidth - borderMargin - 5, (int) (tableRowInitialYPage2 + 23));
//                            checkedDrawable.draw(canvas2);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine1X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine2X - 5), (int) (tableRowInitialYPage2 + 23));
//                            checkedBlankDrawable.draw(canvas2);
//
//                            checkedBlankDrawable.setBounds((int) (columnLine2X + 5), (int) (tableRowInitialYPage2 + 2), (int) (columnLine3X - 5), (int) (tableRowInitialYPage2 + 23));
//                            checkedBlankDrawable.draw(canvas2);
//                            break;
//                    }
//
//
//                }
//
//                //Draw left line of table row
//                canvas2.drawLine(borderMargin, tableRowInitialYPage2, borderMargin, tableRowInitialYPage2 + tableRowHeight, borderPaint);
//                //Draw right line of table row
//                canvas2.drawLine(documentWidth - borderMargin, tableRowInitialYPage2, documentWidth - borderMargin, tableRowInitialYPage2 + tableRowHeight, borderPaint);
//                //Draw bottom line of table row
//                canvas2.drawLine(borderMargin, tableRowInitialYPage2 + tableRowHeight, documentWidth - borderMargin, tableRowInitialYPage2 + tableRowHeight, borderPaint);
//
//
//                //Draw columns
//                canvas2.drawLine(columnLine1X, columnInitialYPage2, columnLine1X, columnInitialYPage2 + tableRowHeight, borderPaint);
//                canvas2.drawLine(columnLine2X, columnInitialYPage2, columnLine2X, columnInitialYPage2 + tableRowHeight, borderPaint);
//                canvas2.drawLine(columnLine3X, columnInitialYPage2, columnLine3X, columnInitialYPage2 + tableRowHeight, borderPaint);
//
//                tableRowInitialYPage2 += tableRowHeight;
//                columnInitialYPage2 += 25;
//            }
//        }
//        //Draw last set of column lines for final entry
//        canvas2.drawLine(columnLine1X, columnInitialYPage2, columnLine1X, columnInitialYPage2 + tableRowHeight, borderPaint);
//        canvas2.drawLine(columnLine2X, columnInitialYPage2, columnLine2X, columnInitialYPage2 + tableRowHeight, borderPaint);
//        canvas2.drawLine(columnLine3X, columnInitialYPage2, columnLine3X, columnInitialYPage2 + tableRowHeight, borderPaint);
//
//
//        visualInspectionDocument.finishPage(documentPage2);
//
//        //Output PDF
//        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String fileName = "VisualInspectionDocument.pdf";
//        File file = new File(fileDir, fileName);
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            visualInspectionDocument.writeTo(fileOutputStream);
//            visualInspectionDocument.close();
//            fileOutputStream.close();
//            Toast.makeText(this, "Outputted to PDF", Toast.LENGTH_SHORT).show();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void digitalSignatureOnPressed(View view) {
        Intent intent = new Intent(SiteSpecificRiskAssessmentActivity.this, digitalSignatureView.class);
        signatureActivityResultLauncher.launch(intent);
    }
}