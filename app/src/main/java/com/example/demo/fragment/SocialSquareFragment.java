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

import com.example.demo.callback.UserFollowActionListener;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.adapter.SocialSquarePostAdapter;
import com.example.demo.databinding.FragmentSocialSquareBinding;
import com.example.demo.viewmodel.UserPersonaViewModel;
import com.example.demo.viewmodel.OtherPersonaPostViewModel;
import com.example.demo.viewmodel.UserFollowedListViewModel;

import java.util.List;

/**
 * 社交广场Fragment
 * 显示Persona发布的帖子列表
 * 实现了添加新帖子的功能，并使用ViewModel管理数据和状态
 * 实现OnFollowActionListener接口处理关注/取消关注操作
 */
public class SocialSquareFragment extends Fragment implements UserFollowActionListener {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentSocialSquareBinding fragmentSocialSquareBinding;

    // ViewModel，用于管理用户已关注的persona列表
    private UserFollowedListViewModel userFollowedListViewModel;
    // ViewModel，用于管理用户创建的persona的动态生成
    private UserPersonaViewModel userPersonaViewModel;
    
    // ViewModel，用于管理其他persona的动态生成
    private OtherPersonaPostViewModel otherPersonaPostViewModel;
    
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

        // 获取与Activity关联的ViewModel实例
        userPersonaViewModel = new ViewModelProvider(requireActivity()).get(UserPersonaViewModel.class);
        otherPersonaPostViewModel = new ViewModelProvider(requireActivity()).get(OtherPersonaPostViewModel.class);
        userFollowedListViewModel = new ViewModelProvider(requireActivity()).get(UserFollowedListViewModel.class);

        // 观察错误信息，当有错误时显示Toast
        userPersonaViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    userPersonaViewModel.clearError(); // 清除错误信息
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
        
       // 获取其他Persona的帖子数据
        List<Post> otherPosts = otherPersonaPostViewModel.getOtherPostsLiveData().getValue();
        
        // 获取我的历史帖子数据
        List<Post> myPosts = userPersonaViewModel.getMyPostsLiveData().getValue();
        
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

        // 创建适配器并设置回调接口
        socialSquarePostAdapter = new SocialSquarePostAdapter(getContext(), postList);
        socialSquarePostAdapter.setOnFollowActionListener(this);
        fragmentSocialSquareBinding.rvSocialSquare.setAdapter(socialSquarePostAdapter);

        // 设置观察者
        setupViewObservers();

        // 设置添加帖子按钮的点击事件
        fragmentSocialSquareBinding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过ViewModel生成新帖子，不再需要传递当前用户Persona
                userPersonaViewModel.generateNewPost();
            }
        });
    }

    /**
     * 设置视图观察者
     * 观察ViewModel中的LiveData变化
     */
    private void setupViewObservers() {

        // 观察我的persona历史帖子数据的变化
        userPersonaViewModel.getMyPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> myPosts) {
                if (myPosts != null && socialSquarePostAdapter != null) {
                    // 获取其他Persona的帖子
                    List<Post> otherPosts = otherPersonaPostViewModel.getOtherPostsLiveData().getValue();
                    
                    // 合并两个数据源的帖子
                    List<Post> mergedPosts = new java.util.ArrayList<>();
                    mergedPosts.addAll(myPosts); // 先添加我的帖子
                    if (otherPosts != null) {
                        mergedPosts.addAll(otherPosts); // 再添加其他Persona的帖子
                    }
                    
                    // 更新适配器的数据
                    socialSquarePostAdapter.updatePosts(mergedPosts);
                    
                    // 如果是新添加的帖子（在列表顶部），滚动到顶部
                    if (!myPosts.isEmpty()) {
                        fragmentSocialSquareBinding.rvSocialSquare.scrollToPosition(0);
                    }
                }
            }
        });

        // 观察其他persona的帖子的变化
        otherPersonaPostViewModel.getOtherPostsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                if (posts != null && socialSquarePostAdapter != null) {
                    // 获取我的历史帖子
                    List<Post> myPosts = userPersonaViewModel.getMyPostsLiveData().getValue();
                    
                    // 合并两个数据源的帖子
                    List<Post> mergedPosts = new java.util.ArrayList<>();
                    if (myPosts != null) {
                        mergedPosts.addAll(myPosts); // 先添加我的帖子
                    }
                    mergedPosts.addAll(posts); // 再添加其他Persona的帖子
                    
                    // 更新适配器的数据
                    socialSquarePostAdapter.updatePosts(mergedPosts);
                }
            }
        });

        // 观察我的persona生成帖子的状态，没生成好就禁用添加按钮，生成好了就启用按钮
        userPersonaViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                fragmentSocialSquareBinding.fabAddPost.setEnabled(!isLoading); // 加载时禁用按钮
            }
        });
        
        // 观察已关注persona列表的变化，当已关注列表更新时刷新适配器
        userFollowedListViewModel.getFollowedPersonas().observe(getViewLifecycleOwner(), new Observer<List<com.example.demo.model.Persona>>() {
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

    /**
     * 实现OnFollowActionListener接口：处理关注按钮点击事件
     * @param persona 被点击的Persona对象
     */
    @Override
    public void onFollowClick(Persona persona) {
        if (userFollowedListViewModel != null) {
            // 通过ViewModel检查当前关注状态
            boolean currentlyFollowed = userFollowedListViewModel.isFollowingPersona(persona);
            
            if (currentlyFollowed) {
                // 如果已关注，则取消关注
                userFollowedListViewModel.removeFollowedPersona(persona);
            } else {
                // 如果未关注，则添加关注
                userFollowedListViewModel.addFollowedPersona(persona);
            }
        }
    }
}