package com.example.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.model.ChatMessage;
import com.example.demo.utils.MarkdownTypewriterEffect;
import com.example.demo.databinding.ItemChatReceivedBinding;
import com.example.demo.databinding.ItemChatSentBinding;

// Markwon库用于在Android中渲染Markdown文本
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

import java.util.HashMap;
import java.util.Map;

// 导入资源类
import com.example.demo.R;

/**
 * Persona聊天适配器
 * 用于在RecyclerView中显示聊天消息
 * 支持发送和接收两种不同类型的消息布局
 * 接收的消息支持打字机效果显示
 */
public class PersonaChatAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {

    // 视图类型常量：发送的消息
    private static final int VIEW_TYPE_SENT = 1;
    // 视图类型常量：接收的消息
    private static final int VIEW_TYPE_RECEIVED = 2;

    // 不需要静态映射，改为使用ChatMessage的isTypewriterComplete字段
    
    // 打字机效果完成回调接口
    public interface OnTypewriterCompleteListener {
        void onTypewriterComplete(String messageId, boolean isComplete);
    }
    
    // 打字机效果完成监听器
    private volatile OnTypewriterCompleteListener onTypewriterCompleteListener;
    
    // Markwon实例，用于渲染Markdown文本
    private final Markwon markwon;

    /**
     * 消息差异回调
     * 用于ListAdapter计算列表项差异
     */
    private static class ChatMessageDiffCallback extends DiffUtil.ItemCallback<ChatMessage> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            // 使用消息的唯一标识符比较
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            // 比较消息内容
            return oldItem.equals(newItem);
        }
    }

    /**
     * 构造函数
     * @param context 上下文
     */
    public PersonaChatAdapter(Context context) {
        super(new ChatMessageDiffCallback());
        // 初始化Markwon，配置各种插件支持Markdown特性
        this.markwon = Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create()) // 支持删除线
                .usePlugin(TablePlugin.create(context)) // 支持表格
                .usePlugin(TaskListPlugin.create(context)) // 支持任务列表
                .usePlugin(LinkifyPlugin.create()) // 支持自动链接识别
                .build();
    }
    
    /**
     * 设置打字机效果完成监听器
     * @param listener 打字机效果完成监听器
     */
    public void setOnTypewriterCompleteListener(OnTypewriterCompleteListener listener) {
        this.onTypewriterCompleteListener = listener;
    }

    /**
     * 获取指定位置项的视图类型
     * 根据消息是发送还是接收返回不同的视图类型
     * @param position 项在列表中的位置
     * @return 视图类型常量
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.isSentByUser()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    /**
     * 创建ViewHolder
     * 根据视图类型创建不同的ViewHolder
     * @param parent 父视图组
     * @param viewType 视图类型
     * @return 新创建的ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_SENT:
                // 创建发送消息的ViewHolder
                ItemChatSentBinding sentBinding = ItemChatSentBinding.inflate(inflater, parent, false);
                return new SentMessageViewHolder(sentBinding, markwon);

            case VIEW_TYPE_RECEIVED:
            default:
                // 创建接收消息的ViewHolder
                ItemChatReceivedBinding receivedBinding = ItemChatReceivedBinding.inflate(inflater, parent, false);
                return new ReceivedMessageViewHolder(receivedBinding, markwon, this);
        }
    }

    /**
     * 绑定数据到ViewHolder
     * 根据ViewHolder类型调用相应的绑定方法
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = getItem(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    /**
     * 发送消息的ViewHolder
     * 用于显示用户发送的消息
     */
    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        // 视图绑定对象
        private final ItemChatSentBinding binding;
        // Markwon实例
        private final Markwon markwon;

        /**
         * 构造函数
         * @param binding 视图绑定对象
         * @param markwon Markwon实例
         */
        public SentMessageViewHolder(ItemChatSentBinding binding, Markwon markwon) {
            super(binding.getRoot());
            this.binding = binding;
            this.markwon = markwon;
        }

        /**
         * 绑定消息数据到视图
         * @param message 要显示的消息
         */
        public void bind(ChatMessage message) {
            // 使用Markwon渲染Markdown内容
            markwon.setMarkdown(binding.tvMessage, message.getText());
            
            // 设置头像
            if (message.getAvatarDrawableId() != 0) {
                // 从资源ID加载头像
                binding.ivAvatar.setImageResource(message.getAvatarDrawableId());
            } else if (message.getAvatarUri() != null) {
                // 从URI加载头像
                binding.ivAvatar.setImageURI(android.net.Uri.parse(message.getAvatarUri()));
            } else {
                // 设置默认头像
                binding.ivAvatar.setImageResource(R.drawable.icon_persona);
            }
        }
    }

    /**
     * 接收消息的ViewHolder
     * 用于显示Persona回复的消息，支持打字机效果
     */
    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        // 视图绑定对象
        private final ItemChatReceivedBinding binding;
        // 打字机效果实例
        private MarkdownTypewriterEffect typewriterEffect;
        // Markwon实例
        private final Markwon markwon;
        // 适配器实例，用于访问监听器
        private final PersonaChatAdapter adapter;

        /**
         * 构造函数
         * @param binding 视图绑定对象
         * @param markwon Markwon实例
         * @param adapter PersonaChatAdapter实例
         */
        public ReceivedMessageViewHolder(ItemChatReceivedBinding binding, Markwon markwon, PersonaChatAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.markwon = markwon;
            this.adapter = adapter;
        }

        /**
         * 绑定消息数据到视图
         * @param message 要显示的消息
         */
        public void bind(ChatMessage message) {
            // 设置头像
            if (message.getAvatarDrawableId() != 0) {
                // 从资源ID加载头像
                binding.ivAvatar.setImageResource(message.getAvatarDrawableId());
            } else if (message.getAvatarUri() != null) {
                // 从URI加载头像
                binding.ivAvatar.setImageURI(android.net.Uri.parse(message.getAvatarUri()));
            } else {
                // 设置默认头像
                binding.ivAvatar.setImageResource(R.drawable.avatar_zero);
            }
            
            // 检查消息是否已经完成打字机效果
            if (message.isTypewriterComplete()) {
                // 如果已完成，直接显示完整消息
                markwon.setMarkdown(binding.tvMessage, message.getText());
                return;
            }
            
            // 如果有正在进行的打字机效果，先取消它
            if (typewriterEffect != null) {
                typewriterEffect.cancel();
            }
            
            // 创建新的打字机效果，这是匿名类，内部重写onComplete方法，打字机完成时调用onComplete方法
            typewriterEffect = new MarkdownTypewriterEffect(binding.tvMessage, message.getText(), 50, markwon) {
                @Override
                protected void onComplete() {
                    // 打字机效果完成后，更新消息对象的状态
                    message.setTypewriterComplete(true);
                    
                    // 调用回调方法，通知外部打字机效果已完成
                    OnTypewriterCompleteListener listener = adapter.onTypewriterCompleteListener;
                    if (listener != null) {
                        listener.onTypewriterComplete(message.getId().toString(), true);
                    }
                }
            };
            typewriterEffect.start(); // 开始打字机效果
        }
    }
}