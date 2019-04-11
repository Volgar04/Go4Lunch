package com.nicolappli.go4lunch.Controllers.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.nicolappli.go4lunch.Adapters.PageAdapter;
import com.nicolappli.go4lunch.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configureViewPagerAndTabs();
        this.configureToolbar();
    }

    private void configureViewPagerAndTabs(){
        ViewPager pager = findViewById(R.id.activity_main_view_pager);

        pager.setAdapter(new PageAdapter(getSupportFragmentManager()) {
        });

        TabLayout tabs = findViewById(R.id.activity_main_tabs);
        tabs.setupWithViewPager(pager);
        tabs.setTabMode(TabLayout.MODE_FIXED);
    }

    private void configureToolbar(){
        // Get the toolbar view inside the activity
        this.mToolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar
        setSupportActionBar(mToolbar);
    }
}
