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
import com.example.demo.model.Persona;
import com.example.demo.databinding.FragmentMyPersonaBinding;
import com.example.demo.adapter.UserPersonaListAdapter;

import java.util.List;

/**
 * 我的Persona Fragment
 * 显示用户创建的所有Persona列表
 * 如果用户没有创建Persona，则显示创建Persona的引导界面
 */
public class UserPersonaFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentMyPersonaBinding fragmentMyPersonaBinding;
    // Persona列表适配器，用于显示Persona列表
    private UserPersonaListAdapter userPersonaListAdapter;
    // 我的Persona和聊天ViewModel
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
        userPersonaViewModel = new ViewModelProvider(this).get(UserPersonaViewModel.class);

        // 初始化Persona列表适配器
        userPersonaListAdapter = new UserPersonaListAdapter(requireContext(), userPersonaViewModel.getUserPersonas().getValue());
        // 设置删除Persona的回调接口
        userPersonaListAdapter.setOnPersonaDeleteListener(new UserPersonaListAdapter.OnPersonaDeleteListener() {
            @Override
            public void onPersonaDelete(Persona persona) {
                // 删除Persona
                userPersonaViewModel.removeUserPersona(persona);
            }
        });
        fragmentMyPersonaBinding.rvPersonaList.setAdapter(userPersonaListAdapter);

        // 观察用户Persona列表的变化，自动更新UI
        userPersonaViewModel.getUserPersonas().observe(getViewLifecycleOwner(), new Observer<List<Persona>>() {
            @Override
            public void onChanged(List<Persona> personas) {
                // 更新适配器数据
                userPersonaListAdapter.updateData(personas);
                // 更新UI显示
                setupUI(personas);
            }
        });

        // 设置创建Persona按钮的点击事件
        fragmentMyPersonaBinding.btnGoToCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserPersonaCreateActivity.class);
            startActivity(intent);
        });

        // 设置底部创建按钮的点击事件
        fragmentMyPersonaBinding.fabCreatePersona.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UserPersonaCreateActivity.class);
            startActivity(intent);
        });

        // 设置初始UI
        setupUI(userPersonaViewModel.getUserPersonas().getValue());
    }

    /**
     * 当Persona创建完成时调用
     * @param persona 新创建的Persona对象
     */
    public void onPersonaCreated(Persona persona) {
        // 通过UserPersonaViewModel将Persona添加到Repository
        userPersonaViewModel.addUserPersona(persona);
    }

    /**
     * 设置UI界面
     * 根据是否有Persona显示不同的界面
     * @param personas 用户的Persona列表
     */
    private void setupUI(List<Persona> personas) {
        if (personas == null || personas.isEmpty()) {
            // 没有Persona时显示空状态界面
            fragmentMyPersonaBinding.groupEmptyState.setVisibility(View.VISIBLE);
            fragmentMyPersonaBinding.rvPersonaList.setVisibility(View.GONE);
            fragmentMyPersonaBinding.fabCreatePersona.setVisibility(View.GONE);
        } else {
            // 有Persona时显示Persona列表
            fragmentMyPersonaBinding.groupEmptyState.setVisibility(View.GONE);
            fragmentMyPersonaBinding.rvPersonaList.setVisibility(View.VISIBLE);
            fragmentMyPersonaBinding.fabCreatePersona.setVisibility(View.VISIBLE);
        }
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