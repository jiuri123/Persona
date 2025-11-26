package com.example.demo.utils;

import android.widget.TextView;

/**
 * 纯文本打字机效果类
 * 继承BaseTypewriterEffect，实现纯文本的打字机效果
 * 在动画完成后使用PlainTextRenderer渲染最终文本
 */
public class PlainTextTypewriterEffect extends BaseTypewriterEffect {
    /**
     * 构造函数
     * @param textView 目标TextView控件
     * @param fullText 要显示的完整文本
     * @param delayMillis 每个字符显示的延迟时间
     */
    public PlainTextTypewriterEffect(TextView textView, String fullText, long delayMillis) {
        super(textView, fullText, delayMillis, new PlainTextRenderer());
    }
}