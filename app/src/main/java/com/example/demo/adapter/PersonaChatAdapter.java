package com.example.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persona聊天适配器
 * 用于在RecyclerView中显示聊天消息
 * 支持发送和接收两种不同类型的消息布局
 * 接收的消息支持打字机效果显示
 */
public class PersonaChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 视图类型常量：发送的消息
    private static final int VIEW_TYPE_SENT = 1;
    // 视图类型常量：接收的消息
    private static final int VIEW_TYPE_RECEIVED = 2;

    // 消息列表
    private List<ChatMessage> messageList = new ArrayList<>();
    
    // 消息完成状态映射，用于记录哪些消息已经完成打字机效果
    private static final Map<String, Boolean> messageCompletionMap = new HashMap<>();
    
    // Markwon实例，用于渲染Markdown文本
    private final Markwon markwon;

    /**
     * 构造函数
     * @param context 上下文
     */
    public PersonaChatAdapter(Context context) {
        // 初始化Markwon，配置各种插件支持Markdown特性
        this.markwon = Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create()) // 支持删除线
                .usePlugin(TablePlugin.create(context)) // 支持表格
                .usePlugin(TaskListPlugin.create(context)) // 支持任务列表
                .usePlugin(LinkifyPlugin.create()) // 支持自动链接识别
                .build();
    }

    /**
     * 设置消息数据
     * @param newMessages 新的消息列表
     */
    public void setData(List<ChatMessage> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged(); // 通知适配器数据已更改
    }

    /**
     * 获取指定位置项的视图类型
     * 根据消息是发送还是接收返回不同的视图类型
     * @param position 项在列表中的位置
     * @return 视图类型常量
     */
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
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
                return new ReceivedMessageViewHolder(receivedBinding, markwon);
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
        ChatMessage message = messageList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    /**
     * 获取列表项总数
     * @return 列表项数量
     */
    @Override
    public int getItemCount() {
        return messageList.size();
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

        /**
         * 构造函数
         * @param binding 视图绑定对象
         * @param markwon Markwon实例
         */
        public ReceivedMessageViewHolder(ItemChatReceivedBinding binding, Markwon markwon) {
            super(binding.getRoot());
            this.binding = binding;
            this.markwon = markwon;
        }

        /**
         * 绑定消息数据到视图
         * @param message 要显示的消息
         */
        public void bind(ChatMessage message) {
            // 生成消息的唯一键
            String messageKey = generateMessageKey(message);
            
            // 检查消息是否已经完成打字机效果
            Boolean isCompleted = messageCompletionMap.get(messageKey);
            if (isCompleted != null && isCompleted) {
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
                    // 打字机效果完成后，记录消息状态
                    messageCompletionMap.put(messageKey, true);
                }
            };
            typewriterEffect.start(); // 开始打字机效果
        }
        
        /**
         * 生成消息的唯一键
         * 用于标识消息是否已完成打字机效果
         * @param message 消息对象
         * @return 消息的唯一键
         */
        private String generateMessageKey(ChatMessage message) {
            return message.getText() + "_" + message.isSentByUser();
        }
    }
}