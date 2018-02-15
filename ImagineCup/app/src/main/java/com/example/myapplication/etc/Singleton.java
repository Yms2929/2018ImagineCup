package com.example.myapplication.etc;

/**
 * Created by Yookmoonsu on 2018-01-15.
 */

public class Singleton {
    private static Singleton instance = null;
    private boolean sleep = false;
    private String date;
    private long time;
    private boolean streaming = false;

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public boolean getSleep() {
        return this.sleep;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getNow() {
        return this.time;
    }

    public void setNow(long time) {
        this.time = time;
    }

    public boolean getStreaming() {
        return this.streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }
}