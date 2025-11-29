package com.example.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ItemFollowedPersonaBinding;
import com.example.demo.activity.OtherPersonaChatActivity;

import java.util.List;

/**
 * 已关注Persona适配器
 * 用于在RecyclerView中显示用户已关注的Persona列表
 * 实现了点击Persona项或头像跳转到聊天界面的功能
 */
public class UserFollowedListAdapter extends RecyclerView.Adapter<UserFollowedListAdapter.FollowedPersonaViewHolder> {

    // 已关注的Persona数据列表
    private List<Persona> followedPersonaList;
    // 上下文，用于启动Activity和加载资源
    private Context context;
    // 取消关注操作的回调接口
    private OnUnfollowClickListener onUnfollowClickListener;

    /**
     * 取消关注点击事件的回调接口
     */
    public interface OnUnfollowClickListener {
        void onUnfollowClick(Persona persona);
    }

    /**
     * 构造函数
     * @param context 上下文
     * @param followedPersonaList 已关注的Persona数据列表
     */
    public UserFollowedListAdapter(Context context, List<Persona> followedPersonaList) {
        this.context = context;
        this.followedPersonaList = followedPersonaList;
    }

    /**
     * 设置取消关注点击事件的回调接口
     * @param onUnfollowClickListener 回调接口实例
     */
    public void setOnUnfollowClickListener(OnUnfollowClickListener onUnfollowClickListener) {
        this.onUnfollowClickListener = onUnfollowClickListener;
    }

    /**
     * 创建ViewHolder
     * RecyclerView会调用此方法创建新的ViewHolder实例
     * @param parent 父视图组
     * @param viewType 视图类型
     * @return 新创建的FollowedPersonaViewHolder
     */
    @NonNull
    @Override
    public FollowedPersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 使用视图绑定创建布局
        ItemFollowedPersonaBinding binding = ItemFollowedPersonaBinding.inflate(inflater, parent, false);
        return new FollowedPersonaViewHolder(binding);
    }

    /**
     * 绑定数据到ViewHolder
     * RecyclerView会调用此方法将数据绑定到指定位置的ViewHolder
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull FollowedPersonaViewHolder holder, int position) {
        Persona persona = followedPersonaList.get(position);
        holder.bind(persona);
    }

    /**
     * 获取列表项总数
     * @return 列表项数量
     */
    @Override
    public int getItemCount() {
        return followedPersonaList != null ? followedPersonaList.size() : 0;
    }

    /**
     * 已关注Persona的ViewHolder类
     * 持有单个Persona项的所有视图，并负责数据绑定和事件处理
     */
    public class FollowedPersonaViewHolder extends RecyclerView.ViewHolder {

        // 视图绑定对象，用于访问布局中的各个组件
        private final ItemFollowedPersonaBinding binding;

        /**
         * ViewHolder构造函数
         * @param binding 视图绑定对象
         */
        public FollowedPersonaViewHolder(ItemFollowedPersonaBinding binding) {
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
                    Intent intent = new Intent(context, OtherPersonaChatActivity.class);
                    // 通过Intent传递Persona对象
                    intent.putExtra(OtherPersonaChatActivity.EXTRA_PERSONA, persona);
                    context.startActivity(intent);
                }
            });

            // 设置长按事件，显示取消关注菜单
            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 创建PopupMenu，设置为竖排显示
                    PopupMenu popupMenu = new PopupMenu(context, v, Gravity.END);
                    popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "取消关注");
                    
                    // 设置菜单点击事件
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == 1) {
                                // 调用取消关注回调
                                if (onUnfollowClickListener != null) {
                                    onUnfollowClickListener.onUnfollowClick(persona);
                                }
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