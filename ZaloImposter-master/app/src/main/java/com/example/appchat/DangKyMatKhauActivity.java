package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangKyMatKhauActivity extends AppCompatActivity {
    String HoTen, SDT, MatKhau;

    Button btnNext;
    ImageButton btnBack;

    EditText editTextMatKhau_DangKy;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky_mat_khau);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnBack_Click();
        btnNext_Click();

        HoTen = getIntent().getExtras().getString("HoTen_DangKy");
        SDT = getIntent().getExtras().getString("SoDienThoai_DangKy");
    }

    protected void Init_Data() {
        btnBack = (ImageButton) findViewById(R.id.btnBack_DangKy_MatKhau);
        btnNext = (Button) findViewById(R.id.btnTiepTuc_MatKhau);
        editTextMatKhau_DangKy = (EditText) findViewById(R.id.editTextMatKhau_DangKy);
    }

    protected void btnBack_Click() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void btnNext_Click() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatKhau = editTextMatKhau_DangKy.getText().toString();

                if (MatKhau.length() < 6){
                    Toast.makeText(DangKyMatKhauActivity.this,"Mật Khẩu Phải Có 6 Kí Tự Trở Lên", Toast.LENGTH_SHORT).show();
                    return;
                }

                NguoiDung nguoiDung = new NguoiDung();

                nguoiDung.setHoTen(HoTen);
                nguoiDung.setSoDienThoai("0" + SDT.substring(3));
                nguoiDung.setPassword(MatKhau);

                DataClient dataClient = APIUtils.getData();
                Call<Message> call = dataClient.ThemNguoiDung(nguoiDung);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message = (Message) response.body();

                        if (message != null) {
                            if (message.getSuccess() == 1) {
                                //Lưu Thông Tin Đăng Nhập Của Người Dùng
                                preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("SoDienThoai", "0" + SDT.substring(3));
                                editor.putString("MatKhau", MatKhau);
                                editor.putString("Token_DangNhap", message.getToken());
                                editor.apply();

                                Intent intent = new Intent(DangKyMatKhauActivity.this, TroChuyenActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Log.e("Lỗi Server: ", t.getMessage());
                    }
                });
            }
        });
    }
}