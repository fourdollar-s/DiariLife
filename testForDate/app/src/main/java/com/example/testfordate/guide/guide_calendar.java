package com.example.testfordate.guide;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.testfordate.R;
import com.example.testfordate.calendar;

import java.util.ArrayList;

public class guide_calendar extends AppCompatActivity {
    private ViewPager viewPager;
    private ArrayList<View> viewPager_List;
    private RadioGroup radioGroup;
    ImageButton end_button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guidepg_calendar);

        //---viewpager宣告---
        viewPager = findViewById(R.id.viewpager_cd);
        //---下方分頁按鈕宣告---
        radioGroup = findViewById(R.id.radioGroup);
        //---宣告layoutInflater還有分頁---
        final LayoutInflater mInflater = getLayoutInflater().from(this);
        View viewPagerPage1 = mInflater.inflate(R.layout.guide_cd1,null);
        View viewPagerPage2 = mInflater.inflate(R.layout.guide_cd2,null);
        View viewPagerPage3 = mInflater.inflate(R.layout.guide_cd3,null);
        //---把分頁加進去viewpagerlist---
        viewPager_List = new ArrayList<View>();
        viewPager_List.add(viewPagerPage1);
        viewPager_List.add(viewPagerPage2);
        viewPager_List.add(viewPagerPage3);
        viewPager.setAdapter(new MyViewPagerAdapter(viewPager_List));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(listener);
        //---結束按鈕宣告---
        end_button = (ImageButton) findViewById(R.id.end_button);
        //---結束按鈕動作---
        end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_guide = new Intent();
                intent_guide.setClass(guide_calendar.this, calendar.class);
                startActivity(intent_guide);
            }
        });
    }
    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    radioGroup.check(R.id.radioButton_1);
                    break;
                case  1:
                    radioGroup.check(R.id.radioButton_2);
                    break;
                case 2:
                    radioGroup.check(R.id.radioButton_3);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };
}
