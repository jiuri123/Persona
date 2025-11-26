package com.example.demo.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import com.example.demo.utils.MockData;
import com.example.demo.model.Post;
import com.example.demo.adapter.SocialPostAdapter;
import com.example.demo.databinding.FragmentSocialSquareBinding;
import com.example.demo.viewmodel.MainViewModel;

import java.util.List;

public class SocialSquareFragment extends Fragment {

    private FragmentSocialSquareBinding binding;

    private SocialPostAdapter adapter;
    private List<Post> postList;
    private MainViewModel mainViewModel;

    public SocialSquareFragment() {
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSocialSquareBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rvSocialSquare.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = MockData.getMockPersonaPosts();

        adapter = new SocialPostAdapter(getContext(), postList);
        adapter.setMainViewModel(mainViewModel);
        binding.rvSocialSquare.setAdapter(adapter);

        setupViewObservers();

        binding.fabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.generateNewPost();
            }
        });
    }

    private void setupViewObservers() {

        mainViewModel.getNewPostLiveData().observe(getViewLifecycleOwner(), new Observer<Post>() {
            @Override
            public void onChanged(Post newPost) {
                if (newPost != null && adapter != null) {
                    adapter.addPostAtTop(newPost);
                    binding.rvSocialSquare.scrollToPosition(0);
                }
            }
        });

        mainViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                binding.fabAddPost.setEnabled(!isLoading);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}