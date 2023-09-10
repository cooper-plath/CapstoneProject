package com.example.capstoneproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.capstoneproject.databinding.ActivityDigitalSignatureViewBinding;
import com.example.capstoneproject.databinding.ActivityMainBinding;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearCanvas();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = signatureView.getSignatureBitmap();
                if (signatureBitmap != null) {
                    BitmapSingleton.getInstance().setSignatureBitmap(signatureBitmap);
                    setResult(RESULT_OK);
                    finish();
                }
//                Intent intentData = new Intent();
//                intentData.putExtra("signatureBitmap", signatureBitmap);


//                Bitmap bitmap = signatureView.getSignatureBitmap();
//                Intent intent = new Intent(digitalSignatureView.this, TestingCertificateDocumentActivity.class);
//                intent.putExtra("signatureBitmap", bitmap);
            }
        });
    }
}

