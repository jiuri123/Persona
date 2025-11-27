package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.R;
import com.example.demo.databinding.ItemPersonaPostBinding;
import com.example.demo.activity.OtherPersonaChatActivity;
import com.example.demo.viewmodel.MyPersonaPostViewModel;
import com.example.demo.viewmodel.SharedViewModel;
import com.example.demo.viewmodel.FollowedPersonaListViewModel;

import java.util.List;

// Markwon库用于在Android中渲染Markdown文本
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

/**
 * 社交广场帖子适配器
 * 用于在RecyclerView中显示Persona发布的帖子
 * 实现了点击头像/名称跳转聊天界面、关注/取消关注功能
 */
public class SocialSquarePostAdapter extends RecyclerView.Adapter<SocialSquarePostAdapter.PostViewHolder> {
    // 帖子数据列表
    private List<Post> postList;
    // 上下文，用于启动Activity和加载资源
    private Context context;
    // ViewModel，用于处理关注列表
    private FollowedPersonaListViewModel followedPersonaListViewModel;

    /**
     * 构造函数
     * @param context 上下文
     * @param postList 帖子数据列表
     */
    public SocialSquarePostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }
    
    /**
     * 设置关注的Persona列表视图模型
     * 
     * @param followedPersonaListViewModel 关注的Persona列表视图模型对象，用于存储和展示关注的Persona相关信息
     */
    public void setFollowedPersonaViewModel(FollowedPersonaListViewModel followedPersonaListViewModel) {
        // 将传入的关注的Persona列表视图模型对象赋值给当前实例的成员变量
        this.followedPersonaListViewModel = followedPersonaListViewModel;
    }

    /**
     * 在列表顶部添加新帖子
     * @param post 要添加的帖子
     */
    public void addPostAtTop(Post post) {
        if (postList != null) {
            postList.add(0, post); // 在索引0处添加，即列表顶部
            notifyItemInserted(0); // 通知适配器在位置0插入了新项
        }
    }

    /**
     * 更新整个帖子列表
     * @param newPosts 新的帖子列表
     */
    public void updatePosts(List<Post> newPosts) {
        this.postList = newPosts;
        notifyDataSetChanged(); // 通知适配器整个列表已更新
    }

    /**
     * 创建ViewHolder
     * RecyclerView会调用此方法创建新的ViewHolder实例
     * @param parent 父视图组
     * @param viewType 视图类型
     * @return 新创建的PostViewHolder
     */
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 使用视图绑定创建布局
        ItemPersonaPostBinding binding = ItemPersonaPostBinding.inflate(inflater, parent, false);
        return new PostViewHolder(binding);
    }

    /**
     * 绑定数据到ViewHolder
     * RecyclerView会调用此方法将数据绑定到指定位置的ViewHolder
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    /**
     * 获取列表项总数
     * @return 列表项数量
     */
    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    /**
     * 帖子ViewHolder类
     * 持有单个帖子项的所有视图，并负责数据绑定和事件处理
     */
    public class PostViewHolder extends RecyclerView.ViewHolder {

        // 视图绑定对象，用于访问布局中的各个组件
        private final ItemPersonaPostBinding binding;
        
        // Markwon实例，用于渲染Markdown文本
        private final Markwon markwon;
        
        /**
         * ViewHolder构造函数
         * @param binding 视图绑定对象
         */
        public PostViewHolder(ItemPersonaPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            // 初始化Markwon，配置各种插件支持Markdown特性
            markwon = Markwon.builder(itemView.getContext())
                    .usePlugin(StrikethroughPlugin.create()) // 支持删除线
                    .usePlugin(TablePlugin.create(itemView.getContext())) // 支持表格
                    .usePlugin(TaskListPlugin.create(itemView.getContext())) // 支持任务列表
                    .usePlugin(LinkifyPlugin.create()) // 支持自动链接识别
                    .build();
        }

        /**
         * 绑定帖子数据到视图
         * @param post 要显示的帖子对象
         */
        public void bind(Post post) {
            Persona author = post.getAuthor();
            // 设置作者名称和简介
            binding.tvAuthorName.setText(author.getName());
            binding.tvAuthorBioOrTime.setText(author.getBio());

            // 创建点击监听器，用于点击头像或作者名称时跳转到聊天界面
            View.OnClickListener startChatListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherPersonaChatActivity.class);
                    // 通过Intent传递Persona对象
                    intent.putExtra(OtherPersonaChatActivity.EXTRA_PERSONA, author);
                    context.startActivity(intent);
                }
            };

            // 为头像和作者名称设置点击监听器
            binding.ivAvatar.setOnClickListener(startChatListener);
            binding.tvAuthorName.setOnClickListener(startChatListener);

            // 使用Markwon渲染Markdown内容
            markwon.setMarkdown(binding.tvContentText, post.getContentText());
            
            // 使用Glide加载头像
            Glide.with(context)
                    .load(author.getAvatarDrawableId())
                    .placeholder(R.drawable.ic_launcher_background) // 占位图
                    .circleCrop() // 圆形裁剪
                    .into(binding.ivAvatar);
            
            // 如果帖子有图片，则显示并加载
            if (post.getImageDrawableId() != null) {
                binding.ivPostImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getImageDrawableId())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.ivPostImage);
            } else {
                // 没有图片则隐藏图片视图
                binding.ivPostImage.setVisibility(View.GONE);
            }

            String authorName = author.getName();

            // 通过ViewModel检查是否已关注该作者
            boolean isFollowed = followedPersonaListViewModel != null && 
                               followedPersonaListViewModel.isFollowingPersonaByName(authorName);
            updateButtonState(isFollowed);

            // 设置关注按钮的点击事件
            binding.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (followedPersonaListViewModel != null) {
                        // 通过ViewModel检查当前关注状态
                        boolean currentlyFollowed = followedPersonaListViewModel.isFollowingPersonaByName(authorName);
                        
                        if (currentlyFollowed) {
                            // 如果已关注，则取消关注
                            followedPersonaListViewModel.removeFollowedPersona(author);
                        } else {
                            // 如果未关注，则添加关注
                            followedPersonaListViewModel.addFollowedPersona(author);
                        }
                    }
                }
            });
        }

        /**
         * 更新关注按钮的状态
         * @param isFollowed 是否已关注
         */
        private void updateButtonState(boolean isFollowed) {
            if (isFollowed) {
                binding.btnFollow.setText("已关注");
                // 设置已关注状态的颜色（灰色）
                binding.btnFollow.setBackgroundColor(context.getResources().getColor(R.color.gray));
                binding.btnFollow.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                binding.btnFollow.setText("关注");
                // 设置未关注状态的颜色（使用主题色或紫色）
                binding.btnFollow.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
                binding.btnFollow.setTextColor(context.getResources().getColor(R.color.white));
            }
        }
    }
}