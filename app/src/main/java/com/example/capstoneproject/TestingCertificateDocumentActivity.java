package com.example.capstoneproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class TestingCertificateDocumentActivity extends AppCompatActivity {

    private Button chooseDateButton;

    ActivityResultLauncher<Intent> signatureActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    int result = activityResult.getResultCode();
                    Intent data = activityResult.getData();

                    if (result == RESULT_OK) {
                        String testString = data.getStringExtra("testString");
                        Log.d("TestString", "TestString*****" + testString);
                    }
                }
            });

    private ImageView signatureImageView;
    private TextView regulationInformationTextView;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_certificate_document);
        initDateButton();
        chooseDateButton = findViewById(R.id.chooseDateBtn);
        regulationInformationTextView = findViewById(R.id.regulationInformationTextView);
        signatureImageView = findViewById(R.id.signatureImageView);

        setRegulationBold();
    }

    private void setRegulationBold() {
        String text = getString(R.string.testCertificateInformation);
        SpannableString ss = new SpannableString(text);
        StyleSpan boldText1 = new StyleSpan(Typeface.BOLD);
        StyleSpan boldText2 = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldText1, 4, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(boldText2, 362, 383, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        regulationInformationTextView.setText(ss);
    }

    private void initDateButton() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = " " + day + " / " + month + " / " + year;
                chooseDateButton.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    public void chooseDateBtn(View view) {datePickerDialog.show();
    }

    public void digitalSignatureOnPressed(View view) {
        Intent intent = new Intent(TestingCertificateDocumentActivity.this, digitalSignatureView.class);
        //startActivityForResult(intent, 1);
        signatureActivityResultLauncher.launch(intent);

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && data != null) {
//            Bundle bundle = data.getExtras();
//            Bitmap signatureBitmap = (Bitmap) bundle.get("signatureBitmap");
//            signatureImageView.setImageBitmap(signatureBitmap);
//        }
//    }

}