package com.example.demo.utils;

import android.widget.TextView;

import io.noties.markwon.Markwon;

public class MarkdownTextRenderer implements TextRenderer {
    private final Markwon markwon;

    public MarkdownTextRenderer(Markwon markwon) {
        this.markwon = markwon;
    }

    @Override
    public void renderText(TextView textView, String text) {
        markwon.setMarkdown(textView, text);
    }
}