package com.example.demo.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/**
 * 打字机效果基类
 * 实现文本逐字显示的动画效果
 * 使用Handler和Looper在主线程中执行动画
 */
public abstract class BaseTypewriterEffect {
    // 主线程Handler，用于执行延迟任务
    protected final Handler handler;
    
    // 目标TextView控件
    protected final TextView textView;
    
    // 要显示的完整文本
    protected final String fullText;
    
    // 每个字符显示的延迟时间（毫秒）
    protected final long delayMillis;
    
    // 文本渲染器，用于最终渲染文本
    protected final TextRenderer textRenderer;
    
    // 当前显示的字符索引
    protected int currentIndex = 0;
    
    // 动画完成回调
    protected Runnable onCompleteCallback;

    /**
     * 构造函数
     * @param textView 目标TextView控件
     * @param fullText 要显示的完整文本
     * @param delayMillis 每个字符显示的延迟时间
     * @param textRenderer 文本渲染器
     */
    public BaseTypewriterEffect(TextView textView, String fullText, long delayMillis, TextRenderer textRenderer) {
        this.textView = textView;
        this.fullText = fullText;
        this.delayMillis = delayMillis;
        this.textRenderer = textRenderer;
        // 使用主线程Looper创建Handler，确保UI更新在主线程执行
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置动画完成回调
     * @param callback 动画完成时执行的回调
     */
    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    /**
     * 开始打字机效果
     */
    public void start() {
        textView.setText("");
        currentIndex = 0;
        typeText();
    }

    /**
     * 逐字显示文本
     */
    protected void typeText() {
        if (currentIndex < fullText.length()) {
            // 添加下一个字符
            textView.append(String.valueOf(fullText.charAt(currentIndex)));
            currentIndex++;
            // 延迟后继续显示下一个字符
            handler.postDelayed(this::typeText, delayMillis);
        } else {
            // 动画完成，使用渲染器最终渲染文本
            textRenderer.renderText(textView, fullText);
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
            // 调用子类实现的完成方法
            onComplete();
        }
    }

    /**
     * 取消打字机效果
     */
    public void cancel() {
        // 移除所有待执行的回调
        handler.removeCallbacksAndMessages(null);
        // 直接显示完整文本
        textRenderer.renderText(textView, fullText);
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
        // 调用子类实现的完成方法
        onComplete();
    }
    
    /**
     * 动画完成时的钩子方法，子类可重写
     */
    protected void onComplete() {
    }
}