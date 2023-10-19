package com.example.capstoneproject;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.capstoneproject.databinding.ActivitySiteSpecificRiskAssessmentBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class SiteSpecificRiskAssessmentActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> signatureActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int result = activityResult.getResultCode();
                    if (result == RESULT_OK) {
                        assert activityResult.getData() != null;
                        int employeeId = activityResult.getData().getIntExtra("employeeId", -1);
                        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
                        if (signatureBitmap != null) {
                            if (employeeId == 0) {
                                binding.signatureImageView.setVisibility(View.VISIBLE);
                            } else {
                                handleSignatureResult(employeeId);
                            }
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

        BitmapSingleton.getInstance().clearMultipleSignatureBitmaps();

        //Remove Hazard Container
        binding.enterHazardContainerLayout.removeAllViews();
        binding.enterTradesmenContainerLayout.removeAllViews();

        DatePickerHelper.initDateButton(binding.chooseDateBtn1, this);

        binding.tradesmenCountEditText.setVisibility(View.INVISIBLE);
        binding.submitDocumentBtn.setVisibility(View.INVISIBLE);
        binding.certifyCheckbox.setVisibility(View.INVISIBLE);

        //Add text listener for when user inputs number of hazards for site
        binding.hazardCountEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                try {
                    hazardCount = Integer.parseInt(binding.hazardCountEditText.getText().toString());
                    if (hazardCount < 1) {
                        Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                    } else if (hazardCount > 7) {
                            Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Please enter a integer < 8", Toast.LENGTH_SHORT).show();
                    } else {
                        updateHazardViews(hazardCount);
                        binding.certifyCheckbox.setVisibility(View.VISIBLE);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Integer Input only", Toast.LENGTH_SHORT).show();
                }
            }

            return false;
        });

        //Check if checkbox is clicked and set visibility of next edittext to visible
        binding.certifyCheckbox.setOnClickListener(view -> binding.tradesmenCountEditText.setVisibility(View.VISIBLE));

        //Add text listener for when user inputs number of employees for site
        binding.tradesmenCountEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                try {
                    employeeCount = Integer.parseInt(binding.tradesmenCountEditText.getText().toString());
                    if (employeeCount < 1) {
                        Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Please enter a positive integer", Toast.LENGTH_SHORT).show();
                    } else if (employeeCount > 7) {
                            Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Please enter a integer < 8", Toast.LENGTH_SHORT).show();
                    } else {
                        updateEmployeeViews(employeeCount);
                        binding.submitDocumentBtn.setVisibility(View.VISIBLE);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Integer Input only", Toast.LENGTH_SHORT).show();
                }
            }

            return false;
        });

        try {

        } catch (NumberFormatException e) {
            Toast.makeText(SiteSpecificRiskAssessmentActivity.this, "Integer Input only", Toast.LENGTH_SHORT).show();
        }


        binding.submitDocumentBtn.setOnClickListener(view -> generatePDF());
        binding.digitalSignatureBtn.setOnClickListener(view -> digitalSignatureOnPressed(0));

    }

    @SuppressLint("SetTextI18n")
    private void updateEmployeeViews(int employeeCount) {
        // Clear existing views
        binding.enterTradesmenContainerLayout.removeAllViews();

        int employeeIDInteger = 1;
        for (int b = 0; b < employeeCount; b++) {
            @SuppressLint("InflateParams") View employeeContainerLayout = getLayoutInflater().inflate(R.layout.tradesmen_entry, null);
            TextView employeeTitle = employeeContainerLayout.findViewById(R.id.employeeTextView);
            Button employeeDateButton = employeeContainerLayout.findViewById(R.id.employeeDateBtn);
            Button employeeSignatureButton = employeeContainerLayout.findViewById(R.id.digitalSignatureBtn1);
            ImageView signatureImageView = employeeContainerLayout.findViewById(R.id.employeeSignatureImageView);

            DatePickerHelper.initDateButton(employeeDateButton, SiteSpecificRiskAssessmentActivity.this);

            employeeTitle.setText("Employee " + employeeIDInteger);

            String employeeSignatureImageViewId = "signatureImageView" + employeeIDInteger;

            signatureImageView.setId(View.generateViewId());
            signatureImageView.setTag(employeeSignatureImageViewId);

            binding.enterTradesmenContainerLayout.addView(employeeContainerLayout);


            int finalEmployeeIDInteger = employeeIDInteger;
            employeeSignatureButton.setOnClickListener(view -> digitalSignatureOnPressed(finalEmployeeIDInteger));
            employeeIDInteger++;
        }

    }

    //If user clicks on digitalSignatureButton
    public void digitalSignatureOnPressed(int employeeIDInteger) {
        Intent intent = new Intent(SiteSpecificRiskAssessmentActivity.this, digitalSignatureView.class);
            intent.putExtra("employeeId", employeeIDInteger);
            signatureActivityResultLauncher.launch(intent);
    }

    //Displays checkmark icon once user enters successful digital signature on activity
    public void handleSignatureResult(int employeeId) {
        // Store the signature bitmap in the map
        String signatureImageViewId = "signatureImageView" + employeeId;

        // Display the signature in the corresponding ImageView
        ImageView signatureImageView = binding.enterTradesmenContainerLayout.findViewWithTag(signatureImageViewId);
        signatureImageView.setVisibility(View.VISIBLE);
    }


    @SuppressLint("SetTextI18n")
    private void updateHazardViews(int newHazardCount) {
        // Clear existing views
        binding.enterHazardContainerLayout.removeAllViews();

        int hazardTitleInteger = 1;
        for (int a = 0; a < newHazardCount; a++) {
            @SuppressLint("InflateParams") View hazardContainerLayout = getLayoutInflater().inflate(R.layout.hazard_entry, null);
            TextView hazardTitle = hazardContainerLayout.findViewById(R.id.hazardTitleTextView);
            hazardTitle.setText("Hazard " + hazardTitleInteger);
            binding.enterHazardContainerLayout.addView(hazardContainerLayout);
            hazardTitleInteger++;
        }
    }

    private void generatePDF() {

        //Validate all inputs have been filled out before generating pdf
        if (binding.jobNumberEditText.getText().toString().isEmpty() || binding.addressEditText.getText().toString().isEmpty() || binding.tradesmenEditText.getText().toString().isEmpty() ||
                binding.chooseDateBtn1.getText().toString().equals("Date") || binding.postCodeEditText.getText().toString().isEmpty() || binding.stateEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //Check all inputs in hazard container have been entered
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

            String SWMS = SWMNSEditText.getText().toString();
            String task = taskEditText.getText().toString();
            String hazard = hazardEditText.getText().toString();
            String risk = riskEditText.getText().toString();
            String riskRating = riskRatingEditText.getText().toString();
            String control = controlEditText.getText().toString();
            String hierarchyOfControl = heirarchyOfControlEditText.getText().toString();
            String residualRiskRating = residualRiskRatingEditText.getText().toString();

            if (SWMS.isEmpty() || task.isEmpty() || hazard.isEmpty() || risk.isEmpty() || riskRating.isEmpty() || control.isEmpty() || hierarchyOfControl.isEmpty() || residualRiskRating.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields in the hazard section", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Check all inputs in employee container have been entered
        for (int i = 0; i < binding.enterTradesmenContainerLayout.getChildCount(); i++) {
            RelativeLayout employeeRelativeLayout = (RelativeLayout) binding.enterTradesmenContainerLayout.getChildAt(i);
            EditText employeeNameEditText = employeeRelativeLayout.findViewById(R.id.employeeEditText);
            @SuppressLint("CutPasteId") Button dateBtn = employeeRelativeLayout.findViewById(R.id.employeeDateBtn);
            String dateBtnString = dateBtn.getText().toString();

            if (employeeNameEditText.getText().toString().isEmpty() || dateBtnString.equals("Date")){
                Toast.makeText(this, "Please fill in all required employee names", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        int documentHeight = 595;
        int documentWidth = 842;
        int borderMargin = 40;

        PdfDocument SiteRiskAssessmentDocument = new PdfDocument();
        PdfDocument.PageInfo documentPageInfo = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 1).create();

        PdfDocument.Page documentPage = SiteRiskAssessmentDocument.startPage(documentPageInfo);

        Canvas canvas = documentPage.getCanvas();


        //Set Pinnacle Power banner at top of document
        Bitmap pinnacleBannerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.banner_abn);
        Rect pinnacleBannerRect = new Rect(documentWidth / 2 - 265, 0, documentWidth / 2 + 265, 105);
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

        canvas.drawText("Date: " + binding.chooseDateBtn1.getText().toString(), documentWidth / 2, 145, textPaint);
        canvas.drawText("Post Code: " + binding.postCodeEditText.getText().toString(), documentWidth / 2, 160, textPaint);
        canvas.drawText("State: " + binding.stateEditText.getText().toString(), documentWidth / 2, 175, textPaint);

        //Retrieve digital signature bitmap and set dimensions
        canvas.drawText("Signed: ", documentWidth / 2 + 100, 145, textPaint);
        Bitmap signatureBitmap = BitmapSingleton.getInstance().getSignatureBitmap();
        Rect signatureRect = new Rect(documentWidth / 2 + 140, 145, 650, 175);
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

        String text1 = "Hierarchy of Control";
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

        float tableRowHeight = 15;
        float tableRowInitialY = 250;

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

        //Draw checkbox and certificate string
        Drawable checkedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_check_box_24);
        assert checkedDrawable != null;
        checkedDrawable.setBounds(50, 357,  60, 367);
        checkedDrawable.draw(canvas);

        canvas.drawText("I certify that the above control measures have been implemented and the site is safe.", 67, 365, textPaint);
        canvas.drawText("Worker in charge: " + binding.tradesmenEditText.getText().toString(), 50, 380, textPaint);

        //Employee table
        canvas.drawLine(borderMargin, 385, documentWidth - borderMargin, 385, borderPaint);
        canvas.drawText("Employee Name:", 180, 395, boldPaint);
        canvas.drawText("Signature:", 450, 395, boldPaint);
        canvas.drawText("Date:", 680, 395, boldPaint);
        canvas.drawLine(borderMargin, 400, documentWidth - borderMargin, 400, borderPaint);

        //Draw employee columns

        canvas.drawLine(columnLine4X, 385, columnLine4X, 400, borderPaint);
        canvas.drawLine(columnLine6X, 385, columnLine6X, 400, borderPaint);

        float employeeTextInitalY = 410;
        float employeeColumnInitalY = 400;
        int employeeSignatureIndex = 1;

        //Signature Dimensions
        int signatureLeft = 445;
        int signatureTop = 399;
        int signatureRight = 550;
        int signatureBottom = 423;

        for (int i = 0; i <binding.enterTradesmenContainerLayout.getChildCount(); i++) {
            //Collect information inputted from user
            RelativeLayout employeeRelativeLayout = (RelativeLayout) binding.enterTradesmenContainerLayout.getChildAt(i);
            EditText employeeNameEditText = employeeRelativeLayout.findViewById(R.id.employeeEditText);
            @SuppressLint("CutPasteId") Button employeeDateButton = employeeRelativeLayout.findViewById(R.id.employeeDateBtn);

            //Employee data table
            canvas.drawText(employeeNameEditText.getText().toString(), 50, employeeTextInitalY, textPaint);

            //Retrieve digital signature bitmaps and set dimensions
            List<Bitmap> signatureBitmaps = BitmapSingleton.getInstance().getMultipleSignatureBitmaps();
            Bitmap employeeSignatureBitmap = signatureBitmaps.get(employeeSignatureIndex);


            Rect employeeSignatureRect = new Rect(signatureLeft, signatureTop, signatureRight, signatureBottom);

            canvas.drawBitmap(employeeSignatureBitmap, null, employeeSignatureRect, null);



            canvas.drawText(employeeDateButton.getText().toString(), 680 , employeeTextInitalY, textPaint);

            //Draw columns about input
            canvas.drawLine(columnLine4X, employeeColumnInitalY, columnLine4X, employeeColumnInitalY + tableRowHeight, borderPaint);
            canvas.drawLine(columnLine6X, employeeColumnInitalY, columnLine6X, employeeColumnInitalY + tableRowHeight, borderPaint);
            canvas.drawLine(borderMargin, employeeColumnInitalY + tableRowHeight, documentWidth - borderMargin, employeeColumnInitalY + tableRowHeight, borderPaint);

            employeeTextInitalY += tableRowHeight;
            employeeColumnInitalY += tableRowHeight;
            signatureTop += tableRowHeight;
            signatureBottom += tableRowHeight;
            employeeSignatureIndex += 1;
        }

        //Close Page1
        SiteRiskAssessmentDocument.finishPage(documentPage);

        PdfDocument.PageInfo documentPageInfo2 = new PdfDocument.PageInfo.Builder(documentWidth, documentHeight, 2).create();
        PdfDocument.Page documentPage2 = SiteRiskAssessmentDocument.startPage(documentPageInfo2);

        Canvas canvas2 = documentPage2.getCanvas();

        //Set Pinnacle Power banner at top of document
        Bitmap riskMatrixBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.risk_matrix);
        Rect riskMatrixRect = new Rect(0, 0, documentWidth, documentHeight);
        canvas2.drawBitmap(riskMatrixBitmap, null, riskMatrixRect, null);


        //Close Page2
        SiteRiskAssessmentDocument.finishPage(documentPage2);


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

}
