package com.example.demo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.demo.viewmodel.UserPersonaCreateViewModel;
import com.example.demo.R;
import com.example.demo.databinding.ActivityCreatePersonaBinding;

import androidx.lifecycle.ViewModelProvider;

/**
 * 创建Persona的活动界面
 * 允许用户手动输入或使用AI生成Persona的名称和背景故事
 */
public class UserPersonaCreateActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityCreatePersonaBinding activityCreatePersonaBinding;

    // ViewModel，处理AI生成Persona的业务逻辑
    private UserPersonaCreateViewModel userPersonaCreateViewModel;

    // 选中的头像URI
    private Uri selectedAvatarUri;

    // ActivityResultLauncher用于处理图片选择结果
    private ActivityResultLauncher<Intent> galleryLauncher;

    // ActivityResultLauncher用于处理权限请求结果
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用视图绑定初始化布局
        activityCreatePersonaBinding = ActivityCreatePersonaBinding.inflate(getLayoutInflater());
        setContentView(activityCreatePersonaBinding.getRoot());

        // 获取ViewModel实例，ViewModel在配置变更时不会被销毁
        userPersonaCreateViewModel = new ViewModelProvider(this).get(UserPersonaCreateViewModel.class);

        // 清除上一次生成的临时Persona对象
        userPersonaCreateViewModel.clearGeneratedPersona();

        // 设置LiveData观察者，监听ViewModel中的数据变化
        setupObservers();

        // 初始化ActivityResultLaunchers
        setupActivityResultLaunchers();

        // 返回按钮点击事件
        activityCreatePersonaBinding.btnBack.setOnClickListener(v -> finish());

        // 头像点击事件，弹出底部菜单
        activityCreatePersonaBinding.ivAvatarPreview.setOnClickListener(this::showAvatarOptionsMenu);

        // AI生成按钮点击事件
        activityCreatePersonaBinding.btnAiGenerate.setOnClickListener(v ->
                userPersonaCreateViewModel.generatePersonaDetails());

        // 创建按钮点击事件
        activityCreatePersonaBinding.btnCreate.setOnClickListener(v -> createPersonaAndSave());
    }

    /**
     * 初始化ActivityResultLaunchers
     */
    private void setupActivityResultLaunchers() {
        // 初始化图片选择ActivityResultLauncher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 获取选中图片的URI
                        selectedAvatarUri = result.getData().getData();
                        if (selectedAvatarUri != null) {
                            // 更新头像显示
                            activityCreatePersonaBinding.ivAvatarPreview.setImageURI(selectedAvatarUri);
                        }
                    }
                }
        );

        // 初始化权限请求ActivityResultLauncher
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    // 检查是否所有请求的权限都被授予
                    boolean allGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }

                    if (allGranted) {
                        // 权限已授予，执行选择图片操作
                        launchGalleryIntent();
                    } else {
                        // 权限被拒绝，显示提示
                        Toast.makeText(this, "需要读取相册权限才能选择图片", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 设置LiveData观察者，监听ViewModel中的数据变化
     */
    private void setupObservers() {
        // 监听加载状态，更新UI显示
        userPersonaCreateViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                // 加载中，禁用按钮并显示加载文本
                activityCreatePersonaBinding.btnAiGenerate.setEnabled(false);
                activityCreatePersonaBinding.btnAiGenerate.setText("生成中...");
            } else {
                // 加载完成，启用按钮并恢复原始文本
                activityCreatePersonaBinding.btnAiGenerate.setEnabled(true);
                activityCreatePersonaBinding.btnAiGenerate.setText("AI 辅助生成");
            }
        });

        // 监听生成的Persona对象，更新所有编辑框
        userPersonaCreateViewModel.getGeneratedPersona().observe(this, generatedPersona -> {
            if (generatedPersona != null) {
                // 将Persona对象的各个属性填充到对应的编辑框中
                activityCreatePersonaBinding.etPersonaName.setText(generatedPersona.getName());
                activityCreatePersonaBinding.etPersonaGender.setText(generatedPersona.getGender());
                activityCreatePersonaBinding.etPersonaPersonality.setText(generatedPersona.getPersonality());
                activityCreatePersonaBinding.etPersonaAge.setText(String.valueOf(generatedPersona.getAge()));
                activityCreatePersonaBinding.etPersonaRelationship.setText(generatedPersona.getRelationship());
                activityCreatePersonaBinding.etPersonaCatchphrase.setText(generatedPersona.getSignature());
                activityCreatePersonaBinding.etPersonaStory.setText(generatedPersona.getBackgroundStory());
            }
        });

        // 观察用户列表，目的是为了激活 ViewModel 中的 MediatorLiveData
        // 从而触发 ViewModel 中的 userPersonaNames 集合的更新逻辑
        // 因为MediatorLiveData在没有观察者时不会触发更新，所以这里需要手动触发一次
        // 这里不需要做任何 UI 更新，因为我们只需要 ViewModel 里的 Set 被填满
        userPersonaCreateViewModel.getUserPersonas().observe(this, userPersonas -> {
            // 这里什么都不用做，或者可以打印个日志看看数据到了没
            // Log.d("CreateActivity", "Loaded " + (personas != null ? personas.size() : 0) + " personas");
        });

        // 监听错误信息，显示Toast提示
        userPersonaCreateViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(UserPersonaCreateActivity.this, "错误: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 创建Persona对象并返回结果给调用者
     */
    private void createPersonaAndSave() {
        // 获取用户输入的所有属性
        final String userPersonaName = activityCreatePersonaBinding.etPersonaName.getText().toString().trim();
        final String userPersonaGender = activityCreatePersonaBinding.etPersonaGender.getText().toString().trim();
        final String userPersonaPersonality = activityCreatePersonaBinding.etPersonaPersonality.getText().toString().trim();
        String ageStr = activityCreatePersonaBinding.etPersonaAge.getText().toString().trim();
        final String userPersonaRelationship = activityCreatePersonaBinding.etPersonaRelationship.getText().toString().trim();
        final String userPersonaCatchphrase = activityCreatePersonaBinding.etPersonaCatchphrase.getText().toString().trim();
        final String userPersonaStory = activityCreatePersonaBinding.etPersonaStory.getText().toString().trim();

        // 验证输入是否为空
        if (userPersonaName.isEmpty() || userPersonaStory.isEmpty()) {
            Toast.makeText(this, "名称和背景故事不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 解析年龄，默认为0
        final int userPersonaAge;
        if (!ageStr.isEmpty()) {
            try {
                userPersonaAge = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的年龄数字", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            userPersonaAge = 0;
        }

        final int avatarId = R.drawable.avatar_zero;
        // 将Uri转换为字符串，保存到Persona对象中
        final String avatarUriString = selectedAvatarUri != null ? selectedAvatarUri.toString() : null;

        if(userPersonaCreateViewModel.isPersonaNameExists(userPersonaName)) {
            Toast.makeText(this, "已存在相同名字的persona，请重新输入名字", Toast.LENGTH_SHORT).show();
            return;
        }
        // 调用ViewModel的方法创建并保存Persona对象
        boolean isCreated = userPersonaCreateViewModel.createPersonaAndSave(
                userPersonaName,
                avatarId,
                avatarUriString,
                userPersonaCatchphrase,
                userPersonaStory,
                userPersonaGender,
                userPersonaAge,
                userPersonaPersonality,
                userPersonaRelationship
        );
        if (isCreated) {
            finish();
        }
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
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                // 从手机相册选择图片
                selectImageFromGallery();
                return true;
            }
            return false;
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
            permissionLauncher.launch(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES});
        } else {
            // Android 6.0-12，使用READ_EXTERNAL_STORAGE权限
            permissionLauncher.launch(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE});
        }
    }

    /**
     * 启动相册Intent
     */
    private void launchGalleryIntent() {
        // 创建Intent，指定动作是选择图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 使用ActivityResultLauncher启动Intent
        galleryLauncher.launch(intent);
    }
}
