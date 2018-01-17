package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.myapplication.R;

/**
 * Created by Yookmoonsu on 2018-01-14.
 */

public class FunctionAdapter extends BaseAdapter {
    private Context context;
    private int[] image;

    public FunctionAdapter(Context context, int[] image) {
        this.context = context;
        this.image = image;
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public Object getItem(int position) {
        return image[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;

        if (gridView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.gridview_function, null);

            CustomViewHolder holder = new CustomViewHolder();
            holder.imageView = (ImageView) gridView.findViewById(R.id.functionImageView);
            holder.imageView.setImageResource(image[position]);
        }

        return gridView;
    }

    public class CustomViewHolder {
        public ImageView imageView;
    }
}