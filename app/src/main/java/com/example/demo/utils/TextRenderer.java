package com.example.demo.utils;

import android.widget.TextView;

/**
 * 文本渲染器接口
 * 定义了如何将文本渲染到TextView中的规范
 */
public interface TextRenderer {
    /**
     * 将文本渲染到TextView中
     * @param textView 目标TextView
     * @param text 要渲染的文本
     */
    void renderText(TextView textView, String text);
}