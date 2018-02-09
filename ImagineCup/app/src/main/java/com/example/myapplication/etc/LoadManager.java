package com.example.myapplication.etc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by woga1 on 2018-01-18.
 */

public class LoadManager {

    URL url;                //접속대상 서버주소를 가진 객체

    static String testurl = "http://52.170.90.244/data";
    HttpURLConnection conn;    //통신을 담당하는 객체

    BufferedReader buffer = null;
    //필요한 객체 초기화

    public LoadManager() {
        try {
            Log.e("LoadManger", "url연결");
            url = new URL(testurl);
            conn = (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            Log.e("LoadManger", "error");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("LoadManger", "io error");
            e.printStackTrace();
        }
    }

    public String request() {

        String data = "";
        Log.e("LoadMangerRequest", "hey");
        try {

            conn.connect();            //웹서버에 요청하는 시점
            InputStream is = conn.getInputStream();    //웹서버로부터 전송받을 데이터에 대한 스트림 얻기

            //1byte기반의 바이트스트림이므로 한글이 깨진다.
            //따라서 버퍼처리된 문자기반의 스트림으로 업그레이드 해야 된다.

            buffer = new BufferedReader(new InputStreamReader(is));
            //스트림을 얻어왔으므로, 문자열로 반환
            StringBuffer str = new StringBuffer();
            String d = null;
            while ((d = buffer.readLine()) != null) {
                str.append(d);
            }
            data = str.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();
                }
            }
        }
        Log.e("LoadManger", data);
        return data;

    }
}