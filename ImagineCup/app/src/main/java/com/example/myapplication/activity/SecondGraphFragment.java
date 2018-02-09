package com.example.myapplication.activity;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AzureServiceAdapter;
import com.example.myapplication.data.SleepRecord;
import com.example.myapplication.etc.LabelFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by woga1 on 2018-01-22.
 */

public class SecondGraphFragment  extends Fragment {
    View view;
    BarChart barChart;
    XAxis xAxis;
    ArrayList<BarEntry> barEntries;
    String[] days = new String[]{}; //xAsix 밑에 들어갈 string 배열
    //    private static final String TAG_RESULTCODE = "result";
//    private static final String TAG_DATEDAY = "date";
//    private static final String TAG_ALLSLEEPTIME = "sleepTime";
//    String json;
//    JSONArray jsonArray;
    ArrayList<String> timeList = new ArrayList<>();
    ArrayList<String> previous1day = new ArrayList<>();
    ArrayList<String> previous2day = new ArrayList<>();
    ArrayList<String> previous3day = new ArrayList<>();
    ArrayList<String> previous4day = new ArrayList<>();
    ArrayList<String> previous5day = new ArrayList<>();
    ArrayList<String> previous6day = new ArrayList<>();
    ArrayList<Integer> weekSleepTime = new ArrayList<>();
    //    String phpConnectUrl = "http://192.168.0.78/PHP_connection.php";
    TextView textSleepAverage;
    TextView textSleepStatus;
    MobileServiceTable<SleepRecord> mobileServiceTable;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_second, container, false);
        barChart = (BarChart) view.findViewById(R.id.sleepbargraph);

        textSleepAverage = view.findViewById(R.id.textSleepAverage);
        textSleepStatus = view.findViewById(R.id.textSleepStatus);

        AzureServiceAdapter.Initialize(getActivity());
        MobileServiceClient mobileServiceClient = AzureServiceAdapter.getInstance().getClient();
        mobileServiceTable = mobileServiceClient.getTable(SleepRecord.class);
        connectAzure();

//        getData(phpConnectUrl);

        return view;
    }

    public void setBarData() {
        //막대바 값
        barEntries = new ArrayList<>();

        String day = doDayOfWeek();
        if (day.equals("Sun")) {
            days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        } else if (day.equals("Mon")) {
            days = new String[]{"Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"};
        } else if (day.equals("Tue")) {
            days = new String[]{"Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue"};
        } else if (day.equals("Wed")) {
            days = new String[]{"Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed"};
        } else if (day.equals("Thu")) {
            days = new String[]{"Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu"};
        } else if (day.equals("Fri")) {
            days = new String[]{"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
        } else if (day.equals("Sat")) {
            days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        }

        float xWidth = 0f;
        for (int i = 0; i < weekSleepTime.size(); i++) {
            barEntries.add(new BarEntry(xWidth, weekSleepTime.get(i))); // 그래프에 데이터 삽입
            xWidth = xWidth + 1f;
        }

        double sleepAverage = 0.0;
        int count = 0;

        for (int j = 0; j < weekSleepTime.size(); j++) {
            sleepAverage = sleepAverage + weekSleepTime.get(j); // 평균시간 구하기

            if (sleepAverage > 0) {
                count++;
            }
        }

        sleepAverage = sleepAverage / count;
        sleepAverage = Math.round(sleepAverage * 100d) / 100d;
        textSleepAverage.setText(String.valueOf(sleepAverage));

        if (sleepAverage < 16) {
            textSleepAverage.setTextColor(Color.parseColor("#E53935")); // 시간에 따른 텍스트 색상 변화
            textSleepStatus.setText(R.string.lessSleep);
        } else if (sleepAverage >= 16 && sleepAverage < 20) {
            textSleepAverage.setTextColor(Color.parseColor("#43A047"));
            textSleepStatus.setText(R.string.keepSleep);
        } else {
            textSleepAverage.setTextColor(Color.parseColor("#E53935"));
            textSleepStatus.setText(R.string.manySleep);
        }

//        barEntries.add(new BarEntry(0f, 17f));
//        barEntries.add(new BarEntry(1f, 16f));
//        barEntries.add(new BarEntry(2f, 18f));
//        barEntries.add(new BarEntry(3f, 16f));
//        barEntries.add(new BarEntry(4f, 17f));
//        barEntries.add(new BarEntry(5f, 18f));
//        barEntries.add(new BarEntry(6f, 16f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Sleep Time(hour)"); //막대값 배열에 추가한 거 정립
        barDataSet.setColor(android.graphics.Color.argb(255, 195, 90, 157)); //바 색깔 정하기

        BarData data = new BarData(barDataSet); //막대값 정한거 막대 그래프에 적용하기 위한 데이터 확정
        data.setBarWidth(0.5f); //막대 바 두께 정하기

        YAxis yLeft = barChart.getAxisLeft();
        YAxis yRight = barChart.getAxisRight();
        yLeft.setLabelCount(6);
        yLeft.setAxisMaxValue(24);
        yLeft.setAxisMinValue(0);
        yRight.setLabelCount(6);
        yRight.setAxisMaxValue(24);
        yRight.setAxisMinValue(0);
        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new LabelFormatter(days)); // 요일 삽입
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setScaleEnabled(false);
        barChart.setFocusableInTouchMode(false);
        Description des = new Description();
        des.setText(" ");
        barChart.setDescription(des);
        barChart.setData(data);
        barChart.invalidate(); //refresh
    }

    public void connectAzure() {
        class GetData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) { // 웹서버에 요청
                try {
                    List<SleepRecord> list = mobileServiceTable.execute().get();

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    String currentDate = format.format(date);

                    for (int i = 0; i < list.size(); i++) { // 데이터베이스 컬럼값 모두 가져옴
                        String dateDay = list.get(i).getDate();
                        String sleepTime = list.get(i).getSleepTime();

                        if (dateDay.equals(currentDate)) { // 오늘 날짜와 데이터베이스의 날짜가 같으면
                            timeList.add(sleepTime);
                        } else if (dateDay.equals(getPreviousDay(1))) { // 전날
                            previous1day.add(sleepTime);
                        } else if (dateDay.equals(getPreviousDay(2))) { // 2일전
                            previous2day.add(sleepTime);
                        } else if (dateDay.equals(getPreviousDay(3))) {
                            previous3day.add(sleepTime);
                        } else if (dateDay.equals(getPreviousDay(4))) {
                            previous4day.add(sleepTime);
                        } else if (dateDay.equals(getPreviousDay(5))) {
                            previous5day.add(sleepTime);
                        } else if (dateDay.equals(getPreviousDay(6))) {
                            previous6day.add(sleepTime);
                        }
                    }

                    weekSleepTime.add(allSleepTime(previous6day));
                    weekSleepTime.add(allSleepTime(previous5day));
                    weekSleepTime.add(allSleepTime(previous4day));
                    weekSleepTime.add(allSleepTime(previous3day));
                    weekSleepTime.add(allSleepTime(previous2day));
                    weekSleepTime.add(allSleepTime(previous1day));
                    weekSleepTime.add(allSleepTime(timeList)); // 총 수면 시간 구하기

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
                setBarData();
            }
        }
        GetData getData = new GetData();
        getData.execute();
    }

//    public void getData(String url) {
//        class GetDataJSON extends AsyncTask<String, Void, String> {
//
//            @Override
//            protected String doInBackground(String... params) { // 웹서버에 요청
//                Log.e("phperror", "doinback");
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
//                json = result;
//                showList();
//                setBarData();
//            }
//        }
//        GetDataJSON getDataJSON = new GetDataJSON();
//        getDataJSON.execute(url);
//    }

//    public void showList() { // Json 형식으로 된 데이터 배열로 가져와서 스트링으로 사용
//        try {
//            JSONObject jsonObject = new JSONObject(json); // json형식 객체
//            jsonArray = jsonObject.getJSONArray(TAG_RESULTCODE); // json배열
//
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//            String currentDate = format.format(date);
//
//            for (int i = 0; i < jsonArray.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
//                JSONObject object = jsonArray.getJSONObject(i);
//                String dateDay = object.getString(TAG_DATEDAY);
//                String sleepTime = object.getString(TAG_ALLSLEEPTIME);
//
//                if (dateDay.equals(currentDate)) { // 오늘 날짜와 데이터베이스의 날짜가 같으면
//                    timeList.add(sleepTime);
//                } else if (dateDay.equals(getPreviousDay(1))) { // 전날
//                    previous1day.add(sleepTime);
//                } else if (dateDay.equals(getPreviousDay(2))) { // 2일전
//                    previous2day.add(sleepTime);
//                } else if (dateDay.equals(getPreviousDay(3))) {
//                    previous3day.add(sleepTime);
//                } else if (dateDay.equals(getPreviousDay(4))) {
//                    previous4day.add(sleepTime);
//                } else if (dateDay.equals(getPreviousDay(5))) {
//                    previous5day.add(sleepTime);
//                } else if (dateDay.equals(getPreviousDay(6))) {
//                    previous6day.add(sleepTime);
//                }
//            }
//
//            weekSleepTime.add(allSleepTime(previous6day));
//            weekSleepTime.add(allSleepTime(previous5day));
//            weekSleepTime.add(allSleepTime(previous4day));
//            weekSleepTime.add(allSleepTime(previous3day));
//            weekSleepTime.add(allSleepTime(previous2day));
//            weekSleepTime.add(allSleepTime(previous1day));
//            weekSleepTime.add(allSleepTime(timeList)); // 총 수면 시간 구하기
//
//            for(int j=0; j<weekSleepTime.size(); j++) {
//                Log.e("phperror", String.valueOf(weekSleepTime.get(j)));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("phperror", e.getMessage());
//        }
//    }

    public String getPreviousDay(int number) { // 이전 날짜 구하기
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -number);  // 오늘 날짜에서 하루를 뺌.
        String day = sdf.format(calendar.getTime());

        return day;
    }

    public String doDayOfWeek() { // 오늘 요일 구하기
        Calendar calendar = Calendar.getInstance();
        String day = "";
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        switch (week) {
            case 1:
                day = "Sun";
                break;
            case 2:
                day = "Mon";
                break;
            case 3:
                day = "Tus";
                break;
            case 4:
                day = "Wed";
                break;
            case 5:
                day = "Thu";
                break;
            case 6:
                day = "Fri";
                break;
            case 7:
                day = "Sat";
                break;
        }

        return day;
    }

    public int allSleepTime(ArrayList<String> sleepList) { // 수면 총시간 구하기
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
            }
        }

        return totalHour;
    }
}