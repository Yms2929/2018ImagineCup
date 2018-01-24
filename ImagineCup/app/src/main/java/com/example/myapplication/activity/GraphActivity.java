package com.example.myapplication.activity;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GraphActivity extends AppCompatActivity {
    ViewPager graphViewPager;
    LinearLayout linearLayout;
    private static final String TAG_RESULTQUREY = "result";
    private static final String TAG_TODAY = "today";
    private static final String TAG_TEMPERATURE = "temperature";
    private static final String TAG_HUMIDITY = "humidity";
    JSONArray jsonArray = null;
    String json;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_tabbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.graphDrawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.graphToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            VectorDrawableCompat compat = VectorDrawableCompat.create(getResources(), R.drawable.ic_dehaze_black_24dp, getTheme()); // 이미지 벡터
            compat.setTint(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, getTheme())); // 벡터 색깔
            actionBar.setHomeAsUpIndicator(compat);
        }

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
                    } else {
                        linearLayout.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getData("http://192.168.0.85/PHP_atmosphere.php");
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
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) { // 웹서버에 요청

                String uri = params[0];
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    StringBuilder stringBuilder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) { // json 값을 모두 가져옴
                        stringBuilder.append(json + "\n");
                    }
                    return stringBuilder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) { // 웹서버에서 post 받으면 실행
                json = result;
                showList();
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }

    public void showList() { // 리스트뷰에 나타내기
        try {
            JSONObject jsonObject = new JSONObject(json); // json형식 객체
            jsonArray = jsonObject.getJSONArray(TAG_RESULTQUREY); // json배열

            for (int i = 0; i < jsonArray.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
                JSONObject object = jsonArray.getJSONObject(i);
                String today = object.getString(TAG_TODAY);
                String temperature = object.getString(TAG_TEMPERATURE);
                String humidity = object.getString(TAG_HUMIDITY);

                Log.e("php", today);
                Log.e("php", temperature);
                Log.e("php", humidity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("php error", e.getMessage());
        }
    }
}