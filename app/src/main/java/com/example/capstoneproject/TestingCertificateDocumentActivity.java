package com.example.capstoneproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.capstoneproject.databinding.ActivityMainBinding;
import com.example.capstoneproject.databinding.ActivityTestingCertificateDocumentBinding;

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
                compileDocument();
            }
        });
    }

    private void compileDocument() {
        PdfDocument testingCertificateDocument = new PdfDocument();
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
                Log.d("DateSetListener", "onDateSet: Setting text to " + date);
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