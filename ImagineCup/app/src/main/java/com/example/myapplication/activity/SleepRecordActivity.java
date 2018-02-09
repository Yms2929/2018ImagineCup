package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AzureServiceAdapter;
import com.example.myapplication.adapter.SleepRecordAdapter;
import com.example.myapplication.data.SleepRecord;
import com.example.myapplication.etc.Singleton;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SleepRecordActivity extends AppCompatActivity {
    //    DrawerLayout drawerLayout;
    ImageButton btnSleep;
    ListView listView;
    TextView textToday;
    TextView textAllTime;
    SleepRecordAdapter sleepRecordAdapter;
    boolean sleep = false;
    String currentDate;
    String sleepTime;
    String strStartTime;
    String strEndTime;
    long startTime;
    long endTime;
    //    String myJSON;
//    private static final String TAG_RESULT = "result";
//    private static final String TAG_DATE = "date";
//    private static final String TAG_STARTTIME = "startTime";
//    private static final String TAG_ENDTIME = "endTime";
//    private static final String TAG_SLEEPTIME = "sleepTime";
//    JSONArray info = null;
    long now;
    Date date;
    SimpleDateFormat format;
    ArrayList<String> sleepList = new ArrayList<String>();
    //    String phpConnectUrl = "http://192.168.0.78/PHP_connection.php";
//    String phpInsertUrl = "http://192.168.0.78/PHP_insert.php";
    MobileServiceClient mobileServiceClient;
    MobileServiceTable<SleepRecord> mobileServiceTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#F48FB1"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.sleeprecordToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            VectorDrawableCompat compat = VectorDrawableCompat.create(getResources(), R.drawable.ic_arrow_back_black_24dp, getTheme()); // 이미지 벡터
            compat.setTint(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, getTheme())); // 벡터 색깔
            actionBar.setHomeAsUpIndicator(compat);
        }

//        drawerLayout = (DrawerLayout) findViewById(R.id.sleeprecordDrawer);
        textToday = (TextView) findViewById(R.id.textToday);
        textAllTime = (TextView) findViewById(R.id.textAllTime);
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
//        getData(phpConnectUrl); // php서버 웹주소

        AzureServiceAdapter.Initialize(this);
        mobileServiceClient = AzureServiceAdapter.getInstance().getClient();
        mobileServiceTable = mobileServiceClient.getTable(SleepRecord.class);
        connectAzure();
    }

    public void connectAzure() {
        class GetData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) { // 웹서버에 요청
                try {
                    sleepRecordAdapter = new SleepRecordAdapter();
                    knowToday("yyyy/MM/dd");
                    currentDate = format.format(date);
                    sleepList.clear();
                    List<SleepRecord> list = mobileServiceTable.execute().get();

                    for (int i = 0; i < list.size(); i++) {
                        String date = list.get(i).getDate();
                        String startTime = list.get(i).getStartTime();
                        String endTime = list.get(i).getEndTime();
                        String sleepTime = list.get(i).getSleepTime();

                        if (date.equals(currentDate)) { // 오늘 날짜만 보여줌
                            sleepRecordAdapter.addItem(startTime, endTime, sleepTime); // 어댑터에 데이터 추가
                            sleepList.add(sleepTime); // 오늘 수면시간 추가
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (MobileServiceException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) { // 웹서버에서 post 받으면 실행
                showAll();
            }
        }
        GetData getData = new GetData();
        getData.execute();
    }

    public void showAll() {
        textToday.setText(currentDate);
        textAllTime.setText(allSleepTime(sleepList));
        listView.setAdapter(sleepRecordAdapter); // 리스트뷰 업데이트
        sleepRecordAdapter.notifyDataSetChanged();
    }

    public class InsertAzure extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String date = (String) params[0]; // (String) 해줘야함
            String startTime = (String) params[1];
            String endTime = (String) params[2];
            String sleepTime = (String) params[3];

            SleepRecord item = new SleepRecord();
            item.setDate(date);
            item.setStartTime(startTime);
            item.setEndTime(endTime);
            item.setSleepTime(sleepTime);

            try {
                mobileServiceTable.insert(item).get(); // 데이터베이스에 삽입
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

//    public void getData(String url) {
//        class GetDataJSON extends AsyncTask<String, Void, String> {
//
//            @Override
//            protected String doInBackground(String... params) { // 웹서버에 요청
//
//                String uri = params[0];
//                BufferedReader bufferedReader = null;
//
//                try {
//                    URL url = new URL(uri);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//                    StringBuilder stringBuilder = new StringBuilder();
//                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//                    String json;
//                    while ((json = bufferedReader.readLine()) != null) { // json 값을 모두 가져옴
//                        stringBuilder.append(json + "\n");
//                    }
//                    return stringBuilder.toString().trim();
//
//                } catch (Exception e) {
//                    return null;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String result) { // 웹서버에서 post 받으면 실행
//                myJSON = result;
//                showList();
//            }
//        }
//        GetDataJSON getDataJSON = new GetDataJSON();
//        getDataJSON.execute(url);
//    }

//    public void showList() { // 리스트뷰에 나타내기
//        try {
//            JSONObject jsonObject = new JSONObject(myJSON); // json형식 객체
//            info = jsonObject.getJSONArray(TAG_RESULT); // json배열
//            sleepRecordAdapter = new SleepRecordAdapter();
//            knowToday("yyyy/MM/dd");
//            currentDate = format.format(date);
//            sleepList.clear();
//
//            for (int i = 0; i < info.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
//                JSONObject object = info.getJSONObject(i);
//                String date = object.getString(TAG_DATE);
//                String startTime = object.getString(TAG_STARTTIME);
//                String endTime = object.getString(TAG_ENDTIME);
//                String sleepTime = object.getString(TAG_SLEEPTIME);
//
//                if (date.equals(currentDate)) { // 오늘 날짜만 보여줌
//                    sleepRecordAdapter.addItem(startTime, endTime, sleepTime); // 어댑터에 데이터 추가
//                    sleepList.add(sleepTime); // 오늘 수면시간 추가
//                }
//            }
//
//            textToday.setText(currentDate);
//            textAllTime.setText(allSleepTime(sleepList));
//            listView.setAdapter(sleepRecordAdapter); // 리스트뷰 업데이트
//            sleepRecordAdapter.notifyDataSetChanged();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("php error", e.getMessage());
//        }
//    }

    public String allSleepTime(ArrayList<String> sleepList) { // 수면 총시간 구하기
        int totalHour = 0;
        int totalMinute = 0;
        int totalSecond = 0;

        for (int i = 0; i < sleepList.size(); i++) {
            String sleepTime = sleepList.get(i);
            String sleepTimeArray[] = sleepTime.split(":");

            int hour = Integer.parseInt(sleepTimeArray[0]);
            int minute = Integer.parseInt(sleepTimeArray[1]);
            int second = Integer.parseInt(sleepTimeArray[2]);

            totalHour = totalHour + hour;
            totalMinute = totalMinute + minute;
            totalSecond = totalSecond + second;
        }

        while (totalSecond >= 60 || totalMinute >= 60) {
            if (totalSecond >= 60) {
                totalMinute = totalMinute + 1;
                totalSecond = totalSecond - 60;
            } else if (totalMinute >= 60) {
                totalHour = totalHour + 1;
                totalMinute = totalMinute - 60;
                break;
            }
        }

        String strHour = String.valueOf(totalHour);
        String strMinute = String.valueOf(totalMinute);
        String strSecond = String.valueOf(totalSecond);

        if (strHour.length() == 1) {
            strHour = "0" + strHour;
        }
        if (strMinute.length() == 1) {
            strMinute = "0" + strMinute;
        }
        if (strSecond.length() == 1) {
            strSecond = "0" + strSecond;
        }

        return strHour + ":" + strMinute + ":" + strSecond;
    }

    public void knowToday(String input) { // 오늘 날짜
        now = System.currentTimeMillis();
        date = new Date(now);
        format = new SimpleDateFormat(input);
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

                InsertAzure insertAzure = new InsertAzure();
                insertAzure.execute(currentDate, strStartTime, strEndTime, sleepTime); // azure에 데이터 삽입
                connectAzure(); // 갱신

//                InsertData task = new InsertData(); // insert쿼리 php실행
//                task.execute(currentDate, strStartTime, strEndTime, sleepTime);
//                getData(phpConnectUrl); // 업데이트
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

    public void getNowTime(String state) { // 오늘날짜 수면시간 기록
        if (state.equals("sleep")) {
            knowToday("yyyy/MM/dd");
            currentDate = format.format(date);
            startTime = System.currentTimeMillis();

            knowToday("HH:mm:ss");
            strStartTime = format.format(date);

            Singleton.getInstance().setDate(currentDate);
            Singleton.getInstance().setNow(startTime);
        } else if (state.equals("awake")) {
            knowToday("yyyy/MM/dd");
            currentDate = Singleton.getInstance().getDate();
            startTime = Singleton.getInstance().getNow();
            endTime = System.currentTimeMillis();

            knowToday("HH:mm:ss");
            strEndTime = format.format(date);

            long resultTime = endTime - startTime;
            sleepTime = longTostring(resultTime);
        }
    }

    public String longTostring(long resultTime) { // 밀리세컨드를 시간,분,초로 계산하여 스트링으로 변환
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

        return strHour + ":" + strMinute + ":" + strSecond;
    }

//    public class InsertData extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String date = (String) params[0]; // (String) 해줘야함
//            String startTime = (String) params[1];
//            String endTime = (String) params[2];
//            String sleepTime = (String) params[3];
//
//            String serverURL = phpInsertUrl; // 서버를 돌리는 pc의 ip주소
//            String postParameters = "date=" + date + "&startTime=" + startTime + "&endTime=" + endTime + "&sleepTime=" + sleepTime; // 구분하는 문자 &삽입
//
//            try {
//                URL url = new URL(serverURL);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setReadTimeout(5000);
//                httpURLConnection.setConnectTimeout(5000);
//                httpURLConnection.setRequestMethod("POST"); // 보안상 POST 방식 사용
//                httpURLConnection.setDoInput(true);
//                httpURLConnection.connect();
//
//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();
//
//                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e("php", "POST response code " + responseStatusCode);
//
//                InputStream inputStream;
//                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    inputStream = httpURLConnection.getInputStream();
//                } else {
//                    inputStream = httpURLConnection.getErrorStream();
//                }
//
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    sb.append(line);
//                }
//
//                bufferedReader.close();
//
//                return sb.toString();
//
//            } catch (Exception e) {
//                Log.e("php error4", String.valueOf(e));
//                return new String("Error: " + e.getMessage());
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            Log.e("php", "POST response " + result);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 드로어 이벤트
        int id = item.getItemId();

        if (id == android.R.id.home) {
//            drawerLayout.openDrawer(GravityCompat.START);
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}