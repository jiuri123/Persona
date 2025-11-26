package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.databinding.ItemFollowedPersonaBinding;
import com.example.demo.ui.activity.ChatActivity;

import java.util.List;

/**
 * 已关注Persona列表的适配器
 */
public class FollowedPersonaAdapter extends RecyclerView.Adapter<FollowedPersonaAdapter.FollowedPersonaViewHolder> {

    private List<Persona> followedPersonaList;
    private Context context;

    public FollowedPersonaAdapter(Context context, List<Persona> followedPersonaList) {
        this.context = context;
        this.followedPersonaList = followedPersonaList;
    }

    @NonNull
    @Override
    public FollowedPersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemFollowedPersonaBinding binding = ItemFollowedPersonaBinding.inflate(inflater, parent, false);
        return new FollowedPersonaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowedPersonaViewHolder holder, int position) {
        Persona persona = followedPersonaList.get(position);
        holder.bind(persona);
    }

    @Override
    public int getItemCount() {
        return followedPersonaList != null ? followedPersonaList.size() : 0;
    }

    /**
     * ViewHolder类，持有单个Persona项的视图
     */
    public class FollowedPersonaViewHolder extends RecyclerView.ViewHolder {

        private final ItemFollowedPersonaBinding binding;

        public FollowedPersonaViewHolder(ItemFollowedPersonaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Persona persona) {
            // 设置Persona信息
            binding.tvPersonaName.setText(persona.getName());
            binding.tvPersonaBio.setText(persona.getBio());

            // 使用Glide加载头像
            Glide.with(context)
                    .load(persona.getAvatarDrawableId())
                    .placeholder(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(binding.ivPersonaAvatar);

            // 设置点击事件，点击整个项跳转到聊天界面
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_PERSONA, persona);
                    context.startActivity(intent);
                }
            });

            // 设置头像点击事件
            binding.ivPersonaAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_PERSONA, persona);
                    context.startActivity(intent);
                }
            });
        }
    }
}