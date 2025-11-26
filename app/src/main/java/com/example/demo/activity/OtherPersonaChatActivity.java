package com.example.demo.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.model.ChatMessage;
import com.example.demo.viewmodel.MyPersonaViewModel;
import com.example.demo.model.Persona;
import com.example.demo.databinding.ActivityChatBinding;

import java.util.List;

public class OtherPersonaChatActivity extends AppCompatActivity {

    public static final String EXTRA_PERSONA = "com.example.demo.EXTRA_PERSONA";

    private ActivityChatBinding binding;
    private PersonaChatAdapter personaChatAdapter;
    private MyPersonaViewModel viewModel;
    private Persona currentPersona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentPersona = getIntent().getParcelableExtra(EXTRA_PERSONA);

        if (currentPersona == null) {
            finish();
            return;
        }

        setupToolbar();

        initChatWithMVVM(currentPersona);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(currentPersona.getName());
        }
    }

    private void initChatWithMVVM(Persona personaToChat) {
        MyPersonaViewModel.Factory factory = new MyPersonaViewModel.Factory(personaToChat);
        viewModel = new ViewModelProvider(this, factory).get(MyPersonaViewModel.class);

        personaChatAdapter = new PersonaChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(personaChatAdapter);

        binding.btnSend.setOnClickListener(v -> {
            String messageText = binding.etChatMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                viewModel.sendMessage(messageText);
                binding.etChatMessage.setText("");
            }
        });

        viewModel.getChatHistory().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(List<ChatMessage> newMessages) {
                personaChatAdapter.setData(newMessages);
                if (personaChatAdapter.getItemCount() > 0) {
                    binding.rvChatMessages.scrollToPosition(personaChatAdapter.getItemCount() - 1);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}