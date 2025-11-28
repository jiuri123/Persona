package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.activity.OtherPersonaChatActivity;
import com.example.demo.callback.SocialSquareFollowActionListener;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.databinding.ItemPersonaPostBinding;

// Markwon库用于在Android中渲染Markdown文本
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 社交广场帖子适配器
 * 用于在RecyclerView中显示Persona发布的帖子
 * 实现了点击头像/名称跳转聊天界面、关注/取消关注功能
 */
public class SocialSquarePostAdapter extends RecyclerView.Adapter<SocialSquarePostAdapter.PostViewHolder> {
    // 帖子数据列表
    private List<Post> postList;
    // 已关注的Persona的名称集合
    private Set<String> followedPersonaNames = new HashSet<>();
    // 上下文，用于启动Activity和加载资源
    private Context context;
    // 关注操作回调接口
    private SocialSquareFollowActionListener socialSquareFollowActionListener;

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
     * 更新已关注的Persona列表
     * 用于在适配器中显示已关注的Persona的帖子
     * @param followedPersonas 已关注的Persona列表
     */
    public void updateFollowedList(List<Persona> followedPersonas) {
        followedPersonaNames.clear();
        for (Persona p : followedPersonas) {
            followedPersonaNames.add(p.getName());
        }
        notifyDataSetChanged(); // 触发刷新
    }
    
    /**
     * 设置关注操作回调接口
     * 
     * @param socialSquareFollowActionListener 关注操作回调接口实现
     */
    public void setOnFollowActionListener(SocialSquareFollowActionListener socialSquareFollowActionListener) {
        this.socialSquareFollowActionListener = socialSquareFollowActionListener;
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
        ItemPersonaPostBinding itemPersonaPostBinding = ItemPersonaPostBinding.inflate(inflater, parent, false);
        return new PostViewHolder(itemPersonaPostBinding);
    }

    /**
     * 绑定数据到ViewHolder，列表首次加载（启动软件）、列表滚动、
     * 特定项被通知更新或调用 notifyDataSetChanged() 时调用
     * 该函数的调用与"视图即将变得可见"这一事件强相关
     * RecyclerView会调用此方法将数据绑定到指定位置的ViewHolder
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // 获取刚刚进入用户视图的那条帖子
        Post post = postList.get(position);
        // 将该帖子绑定到视图，更新视图上的显示内容
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
        private final ItemPersonaPostBinding itemPersonaPostBinding;
        
        // Markwon实例，用于渲染Markdown文本
        private final Markwon markwon;
        
        /**
         * ViewHolder构造函数
         * @param itemPersonaPostBinding 视图绑定对象
         */
        public PostViewHolder(ItemPersonaPostBinding itemPersonaPostBinding) {
            super(itemPersonaPostBinding.getRoot());
            this.itemPersonaPostBinding = itemPersonaPostBinding;
            
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
            itemPersonaPostBinding.tvAuthorName.setText(author.getName());
            itemPersonaPostBinding.tvAuthorBioOrTime.setText(author.getBio());

            // 使用Markwon将帖子的内容渲染成Markdown
            markwon.setMarkdown(itemPersonaPostBinding.tvContentText, post.getContentText());
            
            // 使用Glide加载头像
            Glide.with(context)
                    .load(author.getAvatarDrawableId())
                    .placeholder(R.drawable.ic_launcher_background) // 占位图
                    .circleCrop() // 圆形裁剪
                    .into(itemPersonaPostBinding.ivAvatar);
            
            // 如果帖子有图片，则显示并加载图片
            if (post.getImageDrawableId() != null) {
                itemPersonaPostBinding.ivPostImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getImageDrawableId())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(itemPersonaPostBinding.ivPostImage);
            } else {
                // 没有图片则隐藏图片视图
                itemPersonaPostBinding.ivPostImage.setVisibility(View.GONE);
            }

            // 如果是自己的帖子，隐藏关注按钮
            if (post.isUserPersonaPost()) {
                itemPersonaPostBinding.btnFollow.setVisibility(View.GONE);
            } else { // 如果不是自己的帖子
                // 显示关注按钮
                itemPersonaPostBinding.btnFollow.setVisibility(View.VISIBLE);
                
                // 通过回调接口检查是否已关注该作者
                boolean isFollowed = followedPersonaNames.contains(author.getName());
                updateButtonState(isFollowed);

                // 设置关注按钮的点击事件
                itemPersonaPostBinding.btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (socialSquareFollowActionListener != null) {
                            // 通过回调接口处理关注/取消关注操作
                            socialSquareFollowActionListener.onFollowClick(author);
                        }
                    }
                });
            }

            // 处理点击头像、名字和简介的跳转逻辑
            if (!post.isUserPersonaPost()) {
                // 设置点击头像、作者名称或简介/时间区域时，启动与该作者的聊天界面
                View.OnClickListener startChatListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OtherPersonaChatActivity.class);
                        // 通过Intent传递Persona对象
                        intent.putExtra(OtherPersonaChatActivity.EXTRA_PERSONA, author);
                        context.startActivity(intent);
                    }
                };
                // 为其他persona的帖子设置点击监听器，点击后可直接跳转到与该persona的聊天界面
                itemPersonaPostBinding.ivAvatar.setOnClickListener(startChatListener);              // 点击头像
                itemPersonaPostBinding.tvAuthorName.setOnClickListener(startChatListener);          // 点击作者名称
                itemPersonaPostBinding.tvAuthorBioOrTime.setOnClickListener(startChatListener);     // 点击作者简介或时间
            } else {
                // 清除之前设置的点击监听器，防止视图复用导致的错误跳转
                itemPersonaPostBinding.ivAvatar.setOnClickListener(null);              // 清除头像点击监听器
                itemPersonaPostBinding.tvAuthorName.setOnClickListener(null);          // 清除作者名称点击监听器
                itemPersonaPostBinding.tvAuthorBioOrTime.setOnClickListener(null);     // 清除作者简介或时间点击监听器
            }
        }

        /**
         * 更新关注按钮的状态
         * @param isFollowed 是否已关注
         */
        private void updateButtonState(boolean isFollowed) {
            if (isFollowed) {
                itemPersonaPostBinding.btnFollow.setText("已关注");
                // 设置已关注状态的颜色（灰色）
                itemPersonaPostBinding.btnFollow.setBackgroundColor(context.getResources().getColor(R.color.gray));
                itemPersonaPostBinding.btnFollow.setTextColor(context.getResources().getColor(R.color.white));
            } else {
                itemPersonaPostBinding.btnFollow.setText("关注");
                // 设置未关注状态的颜色（使用主题色或紫色）
                itemPersonaPostBinding.btnFollow.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
                itemPersonaPostBinding.btnFollow.setTextColor(context.getResources().getColor(R.color.white));
            }
        }
    }
}