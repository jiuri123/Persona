package com.example.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.viewmodel.CreatePersonaViewModel;
import com.example.demo.model.Persona;
import com.example.demo.R;
import com.example.demo.databinding.ActivityCreatePersonaBinding;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class CreateMyPersonaActivity extends AppCompatActivity {

    private ActivityCreatePersonaBinding binding;
    private CreatePersonaViewModel viewModel;

    public static final String EXTRA_PERSONA_RESULT = "com.example.demo.PERSONA_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePersonaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CreatePersonaViewModel.class);

        setupObservers();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPersonaAndReturn();
            }
        });

        binding.btnAiGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.generatePersonaDetails();
            }
        });
    }

    private void createPersonaAndReturn() {
        String name = binding.etPersonaName.getText().toString().trim();
        String story = binding.etPersonaStory.getText().toString().trim();

        if (name.isEmpty() || story.isEmpty()) {
            Toast.makeText(this, "名称和背景故事不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String bio = "由你创建的 Persona";
        int avatarId = R.drawable.avatar_zero;

        Persona newPersona = new Persona(name, avatarId, bio, story);

        Intent resultIntent = new Intent();

        resultIntent.putExtra(EXTRA_PERSONA_RESULT, newPersona);

        setResult(AppCompatActivity.RESULT_OK, resultIntent);

        finish();
    }

    private void setupObservers() {

        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if (isLoading) {
                    binding.btnAiGenerate.setEnabled(false);
                    binding.btnAiGenerate.setText("生成中...");
                } else {
                    binding.btnAiGenerate.setEnabled(true);
                    binding.btnAiGenerate.setText("AI 辅助生成");
                }
            }
        });

        viewModel.getGeneratedName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String name) {
                if (name != null) {
                    binding.etPersonaName.setText(name);
                }
            }
        });

        viewModel.getGeneratedStory().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String story) {
                if (story != null) {
                    binding.etPersonaStory.setText(story);
                }
            }
        });

        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(CreateMyPersonaActivity.this, "错误: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}