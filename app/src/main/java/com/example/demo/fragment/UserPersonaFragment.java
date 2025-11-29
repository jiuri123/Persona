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
import com.example.demo.viewmodel.UserPersonaCreateAndChatViewModel;
import com.example.demo.model.Persona;
import com.example.demo.databinding.FragmentMyPersonaBinding;
import com.example.demo.activity.MainActivity;
import com.example.demo.adapter.PersonaChatAdapter;

import java.util.List;

/**
 * 我的Persona Fragment
 * 显示用户的Persona和与它的聊天界面
 * 如果用户没有创建Persona，则显示创建Persona的引导界面
 */
public class UserPersonaFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentMyPersonaBinding fragmentMyPersonaBinding;
    // 聊天适配器，用于显示聊天消息
    private PersonaChatAdapter personaChatAdapter;
    // 我的Persona和聊天ViewModel
    private UserPersonaCreateAndChatViewModel userPersonaCreateAndChatViewModel;

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
        fragmentMyPersonaBinding = FragmentMyPersonaBinding.inflate(inflater, container, false);
        return fragmentMyPersonaBinding.getRoot();
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
        
        // 初始化ViewModel
        userPersonaCreateAndChatViewModel = new ViewModelProvider(this).get(UserPersonaCreateAndChatViewModel.class);

        // 观察当前用户Persona的变化，自动更新UI
        userPersonaCreateAndChatViewModel.getCurrentUserPersona().observe(getViewLifecycleOwner(), new Observer<Persona>() {
            @Override
            public void onChanged(Persona persona) {
                // 当当前用户Persona变化时，更新UI
                setupUI();
            }
        });

        // 设置初始UI
        setupUI();
    }

    /**
     * 当Persona创建完成时调用
     * @param persona 新创建的Persona对象
     */
    public void onPersonaCreated(Persona persona) {
        // 通过UserPersonaViewModel将Persona添加到Repository
        userPersonaCreateAndChatViewModel.addUserPersona(persona);
        // 设置当前聊天的Persona
        userPersonaCreateAndChatViewModel.setCurrentUserPersona(persona);

        // 不再直接调用setupUI()，而是由LiveData的观察者自动处理UI更新
    }

    /**
     * 设置UI界面
     * 根据是否有Persona显示不同的界面
     */
    private void setupUI() {
        // 从UserPersonaViewModel获取当前用户Persona
        Persona currentUserPersona = userPersonaCreateAndChatViewModel.getCurrentUserPersona().getValue();
        
        if (currentUserPersona == null) {
            // 没有Persona时显示空状态界面
            fragmentMyPersonaBinding.groupEmptyState.setVisibility(View.VISIBLE);
            fragmentMyPersonaBinding.personaHeaderLayout.setVisibility(View.GONE);
            fragmentMyPersonaBinding.rvChatMessages.setVisibility(View.GONE);
            fragmentMyPersonaBinding.inputLayout.setVisibility(View.GONE);

            // 设置创建Persona按钮的点击事件
            fragmentMyPersonaBinding.btnGoToCreate.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).launchCreatePersonaActivity();
                }
            });
        } else {
            // 有Persona时显示聊天界面
            fragmentMyPersonaBinding.groupEmptyState.setVisibility(View.GONE);
            fragmentMyPersonaBinding.personaHeaderLayout.setVisibility(View.VISIBLE);
            fragmentMyPersonaBinding.rvChatMessages.setVisibility(View.VISIBLE);
            fragmentMyPersonaBinding.inputLayout.setVisibility(View.VISIBLE);

            // 设置Persona信息
            fragmentMyPersonaBinding.tvPersonaName.setText(currentUserPersona.getName());
            fragmentMyPersonaBinding.ivPersonaAvatar.setImageResource(currentUserPersona.getAvatarDrawableId());

            // 初始化聊天界面
            initChatWithMVVM();
        }
    }

    /**
     * 使用MVVM架构初始化聊天界面
     */
    private void initChatWithMVVM() {
        // 创建聊天适配器
        personaChatAdapter = new PersonaChatAdapter(requireContext());
        // 设置线性布局管理器，并从底部开始显示
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // 从底部开始显示消息
        fragmentMyPersonaBinding.rvChatMessages.setLayoutManager(layoutManager);
        fragmentMyPersonaBinding.rvChatMessages.setAdapter(personaChatAdapter);

        // 设置发送按钮的点击事件
        fragmentMyPersonaBinding.btnSend.setOnClickListener(v -> {
            String messageText = fragmentMyPersonaBinding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                userPersonaCreateAndChatViewModel.sendMessage(messageText); // 通过ViewModel发送消息
                fragmentMyPersonaBinding.etChatMessage.setText(""); // 清空输入框
            }
        });

        // 观察聊天历史变化，更新UI
        userPersonaCreateAndChatViewModel.getChatHistory().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> newMessages) {
                personaChatAdapter.setData(newMessages); // 更新适配器数据
                if (personaChatAdapter.getItemCount() > 0) {
                    // 滚动到最新消息
                    fragmentMyPersonaBinding.rvChatMessages.scrollToPosition(personaChatAdapter.getItemCount() - 1);
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
        fragmentMyPersonaBinding = null; // 避免内存泄漏
    }
}