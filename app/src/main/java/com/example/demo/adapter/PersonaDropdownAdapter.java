package com.example.demo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ItemFollowedPersonaBinding;

import java.util.List;

/**
 * Persona下拉菜单适配器
 * 用于在下拉菜单中显示可用的Persona列表
 * 实现了点击Persona项选择该Persona的功能
 */
public class PersonaDropdownAdapter extends RecyclerView.Adapter<PersonaDropdownAdapter.PersonaDropdownViewHolder> {

    // 可用的Persona数据列表
    private List<Persona> personaList;
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
     * 创建ViewHolder
     * RecyclerView会调用此方法创建新的ViewHolder实例
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
     * @param holder 要绑定数据的ViewHolder
     * @param position 数据在列表中的位置
     */
    @Override
    public void onBindViewHolder(@NonNull PersonaDropdownViewHolder holder, int position) {
        Persona persona = personaList.get(position);
        holder.bind(persona);
    }

    /**
     * 获取列表项总数
     * @return 列表项数量
     */
    @Override
    public int getItemCount() {
        return personaList != null ? personaList.size() : 0;
    }

    /**
     * 更新数据列表
     * @param newPersonaList 新的Persona列表
     */
    public void updateData(List<Persona> newPersonaList) {
        this.personaList = newPersonaList;
        notifyDataSetChanged();
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
