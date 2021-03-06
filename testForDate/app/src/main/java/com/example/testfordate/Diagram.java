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

    //chart??????
    LineDataSet happyDataSet;
    LineDataSet notbadDataSet;
    LineDataSet angryDataSet;
    LineDataSet sadDataSet;

    //??????????????????
    private Button pickDate;
    //??????????????????
    Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);

    //x??????y???????????????
    private float scaleX;
    private float scaleY;
    private ImageButton increase;
    private ImageButton decrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //????????????
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.diagram);//?????????layout
        database = Room.databaseBuilder(this, DiaryDatabase .class,"diary").allowMainThreadQueries().build();

        /**??????????????????????????????*/
        increase=findViewById(R.id.increaseScale);
        decrease=findViewById(R.id.decreaseScale);

        /**???history????????????????????????????????????*/
        Intent getParam = this.getIntent();
        year = getParam.getIntExtra("year", 0);
        month = getParam.getIntExtra("month", 0);

        /**????????????history????????????*/
        back=findViewById(R.id.DiagramBackBtn);
        back.setOnClickListener(backToHistory);

        /**????????????????????????*/
        pickDate=findViewById(R.id.pickDateBTN);
        pickDate.setOnClickListener(testDate);

        /**??????check box*/
        happyBox=findViewById(R.id.checkBoxHappy);
        notbadBox=findViewById(R.id.checkBoxNotbad);
        angryBox=findViewById(R.id.checkBoxAngry);
        sadBox=findViewById(R.id.checkBoxSad);
        happyBox.setChecked(true);//????????????????????????
        notbadBox.setChecked(true);
        angryBox.setChecked(true);
        sadBox.setChecked(true);
        happyBox.setOnCheckedChangeListener(check);//???????????????
        notbadBox.setOnCheckedChangeListener(check);
        angryBox.setOnCheckedChangeListener(check);
        sadBox.setOnCheckedChangeListener(check);

        /**????????????????????????????????????*/
        dateTxt=findViewById(R.id.yearMonth);

        //????????????????????????
        if (year != 0 && month != 0) {
            dateTxt.setText(year + "???" + month + "???");//??????????????????????????????????????????????????????
            lineChart = findViewById(R.id.lineChart);//???????????????
            query = year + "/" + month + "/";//?????????????????????(??????????????????)
            /**??????markerview*/
            chartMV = new chartMarkerView(this, R.layout.markerview, query);
            chartMV.setChartView(lineChart);
            lineChart.setMarker(chartMV);


            float sosoCount = 0;
            float happyCount = 0;
            float angryCount = 0;
            float sadCount = 0;
            float sum = 0;

            /**?????????????????????*/
            lineChart.setDragEnabled(true);//????????????
            lineChart.setScaleEnabled(true);//????????????(x & y)
            lineChart.setTouchEnabled(true);//????????????
            //lineChart.setScaleXEnabled(true);
            //lineChart.setScaleYEnabled(true);
            lineChart.setDoubleTapToZoomEnabled(false);//??????????????????????????????
            scaleX=lineChart.getScaleX();
            scaleY=lineChart.getScaleY();
            increase.setOnClickListener(increaseScaleXY);
            decrease.setOnClickListener(decreaseScaleXY);

            /**??????y?????????*/
            YAxis rightAxis = lineChart.getAxisRight();//???????????????????????????
            YAxis leftAxis = lineChart.getAxisLeft();//???????????????????????????
            rightAxis.setEnabled(false);//??????????????????
            leftAxis.setEnabled(true);//??????????????????
            //leftAxis.setDrawLimitLinesBehindData(true);
            leftAxis.setTextSize(15f);
            leftAxis.setAxisMinimum(0f);

            /**??????x?????????*/
            XAxis xAxis = lineChart.getXAxis();//????????????
            xAxis.setTextSize(11f);
            xAxis.setAxisMinimum(0f);
            xAxis.setDrawAxisLine(true);//????????????
            xAxis.setDrawGridLines(false);
            xAxis.setDrawLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            //xAxis.setDrawLimitLinesBehindData(true);
            xAxis.setTextSize(15f);

            /**??????x??????y?????????*/
            ArrayList<Entry> happyEntries = new ArrayList<>();//??????????????????
            ArrayList<Entry> notbadEntries = new ArrayList<>();//??????????????????
            ArrayList<Entry> angryEntries = new ArrayList<>();//??????????????????
            ArrayList<Entry> sadEntries = new ArrayList<>();//??????????????????

            String tempQuery;
            if (month == 2 && ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))) {
                for (int i = 0; i < 29; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + round(i + 1);
                    } else {
                        tempQuery = query + "" + round(i + 1);
                    }

                    //???????????????????????????
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

                    /**????????????????????????1~12?????????????????????????????????????????????????????????*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }

            }//????????????
            else if (month == 2 && ((year % 4 != 0) || (year % 100 == 0 && year % 400 != 0))) {
                for (int i = 0; i < 28; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + round(i + 1);
                    } else {
                        tempQuery = query + "" + round(i + 1);
                    }
                    //???????????????????????????
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

                    /**????????????????????????1~12?????????????????????????????????????????????????????????*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }
            }//????????????
            else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                for (int i = 0; i < 31; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + round(i + 1);
                    } else {
                        tempQuery = query + "" + round(i + 1);
                    }
                    //???????????????????????????
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

                    /**????????????????????????1~12?????????????????????????????????????????????????????????*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }
            }//??????
            else {
                for (int i = 0; i < 30; i++) {
                    if (i < 9) {
                        tempQuery = query + "0" + (round(i) + 1);
                    } else {
                        tempQuery = query + "" + (round(i) + 1);
                    }
                    System.out.println("query=" + tempQuery);
                    //???????????????????????????
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

                    /**????????????????????????1~12?????????????????????????????????????????????????????????*/
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            String label = "" + (round(value) + 1);
                            return label;
                        }
                    });
                }
            }//??????

            /**?????????????????????*/
            happyDataSet = new LineDataSet(happyEntries, "Happy Percent");//label???????????????
            happyDataSet.setLineWidth(2f);//??????????????????
            happyDataSet.setColor(Color.YELLOW);
            notbadDataSet = new LineDataSet(notbadEntries, "Soso Percent");//label???????????????
            notbadDataSet.setLineWidth(2f);//??????????????????
            notbadDataSet.setColor(Color.BLACK);
            angryDataSet = new LineDataSet(angryEntries, "Angry Percent");//label???????????????
            angryDataSet.setLineWidth(2f);//??????????????????
            angryDataSet.setColor(Color.RED);
            sadDataSet = new LineDataSet(sadEntries, "Sad Percent");//label???????????????
            sadDataSet.setLineWidth(2f);//??????????????????
            sadDataSet.setColor(Color.BLUE);


            /**????????????*/
//            Legend legend = lineChart.getLegend();//?????????????????????
//            legend.setForm(Legend.LegendForm.NONE);//?????????
//            legend.setTextColor(Color.WHITE);//?????????????????????

            /**??????x?????????*/
            Description description = new Description();
            description.setEnabled(false);
            lineChart.setDescription(description);

            /**????????????+??????*/
            lineChart.zoomToCenter(5, 1f);
            BarLineChartTouchListener barLineChartTouchListener = (BarLineChartTouchListener) lineChart.getOnTouchListener();
            barLineChartTouchListener.stopDeceleration();

            /**????????????????????????*/
            LineData lineData = new LineData(happyDataSet, notbadDataSet, angryDataSet, sadDataSet);
            lineData.setDrawValues(true);
            lineData.setValueTextSize(10f);
            lineChart.setData(lineData);
            lineChart.invalidate();//??????
        }
    }

    /**?????????????????????*/
    private final View.OnClickListener backToHistory = v -> {
        Intent backtohistory=new Intent();
        backtohistory.setClass(this,History.class);
        startActivity(backtohistory);
    };

    /**check box????????????*/
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

    /**????????????*/
    //??????????????????
    private final DatePickerDialog.OnDateSetListener pickDateListener = (view, year, month, dayOfMonth) -> {
        selectYear=year;
        selectMonth=month;
        refresh();
    };

    //??????????????????????????????
    private final View.OnClickListener testDate = v -> {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Diagram.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,null,yy,mm,dd){
            /**??????DatePickerDialog???onCreate??????
             * ?????????????????????????????????????????????*/
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
                    dayPickerView.setVisibility(View.GONE);//??????????????????
                }
            }
        };
        datePickerDialog.setOnDateSetListener(pickDateListener);
        datePickerDialog.setTitle("???????????????");
        datePickerDialog.show();//??????
    };

    //????????????
    private void refresh(){
        finish();
        Intent intent = new Intent();
        intent.setClass(Diagram.this,Diagram.class);
        intent.putExtra("year",selectYear);
        intent.putExtra("month",(selectMonth+1));
        startActivity(intent);
    }

    /**????????????*/
    private final View.OnClickListener increaseScaleXY = v -> {
        lineChart.setScaleX(1.5f);
        lineChart.setScaleY(1.5f);
    };

    private final View.OnClickListener decreaseScaleXY = v -> {
            lineChart.setScaleX(1f);
            lineChart.setScaleY(1f);
    };

}

