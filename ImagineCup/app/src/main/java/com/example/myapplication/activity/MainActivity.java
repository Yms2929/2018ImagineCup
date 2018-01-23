package com.example.myapplication.activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FunctionAdapter;
import com.example.myapplication.function.BackgroundService;

import java.util.ArrayList;
import java.util.Arrays;

import pyxis.uzuki.live.rollingbanner.RollingBanner;
import pyxis.uzuki.live.rollingbanner.RollingViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private RollingBanner rollingBanner;

    DrawerLayout drawerLayout;
    GridView gridView;
    FunctionAdapter adapter;
    SoundPool soundPool;
    AudioManager audioManager;
    int[] functionImage = {R.drawable.main_streaming, R.drawable.main_sleep_check, R.drawable.main_heat_check, R.drawable.main_four_icon};
    int soundId;
    int streamId;
    boolean play = false;
    boolean background = false;
    Intent i;

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
                        startActivity(new Intent(getApplicationContext(), LullabyActivity.class)); // 자장가 화면
                        break;
                    default:
                        break;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 롤리팝 이전은 단순히 생성자로 생성 롤리팝 이후에는 Builder()로 생성
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build(); // 동시에 재생할 수 있는 갯수
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        }

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        soundId = soundPool.load(this, R.raw.alarm, 1);

        //btnConnect = (Button) findViewById(R.id.btnConnect);
        //btnAlarm = (Button) findViewById(R.id.btnAlarm);

//        btnConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!background) {
//                    startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "connect"));
//                    background = true;
//                } else if (background) {
//                    stopService(new Intent(getApplicationContext(), BackgroundService.class));
//                    background = false;
//                }
//            }
//        });

//        btnAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showNotification();
//
//                if (!play) {
//                    checkSoundMode();
//                    streamId = soundPool.play(soundId, 1.0F, 1.0F, 1, -1, 1.0F);
//                    play = true;
//                } else if (play) {
//                    soundPool.stop(streamId);
//                    play = false;
//                }
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 이벤트
        int id = item.getItemId();

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkSoundMode() { // 현재 기기 모드
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL: // 소리모드
                break;
            case AudioManager.RINGER_MODE_SILENT: // 무음모드
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); // 소리모드로 변경
                break;
            case AudioManager.RINGER_MODE_VIBRATE: // 진동모드
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                break;
        }
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
            TextView txtText = view.findViewById(R.id.txtText);

            String txt = getItem(position);
            int index = getItemList().indexOf(txt);


            int[] images = {R.drawable.baby, R.drawable.baby_a};
            container.setBackgroundResource(images[index]);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 번호 나누기 다른 홈페이지
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
                    startActivity(i);
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

    //    public void showNotification() {
//        NotificationCompat.Builder builder = createNotification();
//        builder.setContentIntent(createPendingIntent());
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, builder.build());
//    }
//
//    private NotificationCompat.Builder createNotification() {
//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(icon)
//                .setContentTitle("상태바 타이틀")
//                .setContentText("상태바 서브타이틀")
//                .setAutoCancel(true)
//                .setWhen(System.currentTimeMillis())
//                .setDefaults(Notification.DEFAULT_ALL);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder.setCategory(String.valueOf(Notification.PRIORITY_HIGH))
//                    .setVisibility(Notification.VISIBILITY_PUBLIC);
//        }
//
//        return builder;
//    }

//    private PendingIntent createPendingIntent() {
//        Intent intent = new Intent(this, SocketClient.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(SocketClient.class);
//        stackBuilder.addNextIntent(intent);
//
//        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
}