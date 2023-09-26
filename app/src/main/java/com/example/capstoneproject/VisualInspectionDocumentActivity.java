package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.capstoneproject.databinding.ActivityVisualInspectionDocumentBinding;

public class VisualInspectionDocumentActivity extends AppCompatActivity {

    private ActivityVisualInspectionDocumentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVisualInspectionDocumentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatePickerHelper.initDateButton(binding.visualDateBtn, this);
    }
}