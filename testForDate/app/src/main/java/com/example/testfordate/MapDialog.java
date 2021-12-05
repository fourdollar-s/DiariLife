package com.example.testfordate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import DataBase.gmapspot.GmapDatabase;
import DataBase.gmapspot.Gmapspot;


public class MapDialog extends AppCompatDialogFragment {
    private EditText text_title;
    private EditText text_spot;
    private EditText text_info;
    private Button btn_save;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mapmarkerinfo,null);
        builder.setView(view);
        text_title = view.findViewById(R.id.text_title);
        text_info = view.findViewById(R.id.text_info);
        text_spot = view.findViewById(R.id.text_spot);
        btn_save = view.findViewById(R.id.btn_save);
        double latitude,longitude;
        Bundle bundle = getArguments();
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        Log.i("send_latitude: ", String.valueOf(latitude));
        Log.i("send_longitude: ", String.valueOf(longitude));
        GmapDatabase gmapDatabase = GmapDatabase.getInstance(this.getContext());
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = text_title.getText().toString();
                String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String info = text_info.getText().toString();
                String spot = text_spot.getText().toString();
                Gmapspot gmapspot = new Gmapspot(latitude, longitude, title, nowDate, info, spot);
                gmapDatabase.GmapspotDao().insertGmapspot(gmapspot);
                dismiss();
            }
        });
        return builder.create();
    }
}
