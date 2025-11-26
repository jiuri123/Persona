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
import com.example.demo.viewmodel.MainViewModel;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;


public class SocialPostAdapter extends RecyclerView.Adapter<SocialPostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private MainViewModel mainViewModel;

    private Set<String> followedAuthors = new HashSet<>();

    public SocialPostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }
    
    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void addPostAtTop(Post post) {
        if (postList != null) {
            postList.add(0, post);
            notifyItemInserted(0);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPersonaPostBinding binding = ItemPersonaPostBinding.inflate(inflater, parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }


    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private final ItemPersonaPostBinding binding;
        
        private final Markwon markwon;
        
        public PostViewHolder(ItemPersonaPostBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
            
            markwon = Markwon.builder(itemView.getContext())
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TablePlugin.create(itemView.getContext()))
                    .usePlugin(TaskListPlugin.create(itemView.getContext()))
                    .usePlugin(LinkifyPlugin.create())
                    .build();
        }

        public void bind(Post post) {
            Persona author = post.getAuthor();
            binding.tvAuthorName.setText(author.getName());
            binding.tvAuthorBioOrTime.setText(author.getBio());

            View.OnClickListener startChatListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherPersonaChatActivity.class);
                    intent.putExtra(OtherPersonaChatActivity.EXTRA_PERSONA, author);
                    context.startActivity(intent);
                }
            };

            binding.ivAvatar.setOnClickListener(startChatListener);
            binding.tvAuthorName.setOnClickListener(startChatListener);

            markwon.setMarkdown(binding.tvContentText, post.getContentText());
            Glide.with(context)
                    .load(author.getAvatarDrawableId())
                    .placeholder(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(binding.ivAvatar);
            if (post.getImageDrawableId() != null) {
                binding.ivPostImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getImageDrawableId())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(binding.ivPostImage);
            } else {
                binding.ivPostImage.setVisibility(View.GONE);
            }

            String authorName = author.getName();

            boolean isFollowed = followedAuthors.contains(authorName);
            updateButtonState(isFollowed);

            binding.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (followedAuthors.contains(authorName)) {
                        followedAuthors.remove(authorName);
                        updateButtonState(false);
                        
                        if (mainViewModel != null) {
                            mainViewModel.removeFollowedPersona(author);
                        }
                    } else {
                        followedAuthors.add(authorName);
                        updateButtonState(true);
                        
                        if (mainViewModel != null) {
                            mainViewModel.addFollowedPersona(author);
                        }
                    }
                }
            });
        }

        private void updateButtonState(boolean isFollowed) {
            if (isFollowed) {
                binding.btnFollow.setText("已关注");
            } else {
                binding.btnFollow.setText("关注");
            }
        }
    }
}