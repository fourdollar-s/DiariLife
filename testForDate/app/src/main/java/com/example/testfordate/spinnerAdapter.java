package com.example.testfordate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


//下拉式選單的adpter
public class spinnerAdapter  extends ArrayAdapter<spinner_item_weather> {
    private Context context;
    private int spos=0;
    public spinnerAdapter(Context context, ArrayList<spinner_item_weather> PlanetList) {
        super(context, 0, PlanetList);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        spos = position;
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.myspinner, parent, false);
        ImageView imageViewFlag = convertView.findViewById(R.id.sunny);
        //TextView textViewName = convertView.findViewById(R.id.text1);
        spinner_item_weather currentItem = getItem(position);
        if (currentItem != null) {
            imageViewFlag.setImageResource(currentItem.getWeatherImage());
            //textViewName.setText(currentItem.getPlanetName());
            //if (position == spos)
            //{textViewName.setTextColor  (Color.argb(255, 255, 255, 255));}
        }
        return convertView;
    }
}
