package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class TuyChonChatActivity extends AppCompatActivity {

    TextView tvNameSender;
    ImageButton btnBack_Chat_Don;

    String NameSender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuy_chon_chat);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();

        btnBack_Chat_Don.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Init_Data(){
        NameSender = getIntent().getStringExtra("Sender");
        tvNameSender = findViewById(R.id.tvTen_TuyChon_ChatDon);
        tvNameSender.setText(NameSender);

        btnBack_Chat_Don= findViewById(R.id.btnBack_tuy_chon_chat_don);
    }
}