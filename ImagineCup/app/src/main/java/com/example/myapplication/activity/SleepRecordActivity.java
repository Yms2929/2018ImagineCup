package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SleepRecordAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SleepRecordActivity extends AppCompatActivity {
    Button btnSleep;
    ListView listView;
    SleepRecordAdapter sleepRecordAdapter;
    boolean sleep = false;
    String currentDate;
    String sleepTime;
    long startTime;
    long endTime;
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();
    String myJSON;
    private static final String TAG_RESULT = "result";
    private static final String TAG_DATE = "Date";
    private static final String TAG_TIME = "Time";
    JSONArray info = null;
    ArrayList<HashMap<String, String>> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);

        btnSleep = (Button) findViewById(R.id.btnSleep);
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sleep) {
                    sleepDialog();
                } else if (sleep) {
                    awakeDialog();
                }
            }
        });

        listView = (ListView) findViewById(R.id.listview);
        infoList = new ArrayList<HashMap<String, String>>();
        getData("http://192.168.0.85/PHP_connection.php"); // php서버 웹주소
    }

    public void showList() {
        try {
            JSONObject jsonObject = new JSONObject(myJSON); // json형식 객체
            info = jsonObject.getJSONArray(TAG_RESULT);
            sleepRecordAdapter = new SleepRecordAdapter();

            for (int i = 0; i < info.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
                JSONObject object = info.getJSONObject(i);
                String date = object.getString(TAG_DATE);
                String time = object.getString(TAG_TIME);
                sleepRecordAdapter.addItem(date, time); // 어댑터에 데이터 추가
            }

            listView.setAdapter(sleepRecordAdapter); // 리스트뷰 업데이트

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("php error3", e.getMessage());
        }
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) { // 웹서버에 요청

                String uri = params[0];
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    StringBuilder stringBuilder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) { // json형식으로 값을 모두 가져옴
                        stringBuilder.append(json + "\n");
                    }
                    return stringBuilder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) { // 웹서버에서 post 받으면 실행
                myJSON = result;
                showList();
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }

    public void sleepDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Baby Sleeping Record");
        builder.setMessage("Is the baby asleep now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 수면시간 시작
                sleep = true;
                btnSleep.setTextColor(Color.BLUE);
                getNowTime("sleep");
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Baby is awake", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void awakeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Baby Sleeping Record");
        builder.setMessage("Is the baby awake now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 수면시간 종료
                sleep = false;
                btnSleep.setTextColor(Color.RED);
                getNowTime("awake");

                sleepRecordAdapter = new SleepRecordAdapter();

                sleepRecordAdapter.addItem(currentDate, sleepTime); // 데이터베이스에 날짜와 시간 데이터 넣어야함
                listView.setAdapter(sleepRecordAdapter); // 리스트뷰 갱신
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Baby is sleeping", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void getNowTime(String state) {
        if (state.equals("sleep")) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            currentDate = format.format(date);

            Log.e("time", currentDate);
            startTime = System.currentTimeMillis();
        } else if (state.equals("awake")) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            String awakeDate = format.format(date);

            endTime = System.currentTimeMillis();
            long resultTime = endTime - startTime;
            long hour = resultTime / (1000 * 60 * 60);
            long minute = resultTime / (1000 * 60);
            long second = resultTime / 1000;
            String strHour = String.valueOf(hour);
            String strMinute = String.valueOf(minute - (hour * 60));
            String strSecond = String.valueOf(second - (minute * 60));

            if (strHour.length() == 1) {
                strHour = "0" + strHour;
            }
            if (strMinute.length() == 1) {
                strMinute = "0" + strMinute;
            }
            if (strSecond.length() == 1) {
                strSecond = "0" + strSecond;
            }

            sleepTime = strHour + ":" + strMinute + ":" + strSecond;
            Log.e("time", awakeDate);
            Log.e("time", sleepTime);
        }
    }

    public void setListViewHeightBasedOnItems(ListView listView) { // 리스트뷰 높이 계산
        // Get list adpter of listview;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int numberOfItems = listAdapter.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}