package com.example.myapplication.function;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.myapplication.R;

/**
 * Created by woga1 on 2017-12-30.
 */

public class WebViewStreaming extends Activity {
    int width;
    int height;
    String url = "http://192.168.0.175:8083/javascript_simple.html";
    String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewstreaming);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#F48FB1"));
        }

        ImageView imageView = (ImageView) findViewById(R.id.streamView);
        imageView.setImageResource(R.drawable.streaming);

//        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//        width = dm.widthPixels;
//        height = dm.heightPixels;
//        html = "<!DOCTYPE html><html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} div{overflow: hidden;} </style></head><body><div>" +
//                "<img src='\"" + url + "\"/></div></body></html> ";
//        WebView webView = (WebView) findViewById(R.id.webview);
//        //webView.setWebViewClient(new WebViewClient());
//
//        webView.setVisibility(View.VISIBLE);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.setBackgroundColor(Color.BLACK);
//        //webView.loadDataWithBaseURL("",html, "text/html", "UTF-8", "");
//
//        webView.loadUrl(url);
////      영상을 폭을 꽉 차게 하기 위해 직접 html태그로 작성함.
//        //webView.loadData(html, "text/html", "UTF-8");
//        //webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} div{overflow: hidden;} </style></head><body><div><img src='http://192.168.0.175:8083/javascript_simple.html'/></div></body></html>" ,"text/html",  "UTF-8");
    }
}