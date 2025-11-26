package com.example.demo.utils;

import android.widget.TextView;

import io.noties.markwon.Markwon;

/**
 * Markdown文本渲染器实现类
 * 实现TextRenderer接口，使用Markwon库渲染Markdown格式的文本
 * 支持常见的Markdown语法，如标题、列表、加粗、斜体等
 */
public class MarkdownTextRenderer implements TextRenderer {
    // Markwon实例，用于解析和渲染Markdown
    private final Markwon markwon;

    /**
     * 构造函数
     * @param markwon Markwon实例
     */
    public MarkdownTextRenderer(Markwon markwon) {
        this.markwon = markwon;
    }

    /**
     * 渲染Markdown文本到TextView
     * @param textView 目标TextView控件
     * @param text 要渲染的Markdown格式文本
     */
    @Override
    public void renderText(TextView textView, String text) {
        // 使用Markwon将Markdown文本渲染为Spanned并设置到TextView
        markwon.setMarkdown(textView, text);
    }
}