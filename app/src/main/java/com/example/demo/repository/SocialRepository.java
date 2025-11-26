package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.utils.MockData;
import com.example.demo.model.Post;

import java.util.List;

public class SocialRepository {

    private final MutableLiveData<List<Post>> socialPostsLiveData = new MutableLiveData<>();

    public SocialRepository() {
        loadMockSocialPosts();
    }

    private void loadMockSocialPosts() {
        List<Post> mockPosts = MockData.getMockPersonaPosts();
        socialPostsLiveData.setValue(mockPosts);
    }

    public LiveData<List<Post>> getSocialPosts() {
        return socialPostsLiveData;
    }
}