package com.example.myapplication.activity;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private static final String TAG_DATETODAY = "date";
    private static final String TAG_TEMPERATURE = "temperature";
    private static final String TAG_HUMIDITY = "humidity";
    JSONArray jsonArray = null;
    String json;
    ArrayList<String> timeList = new ArrayList<>();
    ArrayList<String> temperatureList = new ArrayList<>();
    ArrayList<String> humidityList = new ArrayList<>();
    String phpConnectUrl = "http://192.168.0.175/phptest.php";
    TextView textTemperature;
    TextView textTemperatureStatus;
    TextView textHumidity;
    TextView textHumidityStatus;
    TextView textCurrentStatus;

    public FirstGraphFragment() { // 생성자

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_first, container, false);
        lineChart = (LineChart) view.findViewById(R.id.templinegraph);
        textTemperature = view.findViewById(R.id.textTemperature);
        textTemperatureStatus = view.findViewById(R.id.textTemperatureStatus);
        textHumidity = view.findViewById(R.id.textHumidity);
        textHumidityStatus = view.findViewById(R.id.textHumidityStatus);
        textCurrentStatus = view.findViewById(R.id.textCurrentStatus);

        getData(phpConnectUrl);

        return view;
    }

    private void setlineData() {
        ArrayList<Entry> line1 = new ArrayList<>();
        ArrayList<Entry> line2 = new ArrayList<>();

        for (int i = 0; i < temperatureList.size(); i++) {
            float value1 = Float.parseFloat(temperatureList.get(i));
            float value2 = Float.parseFloat(humidityList.get(i));
            line1.add(new Entry(i, value1));
            line2.add(new Entry(i, value2));
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
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new LabelFormatter(times));
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
                String today = object.getString(TAG_DATETODAY);
                String temperature = object.getString(TAG_TEMPERATURE);
                String humidity = object.getString(TAG_HUMIDITY);

                String[] days = today.split(" "); // 날짜와 시간
                String day = days[0]; // 날짜
                String time = days[1].substring(0, 2); // 시간

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                String currentDate = format.format(date); // 오늘 날짜
                String textMessage = "";

                if (day.equals(currentDate)) { // 데이터베이스의 날짜와 오늘 날짜가 같으면
                    textTemperature.setText(temperature);
                    textHumidity.setText(humidity);

                    if (time.equals("00") || time.equals("03") || time.equals("06") || time.equals("09") || time.equals("12") || time.equals("15") || time.equals("18") || time.equals("21")) {
                        timeList.add(time);
                        temperatureList.add(temperature);
                        humidityList.add(humidity);
                    }
                    if (Float.parseFloat(temperature) < 20) {
                        textTemperature.setTextColor(Color.parseColor("#E53935"));
                        textTemperatureStatus.setTextColor(Color.parseColor("#E53935"));
                        textTemperatureStatus.setText("Low");
                        textMessage = "Raise the temperature and";
                    } else if (Float.parseFloat(temperature) >= 20 && Float.parseFloat(temperature) <= 23) {
                        textTemperature.setTextColor(Color.parseColor("#43A047"));
                        textTemperatureStatus.setTextColor(Color.parseColor("#43A047"));
                        textTemperatureStatus.setText("Good");
                        textMessage = "Keep the temperature and";
                    } else {
                        textTemperature.setTextColor(Color.parseColor("#E53935"));
                        textTemperatureStatus.setTextColor(Color.parseColor("#E53935"));
                        textTemperatureStatus.setText("High");
                        textMessage = "Lower the temperature and";
                    }
                    if (Float.parseFloat(humidity) < 50) {
                        textHumidity.setTextColor(Color.parseColor("#E53935"));
                        textHumidityStatus.setTextColor(Color.parseColor("#E53935"));
                        textHumidityStatus.setText("Low");
                        textMessage = textMessage + " " + "raise humidity";
                    } else if (Float.parseFloat(humidity) >= 50 && Float.parseFloat(humidity) <= 60) {
                        textHumidity.setTextColor(Color.parseColor("#43A047"));
                        textHumidityStatus.setTextColor(Color.parseColor("#43A047"));
                        textHumidityStatus.setText("Good");
                        textMessage = textMessage + " " + "keep humidity";
                    } else {
                        textHumidity.setTextColor(Color.parseColor("#E53935"));
                        textHumidityStatus.setTextColor(Color.parseColor("#E53935"));
                        textHumidityStatus.setText("High");
                        textMessage = textMessage + " " + "lower humidity";
                    }
                }
                textCurrentStatus.setText(textMessage + " " + "of the room");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("php error", e.getMessage());
        }
    }
}