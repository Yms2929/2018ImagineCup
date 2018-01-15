package com.example.myapplication;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yookmoonsu on 2018-01-15.
 */

public class BackgroundService extends Service {
    String ip = "192.168.0.175";
    String port = "8888";
    ClientSocket clientSocket;
    Timer timer;
    TimerTask timerTask;
    boolean stream = false;

    public BackgroundService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        socketMessage("connect");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        startForeground(1, new Notification());
        return super.onStartCommand(intent, flags, startId);
    }

    public void socketMessage(final String message) { // 서버에 메시지 보내기
        if (!stream) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    clientSocket = new ClientSocket(ip, Integer.parseInt(port), message);
                    clientSocket.execute();
                }
            };

            timer.schedule(timerTask, 1000, 5000);
        }
    }
}