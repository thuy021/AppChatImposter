package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class DangKyHoTenActivity extends AppCompatActivity {
    private Button btnTiepTuc;
    private ImageButton btnBack;
    private EditText etxtHoTen;

    private String SoDienThoai_DangKy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky_ho_ten);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnTiepTuc_Click();
        btnBack_Click();

        SoDienThoai_DangKy = getIntent().getStringExtra("SoDienThoai_DangKy");
    }

    protected void Init_Data(){
        btnTiepTuc = (Button) findViewById(R.id.btnTiepTuc_HoTen);
        btnBack = (ImageButton) findViewById(R.id.btnBack_DangKy_HoTen);
        etxtHoTen = (EditText) findViewById(R.id.editTextHoTen_DangKy);
    }

    protected void btnTiepTuc_Click(){
        btnTiepTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etxtHoTen.getText().toString().isEmpty()){
                    Toast.makeText(DangKyHoTenActivity.this, "Bạn Chưa Nhập Họ Tên", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(DangKyHoTenActivity.this, DangKyMatKhauActivity.class);
                intent.putExtra("HoTen_DangKy", etxtHoTen.getText().toString());
                intent.putExtra("SoDienThoai_DangKy", SoDienThoai_DangKy);

                startActivity(intent);
                finish();
            }
        });
    }

    protected void btnBack_Click(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}