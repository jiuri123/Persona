package com.example.demo.utils;

import android.widget.TextView;

import io.noties.markwon.Markwon;

/**
 * Markdown打字机效果类
 * 继承BaseTypewriterEffect，实现Markdown格式的打字机效果
 * 在动画完成后使用MarkdownTextRenderer渲染最终文本
 */
public class MarkdownTypewriterEffect extends BaseTypewriterEffect {
    /**
     * 构造函数
     * @param textView 目标TextView控件
     * @param fullText 要显示的完整Markdown文本
     * @param delayMillis 每个字符显示的延迟时间
     * @param markwon Markwon实例，用于渲染Markdown
     */
    public MarkdownTypewriterEffect(TextView textView, String fullText, long delayMillis, Markwon markwon) {
        super(textView, fullText, delayMillis, new MarkdownTextRenderer(markwon));
    }
}