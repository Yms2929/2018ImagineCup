package com.example.myapplication.activity;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.etc.LabelFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by woga1 on 2018-01-22.
 */

public class FirstGraphFragment extends Fragment {
    View view;
    XAxis xAxis;
    LineChart lineChart;
    final String[] times = new String[]{"00", "03", "06", "09", "12", "15", "18", "21"};
    private static final String TAG_RESULTQUREY = "result";
    private static final String TAG_TODAY = "today";
    private static final String TAG_TEMPERATURE = "temperature";
    private static final String TAG_HUMIDITY = "humidity";
    JSONArray jsonArray = null;
    String json;
    ArrayList<String> todayList = new ArrayList<>();
    ArrayList<String> temperatureList = new ArrayList<>();
    ArrayList<String> humidityList = new ArrayList<>();
    String phpConnectUrl = "http://192.168.0.78/PHP_atmosphere.php";

    public FirstGraphFragment() { // 생성자

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_first, container, false);
        lineChart = (LineChart) view.findViewById(R.id.templinegraph);

        getData(phpConnectUrl);
//        setlineData();

        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new LabelFormatter(times));

        return view;
    }

    private void setlineData() {
        ArrayList<Entry> line1 = new ArrayList<>();
        for (int i = 0; i < temperatureList.size(); i++) {
            float val = Float.parseFloat(temperatureList.get(i));
            line1.add(new Entry(i, val));
        }
        ArrayList<Entry> line2 = new ArrayList<>();
        for (int i = 0; i < humidityList.size(); i++) {
            float val = Float.parseFloat(humidityList.get(i));
            line2.add(new Entry(i, val));
        }

        LineDataSet set1, set2;

        set1 = new LineDataSet(line1, "Temperature");
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.RED);
        set1.setLineWidth(2f);

        set2 = new LineDataSet(line2, "Humidity");
        set2.setColor(Color.BLUE);
        set2.setCircleColor(Color.BLUE);
        set2.setLineWidth(2f);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        LineData data = new LineData(dataSets);
        Description des = new Description();
        des.setText(" ");
        lineChart.setDescription(des);
        lineChart.setData(data);
        lineChart.setScaleEnabled(false);
        lineChart.invalidate(); // refresh
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
                json = result;
                showList();
                setlineData();
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }

    public void showList() { // Json 형식으로 된 데이터 배열로 가져와서 스트링으로 사용
        try {
            JSONObject jsonObject = new JSONObject(json); // json형식 객체
            jsonArray = jsonObject.getJSONArray(TAG_RESULTQUREY); // json배열

            for (int i = 0; i < jsonArray.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
                JSONObject object = jsonArray.getJSONObject(i);
                String today = object.getString(TAG_TODAY);
                String temperature = object.getString(TAG_TEMPERATURE);
                String humidity = object.getString(TAG_HUMIDITY);

                String[] day = today.split(" ");
                String date = day[0];
                today = day[1].substring(0, 2);

                todayList.add(today);
                temperatureList.add(temperature);
                humidityList.add(humidity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("php error", e.getMessage());
        }
    }
}