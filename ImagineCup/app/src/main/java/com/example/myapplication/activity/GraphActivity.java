package com.example.myapplication.activity;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;


public class GraphActivity extends AppCompatActivity {

    ViewPager graphViewPager;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_tabbar);

        graphViewPager = (ViewPager) findViewById(R.id.graphPager);
        linearLayout = (LinearLayout) findViewById(R.id.TabLinearLayout);

        TextView tab_first = (TextView) findViewById(R.id.graph1);
        TextView tab_second = (TextView) findViewById(R.id.graph2);

        graphViewPager.setAdapter(new pagerAdapter(getFragmentManager()));
        graphViewPager.setCurrentItem(0);

        tab_first.setOnClickListener(movePageListener);
        tab_first.setTag(0);
        tab_second.setOnClickListener(movePageListener);
        tab_second.setTag(1);

        tab_first.setSelected(true);
        graphViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                int i = 0;
                while(i<2)
                {
                    if(position==i)
                    {
                        linearLayout.findViewWithTag(i).setSelected(true);
                    }
                    else
                    {
                        linearLayout.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();

            int i = 0;
            while(i<2)
            {
                if(tag==i)
                {
                    linearLayout.findViewWithTag(i).setSelected(true);
                }
                else
                {
                    linearLayout.findViewWithTag(i).setSelected(false);
                }
                i++;
            }

            graphViewPager.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends android.support.v13.app.FragmentStatePagerAdapter
    {
        public pagerAdapter(android.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new FirstGraphFragment();
                case 1:
                    return new SecondGraphFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
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
}