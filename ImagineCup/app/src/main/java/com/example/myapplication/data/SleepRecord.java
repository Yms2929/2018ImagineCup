package com.example.myapplication.data;

/**
 * Created by Yookmoonsu on 2018-02-09.
 */

public class SleepRecord {
    public SleepRecord() {

    }

    @com.google.gson.annotations.SerializedName("id")
    public String mId;

    @com.google.gson.annotations.SerializedName("date")
    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @com.google.gson.annotations.SerializedName("startTime")
    public String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @com.google.gson.annotations.SerializedName("endTime")
    public String endTime;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @com.google.gson.annotations.SerializedName("sleepTime")
    public String sleepTime;

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }
}