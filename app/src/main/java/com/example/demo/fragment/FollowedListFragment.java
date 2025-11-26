package com.example.demo;

import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.databinding.FragmentFollowedListBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 已关注列表 Fragment
 */
public class FollowedListFragment extends Fragment {

    private FragmentFollowedListBinding binding;
    private FollowedPersonaAdapter adapter;
    private MainViewModel mainViewModel;
    private List<Persona> followedPersonaList;

    public FollowedListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 初始化 MainViewModel
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        // 观察错误事件
        mainViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    mainViewModel.clearError();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFollowedListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化RecyclerView
        binding.rvFollowedList.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 初始化已关注列表数据
        followedPersonaList = new ArrayList<>();
        loadFollowedPersonas();
        
        // 创建并设置Adapter
        adapter = new FollowedPersonaAdapter(getContext(), followedPersonaList);
        binding.rvFollowedList.setAdapter(adapter);
        
        // 观察已关注列表的变化
        mainViewModel.getFollowedPersonas().observe(getViewLifecycleOwner(), new Observer<List<Persona>>() {
            @Override
            public void onChanged(List<Persona> personas) {
                if (personas != null) {
                    followedPersonaList.clear();
                    followedPersonaList.addAll(personas);
                    adapter.notifyDataSetChanged();
                    
                    // 如果没有关注的Persona，显示空状态
                    if (personas.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.rvFollowedList.setVisibility(View.GONE);
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.rvFollowedList.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    
    /**
     * 初始化已关注列表数据
     */
    private void loadFollowedPersonas() {
        // 已关注列表现在通过MainViewModel的LiveData来管理
        // 不需要手动加载，数据会通过观察者自动更新
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}