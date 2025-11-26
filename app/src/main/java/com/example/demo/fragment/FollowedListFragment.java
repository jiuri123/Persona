package com.example.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.demo.adapter.FollowedPersonaAdapter;
import com.example.demo.model.Persona;
import com.example.demo.databinding.FragmentFollowedListBinding;
import com.example.demo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class FollowedListFragment extends Fragment {

    private FragmentFollowedListBinding binding;
    private FollowedPersonaAdapter adapter;
    private MainViewModel mainViewModel;
    private List<Persona> followedPersonaList;

    public FollowedListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        mainViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null && !error.isEmpty()) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    mainViewModel.clearError();
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFollowedListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.rvFollowedList.setLayoutManager(new LinearLayoutManager(getContext()));
        
        followedPersonaList = new ArrayList<>();
        loadFollowedPersonas();
        
        adapter = new FollowedPersonaAdapter(getContext(), followedPersonaList);
        binding.rvFollowedList.setAdapter(adapter);
        
        mainViewModel.getFollowedPersonas().observe(getViewLifecycleOwner(), new Observer<List<Persona>>() {
            @Override
            public void onChanged(List<Persona> personas) {
                if (personas != null) {
                    followedPersonaList.clear();
                    followedPersonaList.addAll(personas);
                    adapter.notifyDataSetChanged();
                    
                    if (personas.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.rvFollowedList.setVisibility(View.GONE);
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.rvFollowedList.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
    
    private void loadFollowedPersonas() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}