package com.example.myapplication.function;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.etc.LoadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yookmoonsu on 2018-01-24.
 */

public class DataResultService extends Service {
    NotifyBabyWarningResult babyWarningResult;
    SoundPool soundPool;
    AudioManager audioManager;
    int soundId;
    int streamId;
    private View mView;
    private WindowManager mManager;
    private WindowManager.LayoutParams mParams;
    boolean play = false;
    Timer timer;
    TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();

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

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.background_service, null);

        // 뷰 터치가능 및 포커스 가지지 않게 함
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager.addView(mView, mParams);

        Button btnConfirm = (Button) mView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 뷰의 확인 버튼 눌러야 알림 중지
                soundPool.stop(streamId);
                mView.setVisibility(View.INVISIBLE);
                play = false;
            }
        });

        mView.setVisibility(View.INVISIBLE); // 뷰 안보이게 함

        final Context context = this;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!play) {
                    babyWarningResult = new NotifyBabyWarningResult(context);
                    babyWarningResult.execute("");
                }
            }
        };

        timer.schedule(timerTask, 1000, 8000); // 8초마다 반복 실행
    }

    //웹에서 데이터를 가져오기 전에 먼저 네트워크 상태부터 확인
    public void conntectCheck() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            // fetch data

            //Toast.makeText(this,"네트워크 연결중입니다.", Toast.LENGTH_SHORT).show();

            babyWarningResult = new NotifyBabyWarningResult(this);

            babyWarningResult.execute("");

        } else {

            // display error

            Toast.makeText(this, "네트워크 상태를 확인하십시오", Toast.LENGTH_SHORT).show();

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotifyBabyWarningResult extends AsyncTask<String, Integer, String> {

        LoadManager load;

        public NotifyBabyWarningResult(Context context) {
            load = new LoadManager();
            Log.e("MenuLoadManger", "context");
        }

        protected void onPreExecute() {

            super.onPreExecute();
            Log.e("MenuLoadManger", "preexecute");
        }

        protected String doInBackground(String... params) {
            //웹서버에 요청시도

            String data = load.request();
            Log.e("AsnkTask ask", data);
            return data;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("MenuLoadManger", "onPostexecute");

            try {
                JSONObject jObj = new JSONObject(result);
                String back = jObj.getString("back");
                String front = jObj.getString("front");
                String side = jObj.getString("side");

                double backValue = Double.parseDouble(back); // string을 long으로 형변환
                double frontValue = Double.parseDouble(front);
                double sideValue = Double.parseDouble(side);

                if (backValue > frontValue && backValue > sideValue) { // 텐서플로 결과값중 back이 가장 높을 때
                    if (!play) {
                        mView.setVisibility(View.VISIBLE);
                        showNotification();
                        checkSoundMode();
                        streamId = soundPool.play(soundId, 1.0F, 1.0F, 1, -1, 1.0F);
                        play = true;
                    } else {
                        Log.e("background", "press confirm");
                    }
                }
            } catch (JSONException e) {
                Log.e("MenuLoadManger", "postexecute error");
                e.printStackTrace();
            }
        }
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
                        .setSmallIcon(R.drawable.baby)
                        .setContentTitle("Baby warning")
                        .setContentText("Check your baby's posture!")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void onDestroy() { // 서비스가 종료될때
        super.onDestroy();

//        soundPool.stop(streamId);
//        mView.setVisibility(View.INVISIBLE);
    }
}