package com.example.appchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.appchat.Adapter.Adapter_Nhom;
import com.example.appchat.Models.BanBe;
import com.example.appchat.Models.Message;
import com.example.appchat.Models.MessageNhom;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Models.Nhom;
import com.example.appchat.Models.ThanhVien;
import com.example.appchat.Retrofit2.APIUtils;
import com.example.appchat.Retrofit2.DataClient;
import com.example.appchat.Socket.SocketChat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NhomFragment extends Fragment {
    View view;
    Button btnTaoNhom;
    RecyclerView recycleDanhSachNhom;
    SwipeRefreshLayout refresh_nhom;
    SharedPreferences preferences;
    ArrayList<Nhom> lstNhom = new ArrayList<>();
    Adapter_Nhom adapter;
    LinearLayoutManager layoutManager;
    SocketChat mSocket;
    ProgressBar progressBarNhom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nhom, container, false);
        init_Data();
        taoNhom_Click();
        GetListGroup();
        SwipeRefreshLayout();
        return view;
    }

    protected void init_Data() {
        mSocket = new SocketChat();
        preferences = getContext().getSharedPreferences("data_dang_nhap", Context.MODE_PRIVATE);
        btnTaoNhom = view.findViewById(R.id.btnTaoNhom);
        recycleDanhSachNhom = view.findViewById(R.id.recycleDanhSachNhom);
        recycleDanhSachNhom.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recycleDanhSachNhom.setLayoutManager(layoutManager);
        refresh_nhom = view.findViewById(R.id.refresh_nhom);
        progressBarNhom = view.findViewById(R.id.progressBarNhom);
    }

    private void GetListGroup() {
        int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
        DataClient client = APIUtils.getData();
        Call<MessageNhom> call = client.GetListGroup(MaNguoiDung);
        call.enqueue(new Callback<MessageNhom>() {
            @Override
            public void onResponse(Call<MessageNhom> call, Response<MessageNhom> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        lstNhom = response.body().getDataGroup();
                        ShowDanhSachNhom();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageNhom> call, Throwable t) {

            }
        });
    }

    private void ShowDanhSachNhom() {
        adapter = new Adapter_Nhom(getContext(), lstNhom);
        recycleDanhSachNhom.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected void SwipeRefreshLayout() {
        refresh_nhom.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBarNhom.setVisibility(View.VISIBLE);
                int MaNguoiDung = preferences.getInt("MaNguoiDung", 0);
                DataClient client = APIUtils.getData();
                Call<MessageNhom> call = client.GetListGroup(MaNguoiDung);
                call.enqueue(new Callback<MessageNhom>() {
                    @Override
                    public void onResponse(Call<MessageNhom> call, Response<MessageNhom> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess() == 1) {
                                lstNhom = response.body().getDataGroup();
                                progressBarNhom.setVisibility(View.GONE);
                                ShowDanhSachNhom();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageNhom> call, Throwable t) {
                        progressBarNhom.setVisibility(View.GONE);
                    }
                });
                refresh_nhom.setRefreshing(false);
            }
        });
    }

    protected void taoNhom_Click() {
        btnTaoNhom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TaoNhomActivity.class);
                startActivity(intent);
            }
        });
    }
}
