package com.example.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.viewmodel.CreateMyPersonaViewModel;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ActivityCreatePersonaBinding;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * 创建Persona的活动界面
 * 允许用户手动输入或使用AI生成Persona的名称和背景故事
 */
public class CreateMyPersonaActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityCreatePersonaBinding binding;
    // ViewModel，处理AI生成Persona的业务逻辑
    private CreateMyPersonaViewModel viewModel;

    // 用于返回结果的Intent键名常量
    public static final String EXTRA_PERSONA_RESULT = "com.example.demo.PERSONA_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用视图绑定初始化布局
        binding = ActivityCreatePersonaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取ViewModel实例，ViewModel在配置变更时不会被销毁
        viewModel = new ViewModelProvider(this).get(CreateMyPersonaViewModel.class);

        // 设置LiveData观察者，监听ViewModel中的数据变化
        setupObservers();

        // 返回按钮点击事件
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭当前Activity，返回上一个界面
                finish();
            }
        });

        // 创建按钮点击事件
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Persona并返回结果
                createPersonaAndReturn();
            }
        });

        // AI生成按钮点击事件
        binding.btnAiGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用ViewModel的方法生成Persona详情
                viewModel.generatePersonaDetails();
            }
        });
    }

    /**
     * 创建Persona对象并返回结果给调用者
     */
    private void createPersonaAndReturn() {
        // 获取用户输入的名称和背景故事
        String name = binding.etPersonaName.getText().toString().trim();
        String story = binding.etPersonaStory.getText().toString().trim();

        // 验证输入是否为空
        if (name.isEmpty() || story.isEmpty()) {
            Toast.makeText(this, "名称和背景故事不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建Persona对象
        String bio = "由你创建的 Persona";
        int avatarId = R.drawable.avatar_zero;
        Persona newPersona = new Persona(name, avatarId, bio, story);

        // 创建Intent并放入Persona对象
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_PERSONA_RESULT, newPersona);

        // 设置结果并关闭Activity
        setResult(AppCompatActivity.RESULT_OK, resultIntent);
        finish();
    }

    /**
     * 设置LiveData观察者，监听ViewModel中的数据变化
     */
    private void setupObservers() {
        // 监听加载状态，更新UI显示
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    // 加载中，禁用按钮并显示加载文本
                    binding.btnAiGenerate.setEnabled(false);
                    binding.btnAiGenerate.setText("生成中...");
                } else {
                    // 加载完成，启用按钮并恢复原始文本
                    binding.btnAiGenerate.setEnabled(true);
                    binding.btnAiGenerate.setText("AI 辅助生成");
                }
            }
        });

        // 监听生成的名称，更新UI
        viewModel.getGeneratedName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String name) {
                if (name != null) {
                    binding.etPersonaName.setText(name);
                }
            }
        });

        // 监听生成的背景故事，更新UI
        viewModel.getGeneratedStory().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String story) {
                if (story != null) {
                    binding.etPersonaStory.setText(story);
                }
            }
        });

        // 监听错误信息，显示Toast提示
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(CreateMyPersonaActivity.this, "错误: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}