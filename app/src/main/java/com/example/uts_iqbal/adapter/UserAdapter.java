package com.example.uts_iqbal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uts_iqbal.R;
import com.example.uts_iqbal.model.User;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private List<User> list;
    Context context;
    Dialog dialog;

    public interface Dialog {
        void onClick(int pos);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public UserAdapter(Context context, List<User>list) {
        this.list = list;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
//        return new MyViewHolder(itemView);
        View v = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.user, null);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//
//        holder.name.setText(list.get(position).getName());
//        holder.desc.setText(list.get(position).getDesc());
        User item = list.get(position);
        holder.name.setText(item.getName());
        holder.desc.setText(item.getDesc());
        Glide.with(holder.avatar.getContext()).load(item.getAvatar()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, desc;
        public ImageView avatar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            avatar = itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.onClick(getLayoutPosition());
                }
            });
        }

    }
}
