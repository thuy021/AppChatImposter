package com.example.appchat.Adapter;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.R;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private ItemClickListener itemClickListener;
    TextView nameUser;
    Button btnChapNhanAdd;
    ImageView call, videocall;

    public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        itemView.setOnClickListener(this);
        nameUser = (TextView) itemView.findViewById(R.id.nameUser);
        btnChapNhanAdd = (Button) itemView.findViewById(R.id.btnChapNhanBanBe);
        call = (ImageView) itemView.findViewById(R.id.call);
        videocall = (ImageView) itemView.findViewById(R.id.videocall);
        btnChapNhanAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vt = getAdapterPosition();
                listener.onItemClick(vt);
            }
        });
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
