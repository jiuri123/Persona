package com.example.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.activity.UserPersonaCreateActivity;
import com.example.demo.viewmodel.UserPersonaViewModel;
import com.example.demo.model.UserPersona;
import com.example.demo.databinding.FragmentUserPersonaBinding;
import com.example.demo.adapter.UserPersonaListAdapter;

import java.util.List;

/**
 * 我的Persona Fragment
 * 显示用户创建的所有Persona列表
 * 如果用户没有创建Persona，则显示创建Persona的引导界面
 */
public class UserPersonaFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentUserPersonaBinding fragmentUserPersonaBinding;
    // 用户Persona列表适配器，用于显示用户Persona列表
    private UserPersonaListAdapter userPersonaListAdapter;
    // 用户Persona ViewModel，用于处理用户Persona相关操作
    private UserPersonaViewModel userPersonaViewModel;

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
        fragmentUserPersonaBinding = FragmentUserPersonaBinding.inflate(inflater, container, false);
        return fragmentUserPersonaBinding.getRoot();
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
        userPersonaViewModel = new ViewModelProvider(this).get(UserPersonaViewModel.class);

        // 初始化Persona列表适配器
        userPersonaListAdapter = new UserPersonaListAdapter(requireContext());
        // 设置删除UserPersona的回调接口
        userPersonaListAdapter.setOnUserPersonaDeleteListener(new UserPersonaListAdapter.OnUserPersonaDeleteListener() {
            @Override
            public void onUserPersonaDelete(UserPersona userPersona) {
                // 删除UserPersona
                userPersonaViewModel.removeUserPersona(userPersona);
            }
        });
        fragmentUserPersonaBinding.rvPersonaList.setAdapter(userPersonaListAdapter);

        // 设置创建Persona按钮（用户没有任何Persona时显示的）的点击事件
        fragmentUserPersonaBinding.btnGoToCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserPersonaCreateActivity.class);
            startActivity(intent);
        });

        // 设置底部创建按钮（用户有Persona时显示的）的点击事件
        fragmentUserPersonaBinding.fabCreatePersona.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserPersonaCreateActivity.class);
            startActivity(intent);
        });

        // 观察用户UserPersona列表的变化，自动更新UI
        userPersonaViewModel.getUserPersonas().observe(getViewLifecycleOwner(), new Observer<List<UserPersona>>() {
            @Override
            public void onChanged(List<UserPersona> userPersonas) {
                // 更新适配器数据
                userPersonaListAdapter.submitList(userPersonas);
                // 设置初始UI
                if (userPersonas == null || userPersonas.isEmpty()) {
                    // 没有Persona时显示空状态界面
                    fragmentUserPersonaBinding.groupEmptyState.setVisibility(View.VISIBLE);
                    fragmentUserPersonaBinding.rvPersonaList.setVisibility(View.GONE);
                    fragmentUserPersonaBinding.fabCreatePersona.setVisibility(View.GONE);
                } else {
                    // 有Persona时显示UserPersona列表
                    fragmentUserPersonaBinding.groupEmptyState.setVisibility(View.GONE);
                    fragmentUserPersonaBinding.rvPersonaList.setVisibility(View.VISIBLE);
                    fragmentUserPersonaBinding.fabCreatePersona.setVisibility(View.VISIBLE);
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
        fragmentUserPersonaBinding = null; // 避免内存泄漏
    }
}