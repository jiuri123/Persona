package com.example.demo.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.model.ChatMessage;
import com.example.demo.viewmodel.OtherPersonaChatViewModel;
import com.example.demo.model.Persona;
import com.example.demo.databinding.ActivityChatBinding;

import java.util.List;

/**
 * 与其他Persona聊天的活动界面
 * 显示聊天界面，允许用户与选择的Persona进行对话
 */
public class OtherPersonaChatActivity extends AppCompatActivity {

    // 用于传递Persona聊天对象的Intent键名常量
    public static final String EXTRA_PERSONA = "com.example.demo.EXTRA_PERSONA";

    // 视图绑定，用于访问布局中的组件
    private ActivityChatBinding activityChatBinding;
    // 聊天消息适配器，用于显示聊天记录
    private PersonaChatAdapter personaChatAdapter;
    // ViewModel，处理聊天相关的业务逻辑
    private OtherPersonaChatViewModel otherPersonaChatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使用视图绑定初始化布局
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        // 从Intent中获取传递的Persona对象
        // 当前聊天的Persona对象
        Persona personaToChat = getIntent().getParcelableExtra(EXTRA_PERSONA);

        // 如果没有传递Persona对象，则关闭Activity
        if (personaToChat == null) {
            finish();
            return;
        }

        // 设置工具栏，显示返回按钮和Persona名称
        setSupportActionBar(activityChatBinding.toolbar);
        if (getSupportActionBar() != null) {
            // 显示返回按钮
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // 设置标题为Persona的名称
            getSupportActionBar().setTitle(personaToChat.getName());
        }

        // 初始化聊天界面和MVVM架构
        initChatWithMVVM(personaToChat);
    }

    /**
     * 初始化聊天界面，使用MVVM架构
     * @param personaToChat 要聊天的Persona对象
     */
    private void initChatWithMVVM(Persona personaToChat) {
        otherPersonaChatViewModel = new ViewModelProvider(this).get(OtherPersonaChatViewModel.class);
        otherPersonaChatViewModel.setCurrentPersona(personaToChat);

        // 初始化聊天消息适配器
        personaChatAdapter = new PersonaChatAdapter(this);
        activityChatBinding.rvChatMessages.setAdapter(personaChatAdapter);
        // 设置RecyclerView的布局管理器，从底部开始显示消息
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        activityChatBinding.rvChatMessages.setLayoutManager(layoutManager);

        // 发送按钮点击事件
        activityChatBinding.btnSend.setOnClickListener(v -> {
            // 获取输入的消息文本
            String messageText = activityChatBinding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // 发送消息并清空输入框
                otherPersonaChatViewModel.sendMessage(messageText);
                activityChatBinding.etChatMessage.setText("");
            }
        });

        // 观察聊天历史数据变化
        otherPersonaChatViewModel.getChatHistory().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> newMessages) {
                // 更新适配器数据
                personaChatAdapter.setData(newMessages);
                // 如果有消息，滚动到最新消息
                if (personaChatAdapter.getItemCount() > 0) {
                    activityChatBinding.rvChatMessages.scrollToPosition(personaChatAdapter.getItemCount() - 1);
                }
            }
        });
    }

    /**
     * 处理选项菜单项点击事件
     * @param item 被点击的菜单项
     * @return 如果事件被处理返回true，否则返回false
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 处理返回按钮点击
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}