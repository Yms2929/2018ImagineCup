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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SleepRecordAdapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);

        btnSleep = (Button) findViewById(R.id.btnSleep);
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute("");
                if (!sleep) {
                    sleepDialog();
                } else if (sleep) {
                    awakeDialog();
                }
            }
        });

        listView = (ListView) findViewById(R.id.listview);
        sleepRecordAdapter = new SleepRecordAdapter();

        sleepRecordAdapter.addItem("17/01/17", "00:52:30"); // 데이터베이스에 저장된 날짜와 시간 데이터 불러와야함
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

    public class CheckLogin extends AsyncTask<String, String, String> {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String url = String.format("jdbc:jtds:sqlserver://yookserver.database.windows.net:1433;database=myDatabase;user=yookmoonsu@yookserver;password=!dbr9389818;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
        String message = "";
        String date = "";
        Boolean isSuccess = false;

        public CheckLogin() {

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
//                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(url); // 데이터베이스 연결

                if (connection == null) {
                    message = "Check your internet access";
                } else {
//                    String query = "select * from SalesLT.ProductCategory";
                    String query = "CREATE TABLE Course2\n" +
                            "(\n" +
                            "CourseId  INT IDENTITY PRIMARY KEY,\n" +
                            "Name   NVARCHAR(50) NOT NULL,\n" +
                            "Teacher   NVARCHAR(256) NOT NULL\n" +
                            ")";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(query);

                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
                        Log.e("sqlerror", resultSet.getString(1) + " " + resultSet.getString(2));
                    }
//                    if (resultSet.next()) {
//                        date = resultSet.getString("Date");
//                        message = "query successful";
//                        isSuccess = true;
//                    } else {
//                        message = "Invalid Query";
//                        isSuccess = false;
//                    }
                }
            } catch (SQLException e) {
                isSuccess = false;
                message = e.getMessage();
                Log.e("sqlerror0", message);
            } catch (Exception e) {
                isSuccess = false;
                message = e.getMessage();
                Log.e("sqlerror1", message);
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Exception e) {
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                    }
                }
            }

            return message;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isSuccess) {
                TextView textView = (TextView) findViewById(R.id.txtSql);
                textView.setText(date);
            }
        }
    }
}