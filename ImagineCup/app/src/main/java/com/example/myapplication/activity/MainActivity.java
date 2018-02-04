package com.example.myapplication.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FunctionAdapter;
import com.example.myapplication.adapter.RecyclerAdapter;
import com.example.myapplication.data.Item;
import com.example.myapplication.function.BackgroundService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pyxis.uzuki.live.rollingbanner.RollingBanner;
import pyxis.uzuki.live.rollingbanner.RollingViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private RollingBanner rollingBanner;

    DrawerLayout drawerLayout;
    GridView gridView;
    FunctionAdapter adapter;
    //int[] functionImage = {R.drawable., R.drawable.baby, R.drawable.baby, R.drawable.baby};
    boolean background = false;
    Intent i; // 배너 클릭 시 이동을 위한 Intent
    final int ITEM_SIZE = 4; // 카드뷰 갯수

    private String[] txtRes = new String[]{"1", "2", "3"}; // 3개 이미지 배너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollingBanner = findViewById(R.id.banner);

        bannerAdapter adapterTrue = new bannerAdapter(new ArrayList<>(Arrays.asList(txtRes)));
        rollingBanner.setAdapter(adapterTrue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar); // 툴바
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 액션바 홈버튼 설정
        getSupportActionBar().setDisplayShowTitleEnabled(true); // 액션바 타이틀 설정

        ActionBar actionBar = getSupportActionBar(); // 액션바
        if (actionBar != null) {
            VectorDrawableCompat compat = VectorDrawableCompat.create(getResources(), R.drawable.ic_dehaze_black_24dp, getTheme()); // 이미지 벡터
            compat.setTint(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, getTheme())); // 벡터 색깔
            actionBar.setHomeAsUpIndicator(compat);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawer); // 드로어

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        //이하 아이템 지정. 전역 변수 final int ITEM_SIZE 와 동일한 갯수 설정
        item[0] = new Item(R.drawable.livestreaming, "streaming");
        item[1] = new Item(R.drawable.graph2, "graph");
        item[2] = new Item(R.drawable.sleeprecord, "record");
        item[3] = new Item(R.drawable.safesleep, "safesleep");

        //Size add
        for(int i=0; i < ITEM_SIZE; i++){
            items.add(item[i]);
        }

        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));

        //startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "connect"));

        //startService(new Intent(getApplicationContext(), DataResultActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 이벤트
        int id = item.getItemId();

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingActivity.class)); // 자장가 화면
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNotification() { // 팝업 알림
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("아기 알람")
                        .setContentText("아기의 수면자세를 확인 해주세요")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    public class bannerAdapter extends RollingViewPagerAdapter<String> {

        public bannerAdapter(ArrayList<String> itemList) {
            super(itemList);
        }

        @Override
        public View getView(final int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main_pager, null, false);
            FrameLayout container = view.findViewById(R.id.container);

            String txt = getItem(position);
            final int index = getItemList().indexOf(txt);

            final int[] images = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3}; // 배너수정
            container.setBackgroundResource(images[index]);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(index == 0) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
                        startActivity(i);
                    }
                    else if(index == 1){
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.daum.net"));
                        startActivity(i);
                    }
                    else if(index == 2){
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.google.com"));
                        startActivity(i);
                    }
                }
            });

            return view;
        }
    }

    @Override
    protected void onRestart() { // 화면 재시작
        super.onRestart();

        stopService(new Intent(getApplicationContext(), BackgroundService.class));
        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "exit"));
    }
}