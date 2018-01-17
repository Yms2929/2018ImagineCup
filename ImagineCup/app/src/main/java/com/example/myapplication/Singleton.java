package com.example.myapplication;

import android.content.Context;

/**
 * Created by Yookmoonsu on 2018-01-15.
 */

public class Singleton {
    private boolean connect = false;
    private static Singleton instance = null;
    private Context context;

    public boolean getConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}