package com.example.demo.callback;

import com.example.demo.model.Persona;

/**
 * 关注操作回调接口
 * 用于解耦Adapter和ViewModel，遵循MVVM架构原则
 */
public interface SocialSquareFollowActionListener {
    /**
     * 处理关注按钮点击事件
     * @param persona 被点击的Persona对象
     */
    void onFollowClick(Persona persona);
}