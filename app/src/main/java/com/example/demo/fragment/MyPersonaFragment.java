package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.model.ChatMessage;
import com.example.demo.viewmodel.MyPersonaViewModel;
import com.example.demo.model.Persona;
import com.example.demo.databinding.FragmentMyPersonaBinding;
import com.example.demo.activity.MainActivity;
import com.example.demo.adapter.PersonaChatAdapter;
import com.example.demo.viewmodel.MainViewModel;

import java.util.List;

public class MyPersonaFragment extends Fragment {

    private FragmentMyPersonaBinding binding;
    private PersonaChatAdapter personaChatAdapter;
    private MyPersonaViewModel viewModel;
    private MainViewModel mainViewModel;

    private Persona myPersona = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyPersonaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupUI();
    }

    public void onPersonaCreated(Persona persona) {
        this.myPersona = persona;

        if (mainViewModel != null) {
            mainViewModel.setUserPersona(persona);
        }

        if (binding != null) {
            setupUI();
        }
    }

    private void setupUI() {
        if (myPersona == null) {
            binding.groupEmptyState.setVisibility(View.VISIBLE);
            binding.personaHeaderLayout.setVisibility(View.GONE);
            binding.rvChatMessages.setVisibility(View.GONE);
            binding.inputLayout.setVisibility(View.GONE);

            binding.btnGoToCreate.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).launchCreatePersonaActivity();
                }
            });
        } else {
            binding.groupEmptyState.setVisibility(View.GONE);
            binding.personaHeaderLayout.setVisibility(View.VISIBLE);
            binding.rvChatMessages.setVisibility(View.VISIBLE);
            binding.inputLayout.setVisibility(View.VISIBLE);

            binding.tvPersonaName.setText(myPersona.getName());
            binding.ivPersonaAvatar.setImageResource(myPersona.getAvatarDrawableId());

            initChatWithMVVM(myPersona);
        }
    }

    private void initChatWithMVVM(Persona personaToChat) {
        MyPersonaViewModel.Factory factory = new MyPersonaViewModel.Factory(personaToChat);
        viewModel = new ViewModelProvider(this, factory).get(MyPersonaViewModel.class);

        personaChatAdapter = new PersonaChatAdapter(requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
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

        viewModel.getChatHistory().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}