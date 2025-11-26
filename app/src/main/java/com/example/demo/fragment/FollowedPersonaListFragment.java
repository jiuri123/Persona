package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.adapter.FollowedPersonaListAdapter;
import com.example.demo.model.Persona;
import com.example.demo.databinding.FragmentFollowedListBinding;
import com.example.demo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注列表Fragment
 * 显示用户已关注的Persona列表
 * 使用ViewModel管理数据，通过LiveData观察数据变化
 */
public class FollowedPersonaListFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentFollowedListBinding binding;
    // 关注列表适配器，用于显示已关注的Persona
    private FollowedPersonaListAdapter adapter;
    // 主ViewModel，用于管理应用的全局状态和数据
    private MainViewModel mainViewModel;
    // 已关注的Persona数据列表
    private List<Persona> followedPersonaList;

    // 默认构造函数
    public FollowedPersonaListFragment() {
    }

    /**
     * Fragment创建时调用
     * 初始化ViewModel和错误观察者
     * @param savedInstanceState 保存的Fragment状态
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 获取与Activity关联的ViewModel实例
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        // 观察错误消息，当有错误时显示Toast
        mainViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    mainViewModel.clearError(); // 清除错误消息
                }
            }
        });
    }

    /**
     * 创建Fragment的视图
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的Fragment状态
     * @return 创建的视图
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 使用视图绑定创建布局
        binding = FragmentFollowedListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * 视图创建完成后调用
     * 初始化RecyclerView和适配器，观察数据变化
     * @param view 创建的视图
     * @param savedInstanceState 保存的Fragment状态
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置RecyclerView的布局管理器
        binding.rvFollowedList.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 初始化已关注Persona列表
        followedPersonaList = new ArrayList<>();
        loadFollowedPersonas(); // 加载已关注的Persona数据
        
        // 创建并设置适配器
        adapter = new FollowedPersonaListAdapter(getContext(), followedPersonaList);
        binding.rvFollowedList.setAdapter(adapter);
        
        // 观察已关注Persona列表的变化
        mainViewModel.getFollowedPersonas().observe(getViewLifecycleOwner(), new Observer<List<Persona>>() {
            @Override
            public void onChanged(List<Persona> personas) {
                if (personas != null) {
                    // 更新本地列表数据
                    followedPersonaList.clear();
                    followedPersonaList.addAll(personas);
                    adapter.notifyDataSetChanged(); // 通知适配器数据已变化
                    
                    // 根据数据是否为空显示不同的UI状态
                    if (personas.isEmpty()) {
                        // 显示空状态视图
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.rvFollowedList.setVisibility(View.GONE);
                    } else {
                        // 显示列表视图
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.rvFollowedList.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    
    /**
     * 加载已关注的Persona数据
     * 此方法为空实现，实际数据通过ViewModel的LiveData获取
     */
    private void loadFollowedPersonas() {
        // 实际数据通过ViewModel的LiveData获取，这里不需要实现
    }

    /**
     * Fragment视图销毁时调用
     * 清理视图绑定，避免内存泄漏
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 清理视图绑定
    }
}