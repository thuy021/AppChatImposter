package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appchat.Adapter.MyAdapter;
import com.example.appchat.Adapter.OnItemClickListener;
import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.GoiY;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.example.appchat.Socket.SocketClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachGoiYKetBan extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    SwipeRefreshLayout refresh_goiy;
    RecyclerView recycleDanhSachGoiY;
    ImageButton btnBack_GoiY;
    LinearLayoutManager layoutManager;
    MyAdapter adapter;
    ArrayList<String> lstContactLocal = new ArrayList<>();
    ArrayList<NguoiDung> lstUser = new ArrayList<>();
    ArrayList<NguoiDung> lstLoiMoi = new ArrayList<>();
    ArrayList<NguoiDung> lstDuocMoi = new ArrayList<>();
    ArrayList<NguoiDung> lstBanBe = new ArrayList<>();
    ProgressBar progressBarGoiY;
    SharedPreferences preferences;
    SocketClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_goi_y_ket_ban);
        mClient = new SocketClient();
        Init();
        showContacts();
        ShowDanhSach();
        GetDanhSachLoiMoi();
        GetDanhSachDuocMoiLoiMoi();
        GetDanhSachBanBe();
        GetDanhSachGoiY();
        Back();
    }

    private void Init() {
        preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        btnBack_GoiY = (ImageButton) findViewById(R.id.btnBack_GoiY);
        recycleDanhSachGoiY = (RecyclerView) findViewById(R.id.recycleDanhSachGoiY);
        refresh_goiy = (SwipeRefreshLayout) findViewById(R.id.refresh_goiy);
        recycleDanhSachGoiY.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycleDanhSachGoiY.setLayoutManager(layoutManager);
        progressBarGoiY = (ProgressBar) findViewById(R.id.progressBarGoiY);
    }

    protected void Back() {
        btnBack_GoiY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(DanhSachGoiYKetBan.this, "Không có quyền truy cập vào danh bạ. Hãy cấp quyền cho ứngd ụng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                //String namePhone = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        lstContactLocal.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    protected void ShowDanhSach() {
        adapter = new MyAdapter(this, lstUser, true, 1);
        recycleDanhSachGoiY.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                NguoiDung nd = lstUser.get(position);
                BanBe banBe = new BanBe();
                banBe.setMaNguoiDung_Mot(preferences.getInt("MaNguoiDung", 0));
                banBe.setMaNguoiDung_Hai(lstUser.get(position).getMaNguoiDung());
                DataClient dataClient = APIUtils.getData();
                Call<Message> call = dataClient.SendRequestAddFriend(banBe);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess() == 1) {
                                SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
                                String SoDienThoai = preferences.getString("SoDienThoai", "");
                                GuiThongBaoKetBan(SoDienThoai, nd);
                                lstUser.remove(lstUser.get(position));
                                Toast.makeText(getApplicationContext(), "Xác nhận thành công", Toast.LENGTH_SHORT).show();
                                ShowDanhSach();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void GuiThongBaoKetBan(String SDT, NguoiDung NguoiNhan) {
        String tenNguoiDung = preferences.getString("TenNguoiDung", "");
        JSONObject object = new JSONObject();
        try {
            object.put("NguoiNhanLoiMoi", NguoiNhan.getSoDienThoai());
            object.put("NguoiGuiLoiMoi", SDT);
            object.put("NguoiGuiLoiMoi_HoTen", tenNguoiDung);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mClient.getmClient().emit("DaGuiLoiMoiKetBan", object);
    }

    protected void GetDanhSachGoiY() {
        progressBarGoiY.setVisibility(View.VISIBLE);
        DataClient client = APIUtils.getData();
        GoiY goiY = new GoiY();
        goiY.setList(lstContactLocal);
        Call<Message> call = client.GetListGoiY(goiY);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        for (NguoiDung user : response.body().getDanhsach()) {
                            if (user.isStatus()) {
                                if (!lstLoiMoi.contains(user) && !lstBanBe.contains(user) && !lstDuocMoi.contains(user))
                                    lstUser.add(user);
                            }
                        }
                        progressBarGoiY.setVisibility(View.GONE);
                        ShowDanhSach();
                    } else {
                        progressBarGoiY.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                progressBarGoiY.setVisibility(View.GONE);
            }
        });
    }

    protected void GetDanhSachLoiMoi() {
        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
        DataClient client = APIUtils.getData();
        BanBe banBe = new BanBe();
        banBe.setMaNguoiDung_Mot(MaNguoiDung);
        Call<Message> call = client.GetListSendRequestFriend(banBe);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        for (NguoiDung user : response.body().getDanhsach()) {
                            if (user.isStatus()) {
                                lstLoiMoi.add(user);
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

    protected void GetDanhSachDuocMoiLoiMoi() {
        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
        DataClient client = APIUtils.getData();
        BanBe banBe = new BanBe();
        banBe.setMaNguoiDung_Mot(MaNguoiDung);
        Call<Message> call = client.GetListRequestFriend(banBe);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        for (NguoiDung user : response.body().getDanhsach()) {
                            if (user.isStatus()) {
                                lstDuocMoi.add(user);
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

    private void GetDanhSachBanBe() {
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
                                lstBanBe.add(user);
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