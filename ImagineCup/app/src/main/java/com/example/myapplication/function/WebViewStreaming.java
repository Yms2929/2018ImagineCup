package com.example.myapplication.function;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;

import com.example.myapplication.R;

/**
 * Created by woga1 on 2017-12-30.
 */

public class WebViewStreaming extends Activity {
    int width;
    int height;
    String url = "http://192.168.0.175:8083/stream_simple.html";
    String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewstreaming);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#F48FB1"));
        }

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> "+
                "<img src=\"" + url  // Set Embedded url
                + "\" frameborder=\"no\" scrolling=\"no\"></>" +" </body> </html> ";
        WebView webView = (WebView) findViewById(R.id.webview);
//
//        webView.setWebViewClient(new WebViewClient());
//        webView.setBackgroundColor(255);
//        //영상을 폭에 꽉 차게 할려고 했지만 먹히지 않음???
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        //이건 최신 버전에서는 사용하지 않게됨
//        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
//
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        webView.loadUrl(url);
//      영상을 폭을 꽉 차게 하기 위해 직접 html태그로 작성함.
        //webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} div{overflow: hidden;} </style></head><body><div><src='" + url + "/></div></body></html>", "text/html", "UTF-8");

    }
}