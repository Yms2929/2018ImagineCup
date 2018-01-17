package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

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
    Context context;

    ClientSocket(String address, int port, String message, Context context) {
        this.destAddress = address;
        this.destPort = port;
        this.myMessage = message;
        this.context = context;
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
        if (response.equals("send")) {
            Log.e("Background", response);
            Intent intent = new Intent(context, WebViewStreaming.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        super.onPostExecute(result);
    }
}