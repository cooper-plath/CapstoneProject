package com.example.capstoneproject;

import android.graphics.Bitmap;

public class BitmapSingleton {
    private static BitmapSingleton instance;
    private Bitmap signatureBitmap;

    private BitmapSingleton() {
        // Private constructor to prevent instantiation
    }

    public static BitmapSingleton getInstance() {
        if (instance == null) {
            instance = new BitmapSingleton();
        }
        return instance;
    }

    public Bitmap getSignatureBitmap() {
        return signatureBitmap;
    }

    public void setSignatureBitmap(Bitmap signatureBitmap) {
        this.signatureBitmap = signatureBitmap;
    }
}

