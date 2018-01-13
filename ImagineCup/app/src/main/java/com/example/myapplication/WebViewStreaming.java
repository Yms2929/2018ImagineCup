package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by woga1 on 2017-12-30.
 */

public class WebViewStreaming extends Activity {
    int width;
    int height;
    String url = "http://192.168.0.175:8083/javascript_simple.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewstreaming);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        width = dm.widthPixels;
        height = dm.heightPixels;

        WebView webView = (WebView) findViewById(R.id.webview);

//        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        Log.e("width", String.valueOf(width));
//        Log.e("density", String.valueOf(dm.density));
//        Double val = new Double(width)/new Double(dm.density);
//        Log.e("val", String.valueOf(val));
//        final Double finalVal = val * 5d;
//        Log.e("finalVal", String.valueOf(finalVal));
//
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d("url", url);
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                Log.d("page", "started " + url);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Log.d("page", "finished " + url);
//
//                view.loadUrl("javascript:var scale = " + finalVal+
//                        " / document.body.scrollWidth; document.body.style.zoom = scale;");
//            }
//        });
        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(255);
        //영상을 폭에 꽉 차게 할려고 했지만 먹히지 않음???
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //이건 최신 버전에서는 사용하지 않게됨
        //webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //영상을 폭을 꽉 차게 하기 위해 직접 html태그로 작성함.
        //webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} div{overflow: hidden;} </style></head><body><div><img src='" + url + "/></div></body></html>", "text/html", "UTF-8");

//        WebSettings webSettings = webView.getSettings();
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setJavaScriptEnabled(true);
//
//        webView.loadUrl(url);
//        webView.setPadding(0,0,0,0);
//        //webView.setInitialScale(100);
//        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        webView.setInitialScale(getScale());
//
        webView.loadUrl(url);
        //String imgSrcHtml = "<html><img src='" + url + "' /></html>";
        // String imgSrcHtml = url;
        //webView.loadData(imgSrcHtml, "text/html", "UTF-8");
    }
//    private int getScale(){
//        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();
//        Double val = new Double(width)/new Double(PIC_WIDTH);
//        val = val * 100d;
//        return val.intValue();
//    }
}