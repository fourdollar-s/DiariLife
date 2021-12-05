package com.example.testfordate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import DataBase.gmapspot.GmapDatabase;
import DataBase.gmapspot.Gmapspot;


public class markerinformation extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mapmarkerclickinfo,null);
        builder.setView(view);
        TextView title1 = view.findViewById(R.id.title);
        TextView info = view.findViewById(R.id.info);
        TextView spot = view.findViewById(R.id.spot);
        TextView date = view.findViewById(R.id.date);
        Bundle bundle = getArguments();
        double latitude = bundle.getDouble("latitude");
        double longitude = bundle.getDouble("longitude");
        String title = bundle.getString("title");
        GmapDatabase gmapDatabase = GmapDatabase.getInstance(this.getContext());
        int dbcount = gmapDatabase.GmapspotDao().countdb();
        String query_title;
        double query_latitude,query_longitude;
        for(int i=1; i <= dbcount; i++){
            query_title = gmapDatabase.GmapspotDao().gettitle(i);
            query_latitude = gmapDatabase.GmapspotDao().getlatitude(i);
            query_longitude = gmapDatabase.GmapspotDao().getlongitude(i);
            if(query_title.equals(title) && query_latitude==latitude && query_longitude==longitude){
                title1.setText(" 標題 : "+gmapDatabase.GmapspotDao().gettitle(i));
                info.setText(" "+gmapDatabase.GmapspotDao().getinfo(i));
                spot.setText(" 地點 : "+gmapDatabase.GmapspotDao().getspot(i));
                date.setText(" 日期 : "+gmapDatabase.GmapspotDao().getdate(i));
                //Log.i("marker_info",gmapDatabase.GmapspotDao().getinfo(i));
            }
        }
        return builder.create();
    }
}
