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

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonaChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessage> messageList = new ArrayList<>();
    
    private static final Map<String, Boolean> messageCompletionMap = new HashMap<>();
    
    private final Markwon markwon;

    public PersonaChatAdapter(Context context) {
        this.markwon = Markwon.builder(context)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(context))
                .usePlugin(TaskListPlugin.create(context))
                .usePlugin(LinkifyPlugin.create())
                .build();
    }

    public void setData(List<ChatMessage> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (message.isSentByUser()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_SENT:
                ItemChatSentBinding sentBinding = ItemChatSentBinding.inflate(inflater, parent, false);
                return new SentMessageViewHolder(sentBinding, markwon);

            case VIEW_TYPE_RECEIVED:
            default:
                ItemChatReceivedBinding receivedBinding = ItemChatReceivedBinding.inflate(inflater, parent, false);
                return new ReceivedMessageViewHolder(receivedBinding, markwon);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatSentBinding binding;
        private final Markwon markwon;

        public SentMessageViewHolder(ItemChatSentBinding binding, Markwon markwon) {
            super(binding.getRoot());
            this.binding = binding;
            this.markwon = markwon;
        }

        public void bind(ChatMessage message) {
            markwon.setMarkdown(binding.tvMessage, message.getText());
        }
    }

    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatReceivedBinding binding;
        private MarkdownTypewriterEffect typewriterEffect;
        private final Markwon markwon;

        public ReceivedMessageViewHolder(ItemChatReceivedBinding binding, Markwon markwon) {
            super(binding.getRoot());
            this.binding = binding;
            this.markwon = markwon;
        }

        public void bind(ChatMessage message) {
            String messageKey = generateMessageKey(message);
            
            Boolean isCompleted = messageCompletionMap.get(messageKey);
            if (isCompleted != null && isCompleted) {
                markwon.setMarkdown(binding.tvMessage, message.getText());
                return;
            }
            
            if (typewriterEffect != null) {
                typewriterEffect.cancel();
            }
            
            typewriterEffect = new MarkdownTypewriterEffect(binding.tvMessage, message.getText(), 50, markwon) {
                @Override
                protected void onComplete() {
                    messageCompletionMap.put(messageKey, true);
                }
            };
            typewriterEffect.start();
        }
        
        private String generateMessageKey(ChatMessage message) {
            return message.getText() + "_" + message.isSentByUser();
        }
    }
}