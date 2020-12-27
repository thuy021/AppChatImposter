package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class DoiMatKhauActivity extends AppCompatActivity {
    final String Regex_CheckSoDienThoai = "^0[1-9][0-9]{8}$";
    String SDT;

    EditText etxtMatKhauHienTai_DoiMatKhau;
    EditText etxtMatKhauMoi_DoiMatKhau;
    EditText etxtXacNhanMatKhauMoi_DoiMatKhau;

    Button btnDoiMatKhau;
    ImageButton btnBack_DoiMatKhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SDT = getIntent().getStringExtra("SoDienThoai_NguoiDung");

        Init_Data();
        btnBack_DoiMatKhau_Click();
        btnDoiMatKhau_Click();
    }

    protected void Init_Data() {
        etxtMatKhauHienTai_DoiMatKhau = (EditText) findViewById(R.id.etxtMatKhauHienTai_DoiMatKhau);
        etxtMatKhauMoi_DoiMatKhau = (EditText) findViewById(R.id.etxtMatKhauMoi_DoiMatKhau);
        etxtXacNhanMatKhauMoi_DoiMatKhau = (EditText) findViewById(R.id.etxtXacNhanMatKhauMoi_DoiMatKhau);

        btnBack_DoiMatKhau = (ImageButton) findViewById(R.id.btnBack_DoiMatKhau);
        btnDoiMatKhau = (Button) findViewById(R.id.btnDoiMatKhau);
    }

    protected void btnDoiMatKhau_Click() {
        btnDoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kiểm Tra Mật Khẩu Hiện Tại Có Trùng Khớp Với Database
                SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
                String MatKhauHienTai_Memory = preferences.getString("MatKhau", "");
                String MatKhauHienTai_Input = etxtMatKhauHienTai_DoiMatKhau.getText().toString();
                String MatKhauMoi = etxtMatKhauMoi_DoiMatKhau.getText().toString().trim();
                String XacNhanMatKhauMoi = etxtXacNhanMatKhauMoi_DoiMatKhau.getText().toString().trim();

                if (MatKhauHienTai_Memory.compareTo(MatKhauHienTai_Input) != 0){
                    Toast.makeText(DoiMatKhauActivity.this, "Mật Khẩu Hiện Tại Không Chính Xác", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (MatKhauMoi.length() < 6){
                    Toast.makeText(DoiMatKhauActivity.this, "Mật Khẩu Phải Từ 6 Kí Tự Trở Lên", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (MatKhauMoi.compareTo(XacNhanMatKhauMoi) != 0){
                    Toast.makeText(DoiMatKhauActivity.this, "Xác Nhận Mật Khẩu Sai", Toast.LENGTH_SHORT).show();
                    return;
                }

                NguoiDung nguoiDung = new NguoiDung();
                nguoiDung.setSoDienThoai(SDT);
                nguoiDung.setPassword(XacNhanMatKhauMoi);

                DataClient client = APIUtils.getData();
                Call<Message> call = client.UpdatePassword(nguoiDung);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Message message = response.body();

                        if (response.isSuccessful()) {
                            if (message != null) {
                                if (response.body().getSuccess() == 1) {
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(DoiMatKhauActivity.this);
                                    builder.setMessage("Đổi Mật Khẩu Thành Công");
                                    builder.setPositiveButton("Đăng Nhập Lại", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(DoiMatKhauActivity.this, DangNhapActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }else {
                                Toast.makeText(DoiMatKhauActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(DoiMatKhauActivity.this,"Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    protected void btnBack_DoiMatKhau_Click() {
        btnBack_DoiMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoiMatKhauActivity.this);
                builder.setMessage("Bạn Có Chắc Muốn Huỷ ?");
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DoiMatKhauActivity.this, TroChuyenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}