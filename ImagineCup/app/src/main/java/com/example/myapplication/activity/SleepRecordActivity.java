package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SleepRecordAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SleepRecordActivity extends AppCompatActivity {
    Button btnSleep;
    ListView listView;
    SleepRecordAdapter sleepRecordAdapter;
    boolean sleep = false;
    long startTime;
    long endTime;
    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();

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
        sleepRecordAdapter = new SleepRecordAdapter();

        sleepRecordAdapter.addItem("17/01/17", "00:52:30"); // 날짜와 시간 데이터 넣어야함
        listView.setAdapter(sleepRecordAdapter);
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
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public void getNowTime(String state) {
        if (state.equals("sleep")) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            String currentDate = format.format(date);
            Log.e("time", currentDate);
            startTime = System.currentTimeMillis();
        }
        else if (state.equals("awake")) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            String time = format.format(date);

            endTime = System.currentTimeMillis();
            long resultTime = endTime - startTime;
            Log.e("minute time", String.valueOf(resultTime / (1000 * 60)));
            Log.e("second time", String.valueOf(resultTime / 1000));
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

            String sleepTime = strHour + ":" + strMinute + ":" + strSecond;
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