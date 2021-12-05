package com.example.testfordate;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import DataBase.Map.MapObject;
import DataBase.Map.MapObjectDao;
import DataBase.Store.StoreObject;
import DataBase.Store.StoreObjectDao;
import DataBase.Person.DiaryDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testfordate.guide.guide_store;

public class store extends AppCompatActivity {
    StoreObjectDao storeObjectDao;
    DiaryDatabase diaryDatabase;
    MapObjectDao mapObjectDao;
    ImageButton tree,little_tree,tree_2;
    TextView myMoney_textView;
    TextView money_tree,money_littleTree,money_tree_2;

    Button back_store;

    LiveData<List<StoreObject>> myMoney_live;

    ImageButton guide_store;

    boolean buy;//判別使用者是否有在商店內購買商品

    //取得系統時間，切割出年份與月份，供判斷哪些房子要放置於畫面上使用
    String system_time = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);

        diaryDatabase = Room.databaseBuilder(this, DiaryDatabase.class,"diary").allowMainThreadQueries().build();
        storeObjectDao = diaryDatabase.getStoreObjectDao();
        mapObjectDao = diaryDatabase.getMapObjectDao();

        tree = findViewById(R.id.tree_1_button);
        little_tree = findViewById(R.id.little_tree_button);
        tree_2 = findViewById(R.id.tree_2_button);
        back_store = findViewById(R.id.back_store);
        myMoney_textView = findViewById(R.id.myMoney);
        money_tree = findViewById(R.id.money_tree);
        money_littleTree = findViewById(R.id.money_littleTree);
        money_tree_2 = findViewById(R.id.money_tree_2);
        guide_store = findViewById(R.id.guide_store);

        buy=false;//預設使用者無購買商品

        new insertAll(storeObjectDao).execute();

        new setAllObjectMoney(storeObjectDao,money_tree,money_littleTree,money_tree_2).execute();

        /**退出商店按鈕
         * 若有購買商品，移動到move object頁面
         * 若無購買商品，移動到main頁面*/
        back_store.setOnClickListener(new View.OnClickListener() {//頁面跳轉
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(buy){
                    intent.setClass(store.this,ObjectMoveActivity.class);
                }
                else{
                    intent.setClass(store.this,MainActivity.class);
                }
                startActivity(intent);
            }
        });

        tree.setOnClickListener(new View.OnClickListener() {//按下樹的那個按鈕
            @Override
            public void onClick(View v) {
                new buyNewObject(storeObjectDao,mapObjectDao).execute("tree_1");
            }
        });

        little_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new buyNewObject(storeObjectDao,mapObjectDao).execute("little_tree");
            }
        });

        tree_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new buyNewObject(storeObjectDao,mapObjectDao).execute("tree_2");
            }
        });

        guide_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_guide = new Intent();
                intent_guide.setClass(store.this, guide_store.class);
                startActivity(intent_guide);
            }
        });

        //--------------------------------------持有金額顯示--------------------------------------------------------
        //只能放在主線程
        myMoney_live = storeObjectDao.getMyMoney_live();//資料有改就會更改
        myMoney_live.observe(this, new Observer<List<StoreObject>>() {
            @Override
            public void onChanged(List<StoreObject> storeObjects) {//當數據改變的時候呼叫
                String text = "";
                StoreObject storeObject = storeObjects.get(0);
                text += storeObject.getMoney_needed();

                myMoney_textView.setText(text);
            }
        });




    }

    static class insertAll extends AsyncTask<Void,Void,Void>{
        private StoreObjectDao storeObjectDao;
        public insertAll(StoreObjectDao storeObjectDao){
            this.storeObjectDao = storeObjectDao;
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            if(storeObjectDao.getStoreNumber() == 1) {
                StoreObject storeObject = new StoreObject(20,"tree_1");
                StoreObject storeObject2 = new StoreObject(15,"tree_2");
                StoreObject storeObject1 = new StoreObject(10,"little_tree");
                storeObjectDao.insertStoreObject(storeObject,storeObject1,storeObject2);
            }
            return null;
        }
    }

    class buyNewObject extends AsyncTask<String,Void,Void>{
        private MapObjectDao mapObjectDao;
        private StoreObjectDao storeObjectDao;

        public buyNewObject(StoreObjectDao storeObjectDao,MapObjectDao mapObjectDao){
            this.mapObjectDao = mapObjectDao;
            this.storeObjectDao = storeObjectDao;
        }

        @Override
        protected Void doInBackground(String... strings) {

            int myMoney = storeObjectDao.getMyMoney();
            int neededMoney = storeObjectDao.getObjectMoney(strings[0]);
            if(myMoney >= neededMoney){
                buy=true;//金額足夠，使用者有進行購買
                int tree_id = storeObjectDao.getObjectID(strings[0]);
                MapObject tree = new MapObject(tree_id,0,300,system_time);
                mapObjectDao.insertMapObject(tree);

                int newMyMoney = myMoney - neededMoney;
                storeObjectDao.updateMyMoney(newMyMoney);
            }
            return null;
        }
    }

    class setAllObjectMoney extends AsyncTask<Void,Void,List<StoreObject>>{
        private StoreObjectDao storeObjectDao;
        private TextView money_tree,money_littleTree,money_tree_2;
        public setAllObjectMoney(StoreObjectDao storeObjectDao,TextView money_tree,TextView money_littleTree,TextView money_catYellow){
            this.money_tree_2 = money_catYellow;
            this.money_littleTree = money_littleTree;
            this.money_tree = money_tree;
            this.storeObjectDao = storeObjectDao;
        }

        @Override
        protected List<StoreObject> doInBackground(Void... voids) {
            List<StoreObject> allObject = storeObjectDao.getAllStoreObject();
            return allObject;
        }

        @Override
        protected void onPostExecute(List<StoreObject> storeObjects) {
            super.onPostExecute(storeObjects);
            for(int i = 0;i < storeObjects.size();i++) {
                StoreObject storeObject = storeObjects.get(i);
                String name = storeObject.getPic_id();
                String neededMoney = Integer.toString(storeObject.getMoney_needed());

                //System.out.println("need = "+neededMoney+name);
                if (name.equals("tree_1")) {
                    //System.out.println("hello"+1);
                    money_tree.setText(neededMoney);
                }
                if (name.equals("little_tree")) {
                    //System.out.println("hello"+2);
                    money_littleTree.setText(neededMoney);
                }
                if (name.equals("tree_2")) {
                    //System.out.println("hello"+3);
                    money_tree_2.setText(neededMoney);
                }
            }
        }
    }


}
