package com.example.demo.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/**
 * 打字机效果抽象基类
 * 包含所有公共的打字机效果逻辑，使用TextRenderer接口处理不同的文本渲染方式
 */
public abstract class BaseTypewriterEffect {
    protected final Handler handler;
    protected final TextView textView;
    protected final String fullText;
    protected final long delayMillis;
    protected final TextRenderer textRenderer;
    protected int currentIndex = 0;
    protected Runnable onCompleteCallback;

    /**
     * 构造函数
     * @param textView 要显示文字的TextView
     * @param fullText 完整的文字内容
     * @param delayMillis 每个字符之间的延迟时间(毫秒)
     * @param textRenderer 文本渲染器，负责最终的文本渲染
     */
    public BaseTypewriterEffect(TextView textView, String fullText, long delayMillis, TextRenderer textRenderer) {
        this.textView = textView;
        this.fullText = fullText;
        this.delayMillis = delayMillis;
        this.textRenderer = textRenderer;
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
    protected void typeText() {
        if (currentIndex < fullText.length()) {
            // 添加下一个字符
            textView.append(String.valueOf(fullText.charAt(currentIndex)));
            currentIndex++;

            // 延迟一段时间后继续添加下一个字符
            handler.postDelayed(this::typeText, delayMillis);
        } else {
            // 打字机效果完成，使用文本渲染器渲染最终文本
            textRenderer.renderText(textView, fullText);
            
            // 执行回调
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
            
            // 调用子类的完成方法
            onComplete();
        }
    }

    /**
     * 取消打字机效果，立即显示完整文本
     */
    public void cancel() {
        handler.removeCallbacksAndMessages(null);
        // 使用文本渲染器渲染最终文本
        textRenderer.renderText(textView, fullText);
        
        // 取消时也执行完成回调
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
        
        // 调用子类的完成方法
        onComplete();
    }
    
    /**
     * 子类可重写此方法，在打字机效果完成时执行自定义逻辑
     */
    protected void onComplete() {
        // 默认空实现，子类可重写
    }
}