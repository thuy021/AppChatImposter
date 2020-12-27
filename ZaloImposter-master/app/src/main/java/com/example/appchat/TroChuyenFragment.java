package com.example.appchat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.appchat.Adapter.ChatAdapter;
import com.example.appchat.Adapter.ItemClickListener;
import com.example.appchat.Adapter.MyAdapter;
import com.example.appchat.Models.ConversationMap;
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

public class TroChuyenFragment extends Fragment {
    SharedPreferences preferences_contact;
    SharedPreferences preferences_login;
    SharedPreferences preferences_chatting;

    int id_user = 0;
    String token = "";

    View view;
    RecyclerView rv_Chat;
    Activity MyActivity;
    ChatAdapter adapter;

    SwipeRefreshLayout swipeRefreshChattings;

    ArrayList<ConversationMap> Conversations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tro_chuyen, container, false);

        MyActivity = getActivity();
        Init_Data();
        RefreshChattings();

        GetAllConversations();
        return view;
    }

    private void RefreshChattings() {
        swipeRefreshChattings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetAllConversations();
            }
        });
    }

    private void Init_Data() {
        preferences_contact = MyActivity.getSharedPreferences("data_danh_ba", MODE_PRIVATE);
        preferences_chatting = MyActivity.getSharedPreferences("data_conversations", MODE_PRIVATE);
        preferences_login = MyActivity.getSharedPreferences("data_dang_nhap", MODE_PRIVATE);
        id_user = preferences_login.getInt("MaNguoiDung", 0);
        token = preferences_login.getString("Token_DangNhap", "");

        swipeRefreshChattings = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshChattings);

        rv_Chat = (RecyclerView) view.findViewById(R.id.rv_Conversation);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyActivity, RecyclerView.VERTICAL, false);
        rv_Chat.setLayoutManager(layoutManager);
    }

    //Lưu Dữ Liệu Xuống Local
    private void PushDataToLocal() {
        SharedPreferences.Editor editor = preferences_chatting.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Conversations);

        editor.putString("Conversations", json);
        editor.commit();
    }

    //Load Danh Sách Chat Lên RecylerViews
    private void Load_Data_To_RecylerView(ArrayList<ConversationMap> temp) {
        adapter = new ChatAdapter(temp);
        rv_Chat.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Tắt Hiệu Ứng Refresh Danh Sách Chat
        swipeRefreshChattings.setRefreshing(false);
    }

    //Call API Lấy Danh Sách Chat Của Người Dùng Hiện Tại
    private void GetAllConversations() {
        DataClient dataClient = APIUtils.getData();
        Call<ArrayList<ConversationMap>> call = dataClient.GetAllConversationFor_A_User(id_user);
        call.enqueue(new Callback<ArrayList<ConversationMap>>() {
            @Override
            public void onResponse(Call<ArrayList<ConversationMap>> call, Response<ArrayList<ConversationMap>> response) {
                if (response.isSuccessful()) {
                    Conversations = response.body();
                    PutDataUserToChattingList(Conversations);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ConversationMap>> call, Throwable t) {

            }
        });
    }

    //Set Thông Tin Người Nhận Tin Nhắn Cho 1 Cuộc Trò Chuyện
    private void PutDataUserToChattingList(ArrayList<ConversationMap> temp) {
        String ListUser = preferences_contact.getString("ListUser", "");
        if (!ListUser.equals("")){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NguoiDung>>() {}.getType();
            ArrayList<NguoiDung> ListFriend = gson.fromJson(ListUser, type);
            for (ConversationMap cm : temp) {
                if(id_user == cm.getIdUser1()){
                    for (NguoiDung nd : ListFriend) {
                        if(nd.getMaNguoiDung() == cm.getIdUser2()){
                            cm.setSender(nd);
                        }
                    }
                }
                if(id_user == cm.getIdUser2()){
                    for (NguoiDung nd : ListFriend) {
                        if(nd.getMaNguoiDung() == cm.getIdUser1()){
                            cm.setSender(nd);
                        }
                    }
                }
            }
            ArrayList<ConversationMap> list = new ArrayList<>();
            for (ConversationMap map : temp){
                if (map.getSender() != null){
                    list.add(map);
                }
            }

            Conversations = list;

            PushDataToLocal();
            Load_Data_To_RecylerView(Conversations);
        }
    }
}
