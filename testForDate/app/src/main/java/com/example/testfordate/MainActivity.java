package com.example.testfordate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View;
import android.view.View.OnClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.room.Room;

import DataBase.Judge.JudgeDao;
import DataBase.Judge.JudgeDiary;
import DataBase.Map.MapObject;
import DataBase.Map.MapObjectDao;
import DataBase.Person.PersonViewModel;
import DataBase.Store.StoreObject;
import DataBase.Store.StoreObjectDao;
import DataBase.Person.DiaryDatabase;
import DataBase.Person.Person;
import DataBase.Person.PersonDao;
import DataBase.Building.MyData;
import DataBase.Building.DataUao;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    /*static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }*/

    private final HashMap<String,Integer> drawable_src_hashmap = new HashMap<>();

    //---------????????????-----------
    ImageButton newDiary_button;
    ImageButton calendar_button;
    ImageButton store_button;
    ImageButton move_map_object_button;
    ImageButton list;
    ImageButton history_button;
    ImageButton weathericon;
    ImageButton mapcheck;
    //Button people;

    //------??????????????????-------
    ImageButton d1;
    ImageButton d2;
    ImageButton m1;
    ImageButton m2;
    ImageButton slash;

    //--------???????????????-----------
    StoreObjectDao storeObjectDao;
    DiaryDatabase diaryDatabase;
    MapObjectDao mapObjectDao;
    DataUao buildingDao;
    PersonDao personDao;
    JudgeDao judgeDao;

    private PersonViewModel personViewModel;//view model, to implement database
    private ArrayList<Person> personArrayList=new ArrayList<>();//list of person
    private LinearLayout select_layout;

    int count=0;//number of row in database = how many people

    ConstraintLayout constraintLayout = null;
    //ConstraintLayout constraintLayout02 = null;

    ImageView backgroundImage = null;
    Rect backgroundRect = new Rect();
    int backgroundWidth=0;

    //PersonScrollView personScrollView = null;

    boolean threadRunning = true;//used to decide the thread working or not

    //----building----
    private int idCount = 1;//building ID
    //----------------
    int viewId = 0;

    //????????????????????????????????????????????????????????????????????????????????????????????????
    String system_time = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
    String year=system_time.split("/")[0];
    String month=system_time.split("/")[1];
    String day=system_time.split("/")[2];//??????????????????????????????

    Boolean show_list = false;

    //??????????????????
    private ConstraintLayout backgroundView;
    private float dx,dy;

    public float dp_view_width;
    public float dp_view_height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//?????????layout

        /**?????????hashmap?????????drawable?????????????????????R.drawable.imgSrc???*/
        drawable_src_hashmap.put("tree_2",R.drawable.tree_2);
        drawable_src_hashmap.put("tree_1",R.drawable.tree_1);
        drawable_src_hashmap.put("little_tree",R.drawable.little_tree);

        viewId = 0;


        //--------------------------????????????--------------------------------
        newDiary_button = findViewById(R.id.newDiary_button);//???????????????????????????
        calendar_button = findViewById(R.id.calendar_button);//?????????????????????
        store_button = findViewById(R.id.store_button);
        move_map_object_button=findViewById(R.id.moveMapObject);
        list = findViewById(R.id.list);
        select_layout = findViewById(R.id.layout_select);
        history_button=findViewById(R.id.checkHistory);
        mapcheck=findViewById(R.id.mapcheck);
        //people = findViewById(R.id.people);
        //final Button button = findViewById(R.id.CreateBTN);
        //Button buttonAdd = (Button) findViewById(R.id.button_addBuilding);

        //-----------------------??????????????????--------------------------
        weathericon = findViewById(R.id.weathericon);
        m1 = findViewById(R.id.m1);
        m2 = findViewById(R.id.m2);
        slash = findViewById(R.id.slash);
        d1 = findViewById(R.id.d1);
        d2 = findViewById(R.id.d2);

        //------------------------?????????????????????------------------------------
        diaryDatabase = Room.databaseBuilder(this, DiaryDatabase.class,"diary").allowMainThreadQueries().build();
        storeObjectDao = diaryDatabase.getStoreObjectDao();
        mapObjectDao = diaryDatabase.getMapObjectDao();
        buildingDao = diaryDatabase.getDataUao();
        personDao = diaryDatabase.personDao();
        judgeDao = diaryDatabase.getJudgeDao();

        //AI??????
        insertData();
        //initPython();
        //????????????
        updatedate();
        //????????????
        getJsonData();
        //????????????????????????
        //personDao.delete();

        idCount=diaryDatabase.getDataUao().countBuilding()+1;

        backgroundView = findViewById(R.id.root);
        backgroundView.setOnTouchListener(this);

        backgroundView.post(new Runnable() {
            @Override
            public void run() {
                float h = backgroundView.getHeight(); //is ready
                float w = backgroundView.getWidth(); //is ready
                setBackgroundWidth(w,h);
            }
        });

        setActivity();//initialize variable, just want to be clear
        //??????????????????
        setBuildings();
        //?????????????????????????????????????????????
        setMapObject();
        setPersonList();//don't put this in onResume, it will double the list, and then all object wont move
        //setBackgroundWidth();

        select_layout.setVisibility(View.GONE);

        Thread t = new Thread(runnable);//thread of person moving
        t.start();

        new insertMyMoney(storeObjectDao,mapObjectDao).execute();//??????????????????????????????

        //????????????
//        people.setOnClickListener(new OnClickListener() {//????????????
//            @Override
//            public void onClick(View v) {
//                Intent intent_people = new Intent();
//                intent_people.setClass(MainActivity.this,people_create.class);
//                startActivity(intent_people);
//            }
//        });

        mapcheck.setOnClickListener(new OnClickListener() {//????????????????????????????????????
            @Override
            public void onClick(View v) {
                Intent intent_map = new Intent();
                //intent_write.setClass(MainActivity.this,writeDiary.class);
                intent_map.setClass(MainActivity.this,MapsActivity.class);
                startActivity(intent_map);
            }
        });

        list.setOnClickListener(new OnClickListener() {//??????????????????
            @Override
            public void onClick(View v) {
                if(show_list){
                    select_layout.setVisibility(View.GONE);
                    show_list = false;
                }
                else{
                    select_layout.setVisibility(View.VISIBLE);
                    show_list = true;
                }
            }
        });

        newDiary_button.setOnClickListener(new OnClickListener() {//???????????????????????????????????????
            @Override
            public void onClick(View v) {
                Intent intent_write = new Intent();
                intent_write.setClass(MainActivity.this,writeDiary.class);
                startActivity(intent_write);
            }
        });

        calendar_button.setOnClickListener(new OnClickListener() {//?????????????????????????????????
            @Override
            public void onClick(View v) {
                Intent intent_calendar = new Intent();
                intent_calendar.setClass(MainActivity.this, calendar.class);
                startActivity(intent_calendar);
            }
        });
        store_button.setOnClickListener(new OnClickListener() {//?????????????????????????????????
            @Override
            public void onClick(View v) {
                Intent intent_store = new Intent();
                intent_store.setClass(MainActivity.this, store.class);
                startActivity(intent_store);
            }
        });

        move_map_object_button.setOnClickListener(new OnClickListener() {//?????????????????????????????????
            @Override
            public void onClick(View v) {
                Intent intentToMoveActivity = new Intent();
                intentToMoveActivity.setClass(MainActivity.this, ObjectMoveActivity.class);//?????????intent?????????MainActivity?????????ObjectMoveActivity
                MainActivity.this.startActivity(intentToMoveActivity);
            }
        });

        history_button.setOnClickListener(new OnClickListener() {//?????????????????????????????????
            @Override
            public void onClick(View v) {
                Intent intentToMoveActivity = new Intent();
                intentToMoveActivity.setClass(MainActivity.this, History.class);//?????????intent?????????MainActivity?????????ObjectMoveActivity
                MainActivity.this.startActivity(intentToMoveActivity);
            }
        });

    }


    public static float dp2px(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        dp_view_width = -(dp2px(this,backgroundView.getWidth()));//??????background?????????????????????px
        dp_view_height = -(dp2px(this,backgroundView.getHeight()));//??????background?????????????????????px
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //System.out.println("down rn");
                dx = view.getX() - motionEvent.getRawX();
                dy = view.getY() - motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //System.out.println("move rn");

                //??????????????????????????????????????????
                float final_x = motionEvent.getRawX() + dx;//???????????????
                float final_y = motionEvent.getRawY() + dy;//???????????????
                if(final_x < dp_view_width) //???????????????????????? ????????????????????????
                    final_x = dp_view_width;
                if(final_x > 0)
                    final_x = 0;
                if(final_y < dp_view_height) //???????????????????????? ????????????????????????
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

    static class insertMyMoney extends AsyncTask<Void,Void,Void> {
        private StoreObjectDao storeObjectDao;
        private MapObjectDao mapObjectDao;
        public insertMyMoney(StoreObjectDao storeObjectDao,MapObjectDao mapObjectDao){
            this.storeObjectDao = storeObjectDao;
            this.mapObjectDao = mapObjectDao;
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            //storeObjectDao.deleteAllStoreObject();
            //storeObjectDao.deleteStoreID();
            //mapObjectDao.deleteAllMapObject();
            //mapObjectDao.deleteMapID();


            /**??????????????????????????????????????????????????????*/
            if(storeObjectDao.getStoreNumber() == 0) { //???????????????????????? ????????????????????? ????????????????????????
                StoreObject myMoney = new StoreObject(0,"my_money"); //????????????0
                storeObjectDao.insertStoreObject(myMoney);
            }
            return null;
        }
    }
    @Override
    protected  void onStart() {
        //setPersonList();
        //threadRunning=true;
        super.onStart();
        //System.out.println("activity start");
    }

    @Override
    protected void onResume(){
        //setActivity();
        threadRunning=true;
        //setPersonList();
        super.onResume();
        //System.out.println("activity resume");
    }

    @Override
    protected void onStop(){
//      threadRunning=false;
        super.onStop();
        //System.out.println("activity stop");
    }

    @Override
    protected void onPause() {
        threadRunning=false;
        super.onPause();
        // System.out.println("activity pause");
    }

    @Override
    protected  void onDestroy() {
//        threadRunning=false;
        super.onDestroy();
        //System.out.println("activity destroy");
    }

    //thead of person moving
    final Runnable runnable = () -> {

        try {
            while (true) {
                //ProgressDialog dialog = ProgressDialog.show(this, "", "?????????");//???????????????
                Thread.sleep(500);
                runOnUiThread(() -> {
                    if (threadRunning) {//when activity is onResume
                        personRunning();
                    }

                    //dialog.dismiss();//??????????????????
                });
            }
        } catch (InterruptedException ignored) {
        }
    };

    private void setPersonList() {

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );//create constraint layout params

        params.topToTop = R.id.background;
        params.leftToLeft = R.id.background;
        params.topMargin = 0;
        params.leftMargin = 0;
        params.height=400;
        params.width=400;

        //set person list
        personArrayList.addAll(0, personViewModel.getPeople(year+"/"+month+"/%"));
        //System.out.println("get people");

        for (int p = 0; p < personArrayList.size(); p++) {

            personArrayList.get(p).initializePerson(MainActivity.this);
            personArrayList.get(p).setPersonImageView();
            //personArrayList.get(p).getPersonImageView().setId(viewId);// set id of imageView
            personArrayList.get(p).setPersonTextView();
            personArrayList.get(p).getPersonImageView().getPerson_block_layout().setLayoutParams(params);
            personArrayList.get(p).setPersonX(0);//set position to start point
            personArrayList.get(p).setPersonY(0);

            constraintLayout.addView(personArrayList.get(p).getPersonImageView().getPerson_block_layout());
            personViewModel.update(personArrayList.get(p));//initialized the position of person in database

            viewId++;
        }
    }

    private void setActivity() {
        constraintLayout = findViewById(R.id.root);
        backgroundImage = new ImageView(MainActivity.this);
        backgroundImage = findViewById(R.id.background);

        personViewModel = new ViewModelProvider(this).get(PersonViewModel.class);

        //------scroll view
        //personScrollView = findViewById(R.id.scrollview);

        //personViewModel.delete();

        //setPersonList();

        //threadRunning=true;

        count = personViewModel.count();
    }

    //??????????????????????????????(???????????????????????????)
    public void setBackgroundWidth(float width,float height) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );//create constraint layout params

        backgroundImage = new ImageView(MainActivity.this);
        backgroundImage = findViewById(R.id.background);

        //backgroundWidth = ((personViewModel.count() / 5) * 500) + 2400;//?????????person?????????500?????????
        //???????????????????????????->?????????

        params.bottomToBottom = R.id.root;
        params.leftToLeft = R.id.root;
        params.bottomMargin=0;
        params.leftMargin = 0;
        //float dp_view_width = -(dp2px(this,backgroundView.getWidth()));
        //float dp_view_height = -(dp2px(this,backgroundView.getHeight()));

        //System.out.println("test = "+dp2px(this,getHeightOfView(backgroundView)));
        //System.out.println("test = "+backgroundImage.getWidth());

        //System.out.println("test = "+width);
        //System.out.println("test = "+height);

        //System.out.println("test = "+dp2px(this,dp2px(this,getHeightOfView(backgroundView))));
        //System.out.println("test = "+dp2px(this,dp2px(this,getWidthOfView(backgroundView))));

        params.height = Math.round(height);
        params.width = Math.round(width);
        backgroundImage.setLayoutParams(params);

    }

    private void personRunning() {

        //set rectangle of background image
        backgroundRect.set(backgroundImage.getLeft(), backgroundImage.getTop(), backgroundImage.getRight(), backgroundImage.getBottom());

        for (int j = 0; j < personArrayList.size(); j++) {
            if (personArrayList.get(j).getPersonMove()) {//if person can move (not touched by user)

                /**constraint layout params
                 * ??????????????????????????????????????????person?????????Layout param
                 * (??????????????????for????????????)
                 * ???????????????????????????person????????????????????????????????????*/
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                );//create constraint layout params

                params.topToTop = R.id.background;
                params.leftToLeft = R.id.background;
                params.topMargin = personArrayList.get(j).getPersonY();
                params.leftMargin = personArrayList.get(j).getPersonX();
                params.width=400;
                params.height=400;

                //System.out.println("person "+j+" is moving");
                boolean run = true;

                int xNum = personArrayList.get(j).getPersonX();//get location from database
                int yNum = personArrayList.get(j).getPersonY();

                personArrayList.get(j).decidePersonDirection();//choose direction

                //personArrayList.get(j).setPersonDirection(1);

                if (personArrayList.get(j).getPersonDirection() == 1) {//right
                    xNum = xNum + 200;
                } else if (personArrayList.get(j).getPersonDirection() == 2) {//left
                    xNum = xNum - 200;
                } else if (personArrayList.get(j).getPersonDirection() == 3) {
                    yNum = yNum + 200;
                } else if (personArrayList.get(j).getPersonDirection() == 4) {
                    yNum = yNum - 200;
                }//new location

                //System.out.println("background = "+backgroundImage.getLeft()+","+ backgroundImage.getTop()+","+  backgroundImage.getRight()+","+  backgroundImage.getBottom());

                //set next rectangle of person j
                personArrayList.get(j).setNextPersonRect(); //?????????rect???????????????????????????

                //touch others or not?
                for (int other = 0; other < personArrayList.size(); other++) {
                    if (other != j) {
                        personArrayList.get(other).setPersonRect();
                        if (personArrayList.get(other).getPersonRect().intersect(personArrayList.get(j).getPersonRect())) {
                            //System.out.println("person " + j + " and person " + other + " are touch!");
                            run = false;
                            break;
                        }
                    }
                }

                boolean insideBackground = backgroundRect.contains(personArrayList.get(j).getPersonRect());
                int buildingCount = buildingDao.countBuilding(xNum,yNum);//????????????????????????????????????????????????

                //if(!insideBackground){
                //System.out.println("person "+j+" outside the background!");
                //}

                if ((run && insideBackground)&&(buildingCount<=0)) {
                    personArrayList.get(j).setPersonX(xNum); //????????????
                    personArrayList.get(j).setPersonY(yNum); //????????????
                    params.leftMargin = personArrayList.get(j).getPersonX();
                    params.topMargin = personArrayList.get(j).getPersonY();
                } else {
                    personArrayList.get(j).setPersonStep(0); //??????????????????
                }

                personArrayList.get(j).getPersonImageView().getPerson_block_layout().setLayoutParams(params);
                personViewModel.update(personArrayList.get(j));
            }
        }
    }

    private void setBuildings(){
        new Thread(() -> {
            List<MyData> data = buildingDao.displayAll(year+"/"+month+"/%");
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

    private void setMapObject(){
        new Thread(() -> {
            List<MapObject> data = DiaryDatabase.getInstance(MainActivity.this).getMapObjectDao().getAllMapObject(year+"/"+month+"/%");
            for (MapObject mapObject : data) {
                if (mapObject.getObject_use()) {//?????????????????????????????????????????????
                    int product_id = mapObject.getId_store_object_of_map();
                    String img_name = diaryDatabase.getStoreObjectDao().getObjectPicID(product_id);
                    int img_drawable_id = (int) drawable_src_hashmap.get(img_name);
                    runOnUiThread(() -> {
                        ProductBlock block = new ProductBlock(mapObject.getId_map_object(), mapObject.getObject_x(), mapObject.getObject_y());
                        block.buildBlock(viewId, this, constraintLayout, img_drawable_id);
                        //block.setScrollView(MainActivity.this);
                        viewId++;
                    });
                }
            }
        }).start();
    }

    private void getJsonData() {
        String JsonFile = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=rdec-key-123-45678-011121314";
        new Thread(()->{
            try{
                URL url = new URL(JsonFile);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(5000);
                connection.connect();
                int status=connection.getResponseCode();
                if(status==200){
                    //System.out.println("OK");
                }
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuffer json=new StringBuffer();
                //String line=in.readLine();//????????????
                String line = null;
                while((line=in.readLine()) != null){
                    json.append(line);
                    //line = in.readLine();
                }

                in.close();
                connection.disconnect();
                //System.out.println(json.toString());
                /*JSONObject j = new JSONObject(json.toString());
                Object jasonOb = j.getJSONObject("records").getJSONArray("location");//?????????
                for(int i=0;i<22;i++) {
                    Object City = ((JSONArray) jasonOb).get(i);
                    System.out.println(City);
                }*/
                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray location = jsonObject.getJSONObject("records").getJSONArray("location");
                for(int i=0;i<location.length();i++){
                    JSONObject jsonObject1 = location.getJSONObject(i);
                    String locationName = jsonObject1.getString("locationName");
                    if(locationName.contains("???"));
                    else
                        continue;
                    //System.out.println("??????:"+locationName);
                    //??????????????????????????????
                    JSONArray jsonObject2 = jsonObject1.getJSONArray("weatherElement");
                    //for(int j=0;j<jsonObject2.length();j++) {
                    JSONObject jsonObject3 = jsonObject2.getJSONObject(0);
                    JSONArray time = jsonObject3.getJSONArray("time");
                    SimpleDateFormat timenow = new SimpleDateFormat();
                    timenow.applyPattern("HH");
                    timenow.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                    Date date = new Date();
                    String strDate = timenow.format(date);
                    //???????????????????????????????????????????????????
                    int t = Integer.parseInt(strDate);
                    String weatherflag = null;
                    boolean flag=false;//?????????checktime????????????????????????(18:00)//??????flag
                    boolean flag2=false;
                    for(int m=0;m<time.length();m++){
                        if(flag==true && flag2==true) {
                            break;
                        }
                        JSONObject weather1 = time.getJSONObject(m);
                        String startTime = weather1.getString("startTime");
                        StringBuilder startTime1 = new StringBuilder(startTime);
                        for(int h=0;h<11;h++){
                            startTime1=startTime1.deleteCharAt(0);
                        }
                        for(int h=0;h<3;h++) {
                            startTime1 = startTime1.deleteCharAt(5);
                        }
                        char z = startTime1.toString().charAt(0);
                        int a = Integer.parseInt(String.valueOf(z));
                        char x = startTime1.toString().charAt(1);
                        int b = Integer.parseInt(String.valueOf(x));
                        int checkstarttime = a*10+b;
                            /*System.out.print("???????????? = ");
                            System.out.println(t);
                            System.out.print("???????????? = ");
                            System.out.println(checktime);*/
                        String endTime = weather1.getString("endTime");
                        StringBuilder endTime1 = new StringBuilder(endTime);
                        for(int h=0;h<11;h++){
                            endTime1=endTime1.deleteCharAt(0);
                        }
                        for(int h=0;h<3;h++){
                            endTime1=endTime1.deleteCharAt(5);
                        }
                        z = endTime1.toString().charAt(0);
                        a = Integer.parseInt(String.valueOf(z));
                        x = endTime1.toString().charAt(1);
                        b = Integer.parseInt(String.valueOf(x));
                        int checkendtime = a*10+b;
                            /*System.out.print("???????????? = ");
                            System.out.println(t);
                            System.out.print("???????????? = ");
                            System.out.println(checktime);*/
                        JSONObject weatherList = weather1.getJSONObject("parameter");
                        String weather = weatherList.getString("parameterName");
                        //System.out.println(weather);
                        if(checkendtime-checkstarttime<0){
                            flag=true;
                        }
                        if(t>=checkstarttime){
                            weatherflag=weather;
                            flag2=true;
                        }
                        else
                            continue;
                    }
                    //System.out.print("???????????? : ");
                    //System.out.println(weatherflag);
                    setWeatherIcon(weatherflag);
                    //JSONObject last = (JSONObject) jsonObject3.getJSONObject(Integer.parseInt("parameter"));
                    //String weather = last.getString("parameterName");
                    //System.out.println("??????:"+locationName);
                    //System.out.println("????????????:"+weather);
                    //}
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setWeatherIcon(String weatherstring){
        weatherstring = "??????";
        //---??????????????????????????????---
        boolean flag01 = false;//???
        boolean flag02 = false;//???
        boolean flag03 = false;//???
        if(weatherstring.contains("???"))
            flag01 = true;
        if(weatherstring.contains("???"))
            flag02 = true;
        if(weatherstring.contains("???"))
            flag03 = true;
        if(flag01==true){
            if(flag02==true){
                if(flag03==true){//cloud???rain???sunny
                    weathericon.setImageResource(R.drawable.cloudsunrain);
                }
                //cloud???sunny
                else {
                    weathericon.setImageResource(R.drawable.cloudysunny);
                }
            }
            else if(flag03==true){
                weathericon.setImageResource(R.drawable.rainy);
            }
            //sunny
            else {
                weathericon.setImageResource(R.drawable.sunny);
            }
        }
        else if(flag02==true) {
            if (flag03 == true){//cloud???rain
                weathericon.setImageResource(R.drawable.rainy);
            }
            //cloudy
            else {
                weathericon.setImageResource(R.drawable.cloudy);
            }
        }
        else if(flag03==true){
            weathericon.setImageResource(R.drawable.rainy);
        }
    }

    private void updatedate() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        //????????????
        sdf.applyPattern("MM");
        Date date =new Date();
        String month = sdf.format(date);
        int m = Integer.parseInt(month);
        //System.out.println(m);
        int month1,month2;
        if(m<10){
            month1=0;
            month2=m;
        }
        else{
            month1=m/10;
            month2=m%10;
        }
        //????????????
        sdf.applyPattern("dd");
        String day = sdf.format(date);
        int d = Integer.parseInt(day);
        //System.out.println(d);
        int day1,day2;
        if(d<10){
            day1=0;
            day2=d;
        }
        else{
            day1=d/10;
            day2=d%10;
        }
        switch (month1){
            case 0:
                m1.setImageResource(R.drawable._0);
                break;
            case 1:
                m1.setImageResource(R.drawable._1);
                break;
            case 2:
                m1.setImageResource(R.drawable._2);
                break;
            case 3:
                m1.setImageResource(R.drawable._3);
                break;
            case 4:
                m1.setImageResource(R.drawable._4);
                break;
            case 5:
                m1.setImageResource(R.drawable._5);
                break;
            case 6:
                m1.setImageResource(R.drawable._6);
                break;
            case 7:
                m1.setImageResource(R.drawable._7);
                break;
            case 8:
                m1.setImageResource(R.drawable._8);
                break;
            case 9:
                m1.setImageResource(R.drawable._9);
                break;
        }
        switch (month2){
            case 0:
                m2.setImageResource(R.drawable._0);
                break;
            case 1:
                m2.setImageResource(R.drawable._1);
                break;
            case 2:
                m2.setImageResource(R.drawable._2);
                break;
            case 3:
                m2.setImageResource(R.drawable._3);
                break;
            case 4:
                m2.setImageResource(R.drawable._4);
                break;
            case 5:
                m2.setImageResource(R.drawable._5);
                break;
            case 6:
                m2.setImageResource(R.drawable._6);
                break;
            case 7:
                m2.setImageResource(R.drawable._7);
                break;
            case 8:
                m2.setImageResource(R.drawable._8);
                break;
            case 9:
                m2.setImageResource(R.drawable._9);
                break;
        }
        switch (day1){
            case 0:
                d1.setImageResource(R.drawable._0);
                break;
            case 1:
                d1.setImageResource(R.drawable._1);
                break;
            case 2:
                d1.setImageResource(R.drawable._2);
                break;
            case 3:
                d1.setImageResource(R.drawable._3);
                break;
            case 4:
                d1.setImageResource(R.drawable._4);
                break;
            case 5:
                d1.setImageResource(R.drawable._5);
                break;
            case 6:
                d1.setImageResource(R.drawable._6);
                break;
            case 7:
                d1.setImageResource(R.drawable._7);
                break;
            case 8:
                d1.setImageResource(R.drawable._8);
                break;
            case 9:
                d1.setImageResource(R.drawable._9);
                break;
        }
        switch (day2){
            case 0:
                d2.setImageResource(R.drawable._0);
                break;
            case 1:
                d2.setImageResource(R.drawable._1);
                break;
            case 2:
                d2.setImageResource(R.drawable._2);
                break;
            case 3:
                d2.setImageResource(R.drawable._3);
                break;
            case 4:
                d2.setImageResource(R.drawable._4);
                break;
            case 5:
                d2.setImageResource(R.drawable._5);
                break;
            case 6:
                d2.setImageResource(R.drawable._6);
                break;
            case 7:
                d2.setImageResource(R.drawable._7);
                break;
            case 8:
                d2.setImageResource(R.drawable._8);
                break;
            case 9:
                d2.setImageResource(R.drawable._9);
                break;
        }
    }
    void insertData(){
        JudgeDiary content1 = new JudgeDiary(0,"???????????????????????????????????????????????????????????????????????????");
        JudgeDiary content2 = new JudgeDiary(0,"??????????????????????????????????????????????????????????????????");
        JudgeDiary content3 = new JudgeDiary(0,"??????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content4 = new JudgeDiary(0,"???????????????????????????????????????????????????????????????????????????");
        JudgeDiary content5 = new JudgeDiary(0,"???????????????????????????????????????????????????????????????");
        JudgeDiary content6 = new JudgeDiary(0,"??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content7 = new JudgeDiary(0,"?????????????????????????????????????????????????????????????????????!");
        JudgeDiary content8 = new JudgeDiary(0,"?????????????????????????????????????????????????????????????????????????????????????????????????????????!");
        JudgeDiary content9 = new JudgeDiary(0,"????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content10 = new JudgeDiary(0,"???????????????????????????????????????????????????");

        JudgeDiary content11 = new JudgeDiary(1,"?????????????????????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content12 = new JudgeDiary(1,"???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content13 = new JudgeDiary(1,"????????????????????????????????????");
        JudgeDiary content14 = new JudgeDiary(1,"??????????????????????????????");
        JudgeDiary content15 = new JudgeDiary(1,"?????????????????????????????????????????????????????????????????????");
        JudgeDiary content16 = new JudgeDiary(1,"???????????????????????????????????????????????????????????????");
        JudgeDiary content17 = new JudgeDiary(1,"?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content18 = new JudgeDiary(1,"????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
        JudgeDiary content19 = new JudgeDiary(1,"???????????????????????????????????????????????????????????????");
        JudgeDiary content20 = new JudgeDiary(1,"??????????????????????????????????????????????????????????????????");

        JudgeDiary content21 = new JudgeDiary(2,"???????????????????????????????????????????????????");
        JudgeDiary content22 = new JudgeDiary(2,"??????????????????????????????????????????100????????????");

        new insertJudgeContent(judgeDao).execute(content1,content2,content3,content4,content5,content6,content7,content8,content9,content10,content11,content12,content13,content14,content15,content16,content17,content18,content19,content20,content21,content22);
    }

    //---------------------------------------------insert?????????----------------------------------------------------
    static class insertJudgeContent extends AsyncTask<JudgeDiary,Void,Void>{
        private JudgeDao judgeDao;

        public insertJudgeContent(JudgeDao judgeDao) {
            this.judgeDao = judgeDao;
        }

        @Override
        protected Void doInBackground(JudgeDiary... judgeDiaries) {
            int num = judgeDao.dataNumber();
            if(num == 0) {
                judgeDao.insertWord(judgeDiaries);//???DB??????????????????
            }
            return null;
        }
    }
}