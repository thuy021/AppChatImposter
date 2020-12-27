package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Models.NguoiDung;
import com.example.appchat.NhanTinDonActivity;
import com.example.appchat.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    ArrayList<NguoiDung> itemNguoiDung;
    boolean statusBanBe;
    SharedPreferences preferences;
    OnItemClickListener mListener;
    int loiMoi = -1;

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public MyAdapter (Context context, ArrayList<NguoiDung> lstNguoDung, Boolean statusBanBe, int loiMoi){
        preferences = context.getSharedPreferences("data_dang_nhap", Context.MODE_PRIVATE);
        this.context = context;
        this.itemNguoiDung = lstNguoDung;
        this.statusBanBe = statusBanBe;
        this.loiMoi = loiMoi;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_recycle, parent, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(!statusBanBe){
                    String sdt = itemNguoiDung.get(position).getSoDienThoai();
                    String ten = itemNguoiDung.get(position).getHoTen();
                    int id_user = itemNguoiDung.get(position).getMaNguoiDung();

                    Intent intent = new Intent(view.getContext(),NhanTinDonActivity.class);

                    intent.putExtra("sdt",sdt);
                    intent.putExtra("ten",ten);
                    intent.putExtra("id_user", id_user);
                    view.getContext().startActivity(intent);
                }
            }
        });
        if (loiMoi == 1){
            holder.btnChapNhanAdd.setText("Kết bạn");
            holder.btnChapNhanAdd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        }
        holder.nameUser.setText(itemNguoiDung.get(position).getHoTen());
        if (statusBanBe){
            holder.btnChapNhanAdd.setVisibility(View.VISIBLE);
        }
        else {
            holder.call.setVisibility(View.VISIBLE);
            holder.videocall.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return itemNguoiDung.size();
    }
}
