package com.example.myapplication.data;

/**
 * Created by TaegyunKim on 2018-02-11.
 */

public class PositionData {
    private String back;
    private String etc;
    private String front;

    public PositionData() {
    }

    public PositionData(String back, String etc, String front) {
        this.back = back;
        this.etc = etc;
        this.front = front;
    }

    public String getBack() {
        return back;
    }

    public String getEtc() {
        return etc;
    }

    public String getFront() {
        return front;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public void setFront(String front) {
        this.front = front;
    }
}