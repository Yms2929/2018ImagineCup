package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.data.SleepRecordListViewItem;

import java.util.ArrayList;

/**
 * Created by Yookmoonsu on 2018-01-17.
 */

public class SleepRecordAdapter extends BaseAdapter {
    private ArrayList<SleepRecordListViewItem> listViewItems = new ArrayList<>();

    public SleepRecordAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sleeprecordlistview_item, parent, false);
        }

        TextView textStart = (TextView) convertView.findViewById(R.id.textStart);
        TextView textEnd = (TextView) convertView.findViewById(R.id.textEnd);
        TextView textTime = (TextView) convertView.findViewById(R.id.textSleep);

        SleepRecordListViewItem listViewItem = listViewItems.get(position);
        textStart.setText(listViewItem.getStartTime());
        textEnd.setText(listViewItem.getEndTime());
        textTime.setText(listViewItem.getSleepTime());

        return convertView;
    }

    public void addItem(String startTime, String endTime, String sleepTime) {
        SleepRecordListViewItem item = new SleepRecordListViewItem();

        item.setStartTime(startTime);
        item.setEndTime(endTime);
        item.setSleepTime(sleepTime);

        listViewItems.add(item);
    }
}