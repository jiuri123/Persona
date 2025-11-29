package com.example.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.demo.adapter.PersonaDropdownAdapter;
import com.example.demo.databinding.ActivityPostEditorBinding;
import com.example.demo.databinding.PersonaDropdownMenuBinding;
import com.example.demo.model.Persona;
import com.example.demo.viewmodel.UserPostCreateViewModel;
import com.example.demo.viewmodel.UserPersonaCreateAndChatViewModel;

import java.util.List;

/**
 * 发布动态编辑页面
 * 允许用户手动输入或使用AI生成/扩展动态内容
 * 实现了发布动态和取消发布的功能
 * 支持选择Persona发布动态
 */
public class UserPostCreateActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityPostEditorBinding binding;
    // Persona下拉菜单绑定
    private PersonaDropdownMenuBinding personaDropdownMenuBinding;
    // PopupWindow用于显示Persona选择菜单
    private PopupWindow personaPopupWindow;
    // Persona下拉菜单适配器
    private PersonaDropdownAdapter personaDropdownAdapter;

    // ViewModel，用于管理编辑页面的业务逻辑
    private UserPostCreateViewModel userPostCreateViewModel;
    // Persona ViewModel，用于获取用户创建的Persona列表
    private UserPersonaCreateAndChatViewModel userPersonaCreateAndChatViewModel;
    // 当前选择的Persona
    private Persona selectedPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使用视图绑定初始化布局
        binding = ActivityPostEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 获取ViewModel实例
        userPostCreateViewModel = new ViewModelProvider(this).get(UserPostCreateViewModel.class);
        userPersonaCreateAndChatViewModel = new ViewModelProvider(this).get(UserPersonaCreateAndChatViewModel.class);

        // 初始化Persona选择功能
        initPersonaSelector();

        // 设置观察者
        setupObservers();

        // 设置按钮点击事件
        setupButtonListeners();
    }

    /**
     * 初始化Persona选择功能
     * 包括下拉菜单、适配器和点击事件
     */
    private void initPersonaSelector() {
        // 初始化下拉菜单绑定
        personaDropdownMenuBinding = PersonaDropdownMenuBinding.inflate(getLayoutInflater());

        // 创建PopupWindow
        personaPopupWindow = new PopupWindow(
                personaDropdownMenuBinding.getRoot(),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // 设置PopupWindow的背景和动画
        personaPopupWindow.setBackgroundDrawable(getDrawable(android.R.drawable.dialog_holo_light_frame));
        personaPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

        // 初始化适配器
        List<Persona> userPersonas = userPersonaCreateAndChatViewModel.getUserPersonas().getValue();
        personaDropdownAdapter = new PersonaDropdownAdapter(this, userPersonas);
        personaDropdownMenuBinding.rvPersonaList.setAdapter(personaDropdownAdapter);

        // 设置Persona选择监听器
        personaDropdownAdapter.setOnPersonaSelectListener(persona -> {
            selectedPersona = persona;
            updateSelectedPersonaUI();
            personaPopupWindow.dismiss();
        });

        // 设置Persona选择器的点击事件
        binding.personaSelector.setOnClickListener(v -> {
            if (personaPopupWindow.isShowing()) {
                personaPopupWindow.dismiss();
            } else {
                // 显示PopupWindow，位于persona_selector下方
                personaPopupWindow.showAsDropDown(binding.personaSelector);
            }
        });
    }

    /**
     * 更新选中Persona的UI显示
     */
    private void updateSelectedPersonaUI() {
        if (selectedPersona != null) {
            // 设置选中Persona的名称
            binding.tvSelectedPersonaName.setText(selectedPersona.getName());
            // 使用Glide加载选中Persona的头像
            if (selectedPersona.getAvatarUri() != null) {
                Glide.with(this)
                        .load(selectedPersona.getAvatarUri())
                        .circleCrop()
                        .into(binding.ivSelectedPersonaAvatar);
            } else {
                Glide.with(this)
                        .load(selectedPersona.getAvatarDrawableId())
                        .circleCrop()
                        .into(binding.ivSelectedPersonaAvatar);
            }
        }
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

        // 观察用户Persona列表变化
        userPersonaCreateAndChatViewModel.getUserPersonas().observe(this, personas -> {
            // 更新适配器数据
            personaDropdownAdapter.updateData(personas);
            // 如果还没有选择Persona，默认选择第一个
            if (selectedPersona == null && personas != null && !personas.isEmpty()) {
                selectedPersona = personas.get(0);
                updateSelectedPersonaUI();
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
            if (selectedPersona == null) {
                Toast.makeText(this, "请先选择一个Persona", Toast.LENGTH_SHORT).show();
                return;
            }
            userPostCreateViewModel.publishPost(content, selectedPersona);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 避免内存泄漏
        personaDropdownMenuBinding = null;
    }
}