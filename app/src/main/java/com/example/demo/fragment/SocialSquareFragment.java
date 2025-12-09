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
import androidx.recyclerview.widget.RecyclerView;


import com.example.demo.activity.UserPostCreateActivity;
import com.example.demo.model.OtherPersona;
import com.example.demo.model.PostUiItem;
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

    // 适配器，用于绘制社交广场的帖子列表
    private SocialSquarePostAdapter socialSquarePostAdapter;

    // 用户是否已创建Persona
    private boolean mHasUserPersona = false;

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

        // 创建社交广场展示帖子的适配器
        socialSquarePostAdapter = new SocialSquarePostAdapter(getContext());

        //  添加数据观察者，用于监听适配器数据变化，当有数据插入时滚动到顶部
        socialSquarePostAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
               // 核心判断：只有当插入位置是 0 (顶部) 时，才滚动到顶部
               // 加载更多时，positionStart 会是 10, 20 等，就不会触发滚动
               if (positionStart == 0) {
                   fragmentSocialSquareBinding.rvSocialSquare.scrollToPosition(0);
               }
            }
        });

        // 设置社交广场的关注按钮回调接口
        socialSquarePostAdapter.setOnFollowActionListener(new SocialSquarePostAdapter.OnFollowClickListener() {
                @Override
                public void onFollowClick(OtherPersona otherPersona) {
                    socialSquareViewModel.onFollowClick(otherPersona);
                }
            });

        // 设置适配器
        fragmentSocialSquareBinding.rvSocialSquare.setAdapter(socialSquarePostAdapter);

        // 设置观察者
        setupViewObservers();

        // 设置添加帖子按钮的点击事件
        fragmentSocialSquareBinding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检查用户是否有Persona
                if (mHasUserPersona) {
                    // 如果有Persona，跳转到发布动态编辑页面
                    Intent intent = new Intent(getContext(), UserPostCreateActivity.class);
                    startActivity(intent);
                } else {
                    // 如果没有Persona，提示用户先创建Persona，不再跳转到创建页面
                    Toast.makeText(getContext(), "请先创建Persona", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 设置视图观察者
     * 观察SocialSquareViewModel中的LiveData变化
     */
    private void setupViewObservers() {
        // 观察合并后的帖子UI列表变化
        socialSquareViewModel.getMergedPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<PostUiItem>>() {
            @Override
            public void onChanged(List<PostUiItem> postUiItems) {
                if (postUiItems != null && socialSquarePostAdapter != null) {
                    // 更新适配器的数据，使用带回调的重载方法
                    socialSquarePostAdapter.submitList(postUiItems);
                }
            }
        });

        // (响应式) 观察“是否已创建Persona”的状态
        socialSquareViewModel.getHasUserPersonaState().observe(getViewLifecycleOwner(), hasPersona -> {
            // 持续更新 Fragment 中保存的本地状态
            this.mHasUserPersona = hasPersona;

            // (可选) 也可以根据这个状态来启用/禁用按钮
            // fragmentSocialSquareBinding.fabAddPost.setEnabled(hasPersona);
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