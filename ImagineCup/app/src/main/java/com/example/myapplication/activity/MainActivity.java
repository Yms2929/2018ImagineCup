package com.example.myapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.RecyclerAdapter;
import com.example.myapplication.data.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pyxis.uzuki.live.rollingbanner.RollingBanner;
import pyxis.uzuki.live.rollingbanner.RollingViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private RollingBanner rollingBanner;
    DrawerLayout drawerLayout;
    Intent i; // 배너 클릭 시 이동을 위한 Intent
    final int ITEM_SIZE = 4; // 카드뷰 갯수
    private String[] txtRes = new String[]{"1", "2", "3"}; // 3개 이미지 배너
    public static int REQ_CODE_OVERLAY_PERMISSION = 5469;
    TextView babyName,temp_navi, humidity_navi;
    SharedPreferences mPref;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#F48FB1"));
        }
        startOverlayWindowService();

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

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        //이하 아이템 지정. 전역 변수 final int ITEM_SIZE 와 동일한 갯수 설정
        item[0] = new Item(R.drawable.livestreaming, "#sleep");
        item[1] = new Item(R.drawable.graph2, "#thermoeter");
        item[2] = new Item(R.drawable.sleeprecord, "#record");
        item[3] = new Item(R.drawable.safesleep, "#setting");

        //Size add
        for(int i=0; i < ITEM_SIZE; i++){
            items.add(item[i]);
        }

//        recyclerView.addOnItemTouchListener();
        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));

//        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "connect"));
//        startService(new Intent(getApplicationContext(), DataResultService.class));

        //내비바
        babyName = (TextView) findViewById(R.id.babyname);
        temp_navi = (TextView) findViewById(R.id.textTemperature);
        humidity_navi = (TextView) findViewById(R.id.textHumidity);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        circleImageView = (CircleImageView) findViewById(R.id.circleProfilImageView);
        circleImageView.setOnClickListener(new clickListener());

    }

    class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "dd", Toast.LENGTH_SHORT).show();
        } // end onClick

    } // end MyListener()


    public void startOverlayWindowService() { // API 23 이상은 Overlay 사용 가능한지 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            onObtainingPermissionOverlayWindow();
        } else {
            System.out.print("버전이 낮거나 오버레이 설정창이 없습니다.");
        }
    }

    public void onObtainingPermissionOverlayWindow() { // 현재 패키지 명을 넘겨 설정화면을 노출
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_OVERLAY_PERMISSION) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "오버레이 권한 확인 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "오버레이 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
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

    protected void setNaviText(){
        temp_navi.setText("-17");
        humidity_navi.setText("10");
        babyName.setText(mPref.getString("userBabyName", "Parkers"));

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


            final int[] images = {R.drawable.baby, R.drawable.banner2, R.drawable.baby}; // 배너수정
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

//        stopService(new Intent(getApplicationContext(), BackgroundService.class));
//        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "exit"));
    }
}