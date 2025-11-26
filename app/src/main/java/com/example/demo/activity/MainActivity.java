package com.example.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demo.fragment.FollowedListFragment;
import com.example.demo.fragment.MyPersonaFragment;
import com.example.demo.model.Persona;
import com.example.demo.fragment.ProfileFragment;
import com.example.demo.R;
import com.example.demo.fragment.SocialSquareFragment;
import com.example.demo.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private SocialSquareFragment socialSquareFragment;
    private MyPersonaFragment myPersonaFragment;
    private ProfileFragment profileFragment;
    private FollowedListFragment followedListFragment;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> createPersonaLauncher;
    
    private static final String TAG_SOCIAL = "SOCIAL_SQUARE";
    private static final String TAG_FOLLOWED = "FOLLOWED_LIST";
    private static final String TAG_PERSONA = "MY_PERSONA";
    private static final String TAG_PROFILE = "PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        createPersonaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Persona persona = data.getParcelableExtra(CreateMyPersonaActivity.EXTRA_PERSONA_RESULT);
                                if (persona != null) {
                                    handleNewPersona(persona);
                                }
                            }
                        }
                    }
                });
        
        socialSquareFragment = new SocialSquareFragment();
        myPersonaFragment = new MyPersonaFragment();
        profileFragment = new ProfileFragment();
        followedListFragment = new FollowedListFragment();
        
        loadFragment(socialSquareFragment, TAG_SOCIAL);
        
        binding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_social_square) {
                    loadFragment(socialSquareFragment, TAG_SOCIAL);
                    return true;
                } else if (itemId == R.id.nav_followed_list) {
                    loadFragment(followedListFragment, TAG_FOLLOWED);
                    return true;
                } else if (itemId == R.id.nav_my_persona) {
                    loadFragment(myPersonaFragment, TAG_PERSONA);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(profileFragment, TAG_PROFILE);
                    return true;
                }
                return false;
            }
        });
    }
    
    public void launchCreatePersonaActivity() {
        Intent intent = new Intent(this, CreateMyPersonaActivity.class);
        createPersonaLauncher.launch(intent);
    }
    
    private void handleNewPersona(Persona persona) {
        binding.bottomNavView.setSelectedItemId(R.id.nav_my_persona);
        
        binding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG_PERSONA);
                
                if (currentFragment instanceof MyPersonaFragment) {
                    ((MyPersonaFragment) currentFragment).onPersonaCreated(persona);
                }
            }
        });
    }
    
    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        transaction.replace(R.id.fragment_container, fragment, tag);
        
        transaction.commit();
    }
}