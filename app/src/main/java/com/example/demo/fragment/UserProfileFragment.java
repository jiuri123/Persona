package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.R;
import com.example.demo.databinding.FragmentProfileBinding;
import com.example.demo.viewmodel.UserProfileViewModel;

/**
 * 个人资料Fragment
 * 显示用户个人资料页面，包含我的Persona、应用设置、关于和退出登录等功能
 */
public class UserProfileFragment extends Fragment {

    // 视图绑定对象，用于访问布局中的组件
    private FragmentProfileBinding binding;
    
    // 个人资料ViewModel
    private UserProfileViewModel viewModel;

    // 默认构造函数
    public UserProfileFragment() {
    }

    /**
     * 创建Fragment的视图
     * @param inflater 布局填充器
     * @param container 父容器
     * @param savedInstanceState 保存的Fragment状态
     * @return 创建的视图
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 使用视图绑定创建布局
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * 视图创建完成后调用
     * 设置各个UI组件的点击事件
     * @param view 创建的视图
     * @param savedInstanceState 保存的Fragment状态
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        // 设置"我的Persona"点击事件
        binding.tvMyPersonas.setOnClickListener(v -> {
            viewModel.onMyPersonasClick();
            showToast("功能暂未开放");
        });

        // 设置"应用设置"点击事件
        binding.tvAppSettings.setOnClickListener(v -> {
            viewModel.onAppSettingsClick();
            showToast("功能暂未开放");
        });

        // 设置"关于"点击事件，显示关于对话框
        binding.tvAbout.setOnClickListener(v -> {
            viewModel.onAboutClick();
            showAboutDialog();
        });

        // 设置"退出登录"点击事件
        binding.btnLogOut.setOnClickListener(v -> {
            viewModel.onLogOutClick();
            showToast("已退出登录");
        });
    }

    /**
     * 显示关于对话框
     * 使用自定义布局创建对话框
     */
    private void showAboutDialog() {
        // 加载自定义对话框布局
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_about, null);

        // 创建AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(dialogView) // 设置自定义视图
                .setCancelable(true); // 设置可取消

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 设置对话框中关闭按钮的点击事件
        dialogView.findViewById(R.id.btnClose).setOnClickListener(v -> {
            dialog.dismiss(); // 关闭对话框
        });
    }

    /**
     * 显示Toast消息
     * @param message 要显示的消息内容
     */
    private void showToast(String message) {
        // 检查上下文是否存在，避免空指针异常
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fragment视图销毁时调用
     * 清理视图绑定，避免内存泄漏
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // 清理视图绑定
    }
}