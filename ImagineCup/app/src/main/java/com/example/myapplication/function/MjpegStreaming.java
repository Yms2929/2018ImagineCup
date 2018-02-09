package com.example.myapplication.function;

import android.app.Activity;
import android.os.Bundle;

import com.example.myapplication.R;
import com.longdo.mjpegviewer.MjpegView;

/**
 * Created by woga1 on 2018-02-02.
 */

public class MjpegStreaming extends Activity {

    MjpegView viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjpegstreaming);
        viewer = (MjpegView) findViewById(R.id.mjpegView);
        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
        viewer.setAdjustHeight(true);
        viewer.setUrl("http://192.168.0.175:8083/?action=stream");
        viewer.startStream();

//when user leaves application

//        int TIMEOUT = 5; //seconds
//
//        Mjpeg.newInstance()
//                .credential("USERNAME", "PASSWORD")
//                .open("http://192.168.0.175:8083//?action=stream", TIMEOUT)
//                .subscribe(inputStream -> {
//                    mjpegView.setSource(inputStream);
//                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
//                    mjpegView.showFps(true);
//                });

//when user leaves application
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        viewer.stopStream();
    }
}