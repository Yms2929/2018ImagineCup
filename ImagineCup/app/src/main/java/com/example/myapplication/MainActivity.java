package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnClient = (Button) findViewById(R.id.btnClient);
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SocketClient.class));
            }
        });

//        Button btn = (Button) findViewById(R.id.btn);
//        btn.setOnClickListener(new Button.OnClickListener(){
//
//                                   @Override
//                                   public void onClick(View view) {
//                                       Intent intent = new Intent(getApplicationContext(), StreamingTest.class);
//                                       startActivity(intent);
//                                   }
//                               }
//        );

//        Button webViewStreamBtn = (Button) findViewById(R.id.btn2);
//        webViewStreamBtn.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), WebViewStreaming.class);
//                startActivity(intent);
//            }
//        });
    }
}