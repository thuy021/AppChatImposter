package com.example.appchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.appchat.Adapter.MyAdapter;
import com.example.appchat.Helper.MyButton;
import com.example.appchat.Helper.MyButtonClickListener;
import com.example.appchat.Helper.SwipeHelper;
import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.example.appchat.Socket.SocketClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class DanhBaFragment extends Fragment {

    ArrayList<NguoiDung> lstUser = new ArrayList<>();
    View view;
    Activity myActivity;
    RecyclerView recyclerView;
    MyAdapter adapter;
    LinearLayoutManager layoutManager;
    SharedPreferences preferences;
    Button btnLoiMoiKetBan;
    SwipeRefreshLayout refreshLayout;
    ProgressBar progressBar;
    SocketClient mClient;

    public void get(){
        Log.e("danhba", "1");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_danh_ba, container, false);
        mClient = new SocketClient();
        myActivity = getActivity();

        Init_Socket();

        //Khởi Tạo Dữ Liệu
        Init();

        SharedPreferences preferences = myActivity.getSharedPreferences("data_danh_ba", MODE_PRIVATE);
        String ListUser = preferences.getString("ListUser", "");

        SwipeHelper();
        if (ListUser.equals("")) {
            GetDanhSachBan();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NguoiDung>>() {
            }.getType();
            lstUser = gson.fromJson(ListUser, type);

            adapter = new MyAdapter(myActivity, lstUser, false, 0);
            recyclerView.setAdapter(adapter);
        }

        //Nhận Thông Báo Cập Nhật Danh Bạ
        mClient.getmClient().on("ThongBaoXacNhanLoiMoiKetBan", isCoNguoiXacNhanKetBan);

        //Nhận Thông Báo Xoá Danh Bạ
        mClient.getmClient().on("ThongBaoXoaDanhBa", ThongBaoXoaDanhBaListener);

        LoiMoiKetBan_Click();
        SwipeRefreshLayout();

        return view;
    }

    private void Init_Socket() {
        SharedPreferences preferences = myActivity.getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        String SoDienThoai = preferences.getString("SoDienThoai", "");
        JSONObject object = new JSONObject();

        try {
            object.put("SoDienThoai", SoDienThoai);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mClient.getmClient().emit("DangKyNhanThongBaoDanhBa", object);
    }

    private final Emitter.Listener isCoNguoiXacNhanKetBan = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean isCapNhat = false;
                    JSONArray dsNguoiDung = new JSONArray();
                    JSONObject object = (JSONObject) args[0];

                    try {
                        isCapNhat = object.getBoolean("success");
                        dsNguoiDung = object.getJSONArray("danhsach");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<NguoiDung> temp = new ArrayList<>();
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<NguoiDung>>() {
                    }.getType();
                    temp = gson.fromJson(String.valueOf(dsNguoiDung), type);

                    if (isCapNhat) {
                        for (NguoiDung nd : temp) {
                            if (!lstUser.contains(nd)) {
                                lstUser.add(nd);
                            }
                        }
                        if (lstUser.size() != 1)
                            adapter.notifyDataSetChanged();
                        else
                            ShowDanhSach();
                    }
                    CapNhatDanhSachLocal(lstUser);
                }
            });
        }
    };

    private final Emitter.Listener ThongBaoXoaDanhBaListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            myActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean isCapNhat = false;
                    String SDT = "";
                    JSONObject object = (JSONObject) args[0];

                    try {
                        isCapNhat = object.getBoolean("success");
                        SDT = object.getString("SoDienThoai_Update");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (isCapNhat) {
                        Xoa_ThongTinNguoiDungLocal(SDT);
                    }
                }
            });
        }
    };

    protected void Init() {
        preferences = myActivity.getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleDanhSachBan);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(myActivity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        btnLoiMoiKetBan = (Button) view.findViewById(R.id.btnLoiMoiKetBan);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_danhba);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    protected void SwipeRefreshLayout() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
                DataClient client = APIUtils.getData();
                BanBe banBe = new BanBe();
                banBe.setMaNguoiDung_Mot(MaNguoiDung);
                Call<Message> call = client.GetListFriend(banBe);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            if (response.body().getSuccess() == 1) {
                                for (NguoiDung user : response.body().getDanhsach()) {
                                    if (user.isStatus()) {
                                        if (!lstUser.contains(user)) {
                                            lstUser.add(user);
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                CapNhatDanhSachLocal(lstUser);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
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
                                if (!lstUser.contains(user)) {
                                    lstUser.add(user);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        CapNhatDanhSachLocal(lstUser);
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
        super.onResume();
    }

    protected void LoiMoiKetBan_Click() {
        btnLoiMoiKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myActivity, DanhSachLoiMoiKetBanActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void SwipeHelper() {
        SwipeHelper swipeHelper = new SwipeHelper(view.getContext(), recyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                MyButton temp = new MyButton(view.getContext(), "", 0, R.drawable.ic_baseline_delete_24, Color.parseColor("#FF3c30"), new MyButtonClickListener() {
                    @Override
                    public void onClick(int pos) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                        dialog.setTitle("Thông báo");
                        dialog.setMessage("Bạn có muốn xoá hay không?");

                        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
                                DataClient client = APIUtils.getData();
                                BanBe banBe = new BanBe();
                                banBe.setMaNguoiDung_Mot(MaNguoiDung);
                                banBe.setMaNguoiDung_Hai(lstUser.get(pos).getMaNguoiDung());
                                Call<Message> call = client.DeleteRequestFriend(banBe);
                                call.enqueue(new Callback<Message>() {
                                    @Override
                                    public void onResponse(Call<Message> call, Response<Message> response) {
                                        if (response.isSuccessful()) {
                                            if (response.body().getSuccess() == 1) {
                                                JSONObject object = new JSONObject();

                                                try {
                                                    object.put("SoDienThoai_Xoa", lstUser.get(pos).getSoDienThoai());
                                                    object.put("SoDienThoai_Update", preferences.getString("SoDienThoai", ""));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                mClient.getmClient().emit("XoaDanhBa", object);
                                                Toast.makeText(view.getContext(), "Xoá thành công", Toast.LENGTH_LONG).show();

                                                lstUser.remove(pos);
                                                adapter.notifyDataSetChanged();
                                                CapNhatDanhSachLocal(lstUser);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Message> call, Throwable t) {

                                    }
                                });
                            }
                        });

                        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ShowDanhSach();
                            }
                        });

                        AlertDialog alertDialog = dialog.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
                buffer.add(temp);
            }
        };
    }

    protected void ShowDanhSach() {
        SharedPreferences preferences = myActivity.getSharedPreferences("data_danh_ba", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(lstUser);
        editor.putString("ListUser", json);
        editor.commit();

        adapter = new MyAdapter(view.getContext(), lstUser, false, 0);
        recyclerView.setAdapter(adapter);
    }

    protected void GetDanhSachBan() {
        progressBar.setVisibility(View.VISIBLE);
        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
        DataClient client = APIUtils.getData();
        BanBe banBe = new BanBe();
        banBe.setMaNguoiDung_Mot(MaNguoiDung);
        Call<Message> call = client.GetListFriend(banBe);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void CapNhatDanhSachLocal(ArrayList<NguoiDung> lst) {
        SharedPreferences preferences = myActivity.getSharedPreferences("data_danh_ba", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        editor.putString("ListUser", json);
        editor.commit();
    }



    private void Xoa_ThongTinNguoiDungLocal(String SDT) {
        String temp = preferences.getString("Token_DangNhap", "");
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", ("Bearer " + temp).trim());

        DataClient client = APIUtils.getData();
        Call<Message> call = client.GetThongTinNguoiDung_bySDT(SDT, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        Toast.makeText(myActivity, "Đang Đồng Bộ...", Toast.LENGTH_LONG).show();
                        NguoiDung nguoi_dung_infor = response.body().getData();

                        lstUser.remove(nguoi_dung_infor);
                        adapter.notifyDataSetChanged();

                        CapNhatDanhSachLocal(lstUser);
                    }
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }
}