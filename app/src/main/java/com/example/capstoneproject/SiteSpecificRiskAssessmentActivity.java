package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstoneproject.databinding.ActivitySiteSpecificRiskAssessmentBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



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

    ActivitySiteSpecificRiskAssessmentBinding binding;

    private int hazardCount = 0;
    private int employeeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteSpecificRiskAssessmentBinding.inflate(getLayoutInflater());
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

        binding.submitDocumentBtn.setOnClickListener(view -> generatePDF());


    }

    private void generatePDF() {


//        int documentHeight = 842;
//        int documentWidth = 595;
//        int borderMargin = 40;

        int documentHeight = 595;
        int documentWidth = 842;
        int borderMargin = 40;

        PdfDocument SiteRiskAssessmentDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = SiteRiskAssessmentDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();




        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.banner_abn);
        Rect pinnacleBannerRect = new Rect(documentWidth /2 - 265, 0, documentWidth /2 + 265, 105);
        canvas.drawBitmap(pinnacleBannerBitmap, null, pinnacleBannerRect, null);

        //Set text size and colour for regular strings
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(10);

        //Set text and size for bold regular text
        Paint boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setTextSize(10);
        Typeface boldTypeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        boldPaint.setTypeface(boldTypeFace);

        //Set text and size for bold title text
        Paint boldTitlePaint = new Paint();
        boldTitlePaint.setColor(Color.BLACK);
        boldTitlePaint.setTextSize(12);
        Typeface boldTitleTypeFace = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        boldTitlePaint.setTypeface(boldTitleTypeFace);

        //Set black margin border dimensions and colour around document
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(0.5F);


        canvas.drawText("SITE SPECIFIC RISK ASSESSMENT FORM", borderMargin, 130, boldTitlePaint);
        canvas.drawText("Job Number: " + binding.jobNumberEditText.getText().toString(), borderMargin, 145, textPaint);
        canvas.drawText("Workplace Location (Address): " + binding.addressEditText.getText().toString(), borderMargin, 160, textPaint);
        canvas.drawText("Form Completed by: " + binding.tradesmenEditText.getText().toString(), borderMargin, 175, textPaint);

        canvas.drawText("Date: " + binding.chooseDateBtn1.getText().toString(), documentWidth/2, 145, textPaint);
        canvas.drawText("Post Code: " + binding.postCodeEditText.getText().toString(), documentWidth/2, 160, textPaint);
        canvas.drawText("State: " + binding.stateEditText.getText().toString(), documentWidth/2, 175, textPaint);

        //Retrieve digital signature bitmap and set dimensions
        canvas.drawText("Signed: ", documentWidth/2 + 100, 145, textPaint);
        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
        Rect signatureRect = new Rect(documentWidth/2 + 140, 145, 650, 175);
        canvas.drawBitmap(signatureBitmap, null, signatureRect, null);

        canvas.drawText("All persons in the work party must participate in the risk assessment and sign this form.", borderMargin, 205, boldPaint);



        //Draw rect table row for inspection activity and checkbox answers
        RectF lineStokeRectf = new RectF(borderMargin, 210, documentWidth - borderMargin, 500);
        canvas.drawRect(lineStokeRectf, borderPaint);

        //Create titles for table layout
        canvas.drawText("SWMS", 50, 235, boldPaint);
        canvas.drawText("Task", 100, 235, boldPaint);
        canvas.drawText("Hazards", 250, 235, boldPaint);
        canvas.drawText("Risks", 350, 235, boldPaint);
        canvas.drawText("Risk Rating", 450, 235, boldPaint);
        canvas.drawText("Controls", 510, 235, boldPaint);

        String text1 = "Heirarchy of Control";
        String text2 = "Residual Risk Rating";

        int text1Y = 225;
        String[] lines1 = text1.split(" ");
        for (String line : lines1) {
            canvas.drawText(line, 680, text1Y, boldPaint);
            text1Y += boldPaint.getTextSize();
        }
        int text2Y = 225;
        String[] lines2 = text2.split(" ");
        for (String line : lines2) {
            canvas.drawText(line, 740, text2Y, boldPaint);
            text2Y += boldPaint.getTextSize();
        }

        float tableRowHeight = 20;
        float tableRowInitialY = 250;
        float columnInitialY = 190;

        float columnLine1X = 95;
        float columnLine2X = 245;
        float columnLine3X = 345;
        float columnLine4X = 445;
        float columnLine5X = 505;
        float columnLine6X = 675;
        float columnLine7X = 735;

        canvas.drawLine(borderMargin, tableRowInitialY, documentWidth - borderMargin, tableRowInitialY, borderPaint);

        canvas.drawLine(columnLine1X, 210, columnLine1X, 250, borderPaint);
        canvas.drawLine(columnLine2X, 210, columnLine2X, 250, borderPaint);
        canvas.drawLine(columnLine3X, 210, columnLine3X, 250, borderPaint);
        canvas.drawLine(columnLine4X, 210, columnLine4X, 250, borderPaint);
        canvas.drawLine(columnLine5X, 210, columnLine5X, 250, borderPaint);
        canvas.drawLine(columnLine6X, 210, columnLine6X, 250, borderPaint);
        canvas.drawLine(columnLine7X, 210, columnLine7X, 250, borderPaint);


        //Collect EditText inputs for each hazardContainer
        float lineColumnY = 250;
        float textY = 262;

        for (int i = 0; i < binding.enterHazardContainerLayout.getChildCount(); i++) {
            LinearLayout hazardLinearLayout = (LinearLayout) binding.enterHazardContainerLayout.getChildAt(i);

            EditText SWMNSEditText = hazardLinearLayout.findViewById(R.id.SWMSEditText);
            EditText taskEditText = hazardLinearLayout.findViewById(R.id.taskEditText);
            EditText hazardEditText = hazardLinearLayout.findViewById(R.id.hazardEntryEditText);
            EditText riskEditText = hazardLinearLayout.findViewById(R.id.riskEditText);
            EditText riskRatingEditText = hazardLinearLayout.findViewById(R.id.rRatingEditText);
            EditText controlEditText = hazardLinearLayout.findViewById(R.id.controlEditText);
            EditText heirarchyOfControlEditText = hazardLinearLayout.findViewById(R.id.heirarchyEditText);
            EditText residualRiskRatingEditText = hazardLinearLayout.findViewById(R.id.residualRatingEditText);

            canvas.drawText(SWMNSEditText.getText().toString(), 50, textY, textPaint);
            canvas.drawText(taskEditText.getText().toString(), 100, textY, textPaint);
            canvas.drawText(hazardEditText.getText().toString(), 250, textY, textPaint);
            canvas.drawText(riskEditText.getText().toString(), 350, textY, textPaint);
            canvas.drawText(riskRatingEditText.getText().toString(), 450, textY, textPaint);
            canvas.drawText(controlEditText.getText().toString(), 510, textY, textPaint);
            canvas.drawText(heirarchyOfControlEditText.getText().toString(), 680, textY, textPaint);
            canvas.drawText(residualRiskRatingEditText.getText().toString(), 740, textY, textPaint);

            canvas.drawLine(columnLine1X, lineColumnY, columnLine1X, lineColumnY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine2X, lineColumnY, columnLine2X, lineColumnY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine3X, lineColumnY, columnLine3X, lineColumnY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine4X, lineColumnY, columnLine4X, lineColumnY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine5X, lineColumnY, columnLine5X, lineColumnY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine6X, lineColumnY, columnLine6X, lineColumnY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine7X, lineColumnY, columnLine7X, lineColumnY + tableRowHeight, borderPaint);

            canvas.drawLine(borderMargin, lineColumnY + tableRowHeight, documentWidth - borderMargin, lineColumnY + tableRowHeight, borderPaint);

            textY += tableRowHeight;
            lineColumnY += tableRowHeight;
        }






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
        SiteRiskAssessmentDocument.finishPage(documentPage);

        //Output PDF
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "SiteSpecificRiskAssessment.pdf";
        File file = new File(fileDir, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            SiteRiskAssessmentDocument.writeTo(fileOutputStream);
            SiteRiskAssessmentDocument.close();
            fileOutputStream.close();
            Toast.makeText(this, "Outputted to PDF", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void digitalSignatureOnPressed(View view) {
        Intent intent = new Intent(SiteSpecificRiskAssessmentActivity.this, digitalSignatureView.class);
        signatureActivityResultLauncher.launch(intent);
    }
}
