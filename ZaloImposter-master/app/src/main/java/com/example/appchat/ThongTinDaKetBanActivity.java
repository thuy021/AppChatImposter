package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.appchat.Models.NguoiDung;

public class ThongTinDaKetBanActivity extends AppCompatActivity {

    NguoiDung nguoi_dung;
    ImageButton btnBack_ThemBan;
    TextView txtHoTen_TaiKhoan, txtContent_GioiTinh_TaiKhoan, txtContent_NgaySinh_TaiKhoan, txtContent_SoDienThoai_TaiKhoan;
    Button btnNhanTin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_da_ket_ban);

        Init_Data();
        Load_Data();
        Back();
    }

    protected void Init_Data(){
        txtHoTen_TaiKhoan = (TextView) findViewById(R.id.txtHoTen_TaiKhoan);
        txtContent_GioiTinh_TaiKhoan = (TextView) findViewById(R.id.txtContent_GioiTinh_TaiKhoan);
        txtContent_NgaySinh_TaiKhoan = (TextView) findViewById(R.id.txtContent_NgaySinh_TaiKhoan);
        txtContent_SoDienThoai_TaiKhoan = (TextView) findViewById(R.id.txtContent_SoDienThoai_TaiKhoan);
        btnNhanTin = (Button) findViewById(R.id.btnNhanTin);
        btnBack_ThemBan = (ImageButton) findViewById(R.id.btnBack_ThemBan);
    }

    protected void Load_Data(){
        Bundle bundle = getIntent().getBundleExtra("Infor");
        nguoi_dung = (NguoiDung) bundle.getSerializable("ThongTinNguoiDung");

        if (nguoi_dung.getHoTen() != null) {
            txtHoTen_TaiKhoan.setText(nguoi_dung.getHoTen());
        }

        if (nguoi_dung.getGioiTinh() != null) {
            if (nguoi_dung.getGioiTinh()) {
                txtContent_GioiTinh_TaiKhoan.setText("Nam");
            } else {
                txtContent_GioiTinh_TaiKhoan.setText("Nữ");
            }
        }

        if (nguoi_dung.getNgaySinh() != null) {
            String ngaySinh = nguoi_dung.getNgaySinh();
            ngaySinh = ngaySinh.substring(0, 10);

            String thang = ngaySinh.substring(5, 7);
            String ngay = ngaySinh.substring(8, 10);
            String nam = ngaySinh.substring(0, 4);

            ngaySinh = ngay + "-" + thang + "-" + nam;

            nguoi_dung.setNgaySinh(thang + "-" + ngay + "-" + nam);
            txtContent_NgaySinh_TaiKhoan.setText(ngaySinh);
        }

        if (nguoi_dung.getSoDienThoai() != null) {
            txtContent_SoDienThoai_TaiKhoan.setText(nguoi_dung.getSoDienThoai());
        }
    }

    protected void Back(){
        btnBack_ThemBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}