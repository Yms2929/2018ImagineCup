package com.example.myapplication.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.etc.LabelFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

/**
 * Created by woga1 on 2018-01-22.
 */

public class SecondGraphFragment  extends Fragment {

    View view;
    BarChart barChart;
    XAxis xAxis;
    ArrayList<BarEntry> barEntries;
    //xAsix 밑에 들어갈 string 배열
    final String[] days = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_graph_second, container, false);
        barChart = (BarChart) view.findViewById(R.id.sleepbargraph);

        //막대바 값
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, 6f));
        barEntries.add(new BarEntry(1f, 12f));
        barEntries.add(new BarEntry(2f, 14f));
        barEntries.add(new BarEntry(3f, 9f));
        barEntries.add(new BarEntry(4f, 20f));
        barEntries.add(new BarEntry(5f, 2f));
        barEntries.add(new BarEntry(6f, 10f));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Sleep Time"); //막대값 배열에 추가한 거 정립
        barDataSet.setColor(android.graphics.Color.argb(255, 195, 90, 157)); //바 색깔 정하기


        BarData data = new BarData(barDataSet); //막대값 정한거 막대 그래프에 적용하기 위한 데이터 확정
        data.setBarWidth(0.5f); //막대 바 두께 정하기

        xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setValueFormatter(new LabelFormatter(days));
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); //refresh
        barChart.setData(data);
        barChart.setScaleEnabled(false);
        barChart.setFocusableInTouchMode(false);
        Description des = new Description();
        des.setText(" ");
        barChart.setDescription(des);

        return view;
    }
}


