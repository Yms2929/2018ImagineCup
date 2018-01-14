package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    String ip = "192.168.0.175";
    String port = "8888";
    Button btnSleepRecord;
    Button btnStreaming;
    Button btnHeatCheck;
    Button btnAlarm;
    Button btnLullaby;
    DrawerLayout drawerLayout;
    GridView gridView;
    FunctionAdapter adapter;
    MyClientTask myClientTask;
    Timer timer;
    TimerTask timerTask;
    SoundPool soundPool;
    AudioManager audioManager;
    int[] functionImage = {R.drawable.main_streaming, R.drawable.main_sleep_check, R.drawable.main_heat_check, R.drawable.main_four_icon};
    int soundId;
    int streamId;
    boolean stream = false;
    boolean play = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar); // 툴바
        setSupportActionBar(toolbar);

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

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        soundId = soundPool.load(this, R.raw.alarm, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 롤리팝 이전은 단순히 생성자로 생성 롤리팝 이후에는 Builder()로 생성
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build(); // 동시에 재생할 수 있는 갯수
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        }

        btnSleepRecord = (Button) findViewById(R.id.btnSleepRecord);
        btnStreaming = (Button) findViewById(R.id.btnStreaming);
        btnHeatCheck = (Button) findViewById(R.id.btnHeatCheck);
        btnAlarm = (Button) findViewById(R.id.btnAlarm);
        btnLullaby = (Button) findViewById(R.id.btnLullaby);

//        socketMessage("connect");

        btnSleepRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RecordActivity.class));
            }
        });

        btnHeatCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HeatCheckActivity.class));
            }
        });

        btnStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                timerTask.cancel();
                myClientTask = new MyClientTask(ip, Integer.parseInt(port), "send");
                myClientTask.execute();
                stream = true;
            }
        });

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification();

                if (!play) {
                    checkSoundMode();
                    streamId = soundPool.play(soundId, 1.0F, 1.0F, 1, -1, 1.0F);
                    play = true;
                } else if (play) {
                    soundPool.stop(streamId);
                    play = false;
                }
            }
        });

        btnLullaby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkSoundMode() {
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

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String destAddress;
        int destPort;
        String myMessage = "";
        String response = "";

        MyClientTask(String address, int port, String message) {
            destAddress = address;
            destPort = port;
            myMessage = message;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket = null;
            myMessage = myMessage.toString();

            try {
                socket = new Socket(destAddress, destPort);
                // 송신
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(myMessage.getBytes());

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                // 수신
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

                response = "서버의 응답: " + response; // 서버로부터 응답을 받아서 웹뷰를 띄워줘야 한다

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnKnownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

            if (myMessage.equals("send")) {
                startActivity(new Intent(getApplicationContext(), WebViewStreaming.class));
            }
            super.onPostExecute(result);
        }
    }

    public void socketMessage(final String message) {
        if (!stream) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    myClientTask = new MyClientTask(ip, Integer.parseInt(port), message);
                    myClientTask.execute();
                }
            };

            timer.schedule(timerTask, 1000, 5000);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

//        socketMessage("exit");
    }
}