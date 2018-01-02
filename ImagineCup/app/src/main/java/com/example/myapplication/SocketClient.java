package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient extends AppCompatActivity {
    Socket socket = null;
    TextView receiveText;
    Button connectBtn;
    String ip = "192.168.0.175";
    int port = 8888;
    String message = "send";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_client);

        getSupportActionBar().setElevation(0);

        connectBtn = (Button) findViewById(R.id.buttonConnect);
        receiveText = (TextView) findViewById(R.id.textViewReceive);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyClientTask myClientTask = new MyClientTask(ip, port, message);
                myClientTask.execute();
            }
        });
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String destAddress;
        int destPort;
        String response = "";
        String myMessage = "";

        MyClientTask(String addr, int port, String message) {
            destAddress = addr;
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

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                response = "서버의 응답: " + response;
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
        protected  void onPostExecute(Void result) {
            receiveText.setText(response);
            super.onPostExecute(result);
        }
    }
}