package com.example.testfordate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import DataBase.Person.DiaryDatabase;

import static java.lang.Math.round;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Diagram extends AppCompatActivity {

    private LineChart lineChart;
    private int year;
    private int month;
    private int selectYear;
    private int selectMonth;
    private TextView dateTxt;
    private String query;
    DiaryDatabase database;
    chartMarkerView chartMV;
    private Button back;
    //check box
    private CheckBox happyBox;
    private CheckBox notbadBox;
    private CheckBox angryBox;
    private CheckBox sadBox;

    //chart參數
    LineDataSet happyDataSet;
    LineDataSet notbadDataSet;
    LineDataSet angryDataSet;
    LineDataSet sadDataSet;

    //日期選擇按鈕
    private Button pickDate;
    //取得系統時間
    Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    //x軸與y軸縮放參數
    private float scaleX;
    private float scaleY;
    private ImageButton increase;
    private ImageButton decrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //設為全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.diagram);//畫面的layout
        database = Room.databaseBuilder(this, DiaryDatabase .class,"diary").allowMainThreadQueries().build();

        /**設置放大與縮小的按鈕*/
        increase=findViewById(R.id.increaseScale);
        decrease=findViewById(R.id.decreaseScale);

        /**從history頁面取得選擇的月份與年份*/
        Intent getParam = this.getIntent();
        year = getParam.getIntExtra("year", 0);
        month = getParam.getIntExtra("month", 0);

        /**設置返回history頁面按鈕*/
        back=findViewById(R.id.DiagramBackBtn);
        back.setOnClickListener(backToHistory);

        /**設置日期選擇按鈕*/
        pickDate=findViewById(R.id.pickDateBTN);
        pickDate.setOnClickListener(testDate);

        /**設置check box*/
        happyBox=findViewById(R.id.checkBoxHappy);
        notbadBox=findViewById(R.id.checkBoxNotbad);
        angryBox=findViewById(R.id.checkBoxAngry);
        sadBox=findViewById(R.id.checkBoxSad);
        happyBox.setChecked(true);//初始狀態設為勾選
        notbadBox.setChecked(true);
        angryBox.setChecked(true);
        sadBox.setChecked(true);
        happyBox.setOnCheckedChangeListener(check);//設置監聽器
        notbadBox.setOnCheckedChangeListener(check);
        angryBox.setOnCheckedChangeListener(check);
        sadBox.setOnCheckedChangeListener(check);

        /**設置畫面上的日期提示文字*/
        dateTxt=findViewById(R.id.yearMonth);

        //有日期再繪製圖表
        if (year != 0 && month != 0) {
            dateTxt.setText(year + "年" + month + "月");//在圖表上方顯示使用者選擇的年份與月份
            lineChart = findViewById(R.id.lineChart);//實例化圖表
            query = year + "/" + month + "/";//用於資料庫查詢(指定日期條件)
            /**設置markerview*/
            chartMV = new chartMarkerView(this, R.layout.markerview, query);
            chartMV.setChartView(lineChart);
            lineChart.setMarker(chartMV);


            float sosoCount = 0;
            float happyCount = 0;
            float angryCount = 0;
            float sadCount = 0;
            float sum = 0;

            /**設置折線圖屬性*/
            lineChart.setDragEnabled(true);//允許拖拽
            lineChart.setScaleEnabled(true);//允許縮放(x & y)
            lineChart.setTouchEnabled(true);//允許觸摸
            //lineChart.setScaleXEnabled(true);
            //lineChart.setScaleYEnabled(true);
            lineChart.setDoubleTapToZoomEnabled(false);//禁止透過雙擊放大圖表
            scaleX=lineChart.getScaleX();
            scaleY=lineChart.getScaleY();
            increase.setOnClickListener(increaseScaleXY);
            decrease.setOnClickListener(decreaseScaleXY);

            /**設定y軸樣式*/
            YAxis rightAxis = lineChart.getAxisRight();//取得折線圖右邊縱軸
            YAxis leftAxis = lineChart.getAxisLeft();//取得折線圖左邊縱軸
            rightAxis.setEnabled(false);//禁用右邊縱軸
            leftAxis.setEnabled(true);//禁用左邊縱軸
            //leftAxis.setDrawLimitLinesBehindData(true);
            leftAxis.setTextSize(15f);
            leftAxis.setAxisMinimum(0f);

            /**設置x軸樣式*/
            XAxis xAxis = lineChart.getXAxis();//取得橫軸
            xAxis.setTextSize(11f);
            xAxis.setAxisMinimum(0f);
            xAxis.setDrawAxisLine(true);//繪製軸線
            xAxis.setDrawGridLines(false);
            xAxis.setDrawLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            //xAxis.setDrawLimitLinesBehindData(true);
            xAxis.setTextSize(15f);

            /**設置x軸與y軸的點*/
            ArrayList<Entry> happyEntries = new ArrayList<>();//開心標籤占比
            ArrayList<Entry> notbadEntries = new ArrayList<>();//普通標籤占比
            ArrayList<Entry> angryEntries = new ArrayList<>();//生氣標籤占比
            ArrayList<Entry> sadEntries = new ArrayList<>();//難過標籤占比

            String tempQuery;
            if (month == 2 && ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))) {
                for (int i = 0; i < 29; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + round(i + 1);
                    } else {
                        tempQuery = query + "" + round(i + 1);
                    }

                    //取得各情緒標籤數量
                    sosoCount = database.getDiaryDao().countEmoNotBad(tempQuery);
                    happyCount = database.getDiaryDao().countEmoHappy(tempQuery);
                    angryCount = database.getDiaryDao().countEmoAngry(tempQuery);
                    sadCount = database.getDiaryDao().countEmoSad(tempQuery);
                    sum = sosoCount + happyCount + angryCount + sadCount;

                    if (sum != 0) {
                        happyEntries.add(new Entry(i, (happyCount / sum) * 100));
                        notbadEntries.add(new Entry(i, (sosoCount / sum) * 100));
                        angryEntries.add(new Entry(i, (angryCount / sum) * 100));
                        sadEntries.add(new Entry(i, (sadCount / sum) * 100));
                    } else {
                        happyEntries.add(new Entry(i, 0));
                        notbadEntries.add(new Entry(i, 0));
                        angryEntries.add(new Entry(i, 0));
                        sadEntries.add(new Entry(i, 0));
                    }

                    /**此處應是直接給予1~12月一個隨機的整數值，用以呈現在折線圖上*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }

            }//閏年二月
            else if (month == 2 && ((year % 4 != 0) || (year % 100 == 0 && year % 400 != 0))) {
                for (int i = 0; i < 28; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + round(i + 1);
                    } else {
                        tempQuery = query + "" + round(i + 1);
                    }
                    //取得各情緒標籤數量
                    sosoCount = database.getDiaryDao().countEmoNotBad(tempQuery);
                    happyCount = database.getDiaryDao().countEmoHappy(tempQuery);
                    angryCount = database.getDiaryDao().countEmoAngry(tempQuery);
                    sadCount = database.getDiaryDao().countEmoSad(tempQuery);
                    sum = sosoCount + happyCount + angryCount + sadCount;

                    if (sum != 0) {
                        happyEntries.add(new Entry(i, (happyCount / sum) * 100));
                        notbadEntries.add(new Entry(i, (sosoCount / sum) * 100));
                        angryEntries.add(new Entry(i, (angryCount / sum) * 100));
                        sadEntries.add(new Entry(i, (sadCount / sum) * 100));
                    } else {
                        happyEntries.add(new Entry(i, 0));
                        notbadEntries.add(new Entry(i, 0));
                        angryEntries.add(new Entry(i, 0));
                        sadEntries.add(new Entry(i, 0));
                    }

                    /**此處應是直接給予1~12月一個隨機的整數值，用以呈現在折線圖上*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }
            }//平年二月
            else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                for (int i = 0; i < 31; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + round(i + 1);
                    } else {
                        tempQuery = query + "" + round(i + 1);
                    }
                    //取得各情緒標籤數量
                    sosoCount = database.getDiaryDao().countEmoNotBad(tempQuery);
                    happyCount = database.getDiaryDao().countEmoHappy(tempQuery);
                    angryCount = database.getDiaryDao().countEmoAngry(tempQuery);
                    sadCount = database.getDiaryDao().countEmoSad(tempQuery);
                    sum = sosoCount + happyCount + angryCount + sadCount;

                    if (sum != 0) {
                        happyEntries.add(new Entry(i, (happyCount / sum) * 100));
                        notbadEntries.add(new Entry(i, (sosoCount / sum) * 100));
                        angryEntries.add(new Entry(i, (angryCount / sum) * 100));
                        sadEntries.add(new Entry(i, (sadCount / sum) * 100));
                    } else {
                        happyEntries.add(new Entry(i, 0));
                        notbadEntries.add(new Entry(i, 0));
                        angryEntries.add(new Entry(i, 0));
                        sadEntries.add(new Entry(i, 0));
                    }

                    /**此處應是直接給予1~12月一個隨機的整數值，用以呈現在折線圖上*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }
            }//大月
            else {
                for (int i = 0; i < 30; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + (round(i) + 1);
                    } else {
                        tempQuery = query + "" + (round(i) + 1);
                    }
                    System.out.println("query=" + tempQuery);
                    //取得各情緒標籤數量
                    sosoCount = database.getDiaryDao().countEmoNotBad(tempQuery);
                    happyCount = database.getDiaryDao().countEmoHappy(tempQuery);
                    angryCount = database.getDiaryDao().countEmoAngry(tempQuery);
                    sadCount = database.getDiaryDao().countEmoSad(tempQuery);
                    sum = sosoCount + happyCount + angryCount + sadCount;

                    System.out.println("happy =" + happyCount);
                    System.out.println("soso =" + sosoCount);
                    System.out.println("angry =" + angryCount);
                    System.out.println("sad =" + sadCount);

                    if (sum != 0) {
                        happyEntries.add(new Entry(i, (happyCount / sum) * 100));
                        notbadEntries.add(new Entry(i, (sosoCount / sum) * 100));
                        angryEntries.add(new Entry(i, (angryCount / sum) * 100));
                        sadEntries.add(new Entry(i, (sadCount / sum) * 100));
                    } else {
                        happyEntries.add(new Entry(i, 0));
                        notbadEntries.add(new Entry(i, 0));
                        angryEntries.add(new Entry(i, 0));
                        sadEntries.add(new Entry(i, 0));
                    }

                    /**此處應是直接給予1~12月一個隨機的整數值，用以呈現在折線圖上*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }
            }//小月

            /**賦予數據給折線*/
            happyDataSet = new LineDataSet(happyEntries, "Happy Percent");//label是線條名稱
            happyDataSet.setLineWidth(2f);//設置折線寬度
            happyDataSet.setColor(Color.YELLOW);
            notbadDataSet = new LineDataSet(notbadEntries, "Soso Percent");//label是線條名稱
            notbadDataSet.setLineWidth(2f);//設置折線寬度
            notbadDataSet.setColor(Color.BLACK);
            angryDataSet = new LineDataSet(angryEntries, "Angry Percent");//label是線條名稱
            angryDataSet.setLineWidth(2f);//設置折線寬度
            angryDataSet.setColor(Color.RED);
            sadDataSet = new LineDataSet(sadEntries, "Sad Percent");//label是線條名稱
            sadDataSet.setLineWidth(2f);//設置折線寬度
            sadDataSet.setColor(Color.BLUE);


            /**隱藏圖例*/
//            Legend legend = lineChart.getLegend();//獲得折線圖圖例
//            legend.setForm(Legend.LegendForm.NONE);//透明化
//            legend.setTextColor(Color.WHITE);//將文字設為白色

            /**隱藏x軸描述*/
            Description description = new Description();
            description.setEnabled(false);
            lineChart.setDescription(description);

            /**左右滑動+放大*/
            lineChart.zoomToCenter(5, 1f);
            BarLineChartTouchListener barLineChartTouchListener = (BarLineChartTouchListener) lineChart.getOnTouchListener();
            barLineChartTouchListener.stopDeceleration();

            /**設置數據刷新圖表*/
            LineData lineData = new LineData(happyDataSet, notbadDataSet, angryDataSet, sadDataSet);
            lineData.setDrawValues(true);
            lineData.setValueTextSize(10f);
            lineChart.setData(lineData);
            lineChart.invalidate();//刷新
        }
    }

    /**返回鍵點擊事件*/
    private final View.OnClickListener backToHistory = v -> {
        Intent backtohistory=new Intent();
        backtohistory.setClass(this,History.class);
        startActivity(backtohistory);
    };

    /**check box點擊事件*/
    private final CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){//buttonView.isChecked()==true
                if(buttonView.getText().equals("Happy")){
                    happyDataSet.setVisible(true);
                }
                else if(buttonView.getText().equals("Not Bad")){
                    notbadDataSet.setVisible(true);
                }
                else if(buttonView.getText().equals("Angry")){
                    angryDataSet.setVisible(true);
                }
                else if(buttonView.getText().equals("Sad")){
                    sadDataSet.setVisible(true);
                }
            }
            else{
                if(buttonView.getText().equals("Happy")){
                    happyDataSet.setVisible(false);
                }
                else if(buttonView.getText().equals("Not Bad")){
                    notbadDataSet.setVisible(false);
                }
                else if(buttonView.getText().equals("Angry")){
                    angryDataSet.setVisible(false);
                }
                else if(buttonView.getText().equals("Sad")){
                    sadDataSet.setVisible(false);
                }
            }
        }
    };

    /**日期選擇*/
    //日期選擇事件
    private final DatePickerDialog.OnDateSetListener pickDateListener = (view, year, month, dayOfMonth) -> {
        selectYear=year;
        selectMonth=month;
        refresh();
    };

    //測試用按鈕的點擊事件
    private final View.OnClickListener testDate = v -> {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Diagram.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,null,yy,mm,dd){
            /**修改DatePickerDialog的onCreate函式
             * 使得日期選擇只會出現月分與年份*/
            @Override
            protected void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);

                LinearLayout mSpinners = (LinearLayout) findViewById(getContext().getResources().getIdentifier("android:id/pickers", null, null));
                if (mSpinners != null) {
                    NumberPicker mMonthSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                    NumberPicker mYearSpinner = (NumberPicker) findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
                    mSpinners.removeAllViews();
                    if (mMonthSpinner != null) {
                        mSpinners.addView(mMonthSpinner);
                    }
                    if (mYearSpinner != null) {
                        mSpinners.addView(mYearSpinner);
                    }
                }
                View dayPickerView = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                if(dayPickerView != null){
                    dayPickerView.setVisibility(View.GONE);//隱藏日期選擇
                }
            }
        };
        datePickerDialog.setOnDateSetListener(pickDateListener);
        datePickerDialog.setTitle("請選擇日期");
        datePickerDialog.show();//顯示
    };

    //刷新頁面
    private void refresh(){
        finish();
        Intent intent = new Intent();
        intent.setClass(Diagram.this,Diagram.class);
        intent.putExtra("year",selectYear);
        intent.putExtra("month",(selectMonth+1));
        startActivity(intent);
    }

    /**縮放事件*/
    private final View.OnClickListener increaseScaleXY = v -> {
        lineChart.setScaleX(1.5f);
        lineChart.setScaleY(1.5f);
    };

    private final View.OnClickListener decreaseScaleXY = v -> {
            lineChart.setScaleX(1f);
            lineChart.setScaleY(1f);
    };

}

