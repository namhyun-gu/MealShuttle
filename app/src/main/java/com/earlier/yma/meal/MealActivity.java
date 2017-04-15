package com.earlier.yma.meal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.earlier.yma.R;
import com.earlier.yma.service.MealDataService;
import com.earlier.yma.ui.PrefActivity;
import com.earlier.yma.util.ActivityUtils;

import java.util.Date;

import javax.inject.Inject;

public class MealActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        TabLayout.OnTabSelectedListener {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    private static final String CURRENT_DATE_KEY = "CURRENT_DATE_KEY";

    @Inject MealPresenter mMealPresenter;

    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        setupToolbar();
        setupDrawerContent();
        setupTabLayout();
        setupFab();

        MealFragment mealFragment =
                (MealFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (mealFragment == null) {
            mealFragment = MealFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mealFragment, R.id.container);
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        DaggerMealComponent.builder()
                .mealPresenterModule(new MealPresenterModule(this, mealFragment))
                .build()
                .inject(this);

        if (savedInstanceState != null) {
            MealFilterType currentFiltering =
                    (MealFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            Date currentDate =
                    (Date) savedInstanceState.getSerializable(CURRENT_DATE_KEY);

            mMealPresenter.setFiltering(currentFiltering);
            mMealPresenter.setDate(currentDate);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mMealPresenter.getFiltering());
        outState.putSerializable(CURRENT_DATE_KEY, mMealPresenter.getDate());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void setupDrawerContent() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = new Intent(MealActivity.this, PrefActivity.class);
                        switch (item.getItemId()) {
                            case R.id.nav_settings:
                                intent.putExtra(PrefActivity.BUNDLE_TYPE,
                                        PrefActivity.TYPE_SETTINGS);
                                startActivity(intent);
                                break;
                            case R.id.nav_info:
                                intent.putExtra(PrefActivity.BUNDLE_TYPE,
                                        PrefActivity.TYPE_INFORMATION);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MealActivity.this, MealDataService.class);
                startService(intent);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        mMealPresenter.setFiltering(MealFilterType.values()[position]);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // no-op
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
