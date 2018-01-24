package com.example.myapplication.activity;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by woga1 on 2018-01-22.
 */

public class FirstGraphFragment  extends Fragment {

    View view;
    XAxis xAxis;
    LineChart lineChart;
    final String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_graph_first, container, false);
        lineChart = (LineChart) view.findViewById(R.id.templinegraph);

        setlineData();
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new LabelFormatter(days));
        return view;
    }

    private void setlineData() {
        ArrayList<Entry> line1 = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            float val = (float) (Math.random() * 10) + 250;
            line1.add(new Entry(i, val));
        }
        ArrayList<Entry> line2 = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            float val = (float) (Math.random() * 10) + 250;
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
}