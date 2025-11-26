package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.model.ChatMessage;
import com.example.demo.viewmodel.MyPersonaChatViewModel;
import com.example.demo.model.Persona;
import com.example.demo.databinding.FragmentMyPersonaBinding;
import com.example.demo.activity.MainActivity;
import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.viewmodel.SharedViewModel;

import java.util.List;

/**
 * 我的Persona Fragment
 * 显示用户的Persona和与它的聊天界面
 * 如果用户没有创建Persona，则显示创建Persona的引导界面
 */
public class MyPersonaFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentMyPersonaBinding binding;
    // 聊天适配器，用于显示聊天消息
    private PersonaChatAdapter personaChatAdapter;
    // ViewModel，用于管理聊天数据和业务逻辑
    private MyPersonaChatViewModel viewModel;

    /**
     * 创建Fragment的视图
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的Fragment状态
     * @return 创建的视图
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 使用视图绑定创建布局
        binding = FragmentMyPersonaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * 视图创建完成后调用
     * 初始化ViewModel和UI
     * @param view 创建的视图
     * @param savedInstanceState 保存的Fragment状态
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置UI
        setupUI();
    }

    /**
     * 当Persona创建完成时调用
     * @param persona 新创建的Persona对象
     */
    public void onPersonaCreated(Persona persona) {
        // 将Persona设置到SharedViewModel中
        SharedViewModel.getInstance().setCurrentUserPersona(persona);

        // 如果视图已创建，则更新UI
        if (binding != null) {
            setupUI();
        }
    }

    /**
     * 设置UI界面
     * 根据是否有Persona显示不同的界面
     */
    private void setupUI() {
        // 从SharedViewModel获取当前用户Persona
        Persona currentUserPersona = SharedViewModel.getInstance().getCurrentUserPersona().getValue();
        
        if (currentUserPersona == null) {
            // 没有Persona时显示空状态界面
            binding.groupEmptyState.setVisibility(View.VISIBLE);
            binding.personaHeaderLayout.setVisibility(View.GONE);
            binding.rvChatMessages.setVisibility(View.GONE);
            binding.inputLayout.setVisibility(View.GONE);

            // 设置创建Persona按钮的点击事件
            binding.btnGoToCreate.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).launchCreatePersonaActivity();
                }
            });
        } else {
            // 有Persona时显示聊天界面
            binding.groupEmptyState.setVisibility(View.GONE);
            binding.personaHeaderLayout.setVisibility(View.VISIBLE);
            binding.rvChatMessages.setVisibility(View.VISIBLE);
            binding.inputLayout.setVisibility(View.VISIBLE);

            // 设置Persona信息
            binding.tvPersonaName.setText(currentUserPersona.getName());
            binding.ivPersonaAvatar.setImageResource(currentUserPersona.getAvatarDrawableId());

            // 初始化聊天界面
            initChatWithMVVM(currentUserPersona);
        }
    }

    /**
     * 使用MVVM架构初始化聊天界面
     * @param personaToChat 要聊天的Persona对象
     */
    private void initChatWithMVVM(Persona personaToChat) {
        // 使用工厂模式创建ViewModel，传入Persona对象
        MyPersonaChatViewModel.Factory factory = new MyPersonaChatViewModel.Factory(personaToChat);
        viewModel = new ViewModelProvider(this, factory).get(MyPersonaChatViewModel.class);

        // 创建聊天适配器
        personaChatAdapter = new PersonaChatAdapter(requireContext());
        // 设置线性布局管理器，并从底部开始显示
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // 从底部开始显示消息
        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(personaChatAdapter);

        // 设置发送按钮的点击事件
        binding.btnSend.setOnClickListener(v -> {
            String messageText = binding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                viewModel.sendMessage(messageText); // 通过ViewModel发送消息
                binding.etChatMessage.setText(""); // 清空输入框
            }
        });

        // 观察聊天历史变化，更新UI
        viewModel.getChatHistory().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> newMessages) {
                personaChatAdapter.setData(newMessages); // 更新适配器数据
                if (personaChatAdapter.getItemCount() > 0) {
                    // 滚动到最新消息
                    binding.rvChatMessages.scrollToPosition(personaChatAdapter.getItemCount() - 1);
                }
            }
        });
    }

    /**
     * Fragment视图销毁时调用
     * 清理视图绑定
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 避免内存泄漏
    }
}