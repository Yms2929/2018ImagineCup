package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SocketClient extends AppCompatActivity {
    TextView receiveText;
    Button btnConnect;
    Button btnStreaming;
    Button btnAlarm;
    String ip = "192.168.0.175";
    String port = "8888";
    MyClientTask myClientTask;
    boolean stream = false;
    Timer timer;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_client);

        getSupportActionBar().setElevation(0);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnStreaming = (Button) findViewById(R.id.btnStreaming);
        receiveText = (TextView) findViewById(R.id.textViewReceive);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stream) {
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            myClientTask = new MyClientTask(ip, Integer.parseInt(port), "connect");
                            myClientTask.execute();
                        }
                    };

                    timer.schedule(timerTask, 1000, 5000);
                }
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
            }
        });
    }

    public void showNotification() {
        NotificationCompat.Builder builder = createNotification();
        builder.setContentIntent(createPendingIntent());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    private NotificationCompat.Builder createNotification() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle("상태바 타이틀")
                .setContentText("상태바 서브타이틀")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(String.valueOf(Notification.PRIORITY_HIGH))
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        return builder;
    }

    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(this, SocketClient.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SocketClient.class);
        stackBuilder.addNextIntent(intent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

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
            receiveText.setText(response);
            if (myMessage.equals("send")) {
                startActivity(new Intent(getApplicationContext(), WebViewStreaming.class));
            }
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (stream) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    myClientTask = new MyClientTask(ip, Integer.parseInt(port), "exit");
                    myClientTask.execute();
                }
            };

            timer.schedule(timerTask, 1000, 5000);
        }
    }
}