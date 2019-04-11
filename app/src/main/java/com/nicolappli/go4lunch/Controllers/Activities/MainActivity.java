package com.nicolappli.go4lunch.Controllers.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.nicolappli.go4lunch.Adapters.PageAdapter;
import com.nicolappli.go4lunch.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //for design
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureViewPagerAndTabs();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }

    //******************************************************************************
    //CONFIGURATION
    //******************************************************************************

    //*****************************
    //TOOLBAR
    //*****************************

    private void configureToolbar(){
        // Get the toolbar view inside the activity
        this.mToolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar
        setSupportActionBar(mToolbar);
    }

    //*****************************
    //VIEW PAGER AND TABS
    //*****************************

    private void configureViewPagerAndTabs(){
        ViewPager pager = findViewById(R.id.activity_main_view_pager);

        pager.setAdapter(new PageAdapter(getSupportFragmentManager()) {
        });

        TabLayout tabs = findViewById(R.id.activity_main_tabs);
        tabs.setupWithViewPager(pager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }

    //*****************************
    //NAVIGATION DRAWER
    //*****************************

    private void configureDrawerLayout(){
        this.mDrawerLayout= findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_drawer_lunch:

                break;
            case R.id.activity_main_drawer_settings:

                break;
            case R.id.activity_main_drawer_logout:

                break;
            default:
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
