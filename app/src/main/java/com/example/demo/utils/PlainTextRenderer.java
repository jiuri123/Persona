package com.example.demo.utils;

import android.widget.TextView;

/**
 * 纯文本渲染器实现
 * 直接将文本设置到TextView中
 */
public class PlainTextRenderer implements TextRenderer {
    @Override
    public void renderText(TextView textView, String text) {
        textView.setText(text);
    }
}