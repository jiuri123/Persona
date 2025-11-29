package com.example.demo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

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
    
    // 选中的头像URI
    private Uri selectedAvatarUri;

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
        
        // 头像点击事件，弹出底部菜单
        activityCreatePersonaBinding.ivAvatarPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarOptionsMenu(v);
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
        // 将Uri转换为字符串，保存到Persona对象中
        String avatarUriString = selectedAvatarUri != null ? selectedAvatarUri.toString() : null;
        Persona myPersona = new Persona(
                myPersonaName, 
                avatarId, 
                avatarUriString, // avatarUri，从相册选择的图片URI
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
     * 显示头像选项菜单
     * @param view 触发菜单的视图
     */
    private void showAvatarOptionsMenu(View view) {
        // 创建PopupMenu，设置为底部显示
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.BOTTOM);
        // 添加菜单选项
        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "从手机相册选择");
        // 设置菜单点击监听器
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 1) {
                    // 从相册选择图片
                    selectImageFromGallery();
                    return true;
                }
                return false;
            }
        });
        // 显示菜单
        popupMenu.show();
    }
    
    /**
     * 从相册选择图片
     */
    private void selectImageFromGallery() {
        // 检查Android版本，确定需要请求的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13及以上，使用READ_MEDIA_IMAGES权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，执行选择图片操作
                launchGalleryIntent();
            } else {
                // 请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 200);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0-12，使用READ_EXTERNAL_STORAGE权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，执行选择图片操作
                launchGalleryIntent();
            } else {
                // 请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            }
        } else {
            // Android 5.1及以下，权限在安装时授予
            launchGalleryIntent();
        }
    }
    
    /**
     * 启动相册Intent
     */
    private void launchGalleryIntent() {
        // 创建Intent，指定动作是选择图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 启动Activity，等待结果
        startActivityForResult(intent, 100);
    }
    
    /**
     * 处理权限请求结果
     * @param requestCode 请求码
     * @param permissions 请求的权限数组
     * @param grantResults 权限授予结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            // 检查权限是否被授予
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，执行选择图片操作
                launchGalleryIntent();
            } else {
                // 权限被拒绝，显示提示
                Toast.makeText(this, "需要读取相册权限才能选择图片", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * 处理Activity返回结果
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // 检查请求码和结果码
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // 获取选中图片的URI
            selectedAvatarUri = data.getData();
            if (selectedAvatarUri != null) {
                // 更新头像显示
                activityCreatePersonaBinding.ivAvatarPreview.setImageURI(selectedAvatarUri);
            }
        }
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