package com.example.myapplication.data;

/**
 * Created by Yookmoonsu on 2018-01-17.
 */

public class SleepRecordListViewItem {
    private String date;
    private String startTime;
    private String endTime;
    private String sleepTime;

    public SleepRecordListViewItem() {

    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSleepTime() {
        return this.sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }
}