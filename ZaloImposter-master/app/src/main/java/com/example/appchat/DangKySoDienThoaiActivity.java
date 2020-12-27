package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangKySoDienThoaiActivity extends AppCompatActivity {
    final String Regex_CheckSoDienThoai = "^0[1-9][0-9]{8}$";
    FirebaseAuth auth;

    private EditText etxtSoDienThoai_DangKy;
    protected Button btnTiepTuc_SoDienThoai;
    protected ImageButton btnBack_DangKy_SDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky_so_dien_thoai);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        auth = FirebaseAuth.getInstance();

        Init_Data();
        btnTiepTuc_SoDienThoai_Click();
        btnBack_DangKy_SDT_Click();
    }

    protected void Init_Data() {
        btnTiepTuc_SoDienThoai = (Button) findViewById(R.id.btnTiepTuc_SoDienThoai);
        etxtSoDienThoai_DangKy = (EditText) findViewById(R.id.etxtSoDienThoai_DangKy);
        btnBack_DangKy_SDT = (ImageButton) findViewById(R.id.btnBack_DangKy_SDT);
    }

    protected void btnTiepTuc_SoDienThoai_Click() {
        btnTiepTuc_SoDienThoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SDT = etxtSoDienThoai_DangKy.getText().toString();
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
                                    Intent intent = new Intent(DangKySoDienThoaiActivity.this, NhapMaOTPActivity.class);
                                    intent.putExtra("Mode_OTP", 2);
                                    intent.putExtra("SoDienThoai_DangKy", final_sdt);
                                    startActivity(intent);
                                    finish();
                                }
                            }else {
                                Toast.makeText(DangKySoDienThoaiActivity.this,"Số Điện Thoại Đã Được Sử Dụng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Message> call, Throwable t) {
                            Toast.makeText(DangKySoDienThoaiActivity.this,"Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(DangKySoDienThoaiActivity.this,"Số Điện Thoại Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void btnBack_DangKy_SDT_Click(){
        btnBack_DangKy_SDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}