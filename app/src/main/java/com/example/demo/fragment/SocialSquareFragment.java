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

import com.example.demo.repository.OtherPersonaPostRepository;
import com.example.demo.model.Post;
import com.example.demo.model.Persona;
import com.example.demo.adapter.SocialSquarePostAdapter;
import com.example.demo.databinding.FragmentSocialSquareBinding;
import com.example.demo.viewmodel.PostGenerationViewModel;
import com.example.demo.viewmodel.SharedViewModel;
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
    private PostGenerationViewModel postGenerationViewModel;
    // ViewModel，用于管理关注列表
    private FollowedPersonaListViewModel followedPersonaListViewModel;
    // Repository，用于管理帖子数据
    private OtherPersonaPostRepository postRepository;

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
        postGenerationViewModel = new ViewModelProvider(requireActivity()).get(PostGenerationViewModel.class);
        followedPersonaListViewModel = new ViewModelProvider(requireActivity()).get(FollowedPersonaListViewModel.class);
        
        // 初始化Repository
        postRepository = new OtherPersonaPostRepository();

        // 观察错误信息，当有错误时显示Toast
        postGenerationViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    postGenerationViewModel.clearError(); // 清除错误信息
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

        // 获取帖子数据
        postList = postRepository.getSocialPosts().getValue();

        // 创建适配器并设置ViewModel
        adapter = new SocialSquarePostAdapter(getContext(), postList);
        adapter.setPostGenerationViewModel(postGenerationViewModel);
        adapter.setSharedViewModel(SharedViewModel.getInstance());
        adapter.setFollowedPersonaViewModel(followedPersonaListViewModel);
        binding.rvSocialSquare.setAdapter(adapter);

        // 设置观察者
        setupViewObservers();

        // 设置添加帖子按钮的点击事件
        binding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过ViewModel生成新帖子，不再需要传递当前用户Persona
                postGenerationViewModel.generateNewPost();
            }
        });
    }

    /**
     * 设置视图观察者
     * 观察ViewModel中的LiveData变化
     */
    private void setupViewObservers() {

        // 观察新帖子的LiveData，当有新帖子时添加到列表顶部
        postGenerationViewModel.getNewPostLiveData().observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post newPost) {
                if (newPost != null && adapter != null) {
                    adapter.addPostAtTop(newPost); // 在列表顶部添加新帖子
                    binding.rvSocialSquare.scrollToPosition(0); // 滚动到顶部显示新帖子
                }
            }
        });

        // 观察加载状态，根据加载状态启用或禁用添加按钮
        postGenerationViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                binding.fabAddPost.setEnabled(!isLoading); // 加载时禁用按钮
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