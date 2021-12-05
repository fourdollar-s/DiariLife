package com.example.testfordate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.ArrayList;

//import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import com.applandeo.materialcalendarview.EventDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import DataBase.Diary.Diary;
import DataBase.Diary.DiaryDao;
import DataBase.Person.DiaryDatabase;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.snackbar.Snackbar;

import com.example.testfordate.guide.guide_calendar;

import static java.lang.Integer.parseInt;


public class calendar extends AppCompatActivity {

    /**溫馨提示:
     * 執行緒:doInBackground -> 執行資料庫存取，return值會傳入onPostExecute
     * 取出之後顯示在畫面上的部分都在onPostExecute內
     * 若是需要動態新增，請將需要用到的東西通通傳入執行緒 ex.this(就是這個畫面)請額外宣告一個context來進行傳遞
     * 傳入執行緒: 在執行緒內新增private+該型態變數，並在建構子的()內傳入，下方指定this.變數 = 傳入的變數即可
     * */

    class getDiary{ //儲存要刪除、查看的日記代表句跟他的id
        String sentence;
        int id;
        String emote;
    }
    Button back_calendar; //返回按鈕
    CalendarView calendarView;
    ImageButton guide_calendar;

    DiaryDatabase diaryDatabase;
    DiaryDao diaryDao;

    ArrayList<getDiary> arrayList;
    RecyclerView recyclerView_list;
    DiaryAdapter diaryAdapter;

    /**搜尋用物件*/
    SearchView searchView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProgressDialog dialog = ProgressDialog.show(this, "", "請稍候");//跳等待視窗

        //------------資料庫------------------
        diaryDatabase = Room.databaseBuilder(this, DiaryDatabase.class,"diary").allowMainThreadQueries().build();
        diaryDao = diaryDatabase.getDiaryDao();

        //-----------載入日曆------------------
        new Thread(() -> {
            /**由於此開源庫的Calender為耗時工作，故加入背景執行使載入介面時不會閃退*/
            runOnUiThread(() -> {
                setContentView(R.layout.calendar);//載入日曆元件
                setOthers();//把其他的按鈕之類的set上去
                dialog.dismiss();//關閉等待視窗
                setSearchView();
            });
        }).start();

    }

    private void setOthers(){
        //---------------------findViewById-----------------------
        back_calendar = findViewById(R.id.back_calendar);//返回按鈕
        //all_id_from_specific_day = findViewById(R.id.show_id_calendar);//下方文字框
        calendarView = findViewById(R.id.calendarView);//日曆
        //scrollView = findViewById(R.id.scrollView); //滾動
        guide_calendar = findViewById(R.id.guide_calendar);


        //--------------------返回按鈕的偵測---------------------------------
        back_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(calendar.this,MainActivity.class);
                startActivity(intent);//返回主畫面
            }
        });
        guide_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_guide = new Intent();
                intent_guide.setClass(calendar.this, guide_calendar.class);
                startActivity(intent_guide);
            }
        });

        //------------------------呼叫執行緒--------------------------------
        new getDiaryAndMark(diaryDao,calendarView).execute();//取得所有的日記然後做好標記
        new getTodayDiaryId(diaryDao).execute();//取得今天的日記ID(因為一進去沒有點選偵測)

        //-----------------------日曆的點選偵測------------------------------
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                //System.out.println("hi我被點擊了");
                Calendar clickedDayCalendar = eventDay.getCalendar();//把選取的日期放到新的calendar變數
                new findDateDiary(diaryDao,clickedDayCalendar).execute();//傳過去進行資料庫選擇
            }
        });

        //設置RecyclerView
        recyclerView_list = findViewById(R.id.recyclerView_list);
        recyclerView_list.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//為RecyclerView每個item畫底線

    }

    //------------取得所選日期的日記id的執行緒-----------------------
    class findDateDiary extends AsyncTask<Void,Void, List<Diary>> {//<傳入,進度,結果>
        private DiaryDao diaryDao; //資料庫
        private Calendar calendar; //所選日期的calendar
        public findDateDiary(DiaryDao diaryDao , Calendar calendar){
            this.diaryDao = diaryDao;
            this.calendar = calendar;//所選日期的calendar
        }

        @Override
        protected List<Diary> doInBackground(Void... voids) {
            String date_click = new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());//把選到的時間從calendar get出來後，再從date轉成sting
            //System.out.println("click date = "+date_click);
            List<Diary> list = diaryDao.findDiaryByDate(date_click);//叫資料庫搜尋，回傳該日期所有的日記ID，型態list
            return list;//傳到OnPostExecute去做後續處理
        }

        @Override
        protected void onPostExecute(List<Diary> diaries) {//執行完會跳到這裡
            super.onPostExecute(diaries);

            arrayList = new ArrayList<>();
            if(diaries.size() != 0) { //如果該日期有日記，所以list陣列大小就會不只一個
                for (int i = 0;i < diaries.size();i++){//尋訪陣列
                    Diary diary = diaries.get(i);//一個一個取出來
                    //diary.setCalendar_context(calendar.this);

                    //更新顯示方式
                    //取得代表句跟id存進去arraylist內
                    getDiary get = new getDiary();
                    get.id=diary.getId();
                    get.sentence=diary.getDiary_sentence();
                    String label = diary.getLabel();
                    String[] label_split = label.split(",");//切割字串
                    get.emote = label_split[1];
                    //System.out.println(label_split[1]);
                    arrayList.add(get);
                }
            }
            diaryAdapter = new DiaryAdapter(arrayList); //因為arraylist會變動，因此乾脆每次都new一個
            recyclerView_list.setAdapter(diaryAdapter); //滑動偵測
            //System.out.println(arrayList.size());
            //recyclerViewAction(recyclerView_list,arrayList, diaryAdapter);
        }
    }

    //----------------取得當天日期的日記ID的執行緒---------------------
    class getTodayDiaryId extends AsyncTask<Void,Void,List<Diary>>{
        private DiaryDao diaryDao; //資料庫
        String dateToday = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime()); //取得今天日期並轉成string型態

        public getTodayDiaryId(DiaryDao diaryDao){
            this.diaryDao = diaryDao;
        }

        @Override
        protected List<Diary> doInBackground(Void... voids) {
            List<Diary> list = diaryDao.findDiaryByDate(dateToday);//叫資料庫搜尋，回傳今天所有的日記ID，型態list
            return list;
        }

        @Override
        protected void onPostExecute(List<Diary> diaries) {//執行完會跳到這裡
            super.onPostExecute(diaries);
            arrayList = new ArrayList<>();//clean array
            if(diaries.size() != 0) { //如果今天有日記，所以list陣列大小就會不只一個
                for (int i = 0;i < diaries.size();i++){//尋訪陣列
                    Diary diary = diaries.get(i);//一個一個取出來
                    //diary.setCalendar_context(calendar.this);//設置跳回來的頁面

                    //更新顯示方式
                    //取得代表句跟id存進去arraylist內
                    getDiary get = new getDiary();
                    get.id=diary.getId();
                    get.sentence=diary.getDiary_sentence();
                    String label = diary.getLabel();
                    String[] label_split = label.split(",");//切割字串
                    get.emote = label_split[1];
                    //System.out.println(label_split[1]);
                    arrayList.add(get);
                    //System.out.println(i);
                }
            }
            //因為下方的recycleView需要重新刷新 所以乾脆每一次都new新的
            diaryAdapter = new DiaryAdapter(arrayList);
            recyclerView_list.setAdapter(diaryAdapter);
            //System.out.println(arrayList.size());
            //recyclerViewAction(recyclerView_list,arrayList, diaryAdapter);
        }
    }

    //-------------取出所有的日記並把標記標上去的執行緒---------------------------------------
    class getDiaryAndMark extends AsyncTask<Void,Void,List<Diary>>{
        private DiaryDao diaryDao;
        private CalendarView calendarView; //因為要更新日曆，所以把正在使用的日曆傳進來
        public getDiaryAndMark(DiaryDao diaryDao,CalendarView calendarView){
            this.diaryDao = diaryDao;
            this.calendarView = calendarView; //指定成現在使用的日曆
        }
        @Override
        protected List<Diary> doInBackground(Void... voids) {
            return diaryDao.getAllDiaries();//把所有的日記取出來，型態List
        }

        @Override
        protected void onPostExecute(List<Diary> diaries) { //取日記的日期並在日曆上標上圖案
            super.onPostExecute(diaries);

            List<EventDay> event = new ArrayList<>(); //更新日曆用的陣列
            for(int i = 0;i < diaries.size();i++){ //用迴圈跑每個日記
                Diary diary = diaries.get(i);//一個一個取出來
                //System.out.println("date = "+diary.getDate());
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd"); //指定日期格式

                try{
                    Calendar c = Calendar.getInstance();//宣告一個新的calendar
                    c.setTime(df2.parse(diary.getDate()));//先將該日記的日期從string轉date之後，指定給剛剛宣告的calendar c
                    event.add(new EventDay(c, R.drawable.ic_baseline_book_24));//把c傳進去(日期選擇)，後面是標記的名字
                }
                catch (ParseException e){} //df2.parse要求要的例外處理
            }
            calendarView.setEvents(event); //畫面更新
        }
    }

    //-------------下方recyclerview的adapter，包含滑動偵測、畫面跳轉---------------
    private class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
        private ArrayList<getDiary> arrayList;
        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();//綁定layout

        public DiaryAdapter(ArrayList<getDiary> arrayList){
            this.arrayList = arrayList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View parent;
            private TextView tvValue;
            private ImageView emote;
            private Button btDelete,btGetInfo;
            private SwipeRevealLayout swipeRevealLayout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvValue = itemView.findViewById(R.id.textView); //顯示的文字view
                emote = itemView.findViewById(R.id.emote);
                parent = itemView;
                btDelete = itemView.findViewById(R.id.button_Delete); //滑動後顯示的buttom
                btGetInfo= itemView.findViewById(R.id.button_Show); //滑動後顯示的buttom
                swipeRevealLayout = itemView.findViewById(R.id.swipeLayout); //滑動layout

            }
        }//ViewHolder
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.item,parent,false);
            return new ViewHolder(view);
        }//

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            viewBinderHelper.setOpenOnlyOne(true);//設置swipe同時只能有一個item被拉出
            viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));//綁定Layout
            holder.tvValue.setText(arrayList.get(position).sentence);//設定顯示的文字
            //System.out.println("test = "+arrayList.get(position).emote);
            switch (arrayList.get(position).emote) {
                case "1":
                    holder.emote.setImageResource(R.drawable.notbad);
                    break;
                case "2":
                    holder.emote.setImageResource(R.drawable.happy);
                    break;
                case "3":
                    holder.emote.setImageResource(R.drawable.angry);
                    break;
                case "4":
                    holder.emote.setImageResource(R.drawable.sad);
                    break;
            }


            holder.btGetInfo.setOnClickListener((v -> { //info按鈕點選偵測
                //Toast.makeText(calendar.this,arrayList.get(position).sentence,Toast.LENGTH_SHORT).show();

                //跳到read
                Intent intent = new Intent();
                intent.setClass(calendar.this, ReadDiary.class);
                Bundle bundle = new Bundle();
                bundle.putInt("DiaryID",arrayList.get(position).id);
                bundle.putInt("context",0);//選擇回來頁面
                intent.putExtras(bundle);
                startActivity(intent);
                holder.swipeRevealLayout.close(true);//關閉已被拉出的視窗
            }));//holder.btGetInfo

            holder.btDelete.setOnClickListener((v -> { //delete按鈕點選偵測
                holder.swipeRevealLayout.close(true);//關閉已被拉出的視窗

                //警告視窗
                Snackbar.make(recyclerView_list, "確認刪除代表句為"+arrayList.get(position).sentence+"的日記嗎?", Snackbar.LENGTH_LONG).setAction("yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //new updateMark(diaryDao,calendarView,arrayList.get(position).id).execute();
                        new deleteDiary(calendar.this,arrayList.get(position).id).execute();
                        arrayList.remove(position); //移除arraylist該position的內容
                        notifyItemRemoved(position);//通知item
                        notifyItemRangeChanged(position,arrayList.size()); //更改size
                        new getDiaryAndMark(diaryDao,calendarView).execute(); //更新日曆顯示的icon
                    }
                }).show();

            }));//holder.btDelete

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

    }



    class deleteDiary extends AsyncTask<Void,Void,Void>{
        //private DiaryDao diaryDao;
        Context context;
        int id;
        public deleteDiary(Context context,int id){
            this.context = context;
            this.id = id;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            diaryDatabase = Room.databaseBuilder(context, DiaryDatabase.class,"diary").build();//呼叫資料庫
            diaryDatabase.getDiaryDao().deleteDiaryByID(id);
            return null;
        }
    }

    /**search view的監聽事件*/
    private void setSearchView(){
        searchView=findViewById(R.id.searchDiary);//搜尋
        searchView.setOnQueryTextListener(actSearch);
        //System.out.println("setting search view");
    }

    SearchView.OnQueryTextListener actSearch = new SearchView.OnQueryTextListener() {
        @Override
        //按下enter觸發
        public boolean onQueryTextSubmit(String query) {
            //System.out.println("string="+query);
            new calendar.getAllDiaryWithString(calendar.this,query).execute();
            return false;
        }

        //欄位內容更換觸發
        @Override
        public boolean onQueryTextChange(String newText) {
            //System.out.println("string="+newText);
            return false;
        }
    };

    //執行緒
    static class getAllDiaryWithString extends AsyncTask<Void, Void, List<Diary>> {
        private Context context;//畫面
        private String s;//搜尋的字串

        public getAllDiaryWithString(Context context,String s) {
            this.context = context;
            this.s=s;
        }

        @Override
        protected List<Diary> doInBackground(Void... voids) {
            DiaryDatabase diaryDatabase = Room.databaseBuilder(context, DiaryDatabase.class, "diary").build(); //建置資料庫
            List<Diary> list = diaryDatabase.getDiaryDao().getAllDiaryWithString(s+"%","%"+s); //取得這個日期所有的日記
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
                    bundle.putInt("context", 0);//選擇回來頁面

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
