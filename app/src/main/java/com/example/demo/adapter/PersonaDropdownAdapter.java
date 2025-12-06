package com.example.demo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ItemFollowedPersonaBinding;

import java.util.Objects;

/**
 * Persona下拉菜单适配器
 * 用于在下拉菜单中显示可用的Persona列表
 * 实现了点击Persona项选择该Persona的功能
 */
public class PersonaDropdownAdapter extends ListAdapter<Persona, PersonaDropdownAdapter.PersonaDropdownViewHolder> {

    // 上下文，用于加载资源
    private Context context;
    // Persona选择点击事件的回调接口
    private OnPersonaSelectListener onPersonaSelectListener;

    /**
     * Persona选择点击事件的回调接口
     */
    public interface OnPersonaSelectListener {
        void onPersonaSelect(Persona persona);
    }

    /**
     * 构造函数
     * @param context 上下文
     */
    public PersonaDropdownAdapter(Context context) {
        super(new PersonaDiffCallback());
        this.context = context;
    }

    /**
     * 设置Persona选择点击事件的回调接口
     * @param onPersonaSelectListener 回调接口实例
     */
    public void setOnPersonaSelectListener(OnPersonaSelectListener onPersonaSelectListener) {
        this.onPersonaSelectListener = onPersonaSelectListener;
    }

    /**
     * DiffUtil回调类，用于比较新旧数据列表的差异
     * 实现了高效的列表更新机制
     */
    private static class PersonaDiffCallback extends DiffUtil.ItemCallback<Persona> {
        @Override
        public boolean areItemsTheSame(@NonNull Persona oldItem, @NonNull Persona newItem) {
            // 使用id判断是否为同一个Item
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Persona oldItem, @NonNull Persona newItem) {
            // 比较所有字段是否一致
            return Objects.equals(oldItem, newItem);
        }
    }

    /**
     * 创建ViewHolder实例
     * RecyclerView会调用此方法创建新的ViewHolder实例
     * 但RecyclerView 不会为所有数据项都创建 ViewHolder，而是只为当前可见的项创建
     * 当列表项滚动出屏幕时，它的 ViewHolder 会被回收并放入缓存池
     * 当新的列表项需要显示时，RecyclerView 会优先从缓存池中获取可复用的 ViewHolder
     * 只有当缓存池中没有可用的 ViewHolder 时，才会调用 onCreateViewHolder 创建新的
     * @param parent 父视图组
     * @param viewType 视图类型
     * @return 新创建的PersonaDropdownViewHolder
     */
    @NonNull
    @Override
    public PersonaDropdownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 使用视图绑定创建布局
        ItemFollowedPersonaBinding binding = ItemFollowedPersonaBinding.inflate(inflater, parent, false);
        return new PersonaDropdownViewHolder(binding);
    }

    /**
     * 绑定数据到ViewHolder
     * RecyclerView会调用此方法将数据绑定到指定位置的ViewHolder
     * 但只有当前可见的项才会调用此方法进行绑定
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull PersonaDropdownViewHolder holder, int position) {
        Persona persona = getItem(position);
        holder.bind(persona);
    }

    /**
     * Persona下拉菜单的ViewHolder类
     * 持有单个Persona项的所有视图，并负责数据绑定和事件处理
     */
    public class PersonaDropdownViewHolder extends RecyclerView.ViewHolder {

        // 视图绑定对象，用于访问布局中的各个组件
        private final ItemFollowedPersonaBinding binding;

        /**
         * ViewHolder构造函数
         * @param binding 视图绑定对象
         */
        public PersonaDropdownViewHolder(ItemFollowedPersonaBinding binding) {
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

            // 设置整个项的点击事件，点击后选择该Persona
            // 每个 ViewHolder 都有自己的点击监听器
            // 当你点击某个列表项时，只有该项对应的 ViewHolder 的点击监听器会被触发
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPersonaSelectListener != null) {
                        onPersonaSelectListener.onPersonaSelect(persona);
                    }
                }
            });
        }
    }
}