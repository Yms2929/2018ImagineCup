package com.example.myapplication;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Yookmoonsu on 2018-01-15.
 */

public class ClientSocket extends AsyncTask<Void, Void, Void> {
    String destAddress;
    int destPort;
    String myMessage = "";
    String response = "";

    ClientSocket(String address, int port, String message) {
        destAddress = address;
        destPort = port;
        myMessage = message;
    }

    @Override
    protected Void doInBackground(Void... voids) { // 메세지 송수신
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
    protected void onPostExecute(Void result) { // 클라이언트 실행
        if (myMessage.equals("send")) {
//            timer.cancel();
//            timerTask.cancel();
//            stream = true;
//            startActivity(new Intent(getApplicationContext(), WebViewStreaming.class));
        }
        super.onPostExecute(result);
    }
}
