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

import com.example.demo.R;
import com.example.demo.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvMyPersonas.setOnClickListener(v -> {
            showToast("功能暂未开放");
        });

        binding.tvAppSettings.setOnClickListener(v -> {
            showToast("功能暂未开放");
        });

        binding.tvAbout.setOnClickListener(v -> {
            showAboutDialog();
        });

        binding.btnLogOut.setOnClickListener(v -> {
            showToast("已退出登录");
        });
    }

    private void showAboutDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_about, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.btnClose).setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}