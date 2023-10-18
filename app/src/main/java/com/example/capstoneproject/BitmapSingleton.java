package com.example.capstoneproject;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class BitmapSingleton {
    private static BitmapSingleton instance;
    private List<Bitmap> signatureBitmaps;
    private Bitmap signatureBitmap;

    private BitmapSingleton() {
        signatureBitmaps = new ArrayList<>();
    }

    public static BitmapSingleton getInstance() {
        if (instance == null) {
            instance = new BitmapSingleton();
        }
        return instance;
    }

    public List<Bitmap> getMultipleSignatureBitmaps() {
        return signatureBitmaps;
    }

    public void addSignatureBitmap(Bitmap signatureBitmap) {
        this.signatureBitmap = signatureBitmap;
        signatureBitmaps.add(signatureBitmap);
    }

    public Bitmap getSignatureBitmap() {
        return signatureBitmap;
    }

    public void clearSignatureBitmap() {
        signatureBitmap = null;
    }

    public void clearMultipleSignatureBitmaps() {
        signatureBitmaps.clear();
    }
}