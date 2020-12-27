package com.example.appchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HeaderMap;

import static android.content.Context.MODE_PRIVATE;

public class ThongTinFragment extends Fragment {
    final int EDIT_CODE_INTENT = 9999;
    SharedPreferences preferences;

    NguoiDung nguoi_dung_infor;

    String token, SDT;
    View view;
    Button btnSuaThongTin;

    TextView txtHoTen_TaiKhoan;
    TextView txtContent_GioiTinh_TaiKhoan;
    TextView txtContent_NgaySinh_TaiKhoan;
    TextView txtContent_SoDienThoai_TaiKhoan;

    ProgressBar prgbr_Loading_Profile;

    ConstraintLayout conatiner_avatar;
    TableLayout container_infor_account;
    LinearLayout container_button_infor;

    Message message;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Init_Data();
        btnSuaThongTin_Click();

        Load_ThongTin_NguoiDung();

        return view;
    }

    protected void Init_Data() {
        preferences = getActivity().getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        token = preferences.getString("Token_DangNhap", "");
        SDT = preferences.getString("SoDienThoai", "");

        btnSuaThongTin = (Button) view.findViewById(R.id.btnSuaThongTin);

        txtHoTen_TaiKhoan = (TextView) view.findViewById(R.id.txtHoTen_TaiKhoan);
        txtContent_GioiTinh_TaiKhoan = (TextView) view.findViewById(R.id.txtContent_GioiTinh_TaiKhoan);
        txtContent_NgaySinh_TaiKhoan = (TextView) view.findViewById(R.id.txtContent_NgaySinh_TaiKhoan);
        txtContent_SoDienThoai_TaiKhoan = (TextView) view.findViewById(R.id.txtContent_SoDienThoai_TaiKhoan);

        conatiner_avatar = (ConstraintLayout) view.findViewById(R.id.constraintLayout);
        container_infor_account = (TableLayout) view.findViewById(R.id.container_infor_account);
        prgbr_Loading_Profile = (ProgressBar) view.findViewById(R.id.prgbr_Loading_Profile);
        container_button_infor = (LinearLayout) view.findViewById(R.id.linearLayout);
    }

    protected void Load_ThongTin_NguoiDung() {
        String temp = token;
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", ("Bearer " + temp).trim());

        DataClient client = APIUtils.getData();
        Call<Message> call = client.GetThongTinNguoiDung_bySDT(SDT, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        nguoi_dung_infor = response.body().getData();

                        if (nguoi_dung_infor.getHoTen() != null) {
                            txtHoTen_TaiKhoan.setText(nguoi_dung_infor.getHoTen());
                        }

                        if (nguoi_dung_infor.getGioiTinh() != null) {
                            if (nguoi_dung_infor.getGioiTinh()) {
                                txtContent_GioiTinh_TaiKhoan.setText("Nam");
                            } else {
                                txtContent_GioiTinh_TaiKhoan.setText("Nữ");
                            }
                        }

                        if (nguoi_dung_infor.getNgaySinh() != null) {
                            String ngaySinh = nguoi_dung_infor.getNgaySinh();
                            ngaySinh = ngaySinh.substring(0, 10);

                            String thang = ngaySinh.substring(5, 7);
                            String ngay = ngaySinh.substring(8, 10);
                            String nam = ngaySinh.substring(0, 4);

                            ngaySinh = ngay + "-" + thang + "-" + nam;
                            
                            nguoi_dung_infor.setNgaySinh(thang + "-" + ngay + "-" + nam);
                            txtContent_NgaySinh_TaiKhoan.setText(ngaySinh);
                        }

                        if (nguoi_dung_infor.getSoDienThoai() != null) {
                            String SDT = response.body().getData().getSoDienThoai();
                            txtContent_SoDienThoai_TaiKhoan.setText(SDT);
                        }

                        prgbr_Loading_Profile.setVisibility(View.GONE);
                        conatiner_avatar.setVisibility(View.VISIBLE);
                        container_button_infor.setVisibility(View.VISIBLE);
                        container_infor_account.setVisibility(View.VISIBLE);

                        //Lưu Lại Mã Người Dùng Khi Sủ Dụng Ở Activity Khác
                        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
                        if(MaNguoiDung == 0){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("MaNguoiDung", response.body().getData().getMaNguoiDung());
                            editor.apply();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    protected void btnSuaThongTin_Click() {
        btnSuaThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), SuaThongTinNguoiDungActivity.class);
                bundle.putSerializable("ThongTinNguoiDung", nguoi_dung_infor);
                intent.putExtra("EditInfor", bundle);
                startActivityForResult(intent, EDIT_CODE_INTENT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_CODE_INTENT  && resultCode == getActivity().RESULT_OK){
            Bundle bundle  = data.getBundleExtra("nguoi_dung_infor_update");
            nguoi_dung_infor = (NguoiDung) bundle.getSerializable("bundle_infor_nguoi_dung_update");

            if (nguoi_dung_infor.getHoTen() != null) {
                txtHoTen_TaiKhoan.setText(nguoi_dung_infor.getHoTen());
            }

            if (nguoi_dung_infor.getGioiTinh() != null) {
                if (nguoi_dung_infor.getGioiTinh()) {
                    txtContent_GioiTinh_TaiKhoan.setText("Nam");
                } else {
                    txtContent_GioiTinh_TaiKhoan.setText("Nữ");
                }
            }

            if (nguoi_dung_infor.getNgaySinh() != null) {
                String[] ngaySinh = nguoi_dung_infor.getNgaySinh().split("-");
                txtContent_NgaySinh_TaiKhoan.setText(ngaySinh[1] + "-" + ngaySinh[0] + "-" + ngaySinh[2]);
            }

            if (nguoi_dung_infor.getSoDienThoai() != null) {
                String SDT = nguoi_dung_infor.getSoDienThoai();
                txtContent_SoDienThoai_TaiKhoan.setText(SDT);
            }
        }
    }
}
