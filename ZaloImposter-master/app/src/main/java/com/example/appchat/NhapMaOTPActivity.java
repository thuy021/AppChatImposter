package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class NhapMaOTPActivity extends AppCompatActivity {
    int mode_otp = 0;
    int Count_Send_Code = 1;
    FirebaseAuth auth;

    private String SoDienThoai_XacThuc;

    private String VerificationId;
    private PhoneAuthProvider.ForceResendingToken ResendToken;

    EditText etxtMaOTP_DangKy;
    Button btnTiepTuc_MaOTP_DangKy;
    Button btnResend_Code_DangKy;
    ImageButton btnBack_OTP;

    TextView txt_notify_sending_otp;
    TextView txt_notify_sent_otp;
    TextView txtSoDienThoai_XacThuc;
    TextView txt_suggest_nhap_ma_otp;
    TextView txt_suggest_timer_otp;

    ProgressBar prgbr_Waiting_OTP_DangKy;
    LinearLayout container_nhap_ma_xac_thuc;
    LinearLayout container_err_sms_sending;

    PhoneAuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhap_ma_o_t_p);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        auth = FirebaseAuth.getInstance();

        Init_Data();
        btnTiepTuc_MaOTP_DangKy_Click();
        btnBack_OTP_Click();
        btnResend_Code_DangKy_Click();

        mode_otp = getIntent().getExtras().getInt("Mode_OTP");
        SoDienThoai_XacThuc = "+84" + getIntent().getExtras().getString("SoDienThoai_DangKy");
        request_OTP(SoDienThoai_XacThuc);

        //Đang Xử Lý OTP Không Cho Back về Activity Trước
        btnBack_OTP.setEnabled(false);
    }

    protected void Init_Data() {
        etxtMaOTP_DangKy = (EditText) findViewById(R.id.etxtMaOTP_DangKy);
        btnTiepTuc_MaOTP_DangKy = (Button) findViewById(R.id.btnTiepTuc_MaOTP_DangKy);
        btnBack_OTP = (ImageButton) findViewById(R.id.btnBack_DangKy_OTP);
        btnResend_Code_DangKy = (Button) findViewById(R.id.btnResend_Code_DangKy);

        txtSoDienThoai_XacThuc = (TextView) findViewById(R.id.txtSoDienThoai_XacThuc);
        txtSoDienThoai_XacThuc.setText("0" + getIntent().getExtras().getString("SoDienThoai_DangKy"));

        txt_notify_sending_otp = (TextView) findViewById(R.id.txt_notify_sending_otp);
        txt_notify_sent_otp = (TextView) findViewById(R.id.txt_notify_sent_otp);
        txt_suggest_nhap_ma_otp = (TextView) findViewById(R.id.txt_suggest_nhap_ma_otp);
        txt_suggest_timer_otp = (TextView) findViewById(R.id.txt_suggest_timer_otp);

        container_nhap_ma_xac_thuc = (LinearLayout) findViewById(R.id.container_nhap_ma_xac_thuc);
        prgbr_Waiting_OTP_DangKy = (ProgressBar) findViewById(R.id.prgbr_Waiting_OTP_DangKy);
        container_err_sms_sending = (LinearLayout) findViewById(R.id.container_err_sms_sending);
    }

    protected void btnBack_OTP_Click() {
        btnBack_OTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void  btnResend_Code_DangKy_Click(){
        btnResend_Code_DangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Count_Send_Code == 2){
                    prgbr_Waiting_OTP_DangKy.setVisibility(View.VISIBLE);
                    txt_notify_sending_otp.setText("Đăng Ký Mới");
                    txt_notify_sending_otp.setVisibility(View.VISIBLE);
                    container_nhap_ma_xac_thuc.setVisibility(View.GONE);

                    Intent intent = new Intent(NhapMaOTPActivity.this, DangKySoDienThoaiActivity.class);
                    startActivity(intent);
                    finish();
                }

                container_nhap_ma_xac_thuc.setVisibility(View.GONE);
                txt_notify_sent_otp.setTextColor(Color.rgb(0, 0, 0));
                txt_notify_sent_otp.setText("Đã Gửi Mã Xác Nhận Đến Số");
                txtSoDienThoai_XacThuc.setTextColor(Color.rgb(0, 0, 0));
                txt_suggest_nhap_ma_otp.setText("Xin vui lòng kiểm tra SMS và điền mã xác nhận bên dưới");
                etxtMaOTP_DangKy.setVisibility(View.VISIBLE);
                container_err_sms_sending.setVisibility(View.GONE);
                btnTiepTuc_MaOTP_DangKy.setVisibility(View.VISIBLE);

                prgbr_Waiting_OTP_DangKy.setVisibility(View.VISIBLE);
                txt_notify_sending_otp.setText("Đang Gửi Lại Mã Xác Nhận Mới");
                txt_notify_sending_otp.setVisibility(View.VISIBLE);

                resend_sms_code_otp(SoDienThoai_XacThuc);

                //Đếm Số Lần Gửi Code Của Người Dùng
                Count_Send_Code = Count_Send_Code + 1;
            }
        });
    }

    protected void btnTiepTuc_MaOTP_DangKy_Click() {
        btnTiepTuc_MaOTP_DangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etxtMaOTP_DangKy.getText().toString();

                credential = PhoneAuthProvider.getCredential(VerificationId, code);
                SignInWithCredential(credential);

                prgbr_Waiting_OTP_DangKy.setVisibility(View.VISIBLE);
                txt_notify_sending_otp.setText("Đang Kiểm Tra");
                txt_notify_sending_otp.setVisibility(View.VISIBLE);

                container_nhap_ma_xac_thuc.setVisibility(View.GONE);
            }
        });
    }

    protected void resend_sms_code_otp(String phone){
        btnResend_Code_DangKy.setEnabled(false);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        if (phoneAuthCredential.getSmsCode() != null) {
                            etxtMaOTP_DangKy.setText(phoneAuthCredential.getSmsCode());
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        prgbr_Waiting_OTP_DangKy.setVisibility(View.GONE);
                        txt_notify_sending_otp.setVisibility(View.GONE);
                        container_nhap_ma_xac_thuc.setVisibility(View.VISIBLE);

                        //Thông Báo Lỗi
                        txt_notify_sent_otp.setTextColor(Color.rgb(255, 0, 0));
                        txt_notify_sent_otp.setText("Có Lỗi Xảy Ra Khi Gửi SMS Đến Số");
                        txtSoDienThoai_XacThuc.setTextColor(Color.rgb(255, 0, 0));
                        txt_suggest_nhap_ma_otp.setText("");
                        txt_suggest_timer_otp.setVisibility(View.VISIBLE);
                        etxtMaOTP_DangKy.setVisibility(View.GONE);
                        container_err_sms_sending.setVisibility(View.VISIBLE);
                        btnTiepTuc_MaOTP_DangKy.setVisibility(View.GONE);

                        CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                btnResend_Code_DangKy.setText(Long.toString(millisUntilFinished/1000) + " s");
                            }

                            @Override
                            public void onFinish() {
                                if(Count_Send_Code == 2){
                                    btnResend_Code_DangKy.setText("Thử Đăng Ký Lại Với Số Điện Thoại Khác");
                                    btnResend_Code_DangKy.setEnabled(true);
                                    txt_suggest_timer_otp.setVisibility(View.GONE);
                                    return;
                                }

                                btnResend_Code_DangKy.setText("Gửi Lại Mã Xác Nhận Mới");
                                btnResend_Code_DangKy.setEnabled(true);
                                txt_suggest_timer_otp.setVisibility(View.GONE);
                            }
                        }.start();

                        btnBack_OTP.setEnabled(true);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationId = s;
                        ResendToken = forceResendingToken;

                        prgbr_Waiting_OTP_DangKy.setVisibility(View.GONE);
                        txt_notify_sending_otp.setVisibility(View.GONE);
                        container_nhap_ma_xac_thuc.setVisibility(View.VISIBLE);
                        btnBack_OTP.setEnabled(true);
                    }
                }, ResendToken);
    }

    protected void request_OTP(String phone) {
        btnResend_Code_DangKy.setEnabled(false);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        if (phoneAuthCredential.getSmsCode() != null) {
                            etxtMaOTP_DangKy.setText(phoneAuthCredential.getSmsCode());
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        prgbr_Waiting_OTP_DangKy.setVisibility(View.GONE);
                        txt_notify_sending_otp.setVisibility(View.GONE);
                        container_nhap_ma_xac_thuc.setVisibility(View.VISIBLE);

                        //Thông Báo Lỗi
                        txt_notify_sent_otp.setTextColor(Color.rgb(255, 0, 0));
                        txt_notify_sent_otp.setText("Có Lỗi Xảy Ra Khi Gửi SMS Đến Số");
                        txtSoDienThoai_XacThuc.setTextColor(Color.rgb(255, 0, 0));
                        txt_suggest_nhap_ma_otp.setText("");
                        txt_suggest_timer_otp.setVisibility(View.VISIBLE);
                        etxtMaOTP_DangKy.setVisibility(View.GONE);
                        container_err_sms_sending.setVisibility(View.VISIBLE);
                        btnTiepTuc_MaOTP_DangKy.setVisibility(View.GONE);

                        CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                btnResend_Code_DangKy.setText(Long.toString(millisUntilFinished/1000) + " s");
                            }

                            @Override
                            public void onFinish() {
                                btnResend_Code_DangKy.setText("Gửi Lại Mã Xác Nhận Mới");
                                btnResend_Code_DangKy.setEnabled(true);
                                txt_suggest_timer_otp.setVisibility(View.GONE);
                            }
                        }.start();

                        btnBack_OTP.setEnabled(true);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerificationId = s;
                        ResendToken = forceResendingToken;

                        prgbr_Waiting_OTP_DangKy.setVisibility(View.GONE);
                        txt_notify_sending_otp.setVisibility(View.GONE);
                        container_nhap_ma_xac_thuc.setVisibility(View.VISIBLE);
                        btnBack_OTP.setEnabled(true);
                    }
                });
    }

    protected void SignInWithCredential(final PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Chuyển Sang Activity Đăng Ký Tài Khoản
                    if(mode_otp == 2){
                        Intent intent_02 = new Intent(NhapMaOTPActivity.this, DangKyHoTenActivity.class);
                        intent_02.putExtra("SoDienThoai_DangKy", SoDienThoai_XacThuc);

                        startActivity(intent_02);
                        finish();
                    }

                    //Chuyển Sang Activity Nhập Mật Khẩu Mới
                    if(mode_otp == 1){
                        Intent intent_02 = new Intent(NhapMaOTPActivity.this, NhapMatKhauMoiActivity.class);

                        SoDienThoai_XacThuc = SoDienThoai_XacThuc.substring(3);
                        SoDienThoai_XacThuc = "0" + SoDienThoai_XacThuc;

                        intent_02.putExtra("SoDienThoai_DangKy", SoDienThoai_XacThuc);
                        startActivity(intent_02);
                        finish();
                    }
                } else {
                    prgbr_Waiting_OTP_DangKy.setVisibility(View.GONE);
                    txt_notify_sending_otp.setVisibility(View.GONE);
                    container_nhap_ma_xac_thuc.setVisibility(View.VISIBLE);

                    Toast.makeText(NhapMaOTPActivity.this, "Xác Nhận Thất Bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}