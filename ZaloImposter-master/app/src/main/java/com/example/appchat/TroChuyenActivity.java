package com.example.appchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.appchat.Adapter.MyAdapter;
import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.example.appchat.Socket.SocketChat;
import com.example.appchat.Socket.SocketClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TroChuyenActivity extends AppCompatActivity {
    SharedPreferences preferences_login;
    Fragment selectedFragment;
    BottomNavigationView bottomNav;
    ImageButton btnSettings_Profile;
    ImageButton btnCross;
    Button btnTimBanBe;
    String name = "ThemBanFragment";
    SocketClient mClient;
    SocketChat cClient;

    ArrayList<NguoiDung> ListFriend = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tro_chuyen);

        mClient = new SocketClient();
        cClient = new SocketChat();
        Init_SocketIO();

        //Ẩn Thanh Trạng Thái
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnCross_Click();
        btnSettings_Profile_Click();
        TimBanBe_Click();

        //Set Fragment Mặc Định Sẽ Mở Khi Load Activity
        selectedFragment = new TroChuyenFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).commit();

        //Cập Nhật Danh Sách Bạn Bè Mới
        GetNewDanhSachBanBe();
    }

    private void Init_Data() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListerner);
        btnSettings_Profile = (ImageButton) findViewById(R.id.btnSettings_Profile);
        btnCross = (ImageButton) findViewById(R.id.btnCross);
        btnTimBanBe = (Button) findViewById(R.id.btnTimBanBe);

        preferences_login = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
    }

    private void Init_SocketIO(){
        SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        String SoDienThoai = preferences.getString("SoDienThoai", "");

        JSONObject object = new JSONObject();

        try {
            object.put("SoDienThoai", SoDienThoai);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mClient.getmClient().emit("DangKyNhanThongBaoDanhBa", object);
        mClient.getmClient().on("ThongBaoLoiMoiKetBanMoi", ThongBaoLoiMoiKetBanListener);
    }

    private Emitter.Listener ThongBaoLoiMoiKetBanListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];

                    String NguoiGuiLoiMoi_HoTen = "";

                    try {
                        NguoiGuiLoiMoi_HoTen = object.getString("NguoiGuiLoiMoi_HoTen");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ThongBaoLoiMoiKetBan(NguoiGuiLoiMoi_HoTen);
                }
            });
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListerner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.nav_message:
                    btnSettings_Profile.setVisibility(View.GONE);
                    selectedFragment = new TroChuyenFragment();
                    btnCross.setVisibility(View.VISIBLE);
                    break;

                case R.id.nav_friend:
                    selectedFragment = new DanhBaFragment();
                    btnSettings_Profile.setVisibility(View.GONE);
                    btnCross.setVisibility(View.VISIBLE);
                    break;

                case R.id.nav_group:
                    selectedFragment = new NhomFragment();
                    btnSettings_Profile.setVisibility(View.GONE);
                    btnCross.setVisibility(View.VISIBLE);
                    break;

                case R.id.nav_profile:
                    selectedFragment = new ThongTinFragment();
                    btnSettings_Profile.setVisibility(View.VISIBLE);
                    btnCross.setVisibility(View.GONE);
                    break;

                default:
                    selectedFragment = new TroChuyenFragment();
                    btnSettings_Profile.setVisibility(View.GONE);
                    btnCross.setVisibility(View.VISIBLE);
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {

        //Back form
        super.onBackPressed();

        //Clear frame ...
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStackImmediate();
        }


    }

    protected void btnCross_Click() {
        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(TroChuyenActivity.this, btnCross);
                popup.inflate(R.menu.plus_navigation_menu);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.imenu_AddBanBe:
                                TimBanBe();
                                break;
                            case R.id.imenu_GoiY:
                                DanhSachGoiY();
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    protected void DanhSachGoiY(){
        Intent intent = new Intent(TroChuyenActivity.this, DanhSachGoiYKetBan.class);
        startActivity(intent);
    }

    protected void btnSettings_Profile_Click() {
        btnSettings_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(TroChuyenActivity.this, btnSettings_Profile);
                popup.inflate(R.menu.profile_menu);
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.imenu_DoiMatKhau:
                                DoiMatKhau();
                                break;
                            case R.id.imenu_DangXuat:
                                DangXuat();
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    protected void DangXuat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TroChuyenActivity.this);
        builder.setTitle("Đăng Xuất");
        builder.setMessage("Bạn Có Chắc Muốn Đăng Xuất ?");
        builder.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Xoá Thông Tin Tài Khoản Trên Điện Thoại
                SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("MaNguoiDung");
                editor.remove("SoDienThoai");
                editor.remove("MatKhau");
                editor.remove("Token_DangNhap");
                editor.apply();

                preferences = getSharedPreferences("data_danh_ba", MODE_PRIVATE);
                editor = preferences.edit();
                editor.remove("ListUser");
                editor.commit();

                preferences = getSharedPreferences("data_conversations", MODE_PRIVATE);
                editor = preferences.edit();
                editor.remove("Conversations");
                editor.commit();

                Intent intent = new Intent(TroChuyenActivity.this, SplashScreen.class);
                startActivity(intent);
                finish();//<---Nhớ Finish Cái Activity
            }
        });
        builder.setNegativeButton("Huỷ Bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void DoiMatKhau() {
        SharedPreferences preferences = getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        String SDT = preferences.getString("SoDienThoai", "");

        if (!SDT.isEmpty()) {
            Intent intent = new Intent(TroChuyenActivity.this, DoiMatKhauActivity.class);
            intent.putExtra("SoDienThoai_NguoiDung", SDT);
            startActivity(intent);
            finish();
        }
    }

    protected void TimBanBe_Click() {
        btnTimBanBe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimBanBe();
            }
        });
    }

    protected void TimBanBe() {
        if (!selectedFragment.getClass().getSimpleName().equals("ThemBanFragment")) {
            selectedFragment = new ThemBanFragment();
            name = selectedFragment.getClass().getSimpleName();
            btnSettings_Profile.setVisibility(View.GONE);
            btnCross.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, selectedFragment).addToBackStack(selectedFragment.getClass().getSimpleName()).commit();
        }
    }

    private void ThongBaoLoiMoiKetBan(String NguoiGuiLoiMoi_HoTen){
        Intent intent = new Intent(TroChuyenActivity.this, DanhSachLoiMoiKetBanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(TroChuyenActivity.this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(TroChuyenActivity.this);

        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("Zalo Imposter","Zalo Imposter", NotificationManager.IMPORTANCE_HIGH);
        }

        Notification notification = builder.setContentTitle("Zalo Imposter")
                .setContentText(NguoiGuiLoiMoi_HoTen + " muốn kết nối với bạn")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo_imposter)
                .setChannelId("Zalo Imposter")
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, notification);
    }

    private void ThongBaoTinNhan(String TenNguoiGuiTinNhan){
        Intent intent = new Intent(TroChuyenActivity.this, DanhSachLoiMoiKetBanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(TroChuyenActivity.this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(TroChuyenActivity.this);

        NotificationChannel channel = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("Zalo Imposter","Zalo Imposter", NotificationManager.IMPORTANCE_HIGH);
        }

        Notification notification = builder.setContentTitle("Zalo Imposter")
                .setContentText("Bạn Có 1 Tin Nhắn Mới")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo_imposter)
                .setChannelId("Zalo Imposter")
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, notification);
    }

    //Cập Nhật Danh Sách Bạn Bè Mới Nhất
    protected void GetNewDanhSachBanBe(){
        int MaNguoiDung = preferences_login.getInt("MaNguoiDung" , 0);
        DataClient client = APIUtils.getData();
        BanBe banBe = new BanBe();
        banBe.setMaNguoiDung_Mot(MaNguoiDung);
        Call<Message> call = client.GetListFriend(banBe);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        for (NguoiDung user : response.body().getDanhsach()){
                            if (user.isStatus()){
                                ListFriend.add(user);
                            }
                        }

                        UpdateNewListFriendToLocal();
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }

    //Cập Nhật Danh Sách Bạn Bè Mới Nhất
    protected void UpdateNewListFriendToLocal(){
        SharedPreferences preferences = getSharedPreferences("data_danh_ba", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(ListFriend);
        editor.putString("ListUser", json);
        editor.commit();
    }
}