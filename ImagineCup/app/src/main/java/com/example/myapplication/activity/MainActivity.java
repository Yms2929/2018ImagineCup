package com.example.myapplication.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.RecyclerAdapter;
import com.example.myapplication.data.Item;
import com.example.myapplication.function.DataResultService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import pyxis.uzuki.live.rollingbanner.RollingBanner;
import pyxis.uzuki.live.rollingbanner.RollingViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private RollingBanner rollingBanner;
    DrawerLayout drawerLayout;
    Intent i; // 배너 클릭 시 이동을 위한 Intent
    final int ITEM_SIZE = 4; // 카드뷰 갯수
    private String[] txtRes = new String[]{"1", "2", "3"}; // 3개 이미지 배너
    public static int REQ_CODE_OVERLAY_PERMISSION = 5469;
    private static final int PICK_FROM_ALBUM = 1;
    TextView babyName, temp_navi, humidity_navi, babyBirth;
    SharedPreferences mPref;
    CircleImageView circleImageView;
    private Uri mImageCaptureUri;
    long now;
    Date date;
    String getCurrentTime;
    JSONArray jsonArray = null;
    String json;
    String phpConnectUrl = "http://192.168.0.175/phptest.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.parseColor("#F48FB1"));
        }
        startOverlayWindowService();

        rollingBanner = findViewById(R.id.banner);
        bannerAdapter adapterTrue = new bannerAdapter(new ArrayList<>(Arrays.asList(txtRes)));
        rollingBanner.setAdapter(adapterTrue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar); // 툴바
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 액션바 홈버튼 설정
        getSupportActionBar().setDisplayShowTitleEnabled(true); // 액션바 타이틀 설정

        ActionBar actionBar = getSupportActionBar(); // 액션바
        if (actionBar != null) {
            VectorDrawableCompat compat = VectorDrawableCompat.create(getResources(), R.drawable.ic_dehaze_black_24dp, getTheme()); // 이미지 벡터
            compat.setTint(ResourcesCompat.getColor(getResources(), R.color.md_white_1000, getTheme())); // 벡터 색깔
            actionBar.setHomeAsUpIndicator(compat);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawer); // 드로어

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);

        List<Item> items = new ArrayList<>();
        Item[] item = new Item[ITEM_SIZE];
        //이하 아이템 지정. 전역 변수 final int ITEM_SIZE 와 동일한 갯수 설정
        item[0] = new Item(R.drawable.livestreaming, "streaming");
        item[1] = new Item(R.drawable.graph2, "graph");
        item[2] = new Item(R.drawable.sleeprecord, "record");
        item[3] = new Item(R.drawable.safesleep, "safesleep");

        //Size add
        for (int i = 0; i < ITEM_SIZE; i++) {
            items.add(item[i]);
        }

//        recyclerView.addOnItemTouchListener();
        recyclerView.setAdapter(new RecyclerAdapter(getApplicationContext(), items, R.layout.activity_main));

//        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "connect"));
        startService(new Intent(getApplicationContext(), DataResultService.class));

        //내비바
        babyName = (TextView) findViewById(R.id.babyname);
        temp_navi = (TextView) findViewById(R.id.navtextTemperature);
        humidity_navi = (TextView) findViewById(R.id.navtextHumidity);
        babyBirth = (TextView) findViewById(R.id.birthNum);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        //현재생년월일
        now = System.currentTimeMillis();
        date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        getCurrentTime = simpleDateFormat.format(date);
        //프로필이미지
        circleImageView = (CircleImageView) findViewById(R.id.circleProfilImageView);
        circleImageView.setOnClickListener(new clickListener());
    }

    class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {


            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    doTakeAlbumAction();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            };

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Select Upload Image")
                    .setPositiveButton("select img in Album", albumListener)
                    .setNegativeButton("cancel", cancelListener)
                    .show();
        } // end onClick

    } // end MyListener()

    public void doTakeAlbumAction() //앨범에서 이미지 가져오기
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void startOverlayWindowService() { // API 23 이상은 Overlay 사용 가능한지 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            onObtainingPermissionOverlayWindow();
        } else {
            System.out.print("버전이 낮거나 오버레이 설정창이 없습니다.");
        }
    }

    public void onObtainingPermissionOverlayWindow() { // 현재 패키지 명을 넘겨 설정화면을 노출
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_OVERLAY_PERMISSION) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "오버레이 권한 확인 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "오버레이 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == PICK_FROM_ALBUM) {
            mImageCaptureUri = data.getData();
            //circleImageView.setImageURI(mImageCaptureUri);
            try {
                circleImageView.setImageBitmap(decodeUri(getApplicationContext(), mImageCaptureUri, 1));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize) //이미지가 클 시 사이즈 줄이기
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 툴바 이벤트
        int id = item.getItemId();

        if (id == android.R.id.home) {
            getData(phpConnectUrl);
            drawerLayout.openDrawer(GravityCompat.START);
//            setNaviText();
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    protected void setNaviText() {
        temp_navi.setText("-17");
        humidity_navi.setText("10");
        babyBirth.setText(circulateBabyBirth() + " days");
        babyName.setText(mPref.getString("userBabyName", "Parkers"));
    }

    private String circulateBabyBirth() {
        String result = null;
        String babyBirth = mPref.getString("userBabyBirth", "not Setting");
        Pattern pattern = Pattern.compile("^[1-2]{1}[0-9]{1}(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))$");
        Matcher matcher = pattern.matcher(babyBirth);

        if (matcher.find()) {
            Calendar todaCal = Calendar.getInstance(); //오늘날자 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날자를 가져와 변경시킴
            //int birth = Integer.parseInt(babyBirth);
            String year = babyBirth.substring(0, 4);
            String month = babyBirth.substring(4, 6);
            String day = babyBirth.substring(6, 8);
            int myear = Integer.parseInt(year);
            int mmonth = Integer.parseInt(month);
            int mday = Integer.parseInt(day);
            //int currentTime = Integer.parseInt(getCurrentTime);
            //int resultInt = currentTime - birth;
            mmonth -= 1; // 받아온날자에서 -1을 해줘야함.
            ddayCal.set(myear, mmonth, mday);// D-day의 날짜를 입력

            long today = todaCal.getTimeInMillis() / 86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis() / 86400000;
            long count = today - dday; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.

            result = String.valueOf(count);
        } else {
            result = "check setting once again";
        }
        return result;
    }

    public class bannerAdapter extends RollingViewPagerAdapter<String> {

        public bannerAdapter(ArrayList<String> itemList) {
            super(itemList);
        }

        @Override
        public View getView(final int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main_pager, null, false);
            FrameLayout container = view.findViewById(R.id.container);

            String txt = getItem(position);
            final int index = getItemList().indexOf(txt);

            final int[] images = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3}; // 배너수정
            container.setBackgroundResource(images[index]);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (index == 0) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cdc.go.kr/CDC/mobile/CdcKrContentView.jsp?menuIds=HOME001-MNU1132-MNU2430-MNU2431-MNU2448&cid=67988"));// 액티비티 넘
                        startActivity(i);
                    } else if (index == 1) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.e-gen.or.kr/egen/main.do#"));
                        startActivity(i);
                    } else if (index == 2) {
                        i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.childcare.go.kr/"));
                        startActivity(i);
                    }
                }
            });

            return view;
        }
    }

    @Override
    public void onDestroy() { // 화면 종료될때
        super.onDestroy();
    }

    @Override
    protected void onRestart() { // 화면 재시작
        super.onRestart();

//        stopService(new Intent(getApplicationContext(), BackgroundService.class));
//        startService(new Intent(getApplicationContext(), BackgroundService.class).putExtra("message", "exit"));
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) { // 웹서버에 요청

                String uri = params[0];
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    StringBuilder stringBuilder = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) { // json 값을 모두 가져옴
                        stringBuilder.append(json + "\n");
                    }
                    return stringBuilder.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) { // 웹서버에서 post 받으면 실행
                json = result;
                showList();
            }
        }
        GetDataJSON getDataJSON = new GetDataJSON();
        getDataJSON.execute(url);
    }

    public void showList() { // Json 형식으로 된 데이터 배열로 가져와서 스트링으로 사용
        try {
            JSONObject jsonObject = new JSONObject(json); // json형식 객체
            jsonArray = jsonObject.getJSONArray("result"); // json배열

            for (int i = 0; i < jsonArray.length(); i++) { // 데이터베이스 컬럼값 모두 가져옴
                JSONObject object = jsonArray.getJSONObject(i);
                String today = object.getString("date");
                String temperature = object.getString("temperature");
                String humidity = object.getString("humidity");

                String[] days = today.split(" "); // 날짜와 시간
                String day = days[0]; // 날짜
                String time = days[1].substring(0, 2); // 시간

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                String currentDate = format.format(date); // 오늘 날짜
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                String currentTime = simpleDateFormat.format(date);

                if (day.equals(currentDate)) { // 데이터베이스의 날짜와 오늘 날짜가 같으면
                    if (time.equals(currentTime.substring(0, 2))) { // 데이터베이스의 시간과 현재 시간이 같으면
                        temp_navi.setText(temperature);
                        humidity_navi.setText(humidity);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("php error", e.getMessage());
        }
    }
}