package com.example.testfordate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import DataBase.Building.MyData;
import DataBase.Map.MapObject;
import DataBase.Person.DiaryDatabase;
import DataBase.Person.PersonViewModel;
import DataBase.Store.StoreObject;

/**
 * 移動物件頁面
 * 包含以下功能：
 * 透過主頁面房屋的move選項進入(完成)
 * 葉面上顯示房屋物件(完成)
 * 於此頁面上選擇房屋的move選項，即顯示所選物件的箭頭標示(完成)
 * 唯有顯示箭頭者可移動(完成)
 * 畫面上只會有一個物件顯示箭頭(完成)
 * 移動到不可放置的地方時收納物件(完成)
 * 點擊儲存後更新資料庫並回到主頁面(完成)*/

public class ObjectMoveActivity extends AppCompatActivity implements View.OnTouchListener{


    private PersonViewModel personViewModel;
    private int objectID = 0;
    private final HashMap<String, Integer> drawable_src_hashmap = new HashMap<>();
    private DiaryDatabase diaryDatabase;
    private boolean showMapObjectList;//物件列表狀態
    private LinearLayout mapObjectList;

    private int yellowCatCount;
    private int treeCount;
    private int littleTreeCount;
    private TextView yellowCatCountText;
    private TextView treeCountText;
    private TextView littleTreeCountText;
    private ArrayList<Integer> getMapObjectId;

    //取得系統時間，切割出年份與月份，供判斷哪些房子要放置於畫面上使用
    String system_time = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
    String year=system_time.split("/")[0];
    String month=system_time.split("/")[1];
    String day=system_time.split("/")[2];//用來測試篩選是否有效

    //新的移動方式
    private ConstraintLayout backgroundView;
    private float dx,dy;

    public float dp_view_width;
    public float dp_view_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_move_activity);

        /**初始化hashmap，存入drawable資源的名稱與其R.drawable.imgSrc值*/
        drawable_src_hashmap.put("tree_2", R.drawable.tree_2);
        drawable_src_hashmap.put("tree_1", R.drawable.tree_1);
        drawable_src_hashmap.put("little_tree", R.drawable.little_tree);

        /**不可置於onCreate前，因為此時activity尚未附加於應用實例上
         * 會出現RuntimeException：
         * Your activity is not yet attached to the Application instance. You can't request ViewModel before onCreate call.*/
        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        diaryDatabase = DiaryDatabase.getInstance(ObjectMoveActivity.this);


        backgroundView = findViewById(R.id.moveLayoutRoot);
        backgroundView.setOnTouchListener(this);

        backgroundView.post(new Runnable() {
            @Override
            public void run() {
                float h = backgroundView.getHeight(); //is ready
                float w = backgroundView.getWidth(); //is ready
                setBackgroundWidth(w,h);
            }
        });

        setMapObjectList();//初始化地圖物件列表
        //setBackgroundWidth();//set width of background image
        setBuildings();
        setProduct();

        //點擊儲存按鈕後回到主頁面
        findViewById(R.id.store_button).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            this.startActivity(intent);
        });

        //點選物件列表圖示後顯示列表
        findViewById(R.id.map_object_list).setOnClickListener(v -> {
            System.out.println("you click the icon of map object list");
            if (showMapObjectList) {
                mapObjectList.setVisibility(View.GONE);
                showMapObjectList = false;
            } else {
                mapObjectList.setVisibility(View.VISIBLE);
                showMapObjectList = true;
            }
        });

        /**物件列表點擊事件
         * 點擊一次未放置數量減一
         * 未放置數量歸零時不觸發任何動作
         * 點擊後放置id較小(較早購買)的物件*/
        ImageButton treeIcon = findViewById(R.id.tree_icon);
        treeIcon.setOnClickListener(treeIconListener);
        ImageButton yellowCatIcon = findViewById(R.id.yellow_cat_icon);
        yellowCatIcon.setOnClickListener(yellowCatIconListener);
        ImageButton littleTreeIcon = findViewById(R.id.little_tree_icon);
        littleTreeIcon.setOnClickListener(littleTreeIconListener);

        /**偵測未放置數量的變動，當使用者取消物件顯示(收納)時，會執行observer.onChange裡的動作*/
        /**偵測未放置數量的變動，當使用者取消物件顯示(收納)時，會執行observer.onChange裡的動作*/
        diaryDatabase.getMapObjectDao().getUnPostTreeCount(year+"/"+month+"/%").observe(this, treeCountObserver);
        diaryDatabase.getMapObjectDao().getUnPostLittleTreeCount(year+"/"+month+"/%").observe(this,littleTreeCountObserver);
        diaryDatabase.getMapObjectDao().getUnPostYellowCatCount(year+"/"+month+"/%").observe(this,yellowCatCountObserver);
    }

    public static float dp2px(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        dp_view_width = -(dp2px(this,backgroundView.getWidth()));//取得background的大小，並轉成px
        dp_view_height = -(dp2px(this,backgroundView.getHeight()));//取得background的大小，並轉成px
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //System.out.println("down rn");
                dx = view.getX() - motionEvent.getRawX();
                dy = view.getY() - motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //System.out.println("move rn");

                //以下判斷為避免物件被拉出畫面
                float final_x = motionEvent.getRawX() + dx;//最後的位置
                float final_y = motionEvent.getRawY() + dy;//最後的位置
                if(final_x < dp_view_width) //最後不能超過寬度 有負號所以是小於
                    final_x = dp_view_width;
                if(final_x > 0)
                    final_x = 0;
                if(final_y < dp_view_height) //最後不能超過高度 有負號所以是小於
                    final_y = dp_view_height;
                if(final_y > 0)
                    final_y = 0;
                //System.out.println(final_x+","+final_y);
                view.setX(final_x);
                view.setY(final_y);
                break;

            default:
                return false;
        }
        //backgroundView.invalidate();
        return true;
    }

    public void setBackgroundWidth(float width,float height) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );//create constraint layout params

        ImageView backgroundImage = findViewById(R.id.moveLayoutBackground);

        params.bottomToBottom = R.id.root;
        params.leftToLeft = R.id.root;
        params.bottomMargin=0;
        params.leftMargin = 0;

        //System.out.println("test = "+width);
        //System.out.println("test = "+height);

        params.height = Math.round(height);
        params.width = Math.round(width); //填滿畫面
        backgroundImage.setLayoutParams(params);

    }

    private void setBuildings() {
        ConstraintLayout constraintLayout = findViewById(R.id.moveLayoutRoot);
        new Thread(() -> {
            List<MyData> data = DiaryDatabase.getInstance(ObjectMoveActivity.this).getDataUao().displayAll(year+"/"+month+"/%");
            for (MyData myData : data) {
                runOnUiThread(() -> {
                    Block block = new Block(myData.getId(), myData.getX(), myData.getY());
                    block.buildBlock(objectID, this, constraintLayout);
                    //block.setScrollView(ObjectMoveActivity.this);
                    //idCount++;
                    objectID++;
                });
            }
        }).start();
    }

    /**
     * 商店部分：
     * 自資料庫讀出已購買商品資訊
     * 顯示商品
     * 移動商品
     * 更新商品位置
     */
    private void setProduct() {
        /**改動：原本是取得move layout root這個layout，但findViewById方法只能找到子物件
         * 因為需要在block layout中取得cancel icon圖示，因此改取得bottom move layout*/
        ConstraintLayout constraintLayout = findViewById(R.id.bottom_move_layout);
        new Thread(() -> {
            List<MapObject> data = DiaryDatabase.getInstance(ObjectMoveActivity.this).getMapObjectDao().getAllMapObject(year+"/"+month+"/%");
            for (MapObject mapObject : data) {
                if (mapObject.getObject_use()) {//狀態為已放置才需要顯示在畫面上
                    int product_id = mapObject.getId_store_object_of_map();//取得物件種類，以此給予物件相應的圖片
                    String img_name = diaryDatabase.getStoreObjectDao().getObjectPicID(product_id);
                    int img_drawable_id = (int) drawable_src_hashmap.get(img_name);
                    runOnUiThread(() -> {
                        ProductBlock block = new ProductBlock(mapObject.getId_map_object(), mapObject.getObject_x(), mapObject.getObject_y());
                        block.buildBlock(objectID, this, constraintLayout, img_drawable_id);
                        //block.setScrollView(ObjectMoveActivity.this);
                        objectID++;
                    });
                }
            }
        }).start();
    }

    /**
     * 地圖物件列表：
     * 設置地圖物件列表狀態
     * 設置未放置的地圖物件數量
     * 此函式僅為初始化所用，只會使用一次
     */
    private void setMapObjectList() {
        showMapObjectList = false;//預設不顯示物件列表
        getMapObjectId = new ArrayList<Integer>();
        //get reference
        mapObjectList = findViewById(R.id.map_object_icon_list);
        yellowCatCountText = findViewById(R.id.yellow_cat_icon_count);
        treeCountText=findViewById(R.id.tree_icon_count);
        littleTreeCountText=findViewById(R.id.little_tree_icon_count);

        yellowCatCount = diaryDatabase.getMapObjectDao().getNotUseObjectNumber(false, 4,year+"/"+month+"/%");
        yellowCatCountText.setText("" + yellowCatCount);
        treeCount = diaryDatabase.getMapObjectDao().getNotUseObjectNumber(false, 2,year+"/"+month+"/%");
        treeCountText.setText("" + treeCount);
        littleTreeCount = diaryDatabase.getMapObjectDao().getNotUseObjectNumber(false, 3,year+"/"+month+"/%");
        littleTreeCountText.setText("" + littleTreeCount);
    }

    /**
     * 透過點擊物件列表新增地圖上顯示的物件
     */
    private void setMapObject(int object_id, int object_type) {
        ConstraintLayout constraintLayout = findViewById(R.id.bottom_move_layout);
        MapObject data = diaryDatabase.getMapObjectDao().getObjectById(object_id);
        data.setObject_use(true);//放置狀態改為"已放置"
        String img_name = diaryDatabase.getStoreObjectDao().getObjectPicID(object_type);
        int img_drawable_id = (int) drawable_src_hashmap.get(img_name);
        diaryDatabase.getMapObjectDao().updateSingleMapObject(data);
        runOnUiThread(() -> {
            ProductBlock block = new ProductBlock(object_id, data.getObject_x(), data.getObject_y());
            block.buildBlock(objectID, this, constraintLayout, img_drawable_id);
            //block.setScrollView(ObjectMoveActivity.this);
            objectID++;
        });
    }

    /**地圖物件列表上圖示的點擊監聽
     * 點擊圖示後將最早購買(id最小)的該種地圖物件取出
     * 在畫面上放置新物件
     * 修改此物件放置狀態
     * 修改顯示的剩餘數量*/
    private final View.OnClickListener treeIconListener = v -> {
        System.out.println("click tree icon");
        if(treeCount>0){
            treeCount=treeCount-1;//未放置的tree物件數量減一
            treeCountText.setText(""+treeCount);//更新顯示的未放置數量
            getMapObjectId.addAll(0,diaryDatabase.getMapObjectDao().getPostedObjectNumber(false,2,year+"/"+month+"/%"));//取得所有狀態為"未放置"的tree物件id
            setMapObject(getMapObjectId.get(0),2);
        }
    };

    private final View.OnClickListener yellowCatIconListener = v -> {
        System.out.println("click cat icon");
        if(yellowCatCount>0){
            yellowCatCount=yellowCatCount-1;
            yellowCatCountText.setText(""+yellowCatCount);
            getMapObjectId.addAll(0,diaryDatabase.getMapObjectDao().getPostedObjectNumber(false,4,year+"/"+month+"/%"));
            setMapObject(getMapObjectId.get(0),4);
        }
    };

    private final View.OnClickListener littleTreeIconListener = v -> {
        System.out.println("click little tree icon");
        if(littleTreeCount>0){
            littleTreeCount=littleTreeCount-1;
            littleTreeCountText.setText(""+littleTreeCount);
            getMapObjectId.addAll(0,diaryDatabase.getMapObjectDao().getPostedObjectNumber(false,3,year+"/"+month+"/%"));
            setMapObject(getMapObjectId.get(0),3);
        }
    };

    /**監聽資料庫*/
    private final Observer<Integer> treeCountObserver = new Observer<Integer>(){
        @Override
        public void onChanged(Integer integer) {
            treeCount = diaryDatabase.getMapObjectDao().getNotUseObjectNumber(false, 2,year+"/"+month+"/%");
            treeCountText.setText("" + treeCount);
        }
    };

    private final Observer<Integer> littleTreeCountObserver = new Observer<Integer>(){
        @Override
        public void onChanged(Integer integer) {
            littleTreeCount = diaryDatabase.getMapObjectDao().getNotUseObjectNumber(false, 3,year+"/"+month+"/%");
            littleTreeCountText.setText("" + littleTreeCount);
        }
    };

    private final Observer<Integer> yellowCatCountObserver = new Observer<Integer>(){
        @Override
        public void onChanged(Integer integer) {
            yellowCatCount = diaryDatabase.getMapObjectDao().getNotUseObjectNumber(false, 4,year+"/"+month+"/%");
            yellowCatCountText.setText("" + yellowCatCount);
        }
    };
}
