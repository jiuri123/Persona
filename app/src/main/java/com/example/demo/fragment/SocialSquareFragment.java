package com.example.demo.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;


import com.example.demo.activity.UserPostCreateActivity;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.adapter.SocialSquarePostAdapter;
import com.example.demo.databinding.FragmentSocialSquareBinding;
import com.example.demo.viewmodel.SocialSquareViewModel;

import java.util.List;

/**
 * 社交广场Fragment
 * 显示Persona发布的帖子列表
 * 实现了添加新帖子的功能，并使用ViewModel管理数据和状态
 * 实现OnFollowActionListener接口处理关注/取消关注操作
 */
public class SocialSquareFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentSocialSquareBinding fragmentSocialSquareBinding;

    // ViewModel，用于管理社交广场的所有数据和业务逻辑
    private SocialSquareViewModel socialSquareViewModel;
    
    // 帖子数据列表，包括用户的帖子和其他persona的帖子
    private List<Post> postList;
    
    // 适配器，用于绘制社交广场的帖子列表
    private SocialSquarePostAdapter socialSquarePostAdapter;

    /**
     * 构造函数
     */
    public SocialSquareFragment() {
    }

    /**
     * Fragment创建时调用
     * 初始化ViewModel和错误观察者
     * @param savedInstanceState 保存的Fragment状态
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取与Activity关联的SocialSquareViewModel实例
        socialSquareViewModel = new ViewModelProvider(requireActivity()).get(SocialSquareViewModel.class);

        // 观察错误信息，当有错误时显示Toast
        socialSquareViewModel.getErrorLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    socialSquareViewModel.clearError(); // 清除错误信息
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
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 使用视图绑定创建布局
        fragmentSocialSquareBinding = FragmentSocialSquareBinding.inflate(inflater, container, false);

        return fragmentSocialSquareBinding.getRoot();
    }
    
    /**
     * 视图创建完成后调用
     * 初始化RecyclerView、适配器和观察者
     * @param view 创建的视图
     * @param savedInstanceState 保存的Fragment状态
    */
   @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 设置RecyclerView的布局管理器为线性布局
        fragmentSocialSquareBinding.rvSocialSquare.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 初始化为空列表，后续由ViewModel更新
        postList = new java.util.ArrayList<>();

        // 创建适配器并设置回调接口
        socialSquarePostAdapter = new SocialSquarePostAdapter(getContext(), postList);
        socialSquarePostAdapter.setOnFollowActionListener(new SocialSquarePostAdapter.OnFollowClickListener() {
            @Override
            public void onFollowClick(Persona persona) {
                socialSquareViewModel.onFollowClick(persona);
            }
        });
        fragmentSocialSquareBinding.rvSocialSquare.setAdapter(socialSquarePostAdapter);

        // 设置观察者
        setupViewObservers();

        // 设置添加帖子按钮的点击事件
        fragmentSocialSquareBinding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到发布动态编辑页面
                Intent intent = new Intent(getContext(), UserPostCreateActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置视图观察者
     * 观察SocialSquareViewModel中的LiveData变化
     */
    private void setupViewObservers() {
        // 观察合并后的帖子列表变化
        socialSquareViewModel.getMergedPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (posts != null && socialSquarePostAdapter != null) {
                    // 更新适配器的数据
                    socialSquarePostAdapter.updatePosts(posts);
                    
                    // 如果是新添加的帖子（在列表顶部），滚动到顶部
                    if (!posts.isEmpty()) {
                        fragmentSocialSquareBinding.rvSocialSquare.scrollToPosition(0);
                    }
                }
            }
        });

        // 观察加载状态变化
        socialSquareViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // 加载时禁用添加按钮
                fragmentSocialSquareBinding.fabAddPost.setEnabled(!isLoading);
            }
        });
        
        // 观察已关注persona列表的变化
        socialSquareViewModel.getFollowedPersonasLiveData().observe(getViewLifecycleOwner(), new Observer<List<com.example.demo.model.Persona>>() {
            @Override
            public void onChanged(List<com.example.demo.model.Persona> followedPersonas) {
                // 当关注列表发生变化时，通知适配器更新所有项目的关注状态
                if (socialSquarePostAdapter != null) {
                    socialSquarePostAdapter.updateFollowedList(followedPersonas);
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
        fragmentSocialSquareBinding = null; // 避免内存泄漏
    }
}