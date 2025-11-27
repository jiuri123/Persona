package com.example.demo.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import com.example.demo.model.Post;
import com.example.demo.adapter.SocialSquarePostAdapter;
import com.example.demo.databinding.FragmentSocialSquareBinding;
import com.example.demo.viewmodel.MyPersonaViewModel;
import com.example.demo.viewmodel.OtherPersonaPostViewModel;
import com.example.demo.viewmodel.FollowedPersonaListViewModel;

import java.util.List;

/**
 * 社交广场Fragment
 * 显示Persona发布的帖子列表
 * 实现了添加新帖子的功能，并使用ViewModel管理数据和状态
 */
public class SocialSquareFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentSocialSquareBinding binding;

    // 适配器，用于管理帖子列表
    private SocialSquarePostAdapter adapter;
    // 帖子数据列表
    private List<Post> postList;
    // ViewModel，用于管理动态生成
    private MyPersonaViewModel myPersonaViewModel;
    // ViewModel，用于管理关注列表
    private FollowedPersonaListViewModel followedPersonaListViewModel;
    // ViewModel，用于管理社交广场数据
    private OtherPersonaPostViewModel otherPersonaPostViewModel;

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

        // 获取与Activity关联的ViewModel实例
        myPersonaViewModel = new ViewModelProvider(requireActivity()).get(MyPersonaViewModel.class);
        followedPersonaListViewModel = new ViewModelProvider(requireActivity()).get(FollowedPersonaListViewModel.class);
        otherPersonaPostViewModel = new ViewModelProvider(requireActivity()).get(OtherPersonaPostViewModel.class);

        // 观察错误信息，当有错误时显示Toast
        myPersonaViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    myPersonaViewModel.clearError(); // 清除错误信息
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
        binding = FragmentSocialSquareBinding.inflate(inflater, container, false);

        return binding.getRoot();
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
        binding.rvSocialSquare.setLayoutManager(new LinearLayoutManager(getContext()));

        // 获取其他Persona的帖子数据
        List<Post> otherPosts = otherPersonaPostViewModel.getOtherPostsLiveData().getValue();
        
        // 获取我的历史帖子数据
        List<Post> myPosts = myPersonaViewModel.getMyPostsLiveData().getValue();
        
        // 合并两个数据源的帖子
        if (otherPosts != null && myPosts != null) {
            // 创建合并后的列表，将我的帖子放在前面
            postList = new java.util.ArrayList<>();
            postList.addAll(myPosts); // 先添加我的帖子
            postList.addAll(otherPosts); // 再添加其他Persona的帖子
        } else if (otherPosts != null) {
            postList = otherPosts;
        } else if (myPosts != null) {
            postList = myPosts;
        } else {
            postList = new java.util.ArrayList<>();
        }

        // 创建适配器并设置ViewModel
        adapter = new SocialSquarePostAdapter(getContext(), postList);
        adapter.setFollowedPersonaViewModel(followedPersonaListViewModel);
        binding.rvSocialSquare.setAdapter(adapter);

        // 设置观察者
        setupViewObservers();

        // 设置添加帖子按钮的点击事件
        binding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过ViewModel生成新帖子，不再需要传递当前用户Persona
                myPersonaViewModel.generateNewPost();
            }
        });
    }

    /**
     * 设置视图观察者
     * 观察ViewModel中的LiveData变化
     */
    private void setupViewObservers() {

        // 观察我的历史帖子数据的变化
        myPersonaViewModel.getMyPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> myPosts) {
                if (myPosts != null && adapter != null) {
                    // 获取其他Persona的帖子
                    List<Post> otherPosts = otherPersonaPostViewModel.getOtherPostsLiveData().getValue();
                    
                    // 合并两个数据源的帖子
                    List<Post> mergedPosts = new java.util.ArrayList<>();
                    mergedPosts.addAll(myPosts); // 先添加我的帖子
                    if (otherPosts != null) {
                        mergedPosts.addAll(otherPosts); // 再添加其他Persona的帖子
                    }
                    
                    // 更新适配器的数据
                    adapter.updatePosts(mergedPosts);
                    
                    // 如果是新添加的帖子（在列表顶部），滚动到顶部
                    if (!myPosts.isEmpty()) {
                        binding.rvSocialSquare.scrollToPosition(0);
                    }
                }
            }
        });

        // 观察社交广场数据的变化
        otherPersonaPostViewModel.getOtherPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (posts != null && adapter != null) {
                    // 获取我的历史帖子
                    List<Post> myPosts = myPersonaViewModel.getMyPostsLiveData().getValue();
                    
                    // 合并两个数据源的帖子
                    List<Post> mergedPosts = new java.util.ArrayList<>();
                    if (myPosts != null) {
                        mergedPosts.addAll(myPosts); // 先添加我的帖子
                    }
                    mergedPosts.addAll(posts); // 再添加其他Persona的帖子
                    
                    // 更新适配器的数据
                    adapter.updatePosts(mergedPosts);
                }
            }
        });

        // 观察加载状态，根据加载状态启用或禁用添加按钮
        myPersonaViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                binding.fabAddPost.setEnabled(!isLoading); // 加载时禁用按钮
            }
        });
        
        // 观察关注列表的变化，当关注列表更新时刷新适配器
        followedPersonaListViewModel.getFollowedPersonas().observe(getViewLifecycleOwner(), new Observer<List<com.example.demo.model.Persona>>() {
            @Override
            public void onChanged(List<com.example.demo.model.Persona> followedPersonas) {
                // 当关注列表发生变化时，通知适配器更新所有项目的关注状态
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
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