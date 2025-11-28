package com.example.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.demo.databinding.ActivityPostEditorBinding;
import com.example.demo.viewmodel.UserPostCreateViewModel;

/**
 * 发布动态编辑页面
 * 允许用户手动输入或使用AI生成/扩展动态内容
 * 实现了发布动态和取消发布的功能
 */
public class UserPostCreateActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityPostEditorBinding binding;

    // ViewModel，用于管理编辑页面的业务逻辑
    private UserPostCreateViewModel userPostCreateViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使用视图绑定初始化布局
        binding = ActivityPostEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取ViewModel实例
        userPostCreateViewModel = new ViewModelProvider(this).get(UserPostCreateViewModel.class);

        // 设置观察者
        setupObservers();

        // 设置按钮点击事件
        setupButtonListeners();
    }

    /**
     * 设置ViewModel的观察者
     * 观察加载状态、错误信息和AI生成/扩展的结果
     */
    private void setupObservers() {
        // 观察加载状态
        userPostCreateViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            // 加载时禁用所有按钮
            binding.btnAiExpand.setEnabled(!isLoading);
            binding.btnAiGenerate.setEnabled(!isLoading);
            binding.btnPublish.setEnabled(!isLoading);
            binding.btnCancel.setEnabled(!isLoading);
        });

        // 观察错误信息
        userPostCreateViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                userPostCreateViewModel.clearError();
            }
        });

        // 观察AI生成/扩展的结果
        userPostCreateViewModel.getGeneratedContent().observe(this, content -> {
            if (content != null && !content.isEmpty()) {
                binding.etPostContent.setText(content);
            }
        });

        // 观察发布状态
        userPostCreateViewModel.getIsPublished().observe(this, isPublished -> {
            if (isPublished) {
                // 发布成功，返回上一页
                finish();
            }
        });
    }

    /**
     * 设置按钮点击事件监听器
     */
    private void setupButtonListeners() {
        // 取消按钮点击事件
        binding.btnCancel.setOnClickListener(v -> {
            finish();
        });

        // AI扩展按钮点击事件
        binding.btnAiExpand.setOnClickListener(v -> {
            String currentContent = binding.etPostContent.getText().toString().trim();
            if (currentContent.isEmpty()) {
                Toast.makeText(this, "请先输入一些内容", Toast.LENGTH_SHORT).show();
                return;
            }
            userPostCreateViewModel.aiExpandContent(currentContent);
        });

        // AI生成按钮点击事件
        binding.btnAiGenerate.setOnClickListener(v -> {
            userPostCreateViewModel.aiGenerateContent();
        });

        // 发布动态按钮点击事件
        binding.btnPublish.setOnClickListener(v -> {
            String content = binding.etPostContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "动态内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            userPostCreateViewModel.publishPost(content);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 避免内存泄漏
    }
}