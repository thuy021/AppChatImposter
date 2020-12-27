package com.example.appchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {
    ConstraintLayout layout_wait_load_splash_screen;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        if(!KiemTraKetNoiInternet()){
            layout_wait_load_splash_screen.setVisibility(View.GONE);
            HienThiThongBao("Có Lỗi Xảy Ra", "Vui Lòng Kiểm Tra Kết Nối Internet Của Bạn", SplashScreen.this);
        }else {
            timer = new CountDownTimer(500, 500) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            };

            //Check Người Dùng Có Đăng Nhập Chưa
            SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
            String token = preferences.getString("Token_DangNhap", "");
            if(!token.isEmpty()){
                Intent intent = new Intent(SplashScreen.this, TroChuyenActivity.class);
                startActivity(intent);
                finish();
            }else {
                timer.start();
            }
        }
    }

    protected void Init_Data(){
        layout_wait_load_splash_screen = (ConstraintLayout) findViewById(R.id.layout_wait_load_splash_screen);
    }

    protected Boolean KiemTraKetNoiInternet() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null) {
            return true;
        } else {
            return false;
        }
    }

    //Hiển Thị Thông Báo Alert
    protected void HienThiThongBao(String title, String msg, Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}