package com.example.testfordate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Ignore;
import androidx.room.Room;

import DataBase.Building.DataUao;
import DataBase.Building.MyData;
import DataBase.Diary.Diary;
import DataBase.Person.DiaryDatabase;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Block {

    /**
     * 與移動頁面相關
     */
    private Intent intentToMoveActivity;//用於跳轉到移動頁面的intent物件
    private Context layout_context;
    private Button storeButton;
    /**
     * 以上是移動葉面相關
     */

    private int index = 0; // 建築ID
    private int x = 0; // 建築座標位置
    private int y = 500;
    private View blockLayout;
    private ImageView moveCtrl; // 建築移動手把
    MyData buildData; // 建築的資料

    //PersonScrollView scrollView;//horizontal scroll view
    DiaryDatabase diaryDatabase;

    Block() {
        this.setIndex(index);
        this.setX(0);
        this.setY(500);

    }

    @Ignore
    Block(int id, int x, int y) {
        this.setIndex(id);
        this.setX(x);
        this.setY(y);
    }

    /*
    //scroll view
    public void setScrollView(Activity activityCopy) {
        this.scrollView = activityCopy.findViewById(R.id.moveLayoutScrollView);
    }//用以設定scroll view 是否為scrollable
    */

    // 蓋房子
    // 建立Layout，在傳過來的 ViewGroup 上新增建築
    public void buildBlock(int viewID, Context context, ConstraintLayout viewGroup) {

        intentToMoveActivity = new Intent();
        intentToMoveActivity.setClass(context, ObjectMoveActivity.class);//設定此intent，會從MainActivity跳轉到ObjectMoveActivity

        LayoutInflater inflater = LayoutInflater.from(context);
        blockLayout = inflater.inflate(R.layout.building, null); // 引入xml

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topToTop = viewGroup.getId();
        params.leftToLeft = viewGroup.getId();
        params.topMargin = this.getY();
        params.leftMargin = this.getX();
        //System.out.println("x_block = "+this.getX());
        //System.out.println("y_block = "+this.getY());
        blockLayout.setLayoutParams(params);



        blockLayout.setId(viewID);//set id of View named block layout

        viewGroup.addView(blockLayout); // 加入main場景裡

        ImageView buildImg = blockLayout.findViewById(R.id.building); // 建築圖片
        buildImg.setId(viewID);//set id of ImageView named buildImg
        this.showOperation(context, buildImg, viewGroup); // 讓建築圖片可以觸發點擊事件

        /**將block layout的觸碰監聽改為在buildBlock時設定(刪除)(改為將觸碰監聽綁定在buildImg上)
         * 在object move activity時，傳入的context會是object move activity
         * 在main activity時，傳入的context會是main activity
         * 限制只有在move activity時設定觸碰監聽給build image
         * (問題)設置觸碰監聽後，showOperation函式中設定的點擊監聽將失效
         * */
        if (context.getClass() == ObjectMoveActivity.class) {
            System.out.println("you are in move activity, so set touch listener");
            storeButton = viewGroup.findViewById(R.id.store_button);
            layout_context = context;
            buildImg.setOnTouchListener(moveBlock);//設置觸碰監聽
        }

        // 處理手把
        moveCtrl = blockLayout.findViewById(R.id.move_ctrl); // 移動手把圖片

    }

    // 使點擊房子顯示可進行的操作
    public void showOperation(Context context, View building, ConstraintLayout scene) {
        //building.onTouchEvent(MotionEvent event)

        building.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(context, building);
            popupMenu.getMenuInflater().inflate(R.menu.building_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.info:
                            buildInfo(context);
                            break;
                        case R.id.move:
                            /**選擇move選項時，根據所處的頁面進行不同動作
                             * 1. 處於主頁面時，跳轉到移動頁面
                             * 2. 處於跳轉頁面時，開放移動*/
                            if (context.getClass() == MainActivity.class) {
                                //System.out.println("this house in main activity");
                                intentToMoveActivity.putExtra("buildingNumber", building.getId());//傳房子編號給移動頁面
                                context.startActivity(intentToMoveActivity);
                            } else if (context.getClass() == ObjectMoveActivity.class) {
                                //System.out.println("this house in move layout");
                                //layout_movable(scene, context);
                                break;
                            }
                    }
                    return true;
                }
            });
            popupMenu.show();
        });

        //新增長按監聽
        building.setOnLongClickListener(v -> {
            new  getAllDiaryOfBuilding(context, this.index).execute(); //顯示該建築物的所有日記
            return true;
        });

    }

    private final View.OnTouchListener moveBlock = new View.OnTouchListener() {
        float dx, dy;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    moveCtrl.setVisibility(View.VISIBLE);//顯示箭頭圖示
                    dx = blockLayout.getX() - event.getRawX();
                    dy = blockLayout.getY() - event.getRawY();
                    //scrollView.setScrollable(false);//停止捲動功能
                    break;
                case MotionEvent.ACTION_UP:
                    moveCtrl.setVisibility(View.GONE);//隱藏箭頭圖示
                    //scrollView.setScrollable(true);
                    /**修正
                     * 用getLocationOnScreen似乎並不是取得他左上角的位置
                     * 他get出來的值與對圖片getX,Y的值差的有點多
                     * blockLayout.getY其實也與對圖片getY不一樣，但是由於我們是看見圖片位置，存入資料庫也是採用圖片位置，因此我並沒有將他們改成一樣的
                     * 如果之後碰撞或是位置對齊有問題的話，請先試試這邊
                     * 基於我們全部都採用左上角去對齊
                     * 因此就直接統一都採用一樣的位置下去對齊，就不會有飄移的問題了*/
                    x = Math.round(blockLayout.getX());
                    y = Math.round(blockLayout.getY());


                    /**更新資輛酷中房子的資訊*/
                    new Thread(() -> {
                        diaryDatabase.getInstance(layout_context).getDataUao().updateData(getIndex(),getX(),getY());
                    }).start();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveCtrl.setVisibility(View.VISIBLE);//顯示箭頭圖示
                    /**更改移動時的動畫顯示，從平滑移動改為格子移動*/
                    blockLayout.animate()
                            .x((Math.round(event.getRawX() + dx)/250)*250)
                            .y((Math.round(event.getRawY() + dy)/250)*250)
                            .setDuration(0)
                            .start();
                    break;
                default:
                    //scrollView.setScrollable(true);
                    break;
            }
            return true;
        }
    };

    // 顯示房子資訊
    private void buildInfo(Context context) {
        Toast.makeText(context, "\n(" + this.getX() + "," + this.getY() + ")", Toast.LENGTH_SHORT).show();
    }

    // 處理layout的移動事件
    private void layoutMoving(Context context) {
        moveCtrl.setVisibility(View.VISIBLE); // 顯示移動手把

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public View getBlockLayout() {
        return blockLayout;
    }

    //執行緒，顯示該房屋所有的日記
    static class getAllDiaryOfBuilding extends AsyncTask<Void, Void, List<Diary>> {
        private Context context;//畫面
        private int id;//房屋ID

        public getAllDiaryOfBuilding(Context context, int id) {
            this.context = context;
            this.id = id;
        }

        @Override
        protected List<Diary> doInBackground(Void... voids) {
            DiaryDatabase diaryDatabase = Room.databaseBuilder(context, DiaryDatabase.class, "diary").build(); //建置資料庫
            String date = diaryDatabase.getDataUao().findDateByID(id); //取得這個房屋的日期
            List<Diary> list = diaryDatabase.getDiaryDao().findDiaryByDate(date); //取得這個日期所有的日記
            return list;
        }

        //更改UI介面->顯示AlertDialog
        @Override
        protected void onPostExecute(List<Diary> list) {
            super.onPostExecute(list);
            AlertDialog.Builder goLogin = new AlertDialog.Builder(context); //新增AlertDialog

            final String[] getDiaryId = new String[list.size()]; //只需要陣列，不能使用List<Diary>，因此額外新增陣列先取出存放
            final String[] getDiarySentence = new String[list.size()]; //只需要陣列，不能使用List<Diary>，因此額外新增陣列先取出存放

            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {//尋訪List<Diary>
                    Diary diary = list.get(i);//一個一個取出來
                    getDiaryId[i] = Integer.toString(diary.getId());
                    getDiarySentence[i] = diary.getDiary_sentence();//抓ID，轉String
                }
            }

            //傳入陣列讓它顯示
            goLogin.setItems(getDiarySentence, new DialogInterface.OnClickListener() {

                //只能使用String陣列
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle bundle = new Bundle();//打包並傳入intent
                    bundle.putInt("DiaryID", parseInt(getDiaryId[which]));
                    bundle.putInt("context", 1);//選擇回來頁面

                    Intent intent = new Intent();
                    intent.setClass(context, ReadDiary.class); //跳到讀取日記頁面
                    intent.putExtras(bundle); //傳入打包的東西
                    context.startActivity(intent); //跳轉
                    //Toast.makeText(context, "你選的是" + getDiaryId[which], Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertLogin = goLogin.create();
            alertLogin.show();
        }
    }
}
