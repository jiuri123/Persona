package com.example.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.databinding.ActivityChatBinding;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.repository.UserPersonaChatRepository;
import com.example.demo.viewmodel.UserPersonaCreateAndChatViewModel;

import java.util.List;

/**
 * 用户自己创建的Persona聊天界面
 * 显示与用户自己创建的Persona的聊天历史
 * 实现了发送消息和接收消息的功能
 */
public class UserPersonaChatActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityChatBinding binding;
    
    // 聊天适配器，用于显示聊天消息
    private PersonaChatAdapter personaChatAdapter;
    
    // 当前聊天的Persona
    private Persona currentPersona;
    
    // 我的Persona和聊天ViewModel
    private UserPersonaCreateAndChatViewModel userPersonaCreateAndChatViewModel;
    
    // 用户自己创建的Persona聊天仓库
    private UserPersonaChatRepository userPersonaChatRepository;
    
    // 用于传递Persona对象的Intent键
    public static final String EXTRA_PERSONA = "extra_persona";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定初始化布局
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 获取从Intent传递过来的Persona对象
        currentPersona = getIntent().getParcelableExtra(EXTRA_PERSONA);
        
        if (currentPersona == null) {
            // 如果没有Persona对象，显示错误信息并返回
            Toast.makeText(this, "没有找到Persona信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 设置工具栏
        setupToolbar();
        
        // 初始化ViewModel
        userPersonaCreateAndChatViewModel = new ViewModelProvider(this).get(UserPersonaCreateAndChatViewModel.class);
        
        // 初始化聊天仓库
        userPersonaChatRepository = UserPersonaChatRepository.getInstance();
        
        // 设置当前聊天的Persona
        userPersonaChatRepository.setCurrentPersona(currentPersona);
        
        // 初始化UI
        setupUI();
        
        // 设置观察者
        setupObservers();
        
        // 设置按钮点击事件
        setupButtonListeners();
    }
    
    /**
     * 设置工具栏，显示返回按钮和Persona名称
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            // 显示返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // 设置标题为Persona的名称
            getSupportActionBar().setTitle(currentPersona.getName());
        }
    }
    
    /**
     * 处理选项菜单项点击事件
     * @param item 被点击的菜单项
     * @return 如果事件被处理返回true，否则返回false
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // 处理返回按钮点击
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 初始化UI
     */
    private void setupUI() {
        // 设置标题为Persona名称
        setTitle(currentPersona.getName());
        
        // 初始化聊天适配器
        personaChatAdapter = new PersonaChatAdapter(this);
        binding.rvChatMessages.setAdapter(personaChatAdapter);
        
        // 设置线性布局管理器，并从底部开始显示
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // 从底部开始显示消息
        binding.rvChatMessages.setLayoutManager(layoutManager);
    }
    
    /**
     * 设置观察者
     */
    private void setupObservers() {
        // 观察聊天历史变化
        userPersonaChatRepository.getChatHistory().observe(this, chatMessages -> {
            if (chatMessages != null) {
                personaChatAdapter.setData(chatMessages);
                // 滚动到最新消息
                binding.rvChatMessages.scrollToPosition(chatMessages.size() - 1);
            }
        });
    }
    
    /**
     * 设置按钮点击事件
     */
    private void setupButtonListeners() {
        // 发送按钮点击事件
        binding.btnSend.setOnClickListener(v -> {
            String messageText = binding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // 发送消息
                userPersonaChatRepository.sendMessage(messageText);
                // 清空输入框
                binding.etChatMessage.setText("");
            }
        });
    }
}
