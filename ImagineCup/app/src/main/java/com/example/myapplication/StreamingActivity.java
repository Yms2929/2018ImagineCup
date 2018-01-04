//package com.example.myapplication;
//
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.preference.PreferenceManager;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.github.niqdev.mjpeg.DisplayMode;
//import com.github.niqdev.mjpeg.Mjpeg;
//import com.github.niqdev.mjpeg.MjpegView;
//
//import butterknife.BindView;
//
///**
// * Created by woga1 on 2017-12-28.
// */
//
//public class StreamingActivity extends AppCompatActivity {
//
//    @BindView(R.id.mjpegView)
//    MjpegView mjpegView;
//
//    String url = "http://192.168.0.175:8083/stream.html";
//    private static final int TIMEOUT = 5;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_streaming);
//        loadIpCam();
//    }
//
//    private String getPreference(String key) {
//        return PreferenceManager
//                .getDefaultSharedPreferences(this)
//                .getString(key, "");
//    }
//
//    private DisplayMode calculateDisplayMode() {
//        int orientation = getResources().getConfiguration().orientation;
//        return orientation == Configuration.ORIENTATION_LANDSCAPE ?
//                DisplayMode.FULLSCREEN : DisplayMode.BEST_FIT;
//    }
//
//    private void loadIpCam() {
//        Mjpeg.newInstance()
//                .open(getPreference(url), TIMEOUT)
//                .subscribe(
//                        inputStream -> {
//                            mjpegView.setSource(inputStream);
//                            mjpegView.setDisplayMode(calculateDisplayMode());
//                            mjpegView.showFps(true);
//                        },
//                        throwable -> {
//                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
//                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
//                        });
//    }
//
//}