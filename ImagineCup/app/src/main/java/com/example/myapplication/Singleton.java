package com.example.myapplication;

/**
 * Created by Yookmoonsu on 2018-01-15.
 */

public class Singleton {
    private boolean connect = false;
    private static Singleton instance = null;

    public boolean getConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}