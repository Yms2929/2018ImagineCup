package com.example.myapplication.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SleepRecordAdapter;

public class SleepRecordActivity extends AppCompatActivity {
    ToggleButton btnSleep;
    ListView listView;
    SleepRecordAdapter sleepRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_record);

        btnSleep = (ToggleButton) findViewById(R.id.btnSleep);
        btnSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSleep.isChecked()) {
                    btnSleep.setTextColor(Color.RED);
                }
                else {
                    btnSleep.setTextColor(Color.BLUE);
                }
            }
        });

        listView = (ListView) findViewById(R.id.listview);

        sleepRecordAdapter = new SleepRecordAdapter();
        sleepRecordAdapter.addItem("17/01/17","00:52:30");
        listView.setAdapter(sleepRecordAdapter);
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
}