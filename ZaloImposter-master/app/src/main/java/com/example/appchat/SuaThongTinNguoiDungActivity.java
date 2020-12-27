package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuaThongTinNguoiDungActivity extends AppCompatActivity {
    NguoiDung nguoi_dung; //<---Dữ Liệu Người Dùng Khi Chưa Update
    NguoiDung nguoi_dung_update;

    ImageButton btnBack_SuaThongTin;
    ImageButton btnDatePicker;
    EditText etxtNgaySinh_EditInfor;
    EditText etxtHoTen_EditInfor;

    RadioButton rbtnGender_Nam_EditInfor;
    RadioButton rbtnGender_Nu_EditInfor;

    Button btnCapNhat_EditInfor;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_thong_tin_nguoi_dung);

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        Load_Data();
        Date_SetListener();
        btnDatePicker_Click();
        btnBack_SuaThongTin_Click();
        btnCapNhat_EditInfor_Click();
    }

    protected void Init_Data(){
        nguoi_dung_update = new NguoiDung();
        etxtNgaySinh_EditInfor = (EditText) findViewById(R.id.etxtNgaySinh_EditInfor);
        etxtNgaySinh_EditInfor.setEnabled(false);
        etxtHoTen_EditInfor = (EditText) findViewById(R.id.etxtHoTen_EditInfor);
        rbtnGender_Nam_EditInfor = (RadioButton) findViewById(R.id.rbtnGender_Nam_EditInfor);
        rbtnGender_Nu_EditInfor = (RadioButton) findViewById(R.id.rbtnGender_Nu_EditInfor);

        btnDatePicker = (ImageButton) findViewById(R.id.btnDatePicker);
        btnBack_SuaThongTin = (ImageButton) findViewById(R.id.btnBack_SuaThongTin);
        btnCapNhat_EditInfor = (Button) findViewById(R.id.btnCapNhat_EditInfor);
    }

    //Load Dữ Liệu Người Dùng
    protected void Load_Data(){
        Bundle bundle = getIntent().getBundleExtra("EditInfor");
        nguoi_dung = (NguoiDung) bundle.getSerializable("ThongTinNguoiDung");

        String HoTen = nguoi_dung.getHoTen();
        String NgaySinh = nguoi_dung.getNgaySinh();
        Boolean GioiTinh = nguoi_dung.getGioiTinh();

        if(HoTen != null){
            etxtHoTen_EditInfor.setText(HoTen);
        }
        if(NgaySinh != null){
            String[] ngaySinh = NgaySinh.split("-");
            etxtNgaySinh_EditInfor.setText(ngaySinh[1] + "-" + ngaySinh[0] + "-" + ngaySinh[2]);
        }
        if(GioiTinh != null){
            if(GioiTinh){
                rbtnGender_Nam_EditInfor.setChecked(true);
                rbtnGender_Nu_EditInfor.setChecked(false);
            }else {
                rbtnGender_Nu_EditInfor.setChecked(true);
                rbtnGender_Nam_EditInfor.setChecked(false);
            }
        }

        nguoi_dung_update = nguoi_dung;
    }

    protected void btnDatePicker_Click(){
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DAY_OF_MONTH);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        SuaThongTinNguoiDungActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        nam, thang, ngay);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    protected void Date_SetListener(){
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int Current_Year = Calendar.getInstance().get(Calendar.YEAR);

                if(year >  (Current_Year - 5)){
                    Toast.makeText(SuaThongTinNguoiDungActivity.this,"Năm Sinh Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                String ngay = Integer.toString(dayOfMonth);
                String thang = Integer.toString(month + 1);
                String nam = Integer.toString(year);

                nguoi_dung_update.setNgaySinh(thang + "-" + ngay + "-" + nam);//<----Định Dạng Ngày Sinh Đúng
                etxtNgaySinh_EditInfor.setText(ngay + "-" + thang + "-" + nam);
            }
        };
    }

    protected void btnBack_SuaThongTin_Click(){
        btnBack_SuaThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SuaThongTinNguoiDungActivity.this);
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
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    protected void btnCapNhat_EditInfor_Click(){
        btnCapNhat_EditInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etxtHoTen_EditInfor.getText().length() == 0){
                    Toast.makeText(SuaThongTinNguoiDungActivity.this,"Họ Tên Không Để Trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                nguoi_dung_update.setHoTen(etxtHoTen_EditInfor.getText().toString());
                nguoi_dung_update.setSoDienThoai(nguoi_dung.getSoDienThoai());

                if(rbtnGender_Nam_EditInfor.isChecked()){
                    nguoi_dung_update.setGioiTinh(true);
                }else {
                    nguoi_dung_update.setGioiTinh(false);
                }

                nguoi_dung_update.setMaNguoiDung(nguoi_dung.getMaNguoiDung());

                Call_API_Update();
            }
        });
    }

    protected void Call_API_Update(){
        SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        String token = preferences.getString("Token_DangNhap", "");
        String matKhau = preferences.getString("MatKhau", "");
        nguoi_dung_update.setPassword(matKhau);

        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer " + token);

        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.SuaThongTin(nguoi_dung_update, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if(response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        Bundle bundle = new Bundle();
                        Intent intent_02 = new Intent();
                        bundle.putSerializable("bundle_infor_nguoi_dung_update",nguoi_dung_update);
                        intent_02.putExtra("nguoi_dung_infor_update", bundle);
                        setResult(RESULT_OK, intent_02);
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Lỗi", t.getMessage());
            }
        });
    }
}