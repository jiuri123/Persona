package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ItemFollowedPersonaBinding;
import com.example.demo.activity.UserPersonaChatActivity;

import java.util.Objects;

/**
 * 用户Persona适配器
 * 用于在RecyclerView中显示用户创建的Persona列表
 * 实现了点击Persona项跳转到聊天界面的功能
 */
public class UserPersonaListAdapter extends ListAdapter<Persona, UserPersonaListAdapter.UserPersonaViewHolder> {

    // 上下文，用于启动Activity和加载资源
    private final Context context;
    // 删除Persona的回调接口
    private OnPersonaDeleteListener onPersonaDeleteListener;

    /**
     * DiffUtil.ItemCallback实现，用于比较Persona对象
     */
    private static class PersonaDiffCallback extends DiffUtil.ItemCallback<Persona> {
        @Override
        public boolean areItemsTheSame(@NonNull Persona oldItem, @NonNull Persona newItem) {
            // 使用id判断是否为同一对象
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Persona oldItem, @NonNull Persona newItem) {
            // 使用Objects.equals比较所有内容是否一致
            return Objects.equals(oldItem, newItem);
        }
    }

    /**
     * 删除Persona的回调接口
     */
    public interface OnPersonaDeleteListener {
        void onPersonaDelete(Persona persona);
    }

    /**
     * 构造函数
     * @param context 上下文
     */
    public UserPersonaListAdapter(Context context) {
        super(new PersonaDiffCallback());
        this.context = context;
    }

    /**
     * 设置删除Persona的回调接口
     * @param onPersonaDeleteListener 回调接口实例
     */
    public void setOnPersonaDeleteListener(OnPersonaDeleteListener onPersonaDeleteListener) {
        this.onPersonaDeleteListener = onPersonaDeleteListener;
    }

    /**
     * 创建ViewHolder
     * RecyclerView会调用此方法创建新的ViewHolder实例
     * @param parent 父视图组
     * @param viewType 视图类型
     * @return 新创建的UserPersonaViewHolder
     */
    @NonNull
    @Override
    public UserPersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 使用视图绑定创建布局
        ItemFollowedPersonaBinding binding = ItemFollowedPersonaBinding.inflate(inflater, parent, false);
        return new UserPersonaViewHolder(binding);
    }

    /**
     * 绑定数据到ViewHolder
     * RecyclerView会调用此方法将数据绑定到指定位置的ViewHolder
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull UserPersonaViewHolder holder, int position) {
        // 使用getItem获取当前位置的数据
        Persona persona = getItem(position);
        holder.bind(persona);
    }

    /**
     * 用户Persona的ViewHolder类
     * 持有单个Persona项的所有视图，并负责数据绑定和事件处理
     */
    public class UserPersonaViewHolder extends RecyclerView.ViewHolder {

        // 视图绑定对象，用于访问布局中的各个组件
        private final ItemFollowedPersonaBinding binding;

        /**
         * ViewHolder构造函数
         * @param binding 视图绑定对象
         */
        public UserPersonaViewHolder(ItemFollowedPersonaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * 绑定Persona数据到视图
         * @param persona 要显示的Persona对象
         */
        public void bind(Persona persona) {
            // 设置Persona名称和简介
            binding.tvPersonaName.setText(persona.getName());
            binding.tvPersonaBio.setText(persona.getSignature());

            // 使用Glide加载头像，优先使用avatarUri，如果没有则使用avatarDrawableId
            if (persona.getAvatarUri() != null) {
                Glide.with(context)
                        .load(Uri.parse(persona.getAvatarUri()))
                        .placeholder(R.drawable.ic_launcher_background) // 占位图
                        .circleCrop() // 圆形裁剪
                        .into(binding.ivPersonaAvatar);
            } else {
                Glide.with(context)
                        .load(persona.getAvatarDrawableId())
                        .placeholder(R.drawable.ic_launcher_background) // 占位图
                        .circleCrop() // 圆形裁剪
                        .into(binding.ivPersonaAvatar);
            }

            // 设置整个项的点击事件，点击后跳转到聊天界面
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserPersonaChatActivity.class);
                    // 通过Intent传递Persona对象
                    intent.putExtra(UserPersonaChatActivity.EXTRA_PERSONA, persona);
                    context.startActivity(intent);
                }
            });

            // 设置整个项的长按事件，长按后显示删除菜单
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 创建PopupMenu，显示删除选项
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "删除角色");
                    
                    // 设置菜单点击事件
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == 1) {
                                // 显示删除确认对话框
                                new AlertDialog.Builder(context)
                                        .setTitle("确认删除")
                                        .setMessage("确定要删除角色\"" + persona.getName() + "\"吗？")
                                        .setPositiveButton("删除", (dialog, which) -> {
                                            // 调用删除回调
                                            if (onPersonaDeleteListener != null) {
                                                onPersonaDeleteListener.onPersonaDelete(persona);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .show();
                                return true;
                            }
                            return false;
                        }
                    });
                    
                    // 显示菜单
                    popupMenu.show();
                    return true;
                }
            });
        }
    }
}
