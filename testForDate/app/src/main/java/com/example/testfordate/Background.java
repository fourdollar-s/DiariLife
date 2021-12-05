package com.example.testfordate;

import android.content.Context;
import android.widget.ImageView;

public class Background {
    ImageView imageView;

    public Background(Context context){
        imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.background);
    }

    public ImageView getImageView(){
        return imageView;
    }
}
