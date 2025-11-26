package com.example.demo.utils;

import android.widget.TextView;

public class PlainTextRenderer implements TextRenderer {
    @Override
    public void renderText(TextView textView, String text) {
        textView.setText(text);
    }
}