package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SleepRecordAdapter;
import com.example.myapplication.etc.Singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SleepRecordActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton btnSleep;
    ListView listView;
    SleepRecordAdapter sleepRecordAdapter;
    boolean sleep = false;
    String currentDate;
    String sleepTime;
    long startTime;
    long endTime;
    String myJSON;
    private static final String TAG_RESULT = "result";
    private static final String TAG_DATE = "Date";
    private static final String TAG_TIME = "Time";
    JSONArray info = null;
    long now;
    Date date;
    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sleeprecordToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            VectorDrawableCompat compat = VectorDrawableCompat.create(getResources(), R.drawable.ic_dehaze_black_24dp, getTheme()); // 이미지 벡터
            compat.setTint(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, getTheme())); // 벡터 색깔
            actionBar.setHomeAsUpIndicator(compat);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.sleeprecordDrawer);
        btnSleep = (ImageButton) findViewById(R.id.btnSleep);

        sleep = Singleton.getInstance().getSleep();
        if (sleep) {
            btnSleep.setImageResource(R.drawable.sleep);
        } else if (!sleep) {
            btnSleep.setImageResource(R.drawable.awake);
        }

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
        getData("http://192.168.0.85/PHP_connection.php"); // php서버 웹주소
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
                    while ((json = bufferedReader.readLine()) != null) { // json 값을 모두 가져옴
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

    public void showList() { // 리스트뷰에 나타내기
        try {
            JSONObject jsonObject = new JSONObject(myJSON); // json형식 객체
            info = jsonObject.getJSONArray(TAG_RESULT); // json배열
            sleepRecordAdapter = new SleepRecordAdapter();
            knowToday();

            for (int i = 0; i < info.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
                JSONObject object = info.getJSONObject(i);
                String date = object.getString(TAG_DATE);
                String time = object.getString(TAG_TIME);

                if (date.equals(currentDate)) { // 오늘 날짜만 보여줌
                    sleepRecordAdapter.addItem(date, time); // 어댑터에 데이터 추가
                }
            }

            listView.setAdapter(sleepRecordAdapter); // 리스트뷰 업데이트
            sleepRecordAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("php error", e.getMessage());
        }
    }

    public void knowToday() { // 오늘 날짜
        now = System.currentTimeMillis();
        date = new Date(now);
        format = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = format.format(date);
    }

    public void sleepDialog() { // 수면할때 다이얼로그
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Baby Sleeping Record");
        builder.setMessage("Is the baby asleep now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 수면시간 시작
                sleep = true;
                Singleton.getInstance().setSleep(sleep);
                btnSleep.setImageResource(R.drawable.sleep);
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

    public void awakeDialog() { // 잠에서 깰때 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Baby Sleeping Record");
        builder.setMessage("Is the baby awake now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 수면시간 종료
                sleep = false;
                Singleton.getInstance().setSleep(sleep);
                btnSleep.setImageResource(R.drawable.awake);
                getNowTime("awake");

                InsertData task = new InsertData(); // insert쿼리 php실행
                task.execute(currentDate, sleepTime);

                getData("http://192.168.0.85/PHP_connection.php"); // 업데이트
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

    public void getNowTime(String state) { // 수면시간 계산하기
        if (state.equals("sleep")) {
            knowToday();

            startTime = System.currentTimeMillis();

            Singleton.getInstance().setDate(currentDate);
            Singleton.getInstance().setNow(startTime);
        }
        else if (state.equals("awake")) {
            knowToday();

            currentDate = Singleton.getInstance().getDate();
            startTime = Singleton.getInstance().getNow();
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
        }
    }

    public class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String date = (String) params[0]; // (String) 해줘야함
            String time = (String) params[1];

            String serverURL = "http://192.168.0.85/PHP_insert.php"; // 서버를 돌리는 pc의 ip주소
            String postParameters = "date=" + date + "&time=" + time; // date와 time 2개를 보내야 되서 구분하는 문자 &삽입

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST"); // 보안상 POST 방식 사용
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e("php", "POST response code " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.e("php error4", String.valueOf(e));
                return new String("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.e("php", "POST response " + result);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 드로어 이벤트
        int id = item.getItemId();

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }
}