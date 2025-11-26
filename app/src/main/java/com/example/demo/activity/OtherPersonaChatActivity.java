package com.example.demo.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.ui.adapter.ChatAdapter;
import com.example.demo.ChatMessage;
import com.example.demo.MyPersonaViewModel;
import com.example.demo.Persona;
import com.example.demo.databinding.ActivityChatBinding; // [!!] 导入新的 Binding 类

import java.util.List;

/**
 * 聊天 Activity
 * 用于和任意 Persona 进行一对一聊天
 */
public class ChatActivity extends AppCompatActivity {

    // 1. [!!] 关键 Key：用于从 Intent 中获取 Persona
    //    我们把它定义为 public static final，以便 SocialPostAdapter 能引用它
    public static final String EXTRA_PERSONA = "com.example.demo.EXTRA_PERSONA";

    // 2. 声明变量
    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private MyPersonaViewModel viewModel;
    private Persona currentPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 3. [!!] 使用新的布局 Binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 4. [!! 核心 !!] 从 Intent 中获取 Persona
        currentPersona = getIntent().getParcelableExtra(EXTRA_PERSONA);

        // 5. 如果 Persona 为 null (理论上不应该发生)，则显示错误并退出
        if (currentPersona == null) {
            // 在实际应用中，这里应该显示一个错误页面
            finish(); // 直接关闭 Activity
            return;
        }

        // 6. [!!] 设置 Toolbar
        setupToolbar();

        // 7. [!! 复用 !!] 调用聊天和 MVVM 的初始化方法
        //    (这个方法和 MyPersonaFragment 里的几乎一样)
        initChatWithMVVM(currentPersona);
    }

    /**
     * [!! 新增 !!]
     * 设置 Toolbar，添加标题和返回按钮
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar); // 将 toolbar 设为 ActionBar
        if (getSupportActionBar() != null) {
            // a. 显示返回箭头
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // b. 设置标题
            getSupportActionBar().setTitle(currentPersona.getName()); //
        }
    }

    /**
     * [!! 核心复用 !!]
     * (这个方法从 MyPersonaFragment 几乎原封不动地复制过来)
     */
    private void initChatWithMVVM(Persona personaToChat) {
        // a. [!!] 使用 Factory 来创建 ViewModel 实例
        MyPersonaViewModel.Factory factory = new MyPersonaViewModel.Factory(personaToChat); //
        viewModel = new ViewModelProvider(this, factory).get(MyPersonaViewModel.class); //

        // b. 初始化 Adapter 和 RecyclerView
        chatAdapter = new ChatAdapter(); //
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // 保证新消息在底部
        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(chatAdapter);

        // c. 设置发送按钮的点击事件 (不包含 /setname 逻辑)
        binding.btnSend.setOnClickListener(v -> {
            String messageText = binding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                viewModel.sendMessage(messageText); //
                binding.etChatMessage.setText("");
            }
        });

        // d. 观察 LiveData 的变化
        viewModel.getChatHistory().observe(this, new Observer<List<ChatMessage>>() { //
            @Override
            public void onChanged(List<ChatMessage> newMessages) {
                chatAdapter.setData(newMessages); //
                // 滚动到底部
                if (chatAdapter.getItemCount() > 0) {
                    binding.rvChatMessages.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });
    }

    /**
     * [!! 新增 !!]
     * 处理 Toolbar 上的"返回"按钮点击
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // android.R.id.home 是 "返回" 按钮的固定 ID
        if (item.getItemId() == android.R.id.home) {
            finish(); // 关闭当前 Activity，返回到上一个界面 (MainActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}