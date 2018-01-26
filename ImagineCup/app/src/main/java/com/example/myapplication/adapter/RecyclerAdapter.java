package com.example.myapplication.adapter;

/**
 * Created by kimtaegyun on 2018. 1. 24..
 */


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.GraphActivity;
import com.example.myapplication.activity.SleepRecordActivity;
import com.example.myapplication.data.Item;
import com.example.myapplication.function.BackgroundService;
import com.example.myapplication.function.WebViewStreaming;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Item> items;
    int item_layout;

    public RecyclerAdapter(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = items.get(position);
        Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
        holder.image.setBackground(drawable);
        //holder.title.setText(item.getTitle());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        context.stopService(new Intent(context, BackgroundService.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        context.startService(new Intent(context, BackgroundService.class).putExtra("message", "send").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case 1:
                        context.startActivity(new Intent(context, SleepRecordActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // 수면기록 화면
                        break;
                    case 2:
                        context.startActivity(new Intent(context, GraphActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // 그래프 화면
                        break;
                    case 3:
                        context.startActivity(new Intent(context, WebViewStreaming.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); // 웹뷰 테스트용
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}