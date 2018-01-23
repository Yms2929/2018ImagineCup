package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.etc.LoadManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by woga1 on 2018-01-02.
 */

public class DataResultActivity extends Activity {

    NotifyBabyWarningResult babyWarningResult;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        babyWarningResult = new NotifyBabyWarningResult(this);

        babyWarningResult.execute("");

        textView = (TextView) findViewById(R.id.testText);

        Log.e("LoadManger", "end");
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
//            JSONObject o = new JSONObject(result);
                Log.e("PostExecute", back);
                Log.e("PostExecute", front);

                textView.setText("back is"+ back + "front is " + front + "side is"+ side);
            } catch (JSONException e) {
                Log.e("MenuLoadManger", "postexecute error");
                e.printStackTrace();

            }

        }

    }

}
