package com.example.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by woga1 on 2017-12-30.
 */

public class WebViewStreaming extends Activity {

    int width;
    int height;
    String url ="http://192.168.0.175:8083/javascript_simple.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webviewstreaming);
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        width = dm.widthPixels;
        height = dm.heightPixels;

        WebView webView = (WebView)findViewById(R.id.webview);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Log.e("width", String.valueOf(width));
        Log.e("density", String.valueOf(dm.density));
        Double val = new Double(width)/new Double(dm.density);
        Log.e("val", String.valueOf(val));
        final Double finalVal = val * 5d;
        Log.e("finalVal", String.valueOf(finalVal));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("url", url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("page", "started " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("page", "finished " + url);

                view.loadUrl("javascript:var scale = " + finalVal+
                        " / document.body.scrollWidth; document.body.style.zoom = scale;");
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(url);
//        webView.setPadding(0,0,0,0);
//        //webView.setInitialScale(100);
//        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        webView.setInitialScale(getScale());
//
//
//        webView.loadUrl(url);
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
