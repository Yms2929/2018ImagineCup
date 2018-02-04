package com.example.myapplication.function;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.myapplication.R;

/**
 * Created by woga1 on 2018-02-04.
 */

public class RightBabySleepVideoView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);

        VideoView videoView = (VideoView)findViewById(R.id.videoView);

        //비디오뷰 커스텀할 수 있는 미디어 컨트롤러 객체생성
        MediaController mediaController = new MediaController(getApplicationContext());
        //비디오뷰에 연결
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse("android.resource://" + getPackageName()+"/raw/safesleep");

        //비디오뷰 컨트롤러 = 미디어컨트롤러
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);

        videoView.requestFocus();
        videoView.start();
    }
}
