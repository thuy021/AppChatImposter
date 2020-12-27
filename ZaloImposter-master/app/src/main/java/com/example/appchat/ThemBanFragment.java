package com.example.appchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appchat.Adapter.MyAdapter;
import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ThemBanFragment extends Fragment {
    final String Regex_CheckSoDienThoai = "^0[1-9][0-9]{8}$";
    final int EDIT_CODE_INTENT = 9999;
    EditText txtSDT;
    SharedPreferences preferences, preferencesDanhBa;
    ProgressBar prgbr_Loading;
    MyAdapter adapter;
    LinearLayoutManager layoutManager;
    View view;
    String token, SDT;
    Button btnTim;
    NguoiDung nguoi_dung_infor;
    ArrayList<NguoiDung> lstUser = new ArrayList<>();
    BanBe ban_be_info;
    Integer status = -1;
    Fragment selectedFragment;
    RecyclerView recycleDanhSachBanBe_Tim;
    ProgressBar progressBar_TimBanBe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_them_ban, container, false);
        ban_be_info = new BanBe();
        lstUser.clear();
        Init_Data();
        GetMaNguoiDung();
        TextChange();
        GetDanhSachBan();
        btnTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Get_ThongTin_BanBe();
            }
        });
        return view;
    }

    protected void Init_Data() {
        preferencesDanhBa = getContext().getSharedPreferences("data_danh_ba", MODE_PRIVATE);
        progressBar_TimBanBe = (ProgressBar) view.findViewById(R.id.progressBar_TimBanBe);
        preferences = getActivity().getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        token = preferences.getString("Token_DangNhap", "");
        SDT = preferences.getString("SoDienThoai", "");
        btnTim = (Button) view.findViewById(R.id.btnTimBan);
        txtSDT = (EditText) view.findViewById(R.id.etxtSoDienThoai_TimBanBe);
        prgbr_Loading = (ProgressBar) view.findViewById(R.id.prgbr_Loading);
        recycleDanhSachBanBe_Tim = (RecyclerView) view.findViewById(R.id.recycleDanhSachBanBe_Tim);
        layoutManager = new LinearLayoutManager(view.getContext());
        recycleDanhSachBanBe_Tim.setLayoutManager(layoutManager);
    }

    protected void ShowDanhSach(){
        adapter = new MyAdapter(view.getContext(), lstUser, false, 0);
        adapter.notifyDataSetChanged();
        recycleDanhSachBanBe_Tim.setAdapter(adapter);
    }

    protected void GetDanhSachBan(){
        String json = preferencesDanhBa.getString("ListUser", "");
        if (json.equals("")){
            progressBar_TimBanBe.setVisibility(View.VISIBLE);
            int MaNguoiDung = preferences.getInt("MaNguoiDung" , 0);
            DataClient client = APIUtils.getData();
            BanBe banBe = new BanBe();
            banBe.setMaNguoiDung_Mot(MaNguoiDung);
            Call<Message> call = client.GetListFriend(banBe);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if (response.isSuccessful()) {
                        progressBar_TimBanBe.setVisibility(View.GONE);
                        if (response.body().getSuccess() == 1) {
                            for (NguoiDung user : response.body().getDanhsach()){
                                if (user.isStatus()){
                                    lstUser.add(user);
                                }
                            }
                            ShowDanhSach();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    progressBar_TimBanBe.setVisibility(View.GONE);
                }
            });
        }
        else {

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NguoiDung>>() {}.getType();
            lstUser = gson.fromJson(String.valueOf(json), type);
            ShowDanhSach();
        }
    }

    protected void TextChange(){
        txtSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<NguoiDung> lstUserTemp = new ArrayList<>();
                for (NguoiDung user : lstUser){
                    if (user.getSoDienThoai().contains(s)){
                        lstUserTemp.add(user);
                    }
                }
                adapter = new MyAdapter(view.getContext(), lstUserTemp, false, 0);
                recycleDanhSachBanBe_Tim.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void GetMaNguoiDung(){
        String temp = token;
        DataClient client = APIUtils.getData();
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", ("Bearer " + temp).trim());
        Call<Message> call = client.GetThongTinNguoiDung_bySDT(SDT, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        nguoi_dung_infor = response.body().getData();
                        int i = nguoi_dung_infor.getMaNguoiDung();
                        ban_be_info.setMaNguoiDung_Mot(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });

        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
        if(MaNguoiDung == 0){

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("MaNguoiDung", nguoi_dung_infor.getMaNguoiDung());
            editor.apply();
        }
    }

    protected void Get_ThongTin_BanBe(){
        String temp = token;
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", ("Bearer " + temp).trim());
        String SDT = txtSDT.getText().toString();
        if(!SDT.matches(Regex_CheckSoDienThoai)) {
            Toast.makeText(view.getContext(),"Số Điện Thoại Không Hợp Lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        prgbr_Loading.setVisibility(view.VISIBLE);
        DataClient client = APIUtils.getData();
        Call<Message> call = client.GetThongTinNguoiDung_bySDT(SDT, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        nguoi_dung_infor = response.body().getData();
                        if (nguoi_dung_infor != null){
                            ban_be_info.setMaNguoiDung_Hai(nguoi_dung_infor.getMaNguoiDung());
                            CheckTrangThaiBanBe();
                        }
                    }
                    else {
                        prgbr_Loading.setVisibility(view.GONE);
                        Toast.makeText(view.getContext(), "Không tìm thấy", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    protected void CheckTrangThaiBanBe(){
        DataClient client = APIUtils.getData();
        Call<Message> call = client.CheckTrangThaiBanBe(ban_be_info);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()){
                    if (response.body().getSuccess() == 1){
                        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
                        if (MaNguoiDung == nguoi_dung_infor.getMaNguoiDung()){
                            selectedFragment = new ThongTinFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).addToBackStack(selectedFragment.getClass().getSimpleName()).commit();
                        }
                        else {
                            status = response.body().getStatus();
                            if (status == -1){
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(getActivity(), ThongTinChuaKetBanActivity.class);
                                bundle.putSerializable("ThongTinNguoiDung", nguoi_dung_infor);
                                intent.putExtra("Infor", bundle);
                                prgbr_Loading.setVisibility(view.GONE);
                                startActivityForResult(intent, EDIT_CODE_INTENT);
                            }
                            else if (status == 0 && (response.body().getAction() == ban_be_info.getMaNguoiDung_Mot())){
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(getActivity(), ThongTinDaGuiLoiMoiKetBan.class);
                                bundle.putSerializable("ThongTinNguoiDung", nguoi_dung_infor);
                                intent.putExtra("Infor", bundle);
                                prgbr_Loading.setVisibility(view.GONE);
                                startActivityForResult(intent, EDIT_CODE_INTENT);
                            }
                            else if (status == 0 && (response.body().getAction() != ban_be_info.getMaNguoiDung_Mot())){
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(getActivity(), ThongTinChapNhanLoiMoiKetBan.class);
                                bundle.putSerializable("ThongTinNguoiDung", nguoi_dung_infor);
                                intent.putExtra("Infor", bundle);
                                prgbr_Loading.setVisibility(view.GONE);
                                startActivityForResult(intent, EDIT_CODE_INTENT);
                            }
                            else if (status == 1){
                                Bundle bundle = new Bundle();
                                Intent intent = new Intent(getActivity(), ThongTinDaKetBanActivity.class);
                                bundle.putSerializable("ThongTinNguoiDung", nguoi_dung_infor);
                                intent.putExtra("Infor", bundle);
                                prgbr_Loading.setVisibility(view.GONE);
                                startActivityForResult(intent, EDIT_CODE_INTENT);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }
}