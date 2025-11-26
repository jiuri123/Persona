package com.example.demo.utils;

import android.widget.TextView;

import io.noties.markwon.Markwon;

/**
 * Markdown文本渲染器实现
 * 使用Markwon库将Markdown文本渲染到TextView中
 */
public class MarkdownTextRenderer implements TextRenderer {
    private final Markwon markwon;

    /**
     * 构造函数
     * @param markwon Markwon实例，用于Markdown渲染
     */
    public MarkdownTextRenderer(Markwon markwon) {
        this.markwon = markwon;
    }

    @Override
    public void renderText(TextView textView, String text) {
        markwon.setMarkdown(textView, text);
    }
}