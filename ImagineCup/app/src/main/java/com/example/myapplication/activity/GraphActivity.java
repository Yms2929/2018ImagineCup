package com.example.myapplication.activity;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    ViewPager graphViewPager;
    LinearLayout linearLayout;
//    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_tabbar);

//        drawerLayout = (DrawerLayout) findViewById(R.id.graphDrawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.graphToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            VectorDrawableCompat compat = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black_24dp, getTheme()); // 이미지 벡터
            compat.setTint(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, getTheme())); // 벡터 색깔
            actionBar.setHomeAsUpIndicator(compat);
        }

        graphViewPager = (ViewPager) findViewById(R.id.graphPager);
        linearLayout = (LinearLayout) findViewById(R.id.TabLinearLayout);

        TextView tab_first = (TextView) findViewById(R.id.graph1);
        TextView tab_second = (TextView) findViewById(R.id.graph2);
        final TextView textDate = (TextView) findViewById(R.id.textDate);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        final String currentDay = format.format(date);
        textDate.setText(currentDay + " " + doDayOfWeek());

        graphViewPager.setAdapter(new pagerAdapter(getFragmentManager()));
        graphViewPager.setCurrentItem(0);

        tab_first.setOnClickListener(movePageListener);
        tab_first.setTag(0);
        tab_second.setOnClickListener(movePageListener);
        tab_second.setTag(1);

        tab_first.setSelected(true);
        graphViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i = 0;
                while (i < 2) {
                    if (position == i) {
                        linearLayout.findViewWithTag(i).setSelected(true);
                        textDate.setText(getPreviousDay() + "~" + currentDay);
                    } else {
                        linearLayout.findViewWithTag(i).setSelected(false);
                        textDate.setText(currentDay + " " + doDayOfWeek());
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    View.OnClickListener movePageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();

            int i = 0;
            while (i < 2) {
                if (tag == i) {
                    linearLayout.findViewWithTag(i).setSelected(true);
                } else {
                    linearLayout.findViewWithTag(i).setSelected(false);
                }
                i++;
            }

            graphViewPager.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends android.support.v13.app.FragmentStatePagerAdapter {
        public pagerAdapter(android.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstGraphFragment();
                case 1:
                    return new SecondGraphFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.animation3, R.anim.animation4);
//    }

    //상태바 없애기
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryTranslucent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 드로어 이벤트
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String doDayOfWeek() { // 오늘 요일 구하기
        Calendar calendar = Calendar.getInstance();
        String day = "";
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        switch (week) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }

        return day;
    }

    public String getPreviousDay() { // 이전 날짜 구하기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -6);  // 오늘 날짜에서 하루를 뺌.
        String day = sdf.format(calendar.getTime());

        return day;
    }
}