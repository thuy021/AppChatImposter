package com.example.appchat.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Models.ConversationMap;
import com.example.appchat.Models.NguoiDung;
import com.example.appchat.NhanTinDonActivity;
import com.example.appchat.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private ArrayList<ConversationMap> Chats;

    public ChatAdapter(ArrayList<ConversationMap> chats) {
        this.Chats = chats;
    }

    private OnChatItemListener mListener;

    public interface OnChatItemListener {
        void onChatItemClick(View v, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle, parent, false);
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (Chats.get(position).getSender() != null) {
            holder.container_recylerview_item_user.setVisibility(View.VISIBLE);
            holder.tvTenNguoiNhan.setText(Chats.get(position).getSender().getHoTen());
            holder.setOnChatItemListener(new OnChatItemListener() {
                @Override
                public void onChatItemClick(View v, int position) {
                    NguoiDung nd = Chats.get(position).getSender();
                    Intent intent = new Intent(v.getContext(), NhanTinDonActivity.class);
                    intent.putExtra("sdt", nd.getSoDienThoai());
                    intent.putExtra("ten", nd.getHoTen());
                    intent.putExtra("id_user", nd.getMaNguoiDung());
                    v.getContext().startActivity(intent);
                }
            });
        } else {
            holder.container_recylerview_item_user.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return Chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTenNguoiNhan;
        CardView container_recylerview_item_user;
        OnChatItemListener onChatItemListener;

        public ViewHolder(@NonNull View itemView, OnChatItemListener listener) {
            super(itemView);
            tvTenNguoiNhan = (TextView) itemView.findViewById(R.id.nameUser);
            container_recylerview_item_user = (CardView) itemView.findViewById(R.id.container_recylerview_item_user);
            this.onChatItemListener = listener;
            itemView.setOnClickListener(this::onClick);
        }

        public void setOnChatItemListener(OnChatItemListener listener) {
            this.onChatItemListener = listener;
        }

        @Override
        public void onClick(View v) {
            this.onChatItemListener.onChatItemClick(v, getAdapterPosition());
        }
    }
}