package com.monosky.zhihudaily.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.monosky.zhihudaily.BaseApplication;
import com.monosky.zhihudaily.ConstData;
import com.monosky.zhihudaily.R;
import com.monosky.zhihudaily.activity.adapter.DrawerMenuAdapter;
import com.monosky.zhihudaily.fragment.IndexFragment;
import com.monosky.zhihudaily.fragment.ThemeFragment;
import com.monosky.zhihudaily.http.JsonParseUtils;
import com.monosky.zhihudaily.module.ThemeData;
import com.monosky.zhihudaily.util.SPUtils;
import com.monosky.zhihudaily.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * APP首页
 */
public class MainActivity extends BaseActivity {

    public static MainActivity mInstance;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private RelativeLayout mContainer;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mDrawerMenu;
    private RecyclerView mDrawerRecycleView;
    private ActionBarDrawerToggle mDrawerToggle;
    // adapter
    private DrawerMenuAdapter menuAdapter;
    private List<ThemeData> themeDataList;
    // ToolBar展示的title
    private String mPageTitle;
    // fragment
    private IndexFragment mIndexFragment;
    private ThemeFragment mThemeFragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private Fragment mCurrentFragment;
    private ThemeData mCurrentTheme;
    private StringBuffer mThemeLikeSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = MainActivity.this;

        getViews();
        setViews();

        mIndexFragment = new IndexFragment();
        mIndexFragment.setOnFragmentInteractionListener(new IndexFragment.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Object object) {
                mPageTitle = String.valueOf(object);
                getSupportActionBar().setTitle(mPageTitle);
            }
        });
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.app_main_container, mIndexFragment).show(mIndexFragment).commit();
        mFragmentList.add(mIndexFragment);
        mCurrentFragment = mIndexFragment;
    }

    /**
     * 获取views
     */
    private void getViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mToolbar = (Toolbar) findViewById(R.id.app_main_toolbar);
        mDrawerMenu = (RelativeLayout) findViewById(R.id.drawer_menu);
        mDrawerRecycleView = (RecyclerView) findViewById(R.id.drawer_recycle_view);
        mContainer = (RelativeLayout) findViewById(R.id.app_main_container);
    }

    /**
     * 设置views
     */
    private void setViews() {
        // 设置toolBar
        mPageTitle = getResources().getString(R.string.main_page);
        mToolbar.setTitle(mPageTitle);
        mToolbar.setTitleTextAppearance(this, R.style.ToolbarTitleAppearance);
        setSupportActionBar(mToolbar);
        // 设置drawerLayout
        setDrawerLayout();
        // 设置drawerMenu
        setDrawerMenu();
    }

    /**
     * 设置drawerMenu
     */
    private void setDrawerMenu() {
        mLayoutManager = new LinearLayoutManager(this);
        mDrawerRecycleView.setLayoutManager(mLayoutManager);
        themeDataList = BaseApplication.themesDatas;
        if (themeDataList == null || themeDataList.isEmpty()) {
            String themes = String.valueOf(SPUtils.get(ConstData.THEMES_DATA, ""));
            if (!TextUtils.isEmpty(themes)) {
                themeDataList = JsonParseUtils.getThemeDatas(themes);
                if (themeDataList != null && !themeDataList.isEmpty()) {
                    BaseApplication.themeDataMap.clear();
                    for (ThemeData themeData : themeDataList) {
                        BaseApplication.themeDataMap.put(themeData.getThemeId(), themeData);
                    }
                }
            }
        }
        menuAdapter = new DrawerMenuAdapter(this, themeDataList, BaseApplication.themeLikes);
        mDrawerRecycleView.setAdapter(menuAdapter);
        menuAdapter.setItemClickListner(new DrawerMenuAdapter.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(View view, Object data) {
                FragmentTransaction fragmentTransaction;
                switch (view.getId()) {
                    case R.id.top_login_layout:
                        showTipDialog();
                        break;
                    case R.id.top_favorites_layout:
                        ToastUtils.showShort(mInstance, "shoucang");
                        break;
                    case R.id.top_download_layout:
                        ToastUtils.showShort(mInstance, "download");
                        break;
                    case R.id.drawer_item_middle_layout:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        if (mCurrentFragment != mIndexFragment) {
                            fragmentTransaction.replace(R.id.app_main_container, mIndexFragment).commit();
                            mCurrentTheme = null;
                        }
                        mDrawerLayout.closeDrawers();
                        getSupportActionBar().setTitle(mPageTitle);
                        break;
                    case R.id.drawer_item_bottom_layout:
                        ThemeData themeData = (ThemeData) data;
                        Boolean showNew = false;
                        if (mCurrentTheme == null) {
                            showNew = true;
                        } else {
                            if (mCurrentFragment != mThemeFragment || !mCurrentTheme.getThemeId().equals(themeData.getThemeId())) {
                                showNew = true;
                            }
                        }
                        if (showNew) {
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putString("themeId", themeData.getThemeId());
                            if (!mFragmentList.contains(mThemeFragment)) {
                                mThemeFragment = new ThemeFragment();
                            } else {
                                fragmentTransaction.remove(mThemeFragment);
                            }
                            mThemeFragment.setArguments(bundle);
                            fragmentTransaction.replace(R.id.app_main_container, mThemeFragment).show(mThemeFragment).commit();
                            mCurrentFragment = mThemeFragment;
                            mCurrentTheme = themeData;
                        }
                        mDrawerLayout.closeDrawers();
                        getSupportActionBar().setTitle(themeData.getThemeName());
                        break;
                    case R.id.bottom_img_layout:
                        ThemeData theme = (ThemeData) data;
                        if (!BaseApplication.themeLikes.contains(theme.getThemeId())) {
                            BaseApplication.themeLikes.add(0, theme.getThemeId());
                        }
                        menuAdapter.refreshAdapter(themeDataList, BaseApplication.themeLikes);
                        mThemeLikeSb = new StringBuffer();
                        for (String str : BaseApplication.themeLikes) {
                            mThemeLikeSb.append(str).append(",");
                        }
                        String themeLikes = mThemeLikeSb.toString();
                        themeLikes = themeLikes.substring(0, themeLikes.length() - 1);
                        SPUtils.put(ConstData.THEME_LIKE, themeLikes);
                        break;
                }
            }
        });
    }

    /**
     * 设置DrawerLayout
     */
    private void setDrawerLayout() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                mInstance,
                mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                mPageTitle = getResources().getString(R.string.app_name);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                mDrawerLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentTheme != null) {
                            menuAdapter.refreshAdapter(mCurrentTheme.getThemeId());
                        } else {
                            menuAdapter.refreshAdapter("-1");
                        }
                    }
                }, 1000);
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mCurrentTheme == null) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem mMessageItem = menu.findItem(R.id.action_message);
            if (mDrawerLayout.isDrawerOpen(mDrawerMenu)) {
                mMessageItem.setVisible(false);
            } else {
                mMessageItem.setVisible(true);
            }
        } else {
            getMenuInflater().inflate(R.menu.menu_theme, menu);
            if (BaseApplication.themeLikes.contains(mCurrentTheme.getThemeId())) {
                menu.findItem(R.id.action_theme_option).setIcon(R.mipmap.theme_remove);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_theme_option) {
            if (BaseApplication.themeLikes.contains(mCurrentTheme.getThemeId())) {
                BaseApplication.themeLikes.remove(mCurrentTheme.getThemeId());
            } else {
                BaseApplication.themeLikes.add(0, mCurrentTheme.getThemeId());
            }
            menuAdapter.refreshAdapter(themeDataList, BaseApplication.themeLikes);
            mThemeLikeSb = new StringBuffer();
            for (String str : BaseApplication.themeLikes) {
                mThemeLikeSb.append(str).append(",");
            }
            String themeLikes = mThemeLikeSb.toString();
            themeLikes = themeLikes.substring(0, themeLikes.length() - 1);
            SPUtils.put(ConstData.THEME_LIKE, themeLikes);
            invalidateOptionsMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
