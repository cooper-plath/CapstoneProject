package com.example.capstoneproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kyanogen.signatureview.SignatureView;

public class digitalSignatureView extends AppCompatActivity  {

    private SignatureView signatureView;
    private Button doneBtn, clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_digital_signature_view);
        doneBtn = findViewById(R.id.signatureBtnDone);
        clearBtn = findViewById(R.id.signatureBtnClear);
        signatureView = findViewById(R.id.signature_view);

        int employeeId = getIntent().getIntExtra("employeeId", 0);

        clearBtn.setOnClickListener(view -> signatureView.clearCanvas());

        doneBtn.setOnClickListener(view -> {
            Bitmap signatureBitmap = signatureView.getSignatureBitmap();
            if (signatureBitmap != null) {
                BitmapSingleton.getInstance().addSignatureBitmap(signatureBitmap);

                Log.d("digitalSignatureView", "Finishing with employeeId:" + employeeId);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("employeeId", employeeId);
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }
}

