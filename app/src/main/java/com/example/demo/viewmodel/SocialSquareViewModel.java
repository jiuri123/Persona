package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Post;
import com.example.demo.repository.SocialRepository;

import java.util.List;

public class SocialSquareViewModel extends ViewModel {

    private final SocialRepository socialRepository;
    private final LiveData<List<Post>> socialPostsLiveData;

    public SocialSquareViewModel() {
        socialRepository = new SocialRepository();
        socialPostsLiveData = socialRepository.getSocialPosts();
    }

    public LiveData<List<Post>> getSocialPosts() {
        return socialPostsLiveData;
    }
}