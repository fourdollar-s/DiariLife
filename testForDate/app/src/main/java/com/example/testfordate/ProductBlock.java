package com.example.testfordate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.icu.text.SymbolTable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import DataBase.Map.MapObject;
import DataBase.Person.DiaryDatabase;
import DataBase.Store.StoreObject;

public class ProductBlock {

    /**
     * 與移動頁面相關
     */
    private Context layout_context;
    /**
     * 取消顯示相關
     */
    private ImageView cancelSymbol;
    private Rect cancelSymbolRect;
    private Rect objectRect;

    /**
     * 以上是移動葉面相關
     */

    private int index = 0; // 物件ID
    private int x = 0; // 物件座標
    private int y = 0;
    private View blockLayout;
    private ImageView moveCtrl;
    //private MapObject mapObject;

    //PersonScrollView scrollView;//horizontal scroll view
    DiaryDatabase diaryDatabase;


    public ProductBlock(int id, int location_x, int location_y) {
        this.index = id;
        this.x = location_x;
        this.y = location_y;
        objectRect = new Rect();
        cancelSymbolRect = new Rect();
    }

    //scroll view
    /*public void setScrollView(Activity activityCopy) {
        this.scrollView = activityCopy.findViewById(R.id.moveLayoutScrollView);
    }//用以設定scroll view 是否為scrollable
     */

    // 顯示商品
    // 建立Layout，在傳過來的 ViewGroup 上新增建築
    public void buildBlock(int viewID, Context context, ConstraintLayout viewGroup, int drawable_id) {

        LayoutInflater inflater = LayoutInflater.from(context);
        blockLayout = inflater.inflate(R.layout.building, null); // 引入xml

        blockLayout.setId(viewID);//set id of View named block layout

        //moveLayoutRoot.addView(blockLayout); // 加入main場景裡

        ImageView buildImg = blockLayout.findViewById(R.id.building); // 建築圖片
        buildImg.setId(viewID);//set id of ImageView named buildImg
        buildImg.setImageResource(drawable_id);

        /**將block layout的觸碰監聽改為在buildBlock時設定(刪除)(改為將觸碰監聽綁定在buildImg上)
         * 在object move activity時，傳入的context會是object move activity
         * 在main activity時，傳入的context會是main activity
         * 限制只有在move activity時設定觸碰監聽給build image
         * (問題)設置觸碰監聽後，showOperation函式中設定的點擊監聽將失效
         * */
        if (context.getClass() == ObjectMoveActivity.class) {
            ConstraintLayout tempLayout = viewGroup.findViewById(R.id.moveLayoutRoot);
            cancelSymbol = viewGroup.findViewById(R.id.cancel_icon);
            layout_context = context;
            if (cancelSymbol != null) {
                System.out.println("get the cancel symbol!");

                cancelSymbolRect.set(cancelSymbol.getLeft(),
                        cancelSymbol.getTop(),
                        cancelSymbol.getRight(),
                        cancelSymbol.getBottom());
            }

            /**將block layout放到move layout root上
             * 雖然動作與else if中的相同，但因為是放置在傳入的view group的子view上
             * 因此分開執行*/
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.topToTop = tempLayout.getId();
            params.leftToLeft = tempLayout.getId();
            params.topMargin = this.getY();
            params.leftMargin = this.getX();
            blockLayout.setLayoutParams(params);
            tempLayout.addView(blockLayout);

            buildImg.setOnTouchListener(moveBlock);//設置觸碰監聽
        } else if (context.getClass() == MainActivity.class) {
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.topToTop = viewGroup.getId();
            params.leftToLeft = viewGroup.getId();
            params.topMargin = this.getY();
            params.leftMargin = this.getX();
            blockLayout.setLayoutParams(params);

            viewGroup.addView(blockLayout);
        }

        // 處理手把
        moveCtrl = blockLayout.findViewById(R.id.move_ctrl); // 移動手把圖片
    }

    private final View.OnTouchListener moveBlock = new View.OnTouchListener() {
        float dx, dy;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dx = blockLayout.getX() - event.getRawX();
                    dy = blockLayout.getY() - event.getRawY();
                    //scrollView.setScrollable(false);//停止捲動功能
                    break;
                case MotionEvent.ACTION_UP:

                    //scrollView.setScrollable(true);
                    x = Math.round(blockLayout.getX());
                    y = Math.round(blockLayout.getY());

                    /**取消顯示*/
                    if (detectObjectRect()) {//目的為取消顯示
                        blockLayout.setVisibility(View.GONE);
                        new Thread(() -> {
                            diaryDatabase.getInstance(layout_context).getMapObjectDao().updateMapData(getIndex(), 0, 300);
                            diaryDatabase.getInstance(layout_context).getMapObjectDao().updateMapData(false, getIndex());
                        }).start();


                    } else {//目的不為取消顯示
                        new Thread(() -> {
                            //原本的update有點問題，所以我修正成跟block一樣的修改方式
                            diaryDatabase.getInstance(layout_context).getMapObjectDao().updateMapData(getIndex(), getX(), getY());
                        }).start();
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    //moveCtrl.setVisibility(View.VISIBLE);//顯示箭頭圖示
                    //scrollView.setScrollable(false);//停止捲動功能
                    blockLayout.animate()
                            .x(event.getRawX() + dx)
                            .y(event.getRawY() + dy)
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

    /**
     * 設置取消放置圖示
     */
    private boolean detectObjectRect() {
        System.out.println("check object touch trash can or not");
        objectRect.set(getX() - 100, getY() - 100, getX() + 100, getY() + 100);
        //System.out.println("x,y = "+getX()+","+getY());
        if (objectRect.intersect(cancelSymbolRect)) {
            System.out.println("object is touching trash can");
            return true;
        } else {
            System.out.println("object is not touching trash can");
            return false;
        }
    }
}
