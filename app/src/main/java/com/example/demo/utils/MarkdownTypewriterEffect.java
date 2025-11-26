package com.example.demo.utils;

import android.widget.TextView;

import io.noties.markwon.Markwon;

public class MarkdownTypewriterEffect extends BaseTypewriterEffect {
    public MarkdownTypewriterEffect(TextView textView, String fullText, long delayMillis, Markwon markwon) {
        super(textView, fullText, delayMillis, new MarkdownTextRenderer(markwon));
    }
}