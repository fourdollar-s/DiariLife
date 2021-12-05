package com.example.testfordate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import DataBase.Building.DataUao;
import DataBase.Building.MyData;
import DataBase.Map.MapObject;
import DataBase.Map.MapObjectDao;
import DataBase.Person.DiaryDatabase;
import DataBase.Person.PersonDao;
import DataBase.Store.StoreObjectDao;

/**供使用者查看歷史紀錄的頁面
 * 會根據使用者選擇的年份與月份呈現*/

@RequiresApi(api = Build.VERSION_CODES.N)
public class History extends AppCompatActivity {

    private Button datePickBtn;
    private Button backBtn;
    private Button diagramBtn;
    private TextView hint;
    private ImageView background;
    //取得系統時間
    Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);
    String today;

    int viewId;
    ConstraintLayout constraintLayout;

    //database
    //--------資料庫宣告-----------
    StoreObjectDao storeObjectDao;
    DiaryDatabase diaryDatabase;
    MapObjectDao mapObjectDao;
    DataUao buildingDao;
    PersonDao personDao;

    private final HashMap<String,Integer> drawable_src_hashmap = new HashMap<>();

    //決定頁面呈現的年份與月份
    private int checkYear;//使用者選擇的年份
    private int checkMonth;//使用者選擇的月份
    private String databaseQuery;//用於篩選要放置在畫面上的物件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);//畫面的layout

        today=yy+"/"+(mm+1)+"%";

        viewId=0;
        constraintLayout=findViewById(R.id.historyRoot);

        /**初始化hashmap，存入drawable資源的名稱與其R.drawable.imgSrc值*/
        drawable_src_hashmap.put("cat_yellow",R.drawable.cat_yellow);
        drawable_src_hashmap.put("tree",R.drawable.tree);
        drawable_src_hashmap.put("little_tree",R.drawable.little_tree);

        diaryDatabase = Room.databaseBuilder(this, DiaryDatabase .class,"diary").allowMainThreadQueries().build();
        storeObjectDao = diaryDatabase.getStoreObjectDao();
        mapObjectDao = diaryDatabase.getMapObjectDao();
        buildingDao = diaryDatabase.getDataUao();
        personDao = diaryDatabase.personDao();

        //設置按鈕點擊監聽事件
        hint = findViewById(R.id.historyHint);
        background=findViewById(R.id.historyBackground);
        datePickBtn=findViewById(R.id.pickDatebutton);
        datePickBtn.setOnClickListener(testDate);
        backBtn=findViewById(R.id.backBTN);
        backBtn.setOnClickListener(backToMain);
        diagramBtn=findViewById(R.id.diagramBTN);
        diagramBtn.setOnClickListener(checkDiagram);

        databaseQuery = this.getIntent().getStringExtra("select");
        checkYear=this.getIntent().getIntExtra("lastSelectYear",0);
        checkMonth=this.getIntent().getIntExtra("lastSelectMonth",0);
        setScreen(databaseQuery);
    }

    //日期選擇事件
    private final DatePickerDialog.OnDateSetListener pickDate = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            checkYear=year;
            checkMonth=month+1;//給兩個live data賦值，observer動作
            System.out.println(checkYear);
            System.out.println(checkMonth);
            databaseQuery=checkYear+"/"+checkMonth+"%";
            System.out.println(databaseQuery);
            refresh();
        }
    };

    //測試用按鈕的點擊事件
    private final View.OnClickListener testDate = v -> {
        DatePickerDialog datePickerDialog = new DatePickerDialog(History.this,
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
        datePickerDialog.setOnDateSetListener(pickDate);
        datePickerDialog.setTitle("請選擇日期");
        datePickerDialog.show();//顯示
    };

    private final View.OnClickListener backToMain = v -> {
        Intent backtomain=new Intent();
        backtomain.setClass(this,MainActivity.class);
        startActivity(backtomain);
    };

    //Diagram按鈕點擊事件
    private final View.OnClickListener checkDiagram = v -> {
        Intent checkDiagram =new Intent();
        checkDiagram.setClass(this,Diagram.class);
        checkDiagram.putExtra("year",checkYear);//將選擇的年份與月份傳給圖表頁面
        checkDiagram.putExtra("month",checkMonth);
        startActivity(checkDiagram);
    };

    //放置符合年分與月份的物件
    //房屋
    private void setBuildings(String dataQ){
        new Thread(() -> {
            List<MyData> data = buildingDao.displayAll(dataQ);
            for (MyData myData : data) {
                runOnUiThread(() -> {
                    //buildingDao.delete();
                    Block block = new Block(myData.getId(), myData.getX(), myData.getY());
                    //block.setScrollView(this);
                    block.buildBlock(viewId, this, constraintLayout);

                    //idCount++;
                    viewId++;
                });
            }
        }).start();
    }

    //地圖物件
    private void setMapObject(String dataQ){
        new Thread(() -> {
            List<MapObject> data = DiaryDatabase.getInstance(History.this).getMapObjectDao().getAllMapObject(dataQ);
            for (MapObject mapObject : data) {
                if (mapObject.getObject_use()) {//狀態為已放置才需要顯示在畫面上
                    int product_id = mapObject.getId_store_object_of_map();
                    String img_name = diaryDatabase.getStoreObjectDao().getObjectPicID(product_id);
                    int img_drawable_id = (int) drawable_src_hashmap.get(img_name);
                    runOnUiThread(() -> {
                        ProductBlock block = new ProductBlock(mapObject.getId_map_object(), mapObject.getObject_x(), mapObject.getObject_y());
                        block.buildBlock(viewId, this, constraintLayout, img_drawable_id);
                        //block.setScrollView(History.this);
                        viewId++;
                    });
                }
            }
        }).start();
    }

    //刷新頁面
    private void refresh(){
        finish();
        Intent intent = new Intent();
        intent.setClass(History.this,History.class);
        intent.putExtra("select", databaseQuery);
        intent.putExtra("lastSelectYear",checkYear);
        intent.putExtra("lastSelectMonth",checkMonth);
        startActivity(intent);
    }

    //設置畫面
    private void setScreen(String dq){

        if(dq!=null){
            if(dq.compareTo(today)==0){
                background.setVisibility(View.GONE);
                hint.setText("這個月還沒結束喔!");
            }
            else{
                setBuildings(dq);
                setMapObject(dq);
            }
        }
        else{
            System.out.println("date is null");
            background.setVisibility(View.GONE);
            hint.setText("請選擇要查看的日期");
        }
    }

}
