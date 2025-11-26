package com.example.demo.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public abstract class BaseTypewriterEffect {
    protected final Handler handler;
    protected final TextView textView;
    protected final String fullText;
    protected final long delayMillis;
    protected final TextRenderer textRenderer;
    protected int currentIndex = 0;
    protected Runnable onCompleteCallback;

    public BaseTypewriterEffect(TextView textView, String fullText, long delayMillis, TextRenderer textRenderer) {
        this.textView = textView;
        this.fullText = fullText;
        this.delayMillis = delayMillis;
        this.textRenderer = textRenderer;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    public void start() {
        textView.setText("");
        currentIndex = 0;
        typeText();
    }

    protected void typeText() {
        if (currentIndex < fullText.length()) {
            textView.append(String.valueOf(fullText.charAt(currentIndex)));
            currentIndex++;
            handler.postDelayed(this::typeText, delayMillis);
        } else {
            textRenderer.renderText(textView, fullText);
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }
            onComplete();
        }
    }

    public void cancel() {
        handler.removeCallbacksAndMessages(null);
        textRenderer.renderText(textView, fullText);
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
        onComplete();
    }
    
    protected void onComplete() {
    }
}