package com.example.demo.utils;

import android.widget.TextView;

/**
 * 纯文本渲染器实现类
 * 实现TextRenderer接口，提供简单的纯文本渲染功能
 * 不进行任何特殊格式化，直接显示原始文本
 */
public class PlainTextRenderer implements TextRenderer {
    /**
     * 渲染纯文本到TextView
     * @param textView 目标TextView控件
     * @param text 要渲染的文本内容
     */
    @Override
    public void renderText(TextView textView, String text) {
        textView.setText(text);
    }
}