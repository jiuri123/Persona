package com.example.demo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.databinding.ActivityChatBinding;
import com.example.demo.model.UserPersona;
import com.example.demo.viewmodel.UserPersonaChatViewModel;

/**
 * 用户自己创建的Persona聊天界面
 * 显示与用户自己创建的Persona的聊天历史
 * 实现了发送消息和接收消息的功能
 */
public class UserPersonaChatActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityChatBinding activityChatBinding;

    // 用于传递Persona对象的Intent键
    public static final String EXTRA_PERSONA = "EXTRA_PERSONA";
    
    // 聊天消息适配器，用于显示聊天消息
    private PersonaChatAdapter personaChatAdapter;
    
    // 当前聊天的UserPersona
    private UserPersona personaToChat;
    
    // 聊天ViewModel，处理聊天相关的业务逻辑
    private UserPersonaChatViewModel userPersonaChatViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用视图绑定初始化布局
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());
        
        // 获取从Intent传递过来的UserPersona对象
        personaToChat = getIntent().getParcelableExtra(EXTRA_PERSONA);
        
        if (personaToChat == null) {
            // 如果没有Persona对象，显示错误信息并返回
            Toast.makeText(this, "没有找到Persona信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化ViewModel
        userPersonaChatViewModel = new ViewModelProvider(this).get(UserPersonaChatViewModel.class);
        
        // 设置当前聊天的Persona
        userPersonaChatViewModel.setCurrentPersona(personaToChat);

        // 初始化UI
        setupUI();

        // 设置发送按钮点击事件
        activityChatBinding.btnSend.setOnClickListener(v -> {
            String messageText = activityChatBinding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // 发送消息
                userPersonaChatViewModel.sendMessage(messageText);
                // 清空输入框
                activityChatBinding.etChatMessage.setText("");
            }
        });

        // 设置观察者，观察聊天历史变化
        userPersonaChatViewModel.getChatHistory().observe(this, chatMessages -> {
            if (chatMessages != null) {
                personaChatAdapter.submitList(chatMessages);
                // 滚动到底部最新消息，确保最新消息可见
                activityChatBinding.rvChatMessages.scrollToPosition(chatMessages.size() - 1);
            }
        });
    }
    
    /**
     * 初始化UI
     */
    private void setupUI() {
        // 设置工具栏
        setSupportActionBar(activityChatBinding.toolbar);
        if (getSupportActionBar() != null) {
            // 显示返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // 设置标题为Persona的名称
            getSupportActionBar().setTitle(personaToChat.getName());
        }

        // 设置标题为Persona名称
        setTitle(personaToChat.getName());
        
        // 初始化聊天适配器
        personaChatAdapter = new PersonaChatAdapter(this);
        // 设置打字机效果完成监听器，使用匿名内部类实现
        personaChatAdapter.setOnTypewriterCompleteListener(new PersonaChatAdapter.OnTypewriterCompleteListener() {
            @Override
            public void onTypewriterComplete(String messageId, boolean isComplete) {
                // 通过ViewModel更新消息的打字机完成状态
                userPersonaChatViewModel.updateMessageTypewriterStatus(messageId, isComplete);
            }
        });
        activityChatBinding.rvChatMessages.setAdapter(personaChatAdapter);
        
        // 设置线性布局管理器，并从底部开始显示
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // 从底部开始显示消息
        activityChatBinding.rvChatMessages.setLayoutManager(layoutManager);
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
    

}
