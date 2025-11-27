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
import com.example.demo.fragment.MyProfileFragment;
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
    private MyPersonaFragment myPersonaFragment;
    private MyProfileFragment myProfileFragment;
    private FollowedListFragment followedListFragment;
    
    // 视图绑定，用于替代findViewById，提高性能和类型安全
    private ActivityMainBinding activityMainBinding;
    
    // Activity结果启动器，用于处理从CreateMyPersonaActivity返回的结果
    // 这是AndroidX推荐的替代startActivityForResult的方式
    private ActivityResultLauncher<Intent> createMyPersonaLauncher;
    
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
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        
        // 注册Activity结果启动器，用于处理从CreateMyPersonaActivity返回的数据
        // 使用registerForActivityResult替代已废弃的startActivityForResult方法
        createMyPersonaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // 检查结果是否成功
                        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                // 获取从CreateMyPersonaActivity返回的Persona对象
                                Persona myPersona = data.getParcelableExtra(CreateMyPersonaActivity.EXTRA_PERSONA_RESULT);
                                if (myPersona != null) {
                                    handleNewPersona(myPersona);
                                }
                            }
                        }
                    }
                });
        
        // 初始化所有Fragment实例
        socialSquareFragment = new SocialSquareFragment();
        myPersonaFragment = new MyPersonaFragment();
        myProfileFragment = new MyProfileFragment();
        followedListFragment = new FollowedListFragment();
        
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
                    loadFragment(followedListFragment, TAG_FOLLOWED);
                    return true;
                } else if (itemId == R.id.nav_my_persona) {
                    loadFragment(myPersonaFragment, TAG_PERSONA);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(myProfileFragment, TAG_PROFILE);
                    return true;
                }
                return false;
            }
        });
    }
    
    /**
     * 启动创建Persona的Activity
     * 使用ActivityResultLauncher而不是传统的startActivityForResult
     * 这是AndroidX推荐的现代方式，提供类型安全和更清晰的API
     */
    public void launchCreatePersonaActivity() {
        Intent intent = new Intent(this, CreateMyPersonaActivity.class);
        createMyPersonaLauncher.launch(intent);
    }
    
    /**
     * 处理新创建的Persona对象
     * @param myPersona 新创建的Persona对象
     */
    private void handleNewPersona(Persona myPersona) {
        // 切换到我的Persona页面
        activityMainBinding.bottomNavView.setSelectedItemId(R.id.nav_my_persona);
        
        // 使用post方法确保在UI线程中执行，并且等待Fragment完全加载后再调用
        // 这是一种处理Fragment生命周期和UI更新的安全方式
        activityMainBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                // 获取当前显示的Fragment
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG_PERSONA);
                
                // 如果当前显示的是MyPersonaFragment，则调用其onPersonaCreated方法
                if (currentFragment instanceof MyPersonaFragment) {
                    ((MyPersonaFragment) currentFragment).onPersonaCreated(myPersona);
                }
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