package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;

/**
 * Created by woga1 on 2018-01-02.
 */

public class MenuActivity extends FragmentActivity {

    private HomeFragment homeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        homeFragment= new HomeFragment();
        initFragment();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(tabId == R.id.tab_call_log){
                    transaction.replace(R.id.contentTab, homeFragment).commit();
                }
            }
        });
    }

    public void initFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentTab, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    }
