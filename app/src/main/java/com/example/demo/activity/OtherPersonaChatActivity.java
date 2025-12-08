package com.example.demo.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.viewmodel.OtherPersonaChatViewModel;
import com.example.demo.model.OtherPersona;
import com.example.demo.databinding.ActivityChatBinding;

/**
 * 与其他Persona聊天的活动界面
 * 显示聊天界面，允许用户与选择的Persona进行对话
 */
public class OtherPersonaChatActivity extends AppCompatActivity {

    // 视图绑定，用于访问布局中的组件
    private ActivityChatBinding activityChatBinding;

    // 用于传递Persona聊天对象的Intent键名常量
    public static final String EXTRA_PERSONA = "EXTRA_PERSONA";

    // 聊天消息适配器，用于显示聊天消息
    private PersonaChatAdapter personaChatAdapter;

    // 当前聊天的OtherPersona
    private OtherPersona personaToChat;

    // ViewModel，处理聊天相关的业务逻辑
    private OtherPersonaChatViewModel otherPersonaChatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使用视图绑定初始化布局
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        // 获取从Intent传递过来的OtherPersona对象
        personaToChat = getIntent().getParcelableExtra(EXTRA_PERSONA);

        // 如果没有传递Persona对象，则关闭Activity
        if (personaToChat == null) {
            // 如果没有Persona对象，显示错误信息并返回
            Toast.makeText(this, "没有找到Persona信息", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化ViewModel
        otherPersonaChatViewModel = new ViewModelProvider(this).get(OtherPersonaChatViewModel.class);

        // 设置当前聊天的Persona
        otherPersonaChatViewModel.setCurrentPersona(personaToChat);

        // 初始化UI
        setupUI();

        // 设置发送按钮点击事件
        activityChatBinding.btnSend.setOnClickListener(v -> {
            String messageText = activityChatBinding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // 发送消息
                otherPersonaChatViewModel.sendMessage(messageText);
                // 清空输入框
                activityChatBinding.etChatMessage.setText("");
            }
        });

        // 设置观察者，观察聊天历史变化
        otherPersonaChatViewModel.getChatHistory().observe(this, chatMessages -> {
            if (chatMessages != null) {
                personaChatAdapter.submitList(chatMessages);
                // 滚动到最新消息
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