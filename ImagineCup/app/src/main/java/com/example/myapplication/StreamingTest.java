//package com.example.myapplication;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//
//import com.example.mjpegview.MjpegView;
//
///**
// * Created by woga1 on 2017-12-30.
// */
//
//public class StreamingTest extends Activity {
//    //@BindView(R.id.mjpegView)
//    private MjpegView view;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_streaming);
//
//        view = (MjpegView) findViewById(R.id.mjpegView);
//
//        view.setAdjustHeight(true);
//        //view.setAdjustWidth(true);
//        view.setMode(MjpegView.MODE_FIT_WIDTH);
//        view.setMsecWaitAfterReadImageError(1000);
//        view.setUrl("http://192.168.0.175:8083/stream.html");
//        view.setRecycleBitmap(true);
//    }
//
//    @Override
//    protected void onResume() {
//        view.startStream();
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        view.stopStream();
//        super.onPause();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return false;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onStop() {
//        view.stopStream();
//        super.onStop();
//    }
//}