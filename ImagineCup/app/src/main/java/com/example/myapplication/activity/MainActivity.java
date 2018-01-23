package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FunctionAdapter;
import com.example.myapplication.function.BackgroundService;

public class MainActivity extends AppCompatActivity {
    Button btnConnect;
    DrawerLayout drawerLayout;
    GridView gridView;
    FunctionAdapter adapter;
    int[] functionImage = {R.drawable.main_streaming, R.drawable.main_sleep_check, R.drawable.main_heat_check, R.drawable.main_four_icon};
    boolean background = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        gridView = (GridView) findViewById(R.id.gridView);
        adapter = new FunctionAdapter(getApplicationContext(), functionImage);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 그리드뷰 클릭 이벤트
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        stopService(new Intent(getApplicationContext(), BackgroundService.class));
                        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "send"));
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), SleepRecordActivity.class)); // 수면기록 화면
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), GraphActivity.class)); // 그래프 화면
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), DataResultActivity.class)); // 자장가 화면
                        break;
                    default:
                        break;
                }
            }
        });

        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "connect"));

//        startService(new Intent(getApplicationContext(), DataResultActivity.class));
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

    @Override
    protected void onRestart() { // 화면 재시작
        super.onRestart();

        stopService(new Intent(getApplicationContext(), BackgroundService.class));
        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "exit"));
    }
}