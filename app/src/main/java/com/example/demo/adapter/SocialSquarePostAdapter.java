package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.R;
import com.example.demo.activity.OtherPersonaChatActivity;

import com.example.demo.model.OtherPersona;
import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.model.PostUiItem;
import com.example.demo.databinding.ItemPersonaPostBinding;

// Markwon库用于在Android中渲染Markdown文本
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

import java.util.Objects;

/**
 * 社交广场帖子适配器
 * 用于在RecyclerView中显示Persona发布的帖子
 * 实现了点击头像/名称跳转聊天界面、关注/取消关注功能
 */
public class SocialSquarePostAdapter extends ListAdapter<PostUiItem, SocialSquarePostAdapter.PostViewHolder> {
    // 上下文，用于启动Activity和加载资源
    private final Context context;
    // 用户关注其他persona操作的回调接口
    private OnFollowClickListener onFollowClickListener;

    /**
     * 构造函数
     * @param context 上下文
     */
    public SocialSquarePostAdapter(Context context) {
        super(new PostUiItemDiffCallback());
        this.context = context;
    }

    /**
     * DiffUtil.ItemCallback实现，用于比较PostUiItem对象
     */
    private static class PostUiItemDiffCallback extends DiffUtil.ItemCallback<PostUiItem> {
        @Override
        public boolean areItemsTheSame(@NonNull PostUiItem oldItem, @NonNull PostUiItem newItem) {
            // 使用帖子的内容和作者作为唯一标识
            Post oldPost = oldItem.getPost();
            Post newPost = newItem.getPost();
            return Objects.equals(oldPost.getAuthor(), newPost.getAuthor()) &&
                    Objects.equals(oldPost.getContentText(), newPost.getContentText()) &&
                    Objects.equals(oldPost.getTimestamp(), newPost.getTimestamp());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PostUiItem oldItem, @NonNull PostUiItem newItem) {
            // 使用Objects.equals比较所有内容是否一致
            return Objects.equals(oldItem, newItem);
        }
    }

    /**
     * 关注操作回调接口
     * 用于解耦Adapter和ViewModel，遵循MVVM架构原则
     */
    public interface OnFollowClickListener {
        /**
         * 处理关注按钮点击事件
         * @param otherPersona 被点击的OtherPersona对象
         */
        void onFollowClick(OtherPersona otherPersona);
    }

    /**
     * 设置关注操作回调接口
     * 
     * @param onFollowClickListener 关注操作回调接口实现
     */
    public void setOnFollowActionListener(OnFollowClickListener onFollowClickListener) {
        this.onFollowClickListener = onFollowClickListener;
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
     * 绑定数据到ViewHolder
     * RecyclerView会调用此方法将数据绑定到指定位置的ViewHolder
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        // 使用getItem获取当前位置的PostUiItem
        PostUiItem postUiItem = getItem(position);
        // 将该PostUiItem绑定到视图
        holder.bind(postUiItem);
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
            markwon = Markwon.builder(itemPersonaPostBinding.getRoot().getContext())
                    .usePlugin(StrikethroughPlugin.create()) // 支持删除线
                    .usePlugin(TablePlugin.create(itemPersonaPostBinding.getRoot().getContext())) // 支持表格
                    .usePlugin(TaskListPlugin.create(itemPersonaPostBinding.getRoot().getContext())) // 支持任务列表
                    .usePlugin(LinkifyPlugin.create()) // 支持自动链接识别
                    .build();
        }

        /**
         * 绑定帖子UI数据到视图
         * @param postUiItem 要显示的PostUiItem对象
         */
        public void bind(PostUiItem postUiItem) {
            Post post = postUiItem.getPost();
            Persona author = post.getAuthor();
            // 设置作者名称和简介
            itemPersonaPostBinding.tvAuthorName.setText(author.getName());
            itemPersonaPostBinding.tvAuthorBioOrTime.setText(author.getSignature());
            
            // 使用Glide加载头像，优先使用avatarUri，如果没有则使用avatarDrawableId
            if (author.getAvatarUri() != null) {
                Glide.with(context)
                        .load(Uri.parse(author.getAvatarUri()))
                        .placeholder(R.drawable.ic_launcher_background) // 占位图
                        .circleCrop() // 圆形裁剪
                        .into(itemPersonaPostBinding.ivAvatar);
            } else {
                Glide.with(context)
                        .load(author.getAvatarDrawableId())
                        .placeholder(R.drawable.ic_launcher_background) // 占位图
                        .circleCrop() // 圆形裁剪
                        .into(itemPersonaPostBinding.ivAvatar);
            }

            // 使用Markwon将帖子的内容渲染成Markdown
            markwon.setMarkdown(itemPersonaPostBinding.tvContentText, post.getContentText());
            
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

            // 处理关注按钮的显示逻辑和点击事件
            if (post.isUserPersonaPost()) {
                // 如果是自己的帖子，则隐藏关注按钮
                itemPersonaPostBinding.btnFollow.setVisibility(View.GONE);
            } else { // 如果不是自己的帖子
                // 则显示关注按钮
                itemPersonaPostBinding.btnFollow.setVisibility(View.VISIBLE);
                
                // 更新按钮状态，根据isFollowed状态设置按钮文本和颜色
                if (postUiItem.isFollowed()) {
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

                // 设置关注按钮的点击事件
                itemPersonaPostBinding.btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onFollowClickListener != null) {
                            // 通过回调接口处理关注/取消关注操作
                            onFollowClickListener.onFollowClick((OtherPersona) author);
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
    }
}