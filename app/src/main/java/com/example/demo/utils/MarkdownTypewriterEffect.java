package com.example.demo;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import io.noties.markwon.Markwon;

/**
 * 支持Markdown渲染的打字机效果工具类
 * 用于逐字显示文本，并在完成后应用Markdown渲染
 */
public class MarkdownTypewriterEffect {
    private final Handler handler;
    private final TextView textView;
    private final String fullText;
    private final long delayMillis;
    private final Markwon markwon;
    private int currentIndex = 0;
    private Runnable onCompleteCallback;

    /**
     * 构造函数
     * @param textView 要显示文字的TextView
     * @param fullText 完整的文字内容
     * @param delayMillis 每个字符之间的延迟时间(毫秒)
     * @param markwon Markwon实例，用于Markdown渲染
     */
    public MarkdownTypewriterEffect(TextView textView, String fullText, long delayMillis, Markwon markwon) {
        this.textView = textView;
        this.fullText = fullText;
        this.delayMillis = delayMillis;
        this.markwon = markwon;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置完成回调
     * @param callback 打字机效果完成时的回调
     */
    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    /**
     * 开始执行打字机效果
     */
    public void start() {
        // 清空TextView的内容
        textView.setText("");
        currentIndex = 0;
        
        // 开始逐字显示
        typeText();
    }

    /**
     * 递归方法，逐字显示文本
     */
    private void typeText() {
        if (currentIndex < fullText.length()) {
            // 添加下一个字符
            textView.append(String.valueOf(fullText.charAt(currentIndex)));
            currentIndex++;

            // 延迟一段时间后继续添加下一个字符
            handler.postDelayed(this::typeText, delayMillis);
        } else {
            // 打字机效果完成，应用Markdown渲染
            markwon.setMarkdown(textView, fullText);
            
            // 执行回调
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
            
            // 如果子类重写了onComplete方法，也调用它
            onComplete();
        }
    }

    /**
     * 取消打字机效果，立即显示完整文本并应用Markdown渲染
     */
    public void cancel() {
        handler.removeCallbacksAndMessages(null);
        // 立即应用Markdown渲染
        markwon.setMarkdown(textView, fullText);
        
        // 取消时也执行完成回调
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
        
        // 如果子类重写了onComplete方法，也调用它
        onComplete();
    }
    
    /**
     * 子类可重写此方法，在打字机效果完成时执行自定义逻辑
     */
    protected void onComplete() {
        // 默认空实现，子类可重写
    }
}