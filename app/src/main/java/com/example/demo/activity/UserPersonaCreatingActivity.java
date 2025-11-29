package com.example.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.viewmodel.UserPersonaCreatingViewModel;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ActivityCreatePersonaBinding;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

/**
 * 创建Persona的活动界面
 * 允许用户手动输入或使用AI生成Persona的名称和背景故事
 */
public class UserPersonaCreatingActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityCreatePersonaBinding activityCreatePersonaBinding;
    // ViewModel，处理AI生成Persona的业务逻辑
    private UserPersonaCreatingViewModel userPersonaCreatingViewModel;

    // 用于返回结果的Intent键名常量
    public static final String EXTRA_PERSONA_RESULT = "com.example.demo.PERSONA_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用视图绑定初始化布局
        activityCreatePersonaBinding = ActivityCreatePersonaBinding.inflate(getLayoutInflater());
        setContentView(activityCreatePersonaBinding.getRoot());

        // 获取ViewModel实例，ViewModel在配置变更时不会被销毁
        userPersonaCreatingViewModel = new ViewModelProvider(this).get(UserPersonaCreatingViewModel.class);

        // 设置LiveData观察者，监听ViewModel中的数据变化
        setupObservers();

        // 返回按钮点击事件
        activityCreatePersonaBinding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 关闭当前Activity，返回上一个界面
                finish();
            }
        });

        // 创建按钮点击事件
        activityCreatePersonaBinding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Persona并返回结果
                createPersonaAndReturn();
            }
        });

        // AI生成按钮点击事件
        activityCreatePersonaBinding.btnAiGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 调用ViewModel的方法生成Persona详情
                userPersonaCreatingViewModel.generatePersonaDetails();
            }
        });
    }

    /**
     * 创建Persona对象并返回结果给调用者
     */
    private void createPersonaAndReturn() {
        // 获取用户输入的所有属性
        String myPersonaName = activityCreatePersonaBinding.etPersonaName.getText().toString().trim();
        String myPersonaGender = activityCreatePersonaBinding.etPersonaGender.getText().toString().trim();
        String myPersonaPersonality = activityCreatePersonaBinding.etPersonaPersonality.getText().toString().trim();
        String ageStr = activityCreatePersonaBinding.etPersonaAge.getText().toString().trim();
        String myPersonaRelationship = activityCreatePersonaBinding.etPersonaRelationship.getText().toString().trim();
        String myPersonaCatchphrase = activityCreatePersonaBinding.etPersonaCatchphrase.getText().toString().trim();
        String myPersonaStory = activityCreatePersonaBinding.etPersonaStory.getText().toString().trim();

        // 验证输入是否为空
        if (myPersonaName.isEmpty() || myPersonaStory.isEmpty()) {
            Toast.makeText(this, "名称和背景故事不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 解析年龄，默认为0
        int myPersonaAge = 0;
        if (!ageStr.isEmpty()) {
            try {
                myPersonaAge = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的年龄数字", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // 创建Persona对象
        String myPersonaBio = "你创建的 Persona";
        int avatarId = R.drawable.avatar_zero;
        Persona myPersona = new Persona(
                myPersonaName, 
                avatarId, 
                myPersonaBio, 
                myPersonaStory,
                myPersonaGender,
                myPersonaAge,
                myPersonaPersonality,
                myPersonaRelationship,
                myPersonaCatchphrase
        );

        // 创建Intent并放入Persona对象
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_PERSONA_RESULT, myPersona);

        // 设置结果并关闭Activity
        setResult(AppCompatActivity.RESULT_OK, resultIntent);
        finish();
    }

    /**
     * 设置LiveData观察者，监听ViewModel中的数据变化
     */
    private void setupObservers() {
        // 监听加载状态，更新UI显示
        userPersonaCreatingViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    // 加载中，禁用按钮并显示加载文本
                    activityCreatePersonaBinding.btnAiGenerate.setEnabled(false);
                    activityCreatePersonaBinding.btnAiGenerate.setText("生成中...");
                } else {
                    // 加载完成，启用按钮并恢复原始文本
                    activityCreatePersonaBinding.btnAiGenerate.setEnabled(true);
                    activityCreatePersonaBinding.btnAiGenerate.setText("AI 辅助生成");
                }
            }
        });

        // 监听生成的Persona对象，更新所有编辑框
        userPersonaCreatingViewModel.getGeneratedPersona().observe(this, new Observer<Persona>() {
            @Override
            public void onChanged(Persona generatedPersona) {
                if (generatedPersona != null) {
                    // 将Persona对象的各个属性填充到对应的编辑框中
                    activityCreatePersonaBinding.etPersonaName.setText(generatedPersona.getName());
                    activityCreatePersonaBinding.etPersonaGender.setText(generatedPersona.getGender());
                    activityCreatePersonaBinding.etPersonaPersonality.setText(generatedPersona.getPersonality());
                    activityCreatePersonaBinding.etPersonaAge.setText(String.valueOf(generatedPersona.getAge()));
                    activityCreatePersonaBinding.etPersonaRelationship.setText(generatedPersona.getRelationship());
                    activityCreatePersonaBinding.etPersonaCatchphrase.setText(generatedPersona.getCatchphrase());
                    activityCreatePersonaBinding.etPersonaStory.setText(generatedPersona.getBackgroundStory());
                }
            }
        });

        // 监听错误信息，显示Toast提示
        userPersonaCreatingViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(UserPersonaCreatingActivity.this, "错误: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}