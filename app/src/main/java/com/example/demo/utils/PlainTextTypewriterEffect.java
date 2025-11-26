package com.example.demo.utils;

import android.widget.TextView;

public class PlainTextTypewriterEffect extends BaseTypewriterEffect {
    public PlainTextTypewriterEffect(TextView textView, String fullText, long delayMillis) {
        super(textView, fullText, delayMillis, new PlainTextRenderer());
    }
}