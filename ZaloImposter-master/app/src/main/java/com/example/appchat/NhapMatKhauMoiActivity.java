package com.example.appchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NhapMatKhauMoiActivity extends AppCompatActivity {
    String SDT;

    EditText etxtMatKhauMoi_QuenMatKhau;
    EditText etxtXacNhanMatKhau_QuenMatKhau;
    Button btnTiepTuc_MatKhau;

    ImageButton btnBack_QuenMatKhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhap_mat_khau_moi);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SDT = getIntent().getStringExtra("SoDienThoai_DangKy");

        Init_Data();
        btnTiepTuc_MatKhau_Click();
        btnBack_QuenMatKhau_Click();
    }

    protected void Init_Data(){
        etxtMatKhauMoi_QuenMatKhau = (EditText) findViewById(R.id.etxtMatKhauMoi_QuenMatKhau);
        etxtXacNhanMatKhau_QuenMatKhau = (EditText) findViewById(R.id.etxtXacNhanMatKhau_QuenMatKhau);
        btnTiepTuc_MatKhau = (Button) findViewById(R.id.btnTiepTuc_MatKhau);
        btnBack_QuenMatKhau = (ImageButton) findViewById(R.id.btnBack_QuenMatKhau);
    }

    protected void btnTiepTuc_MatKhau_Click(){
        btnTiepTuc_MatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MatKhauMoi = etxtMatKhauMoi_QuenMatKhau.getText().toString().trim();
                String MatKhau_XacNhan = etxtXacNhanMatKhau_QuenMatKhau.getText().toString().trim();

                if(MatKhauMoi.length() < 6 || MatKhau_XacNhan.length() < 6){
                    Toast.makeText(NhapMatKhauMoiActivity.this, "Mật Khẩu Phải Nhiều Hơn 6 Kí Tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (MatKhauMoi.compareTo(MatKhau_XacNhan) != 0){
                    Toast.makeText(NhapMatKhauMoiActivity.this, "Xác Nhận Mật Khẩu Sai", Toast.LENGTH_SHORT).show();
                    return;
                }

                NguoiDung nguoiDung = new NguoiDung();
                nguoiDung.setSoDienThoai(SDT);
                nguoiDung.setPassword(MatKhau_XacNhan);

                DataClient client = APIUtils.getData();
                Call<Message> call = client.UpdatePassword(nguoiDung);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message = response.body();

                        if (response.isSuccessful()) {
                            if (message != null) {
                                if (response.body().getSuccess() == 1) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(NhapMatKhauMoiActivity.this);
                                    builder.setMessage("Đổi Mật Khẩu Thành Công");
                                    builder.setPositiveButton("Đăng Nhập Lại", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(NhapMatKhauMoiActivity.this, DangNhapActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }else {
                                Toast.makeText(NhapMatKhauMoiActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(NhapMatKhauMoiActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected void btnBack_QuenMatKhau_Click(){
        btnBack_QuenMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}