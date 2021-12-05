package com.example.testfordate;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import DataBase.Building.DataUao;
import DataBase.Building.MyData;
import DataBase.Diary.Diary;
import DataBase.Diary.DiaryDao;
import DataBase.Person.DiaryDatabase;
import DataBase.Person.Person;
import DataBase.Person.PersonDao;
import DataBase.Person.PersonViewModel;
import DataBase.Picture.Picture;
import DataBase.Picture.PictureDao;
import DataBase.Store.StoreObjectDao;


public class people_create extends AppCompatActivity implements Fragment_accessories.MyListener_accessories,Fragment_hair.MyListener_hair,Fragment_face.MyListener_face,Fragment_clothes.MyListener_clothes,Fragment_pants.MyListener_pants,ViewPager.OnPageChangeListener,RadioGroup.OnCheckedChangeListener{

    RadioButton select_clothes,select_pants,select_face,select_accessories,select_hair;
    RadioGroup radioGroup; //上方清單的group
    //Button store_people;

    private List<Fragment> mList = new ArrayList<>(); //fragment的list
    private Fragment_clothes fragment_clothes = null; //fragment
    private Fragment_pants fragment_pants = null; //fragment
    private Fragment_face fragment_face = null;
    private Fragment_accessories fragment_accessories = null;
    private Fragment_hair fragment_hair = null;
    private fragmentPagerAdapter mAdapter; //切fragment的東西
    private ViewPager vpager; //切fragment的東西

    //人物穿的衣服圖片
    private ImageView cloth;
    private ImageView pant;
    private ImageView face;
    private ImageView hair;
    private ImageView accessories;

    /**
     * 資料庫
     * 將使用者所選擇的樣式存於資料庫中
     * 用以設定顯示在螢幕上的人物外貌
     */
    private PersonViewModel personViewModel;
    private Intent personIntent;
    int diaryId;
    private Button storeButton;
    private int clothType;
    private int pantType;
    private int faceType;
    private int accessoriesType;
    private int hairType;
    private int back_context;
    private int read_back_context;
    DiaryDatabase diaryDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_create);

        /**資料庫相關*/
        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);
        diaryDatabase = Room.databaseBuilder(this, DiaryDatabase.class,"diary").allowMainThreadQueries().build();
        DiaryDao diaryDao = diaryDatabase.getDiaryDao();

        /*獲取intent傳遞的值*/
        personIntent = getIntent();
        Bundle bundle = personIntent.getExtras(); //intent傳入多值

        back_context = bundle.getInt("intent");//返回頁面
        read_back_context = bundle.getInt("context");//返回頁面


        //-----------------按鈕關聯------------------------
        storeButton = findViewById(R.id.storeClothButton);
        storeButton.setOnClickListener(storeClothButtonClick);
        radioGroup = findViewById(R.id.radioGroup); //上方選擇清單的radioGroup
        select_hair = findViewById(R.id.select1);
        select_clothes = findViewById(R.id.select2);
        select_face = findViewById(R.id.select3);
        select_pants = findViewById(R.id.select4);
        select_accessories = findViewById(R.id.select5);

        select_hair.setChecked(true);//一開始選擇第一個選項
        radioGroup.setOnCheckedChangeListener(this); //偵測有沒有被改選別的

        //圖片關聯
        cloth = findViewById(R.id.cloth_1);
        pant = findViewById(R.id.pant_1);
        face = findViewById(R.id.face);
        hair = findViewById(R.id.hair_1);
        accessories = findViewById(R.id.accessories);

        //把fragment關聯後加進List
        fragment_clothes = new Fragment_clothes();
        fragment_pants = new Fragment_pants();
        fragment_face = new Fragment_face();
        fragment_accessories = new Fragment_accessories();
        fragment_hair = new Fragment_hair();
        mList.add(fragment_hair);
        mList.add(fragment_clothes);
        mList.add(fragment_face);
        mList.add(fragment_pants);
        mList.add(fragment_accessories);


        mAdapter = new fragmentPagerAdapter(getSupportFragmentManager(), mList); //宣告adapter

        bindViews(); //使用vpager

        int type = bundle.getInt("type");

        Random ran = new Random();
        Integer num = 0;//隨機挑句子

        if(type == 1){ //預測正面
            num = (ran.nextInt(3)+1);//1~3
            if(num == 1)
                faceType = R.drawable.face_1;//正面
            else if(num == 2)
                faceType = R.drawable.face_3;//正面
            else
                faceType = R.drawable.face_4;//正面

            num = (ran.nextInt(2)+1);//1~3
            if(num == 1)
                hairType = R.drawable.hair_1;
            else
                hairType = R.drawable.hair_2;

            clothType = R.drawable.cloth_1;
            pantType = R.drawable.pants_1;
            accessoriesType = R.drawable.accessories_1;

            Bundle face_bundle = new Bundle();
            face_bundle.putInt("face",faceType);
            fragment_face.setArguments(face_bundle);

            Bundle hair_bundle = new Bundle();
            hair_bundle.putInt("hair",hairType);
            fragment_hair.setArguments(hair_bundle);
        }
        else if(type == 0){//預測負面
            num = (ran.nextInt(2)+1);//1~3
            if(num == 1)
                faceType = R.drawable.face_5;
            else
                faceType = R.drawable.face_2;

            clothType = R.drawable.cloth_5;
            pantType = R.drawable.pants_1;
            //faceType = R.drawable.face_2;
            accessoriesType = R.drawable.accessories_1;
            hairType = R.drawable.hair_3;

            Bundle face_bundle = new Bundle();
            face_bundle.putInt("face",faceType);
            fragment_face.setArguments(face_bundle);

            Bundle hair_bundle = new Bundle();
            hair_bundle.putInt("hair",hairType);
            fragment_hair.setArguments(hair_bundle);

            Bundle cloth_bundle = new Bundle();
            cloth_bundle.putInt("cloth",clothType);
            fragment_hair.setArguments(cloth_bundle);
        }
        else {
            clothType = R.drawable.cloth_1;
            pantType = R.drawable.pants_1;
            faceType = R.drawable.face_1;
            accessoriesType = R.drawable.accessories_1;
            hairType = R.drawable.hair_1;
        }
        cloth.setImageResource(clothType);
        pant.setImageResource(pantType);
        face.setImageResource(faceType);
        hair.setImageResource(hairType);
        accessories.setImageResource(accessoriesType);

        if(back_context == 1){
            diaryId = bundle.getInt("DiaryID");//獲取從write diary傳來的日記id，以指定要更新衣裝的人物
            PersonDao personDao = diaryDatabase.personDao();
            new changeClothByDatabase(personDao,diaryId).execute();
        }
        else
            new getDiaryID(diaryDao).execute();

    }

    @Override //因為是宣告在fragment_clothes裡的interface 在這裡要改寫
    public void sendValue_clothes(int num) { //接到從接口回來的值
        switch (num) {
            case 0:
                cloth.setImageResource(R.drawable.cloth_1);
                clothType = R.drawable.cloth_1;
                break;
            case 1:
                cloth.setImageResource(R.drawable.cloth_2);
                clothType = R.drawable.cloth_2;
                break;
            case 2:
                cloth.setImageResource(R.drawable.cloth_3);
                clothType = R.drawable.cloth_3;
                break;
            case 3:
                cloth.setImageResource(R.drawable.cloth_4);
                clothType = R.drawable.cloth_4;
                break;
            case 4:
                cloth.setImageResource(R.drawable.cloth_5);
                clothType = R.drawable.cloth_5;
                break;


        }
    }

    @Override //因為是宣告在fragment_pants裡的interface 在這裡要改寫
    public void sendValue_pants(int num) { //接到從接口回來的值
        switch (num) {
            case 0:
                pant.setImageResource(R.drawable.pants_1);
                pantType = R.drawable.pants_1;
                break;
            case 1:
                pant.setImageResource(R.drawable.pants_2);
                pantType = R.drawable.pants_2;
                break;
            case 2:
                pant.setImageResource(R.drawable.pants_3);
                pantType = R.drawable.pants_3;
                break;
            case 3:
                pant.setImageResource(R.drawable.pants_4);
                pantType = R.drawable.pants_4;
                break;
            case 4:
                pant.setImageResource(R.drawable.pants_5);
                pantType = R.drawable.pants_5;
                break;

        }
        System.out.println(pantType);
    }

    @Override
    public void sendValue_face(int num) {
        switch (num){
            case 0:
                face.setImageResource(R.drawable.face_1);
                faceType = R.drawable.face_1;
                break;
            case 1:
                face.setImageResource(R.drawable.face_2);
                faceType = R.drawable.face_2;
                break;
            case 2:
                face.setImageResource(R.drawable.face_3);
                faceType = R.drawable.face_3;
                break;
            case 3:
                face.setImageResource(R.drawable.face_4);
                faceType = R.drawable.face_4;
                break;
            case 4:
                face.setImageResource(R.drawable.face_5);
                faceType = R.drawable.face_5;
                break;

        }
        System.out.println(faceType);
    }

    @Override
    public void sendValue_hair(int num) {
        switch (num){
            case 0:
                hair.setImageResource(R.drawable.hair_1);

                hairType = R.drawable.hair_1;
                break;
            case 1:
                hair.setImageResource(R.drawable.hair_2);
                hairType = R.drawable.hair_2;
                break;
            case 2:
                hair.setImageResource(R.drawable.hair_3);
                hairType = R.drawable.hair_3;
                break;
        }
        System.out.println(hairType);
    }

    @Override
    public void sendValue_accessories(int num) {
        switch (num){
            case 0:
                accessories.setImageResource(R.drawable.accessories_1);
                System.out.println("accessories_1");
                accessoriesType = R.drawable.accessories_1;
                break;
            case 1:
                accessories.setImageResource(R.drawable.accessories_2);
                System.out.println("accessories_2");
                accessoriesType = R.drawable.accessories_2;
                break;
        }
        System.out.println(accessoriesType);
    }

    private void bindViews() {
        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter); //利用自己寫的adapter
        vpager.setCurrentItem(0); //一開始顯示第一個fragment
        vpager.addOnPageChangeListener(this); //滑動監視
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { //改選別的會跑來這裡判斷

        switch (checkedId)
        {
            case R.id.select1: //點第一個select
                vpager.setCurrentItem(0);
                break;
            case R.id.select2://點第二個select
                vpager.setCurrentItem(1);
                break;
            case R.id.select3://點第三個select
                vpager.setCurrentItem(2);
                break;
            case R.id.select4://點第三個select
                vpager.setCurrentItem(3);
                break;
            case R.id.select5://點第三個select
                vpager.setCurrentItem(4);
                break;
        }

    }
    //重寫ViewPager頁面切換的處理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的狀態有三個，0表示什麼都沒做，1正在滑動，2滑動完畢
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case 0:
                    select_hair.setChecked(true);
                    break;
                case 1:
                    select_clothes.setChecked(true);
                    break;
                case 2:
                    select_face.setChecked(true);
                    break;
                case 3:
                    select_pants.setChecked(true);
                    break;
                case 4:
                    select_accessories.setChecked(true);
                    break;

            }
        }
    }
    class getDiaryID extends AsyncTask<Void,Void,Void> {
        private DiaryDao diaryDao;
        public getDiaryID(DiaryDao diaryDao) {
            this.diaryDao = diaryDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            diaryId = diaryDao.getNewestDiaryID();
            return null;
        }
    }

    /**store person cloth button點擊事件*/
    private View.OnClickListener storeClothButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //更新資料庫
            //System.out.println("cloth type = "+clothType+" diary id = "+diaryId);
            //System.out.println(faceType);
            System.out.println("type = "+faceType+hairType+accessoriesType);

            personViewModel.updateClothType(clothType,pantType,faceType,hairType,accessoriesType,diaryId); //?
            //返回main activity
            if(back_context == 0) {
                Intent intent = new Intent();
                intent.setClass(people_create.this, MainActivity.class);
                startActivity(intent);
            }
            else if(back_context == 1) { //read
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("DiaryID", diaryId);//此篇日記的id
                bundle.putInt("context", read_back_context);//此篇日記的id
                intent.putExtras(bundle);
                intent.setClass(people_create.this, ReadDiary.class);
                startActivity(intent);
            }
        }
    };

    class changeClothByDatabase extends AsyncTask<Void,Void,Integer> {
        private PersonDao personDao;
        //private ImageView cloth;
        private int diaryId;

        public changeClothByDatabase(PersonDao personDao,int diaryId){
            this.personDao = personDao;
            //this.cloth = cloth;
            this.diaryId = diaryId;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return 1;
        }

        @Override
        protected void onPostExecute(Integer num) {
            super.onPostExecute(num);

            //抓到資料庫內的服裝
            int cloth_db = personDao.findClothByDiaryID(diaryId);
            int pant_db = personDao.findPantByDiaryID(diaryId);
            int face_db = personDao.findFaceByDiaryID(diaryId);
            int accessories_db = personDao.findAccessoriesByDiaryID(diaryId);
            int hair_db = personDao.findHairByDiaryID(diaryId);
            //System.out.println("cloth = "+cloth_db);

            //set
            cloth.setImageResource(cloth_db);
            pant.setImageResource(pant_db);
            face.setImageResource(face_db);
            hair.setImageResource(hair_db);
            accessories.setImageResource(accessories_db);

            Bundle bundle = new Bundle();
            bundle.putInt("cloth",cloth_db);
            fragment_clothes.setArguments(bundle);

            Bundle pant_bundle = new Bundle();
            pant_bundle.putInt("pant",pant_db);
            fragment_pants.setArguments(pant_bundle);

            Bundle face_bundle = new Bundle();
            face_bundle.putInt("face",face_db);
            fragment_face.setArguments(face_bundle);

            Bundle accessories_bundle = new Bundle();
            accessories_bundle.putInt("accessories",accessories_db);
            fragment_accessories.setArguments(accessories_bundle);

            Bundle hair_bundle = new Bundle();
            hair_bundle.putInt("hair",hair_db);
            fragment_hair.setArguments(hair_bundle);

            //System.out.println(face_db);

            //store
            clothType = cloth_db;
            pantType = pant_db;
            faceType = face_db;
            accessoriesType = accessories_db;
            hairType = hair_db;
        }
    }


}
