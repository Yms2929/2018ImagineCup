package com.example.myapplication;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by woga1 on 2018-01-18.
 */

public class BackgroundService extends Service {
    String ip = "192.168.0.175";
    String port = "8888";
    Timer timer;
    TimerTask timerTask;
    String activityMessage;

    public BackgroundService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        activityMessage = intent.getStringExtra("message");

        if (activityMessage.equals("connect") || activityMessage.equals("exit")) {
            socketMessage(activityMessage);
        } else if (activityMessage.equals("send")) {
            socketMessage(activityMessage);
        }

        startForeground(1, new Notification());
        return super.onStartCommand(intent, flags, startId);
    }

    public void socketMessage(final String message) { // 서버에 메시지 보내기
        if (message.equals("connect") || message.equals("exit")) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    ClientSocket clientSocket = new ClientSocket(ip, Integer.parseInt(port), message, getApplicationContext());
                    clientSocket.execute();
                    Log.e("Background", message);

                }
            };

            timer.schedule(timerTask, 1000, 5000);
        } else if (message.equals("send")) {
            ClientSocket clientSocket = new ClientSocket(ip, Integer.parseInt(port), message, getApplicationContext());
            clientSocket.execute();
            Log.e("Background", message);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("Background", "cancel");
        if (activityMessage.equals("connect") || activityMessage.equals("exit")) {
            timer.cancel();
            timerTask.cancel();
        }
    }
}
