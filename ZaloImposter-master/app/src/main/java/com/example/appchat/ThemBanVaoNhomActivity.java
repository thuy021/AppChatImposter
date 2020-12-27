package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.appchat.Models.ThanhVien;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.example.appchat.Socket.SocketChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemBanVaoNhomActivity extends AppCompatActivity {

    ImageButton btnBack_Khung_Chat_Nhom;
    TextView textViewTenAdd;
    Button btnLuu_ThemBanVaoChatNhom;
    EditText sreach_Sdt_add_group;
    RecyclerView recycleView;
    String id_nhom = "";
    String ten_nhom = "";
    Adapter_Create_Nhom adapter;
    ArrayList<NguoiDung> lstUser = new ArrayList<>();
    ArrayList<NguoiDung> lstCheck = new ArrayList<>();
    LinearLayoutManager layoutManager;
    SharedPreferences preferencesDanhBa, preferences;
    SocketChat mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_ban_vao_nhom);
        lstUser.clear();
        lstCheck.clear();
        Init();
        Back_Activity();
        GetDanhSachThanhVien();
        AddItemGroup();
    }

    private void Init() {
        mSocket = new SocketChat();
        btnBack_Khung_Chat_Nhom = (ImageButton) findViewById(R.id.btnBack_Khung_Chat_Nhom);
        textViewTenAdd = (TextView) findViewById(R.id.textViewTenAdd);
        btnLuu_ThemBanVaoChatNhom = (Button) findViewById(R.id.btnLuu_ThemBanVaoChatNhom);

        recycleView = (RecyclerView) findViewById(R.id.recycleView_DanhSachBanBe_ThemNhom_Activity);
        layoutManager = new LinearLayoutManager(this);
        recycleView.setLayoutManager(layoutManager);
        id_nhom = getIntent().getStringExtra("id_nhom");
        ten_nhom = getIntent().getStringExtra("ten_nhom");
        textViewTenAdd.setText(ten_nhom);
        preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        preferencesDanhBa = getSharedPreferences("data_danh_ba", MODE_PRIVATE);
    }

    private void Back_Activity() {
        btnBack_Khung_Chat_Nhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GetDanhSachThanhVien() {
        DataClient client = APIUtils.getData();
        Call<MessageNhom> call = client.GetListThanhVien(id_nhom);
        call.enqueue(new Callback<MessageNhom>() {
            @Override
            public void onResponse(Call<MessageNhom> call, Response<MessageNhom> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        ArrayList<ThanhVien> lst = response.body().getData();
                        GetDanhSachBan();
                        for (ThanhVien tv : lst) {
                            for (NguoiDung nd : lstUser) {
                                if (tv.getMaThanhVien().equals(nd.getMaNguoiDung() + "")) {
                                    lstUser.remove(nd);
                                    break;
                                }
                            }
                        }
                        ShowDanhSach();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageNhom> call, Throwable t) {

            }
        });
    }

    private void AddItemGroup() {
        btnLuu_ThemBanVaoChatNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lstCheck.size() == 0) {
                    Toast.makeText(ThemBanVaoNhomActivity.this, "Không tồn tại bạn để thêm", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    int count = lstCheck.size();
                    int i = 0;
                    for (NguoiDung nd : lstCheck) {
                        DataClient client = APIUtils.getData();
                        ThanhVien tv = new ThanhVien();
                        tv.setMaNhom(id_nhom);
                        tv.setMaThanhVien(Integer.toString(nd.getMaNguoiDung()));
                        Call<MessageNhom> call = client.AddItemGroup(tv);
                        call.enqueue(new Callback<MessageNhom>() {
                            @Override
                            public void onResponse(Call<MessageNhom> call, Response<MessageNhom> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getSuccess() == 0) {
                                        Toast.makeText(ThemBanVaoNhomActivity.this, "Đang có lỗi thêm thành viên", Toast.LENGTH_LONG).show();
                                        return;
                                    } else {
                                        JSONObject object = new JSONObject();
                                        try {
                                            object.put("idUser", nd.getMaNguoiDung());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        mSocket.getmClient().emit("CLIENT_GUI_THONG_BAO_ADD_GROUP", object);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MessageNhom> call, Throwable t) {

                            }
                        });
                        i++;
                    }
                    if (i == count) {
                        Toast.makeText(ThemBanVaoNhomActivity.this, "Thêm vào nhóm thành công", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });
    }

    private void GetDanhSachBan() {
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
        }
    }

    protected void ShowDanhSach() {
        adapter = new Adapter_Create_Nhom(getApplicationContext(), lstUser, false);
        CheckMultiItem();
        recycleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void CheckMultiItem() {
        adapter.setOnMultiClickCheckBoxListener(new OnMultiClickCheckBoxListener() {
            @Override
            public void onItemClicked(boolean isChecked, int position) {
                if (isChecked) {
                    lstCheck.add(lstUser.get(position));
                } else {
                    lstCheck.remove(lstUser.get(position));
                }
            }

            @Override
            public void onItemCheckBoxChecked(boolean isChecked, int position) {
                if (isChecked) {
                    lstCheck.add(lstUser.get(position));
                } else {
                    lstCheck.remove(lstUser.get(position));
                }
            }
        });
    }
}