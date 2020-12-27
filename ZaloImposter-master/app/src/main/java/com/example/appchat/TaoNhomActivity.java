package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.Adapter.Adapter_Create_Nhom;
import com.example.appchat.Adapter.OnMultiClickCheckBoxListener;
import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.MessageNhom;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Models.Room;
import com.example.appchat.Models.ThanhVien;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaoNhomActivity extends AppCompatActivity {

    ImageButton btnBackNhom, btnDongYTaoNhom;
    TextView tenNhom;
    String idGroup = "";
    SharedPreferences preferences, preferencesDanhBa;
    EditText txtSDT;
    String SDT;
    Adapter_Create_Nhom adapter_Create_nhom;
    LinearLayoutManager layoutManager;
    ArrayList<NguoiDung> lstUser = new ArrayList<>();
    ArrayList<NguoiDung> lstCheck = new ArrayList<>();
    BanBe ban_be_info;
    RecyclerView recycleDSNhom_Tao;
    TextView tvCap_Nhat_Chon_Ban_Be_Vao_Nhom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_nhom);

        ban_be_info = new BanBe();
        lstUser.clear();
        lstCheck.clear();
        init_Data();
        GetDanhSachBan();
        backFragmentNhom_Click();
        CheckMultiItem();
        CreateGroup();
    }


    protected void init_Data() {
        preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        preferencesDanhBa = getSharedPreferences("data_danh_ba", MODE_PRIVATE);

        btnBackNhom = (ImageButton) findViewById(R.id.btnBack_FragmentNhom);
        btnDongYTaoNhom = (ImageButton) findViewById(R.id.btnXac_Nhan_Tao_Nhom);
        tenNhom = (TextView) findViewById(R.id.txtDatTenNhom);
        SDT = preferences.getString("SoDienThoai", "");
        recycleDSNhom_Tao = (RecyclerView) findViewById(R.id.recycleDSNhom_Tao);
        layoutManager = new LinearLayoutManager(this);
        recycleDSNhom_Tao.setLayoutManager(layoutManager);
        tvCap_Nhat_Chon_Ban_Be_Vao_Nhom = (TextView) findViewById(R.id.tvCap_Nhat_Chon_Ban_Be_Vao_Nhom);
    }

    protected void backFragmentNhom_Click() {
        btnBackNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void CheckMultiItem() {
        adapter_Create_nhom.setOnMultiClickCheckBoxListener(new OnMultiClickCheckBoxListener() {
            @Override
            public void onItemClicked(boolean isChecked, int position) {
                if (isChecked) {
                    lstCheck.add(lstUser.get(position));
                } else {
                    lstCheck.remove(lstUser.get(position));
                }
                tvCap_Nhat_Chon_Ban_Be_Vao_Nhom.setText("Đã chọn : " + lstCheck.size());
            }

            @Override
            public void onItemCheckBoxChecked(boolean isChecked, int position) {
                if (isChecked) {
                    lstCheck.add(lstUser.get(position));
                } else {
                    lstCheck.remove(lstUser.get(position));
                }
                tvCap_Nhat_Chon_Ban_Be_Vao_Nhom.setText("Đã chọn : " + lstCheck.size());
            }
        });
    }

    protected void ShowDanhSach() {
        adapter_Create_nhom = new Adapter_Create_Nhom(TaoNhomActivity.this, lstUser, false);
        recycleDSNhom_Tao.setAdapter(adapter_Create_nhom);
        adapter_Create_nhom.notifyDataSetChanged();
    }

    protected void GetDanhSachBan() {
        String json = preferencesDanhBa.getString("ListUser", "");
        if (json.equals("")) {
            int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
            DataClient client = APIUtils.getData();
            BanBe banBe = new BanBe();
            banBe.setMaNguoiDung_Mot(MaNguoiDung);
            Call<Message> call = client.GetListFriend(banBe);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() == 1) {
                            for (NguoiDung user : response.body().getDanhsach()) {
                                if (user.isStatus()) {
                                    lstUser.add(user);
                                }
                            }
                            ShowDanhSach();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {

                }
            });
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NguoiDung>>() {
            }.getType();
            lstUser = gson.fromJson(String.valueOf(json), type);
            ShowDanhSach();
        }
    }

    private void CreateTableChat(String id) {
        DataClient client = APIUtils.getData();
        Room room = new Room();
        room.setId_room(id);
        Call<Message> call = client.CreateTable(room);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    private void CreateGroup() {
        btnDongYTaoNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lstCheck.size() == 0) {
                    Toast.makeText(TaoNhomActivity.this, "Hãy chọn thành viên", Toast.LENGTH_LONG).show();
                    return;
                } else if (lstCheck.size() == 1) {
                    NguoiDung nd = lstCheck.get(0);
                    Intent intent = new Intent(TaoNhomActivity.this, NhanTinDonActivity.class);
                    String sdt = nd.getSoDienThoai();
                    String ten = nd.getHoTen();
                    int id_user = nd.getMaNguoiDung();
                    intent.putExtra("sdt", sdt);
                    intent.putExtra("ten", ten);
                    intent.putExtra("id_user", id_user);
                    startActivity(intent);
                } else {
                    DataClient client = APIUtils.getData();
                    ThanhVien tv = new ThanhVien();
                    if (tenNhom.getText().toString().equals("")) {
                        Toast.makeText(TaoNhomActivity.this, "Hãy nhập tên nhóm để tạo nhóm", Toast.LENGTH_LONG).show();
                        tenNhom.setText("");
                        tenNhom.requestFocus();
                        return;
                    }
                    tv.setTenNhom(tenNhom.getText().toString().trim());
                    int maNguoiDung = preferences.getInt("MaNguoiDung", 0);
                    tv.setTruongNhom(maNguoiDung);
                    Call<MessageNhom> call = client.CreateGroup(tv);
                    call.enqueue(new Callback<MessageNhom>() {
                        @Override
                        public void onResponse(Call<MessageNhom> call, Response<MessageNhom> response) {
                            if (response.isSuccessful())
                                if (response.body().getSuccess() == 1) {
                                    idGroup = response.body().getMaNhom();
                                    if (idGroup.equals("")) {
                                        Toast.makeText(TaoNhomActivity.this, "Nhóm chưa được tạo", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    CreateTableChat(response.body().getMaNhom());
                                    AddThanhVienToGroup(lstCheck);
                                    Intent intent = new Intent(TaoNhomActivity.this, NhanTinNhomActivity.class);
                                    String id_nhom = idGroup;
                                    String ten_nhom = tenNhom.getText().toString().trim();
                                    intent.putExtra("ten_nhom", ten_nhom);
                                    intent.putExtra("id_nhom", id_nhom);
                                    startActivity(intent);
                                }
                        }

                        @Override
                        public void onFailure(Call<MessageNhom> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

    private void AddThanhVienToGroup(ArrayList<NguoiDung> lst) {
        if (lst.size() == 0) {
            Toast.makeText(TaoNhomActivity.this, "Hãy chọn thành viên", Toast.LENGTH_LONG).show();
            return;
        }
        for (NguoiDung nd : lst) {
            DataClient client = APIUtils.getData();
            ThanhVien tv = new ThanhVien();
            tv.setMaNhom(idGroup);
            tv.setMaThanhVien(Integer.toString(nd.getMaNguoiDung()));
            Call<MessageNhom> call = client.AddItemGroup(tv);
            call.enqueue(new Callback<MessageNhom>() {
                @Override
                public void onResponse(Call<MessageNhom> call, Response<MessageNhom> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() == 0) {
                            Toast.makeText(TaoNhomActivity.this, "Đang có lỗi thêm thành viên", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                @Override
                public void onFailure(Call<MessageNhom> call, Throwable t) {

                }
            });
        }
    }
}