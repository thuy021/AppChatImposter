package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    private Button btnDangNhap,btnDangKy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnDangNhap_Click();
        btnDangKy_Click();
    }

    protected void Init_Data(){
        btnDangNhap = (Button)findViewById(R.id.btnDangNhap);
        btnDangKy = (Button)findViewById(R.id.btnDangKy);
    }

    protected void btnDangNhap_Click(){
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,DangNhapActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void btnDangKy_Click(){
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, DangKySoDienThoaiActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}