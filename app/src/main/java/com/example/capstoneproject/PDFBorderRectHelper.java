package com.example.capstoneproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

//Draw lined rectangle in PDF
public class PDFBorderRectHelper {
    public static void drawLinedRect(Canvas canvas, float strokeWidth, float documentStartY, float documentEndY) {
        float borderLeft = 40;
        float borderRight = 555;

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(strokeWidth);

        canvas.drawRect(borderLeft, documentStartY, borderRight, documentEndY, borderPaint);

    }
    public static void drawStokedLine(Canvas canvas, float strokeWidth, float lineYValue) {
        float startX = 40;
        float endX = 555;

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(strokeWidth);

        canvas.drawLine(startX, lineYValue, endX, lineYValue, borderPaint);
    }

    public static StaticLayout textBoxLayout(String text, TextPaint paint, int textBoxWidth) {
        return new StaticLayout(text, paint, textBoxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
        );
    }
}
