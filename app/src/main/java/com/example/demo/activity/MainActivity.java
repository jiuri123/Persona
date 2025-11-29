package com.example.demo.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demo.fragment.UserFollowedListFragment;
import com.example.demo.fragment.UserPersonaFragment;
import com.example.demo.fragment.UserProfileFragment;
import com.example.demo.R;
import com.example.demo.fragment.SocialSquareFragment;
import com.example.demo.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

/**
 * 主活动 - 应用程序的入口界面
 * 包含底部导航栏和多个Fragment，实现单Activity多Fragment的架构
 * 使用视图绑定(ViewBinding)替代findViewById，提高代码可读性和性能
 * 使用ActivityResultLauncher处理Activity间的结果传递，替代已废弃的startActivityForResult
 */
public class MainActivity extends AppCompatActivity {
    // Fragment实例，用于管理不同的页面
    private SocialSquareFragment socialSquareFragment;
    private UserPersonaFragment userPersonaFragment;
    private UserProfileFragment userProfileFragment;
    private UserFollowedListFragment userFollowedListFragment;

    // Fragment标签，用于标识不同的Fragment
    private static final String TAG_SOCIAL = "SOCIAL_SQUARE";
    private static final String TAG_FOLLOWED = "FOLLOWED_LIST";
    private static final String TAG_PERSONA = "MY_PERSONA";
    private static final String TAG_PROFILE = "PROFILE";

    /**
     * Activity创建时调用，进行初始化操作
     * @param savedInstanceState 保存的Activity状态
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 使用视图绑定初始化布局，避免findViewById的性能开销和类型转换错误
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        
        // 初始化所有Fragment实例
        socialSquareFragment = new SocialSquareFragment();
        userPersonaFragment = new UserPersonaFragment();
        userProfileFragment = new UserProfileFragment();
        userFollowedListFragment = new UserFollowedListFragment();
        
        // 默认加载社交广场Fragment
        loadFragment(socialSquareFragment, TAG_SOCIAL);
        
        // 设置底部导航栏的选中项监听器
        activityMainBinding.bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                // 根据选中的菜单项加载对应的Fragment
                if (itemId == R.id.nav_social_square) {
                    loadFragment(socialSquareFragment, TAG_SOCIAL);
                    return true;
                } else if (itemId == R.id.nav_followed_list) {
                    loadFragment(userFollowedListFragment, TAG_FOLLOWED);
                    return true;
                } else if (itemId == R.id.nav_my_persona) {
                    loadFragment(userPersonaFragment, TAG_PERSONA);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(userProfileFragment, TAG_PROFILE);
                    return true;
                }
                return false;
            }
        });
    }
    
    /**
     * 加载指定的Fragment
     * 使用Fragment事务管理Fragment的切换
     * @param fragment 要加载的Fragment
     * @param tag Fragment的标签，用于标识Fragment
     */
    private void loadFragment(Fragment fragment, String tag) {
        // 获取Fragment管理器
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 开始Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // 替换当前Fragment
        // 使用replace方法而不是add/remove，简化Fragment管理
        transaction.replace(R.id.fragment_container, fragment, tag);
        
        // 提交事务
        // 注意：commit是异步的，如果需要立即执行，可以使用commitNow
        transaction.commit();
    }
}