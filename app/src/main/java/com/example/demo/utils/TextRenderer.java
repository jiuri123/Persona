package com.example.demo.utils;

import android.widget.TextView;

/**
 * 文本渲染器接口
 * 定义文本渲染的通用接口，支持不同的文本渲染方式
 * 实现策略模式，允许在运行时切换不同的文本渲染策略
 */
public interface TextRenderer {
    /**
     * 渲染文本到TextView
     * @param textView 目标TextView控件
     * @param text 要渲染的文本内容
     */
    void renderText(TextView textView, String text);
}