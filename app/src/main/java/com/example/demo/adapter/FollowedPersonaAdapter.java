package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ItemFollowedPersonaBinding;
import com.example.demo.activity.OtherPersonaChatActivity;

import java.util.List;

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

    public class FollowedPersonaViewHolder extends RecyclerView.ViewHolder {

        private final ItemFollowedPersonaBinding binding;

        public FollowedPersonaViewHolder(ItemFollowedPersonaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Persona persona) {
            binding.tvPersonaName.setText(persona.getName());
            binding.tvPersonaBio.setText(persona.getBio());

            Glide.with(context)
                    .load(persona.getAvatarDrawableId())
                    .placeholder(R.drawable.ic_launcher_background)
                    .circleCrop()
                    .into(binding.ivPersonaAvatar);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherPersonaChatActivity.class);
                    intent.putExtra(OtherPersonaChatActivity.EXTRA_PERSONA, persona);
                    context.startActivity(intent);
                }
            });

            binding.ivPersonaAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OtherPersonaChatActivity.class);
                    intent.putExtra(OtherPersonaChatActivity.EXTRA_PERSONA, persona);
                    context.startActivity(intent);
                }
            });
        }
    }
}