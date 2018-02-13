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
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.myapplication.R;
import com.example.myapplication.data.PositionData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Yookmoonsu on 2018-01-24.
 */

public class DataResultService extends Service {
    SoundPool soundPool;
    AudioManager audioManager;
    int soundId;
    int streamId;
    private View mView;
    private WindowManager mManager;
    private WindowManager.LayoutParams mParams;
    boolean play = false;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    double checkBack = 0.62;

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

        databaseReference.child("data").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) { // 처음 생성했을 때 실행

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { // 파이어베이스 값 바뀌면 실행되는 부분
                PositionData positionData = dataSnapshot.getValue(PositionData.class);
                Log.e("TAG", "back : " + positionData.getBack() + ", front : " + positionData.getFront() + ", etc : " + positionData.getEtc());

                double backValue = Double.parseDouble(positionData.getBack()); // string을 double로 형변환
                double frontValue = Double.parseDouble(positionData.getFront());
                double etcValue = Double.parseDouble(positionData.getEtc());


                if(checkBack < backValue) { // back 80 이상만 알람.
                    if (backValue > frontValue && backValue > etcValue) { // 텐서플로 결과값중 back이 가장 높을 때
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
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { // Child 삭제 되었을 때

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                        .setSmallIcon(R.drawable.warning)
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
    }
}