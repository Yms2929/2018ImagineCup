package com.example.myapplication.etc;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by woga1 on 2018-01-23.
 */

public class LabelFormatter implements IAxisValueFormatter {
    private final String[] mLabels;

    public LabelFormatter(String[] labels) {
        mLabels = labels;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Log.e("LabelFormatter", mLabels[(int) value]);
        return mLabels[(int) value];
    }
}