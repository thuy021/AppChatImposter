package com.example.appchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Models.NguoiDung;
import com.example.appchat.R;

import java.util.ArrayList;

public class Adapter_Create_Nhom extends RecyclerView.Adapter<Adapter_Create_Nhom.MyViewHolderNhom> {
    Context context;
    ArrayList<NguoiDung> itemNguoiDung;
    boolean statusBanBe;
    private static OnMultiClickCheckBoxListener mListener;

    public void setOnMultiClickCheckBoxListener(OnMultiClickCheckBoxListener onListener) {
        this.mListener = onListener;
    }

    public Adapter_Create_Nhom(Context context, ArrayList<NguoiDung> lstNguoiDung, Boolean statusBanBe) {
        this.context = context;
        this.itemNguoiDung = lstNguoiDung;
        this.statusBanBe = statusBanBe;
    }

    @NonNull
    @Override
    public MyViewHolderNhom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_recycle_thembanchat, parent, false);
        return new MyViewHolderNhom(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderNhom holder, int position) {
        holder.nameUser_chatNhom.setText(itemNguoiDung.get(position).getHoTen());
    }

    @Override
    public int getItemCount() {
        return itemNguoiDung.size();
    }

    public class MyViewHolderNhom extends RecyclerView.ViewHolder {

        CheckBox checkBoxThemBan;
        TextView nameUser_chatNhom;
        ImageView item_avatar_nhom;

        public MyViewHolderNhom(@NonNull View itemView) {
            super(itemView);
            checkBoxThemBan = itemView.findViewById(R.id.checkBox_themBanVaoNhom);
            nameUser_chatNhom = itemView.findViewById(R.id.nameUser_chatNhom);
            item_avatar_nhom = itemView.findViewById(R.id.item_avatar_nhom);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBoxThemBan.isChecked()) {
                        checkBoxThemBan.setChecked(false);
                    } else {
                        checkBoxThemBan.setChecked(true);
                    }
                    mListener.onItemClicked(checkBoxThemBan.isChecked(), getLayoutPosition());
                }
            });

            checkBoxThemBan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemCheckBoxChecked(((CheckBox) view).isChecked(), getLayoutPosition());
                }
            });
        }
    }
}
