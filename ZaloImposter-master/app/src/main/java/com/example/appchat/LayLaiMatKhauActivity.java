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

import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LayLaiMatKhauActivity extends AppCompatActivity {
    final String Regex_CheckSoDienThoai = "^0[1-9][0-9]{8}$";

    EditText etxtSoDienThoai_QuenMatKhau;
    Button btnTiepTuc_GetOTP;
    ImageButton btnBack_QuenMatKhau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lay_lai_mat_khau);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnTiepTuc_GetOTP_Click();
        btnBack_QuenMatKhau_Click();
    }

    protected void Init_Data(){
        btnTiepTuc_GetOTP = (Button) findViewById(R.id.btnTiepTuc_QuenMatKhau);
        btnBack_QuenMatKhau = (ImageButton) findViewById(R.id.btnBack_LayLaiMatKhau);
        etxtSoDienThoai_QuenMatKhau = (EditText) findViewById(R.id.etxtSoDienThoai_QuenMatKhau);
    }

    protected void btnTiepTuc_GetOTP_Click(){
        btnTiepTuc_GetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SDT = etxtSoDienThoai_QuenMatKhau.getText().toString();
                if(SDT.matches(Regex_CheckSoDienThoai)){
                    NguoiDung nguoiDung = new NguoiDung();
                    nguoiDung.setSoDienThoai(SDT);

                    SDT = SDT.substring(1);
                    String final_sdt = SDT;

                    DataClient client = APIUtils.getData();
                    Call<Message> call = client.CheckSoDienThoai(nguoiDung);

                    call.enqueue(new Callback<Message>() {
                        @Override
                        public void onResponse(Call<Message> call, Response<Message> response) {
                            Message message = response.body();

                            if(message != null){
                                if (message.getSuccess() == 1){
                                    Toast.makeText(LayLaiMatKhauActivity.this,"Số Điện Thoại Không Tồn Tại", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Intent intent = new Intent(LayLaiMatKhauActivity.this, NhapMaOTPActivity.class);
                                intent.putExtra("Mode_OTP", 1);
                                intent.putExtra("SoDienThoai_DangKy", final_sdt);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Toast.makeText(LayLaiMatKhauActivity.this,"Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(LayLaiMatKhauActivity.this, "Số Điện Thoại Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
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