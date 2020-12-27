package com.example.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Models.NguoiDung;
import com.example.appchat.Models.Nhom;
import com.example.appchat.NhanTinNhomActivity;
import com.example.appchat.R;

import java.util.ArrayList;

public class Adapter_Nhom extends RecyclerView.Adapter<Adapter_Nhom.MyViewHolderNhom> {

    ArrayList<Nhom> lstNhom;
    Context context;

    public Adapter_Nhom(Context context, ArrayList<Nhom> lst){
        this.context = context;
        this.lstNhom = lst;
    }

    @NonNull
    @Override
    public Adapter_Nhom.MyViewHolderNhom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new MyViewHolderNhom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Nhom.MyViewHolderNhom holder, int position) {
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), NhanTinNhomActivity.class);
                String id_nhom = lstNhom.get(position).getMaNhom();
                String ten_nhom = lstNhom.get(position).getTenNhom();
                intent.putExtra("ten_nhom", ten_nhom);
                intent.putExtra("id_nhom", id_nhom);
                view.getContext().startActivity(intent);
            }
        });
        holder.nameNhom.setText(lstNhom.get(position).getTenNhom());
    }

    @Override
    public int getItemCount() {
        return lstNhom.size();
    }

    public class MyViewHolderNhom extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemClickListener itemClickListener;
        TextView nameNhom;

        public MyViewHolderNhom(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            nameNhom = itemView.findViewById(R.id.nameNhom);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }
    }
}
