package com.example.testfordate;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import DataBase.Building.MyData;
import DataBase.Diary.Diary;
import DataBase.Diary.DiaryDao;
import DataBase.Person.DiaryDatabase;
import DataBase.Person.PersonDao;
import DataBase.Person.PersonViewModel;
import DataBase.Picture.Picture;
import DataBase.Picture.PictureDao;

import android.net.Uri;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.testfordate.guide.MyViewPagerAdapter;


public class ReadDiary extends AppCompatActivity {

    Intent intent = new Intent();
    Intent backIntent = new Intent();

    TextView date;
    Button updateDiary;
    TextView diaryText;
    TextView sentence;
    ImageView weather;
    ImageView emote;
    //String cloth;

    int contextNum;
    int diaryID;

    PersonBlock personBlock;
    ConstraintLayout people_layout;

    DiaryDatabase diaryDatabase;
    DiaryDao diaryDao;
    PictureDao pictureDao;
    PersonViewModel personViewModel;

    private ViewPager viewPager;
    private ArrayList<View> viewPager_List;
    private RadioGroup radioGroup;
    private List<Integer> radioButtonID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_diary);

        //person viewmodel
        personViewModel=new ViewModelProvider(this).get(PersonViewModel.class);

        //System.out.println("read onCreate");

        /*資料庫*/
        diaryDatabase= Room.databaseBuilder(this, DiaryDatabase.class,"diary").allowMainThreadQueries().build();
        diaryDao=diaryDatabase.getDiaryDao();
        pictureDao = diaryDatabase.getPictureDao();

        date = findViewById(R.id.date);
        updateDiary = findViewById(R.id.update_diary);
        diaryText = findViewById(R.id.diaryText);
        weather = findViewById(R.id.weather);
        emote = findViewById(R.id.emote);
        sentence = findViewById(R.id.sentence);
        people_layout = findViewById(R.id.people_layout);

        //---viewpager宣告---
        viewPager = findViewById(R.id.vpager);
        //---下方分頁按鈕宣告---
        radioGroup = findViewById(R.id.radioGroup);
        viewPager_List = new ArrayList<View>();


        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(listener);

        personBlock = new PersonBlock(this);

        intent=getIntent();


        /*獲取intent傳遞的值*/
        //System.out.println("first times here");
        Bundle bundle = intent.getExtras(); //intent傳入多值
        diaryID=bundle.getInt("DiaryID"); //欲察看的日記id
        contextNum = bundle.getInt("context"); //選擇返回頁面用

        new getDiaryInfo(this,diaryID,diaryText,date,weather,emote,sentence,people_layout).execute();
        new getImage(diaryID).execute();

        final Button backBtn = findViewById(R.id.backBTN);
        backBtn.setOnClickListener(v -> { //選擇返回哪個頁面
            if(contextNum == 0)
                backIntent.setClass(ReadDiary.this,calendar.class); //返回日歷頁面
            else if(contextNum == 1)
                backIntent.setClass(ReadDiary.this, MainActivity.class); //返回主畫面
            ReadDiary.this.startActivity(backIntent);
        });
        updateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getContent1 = sentence.getText().toString();
                diaryDao.updatediarysentence(getContent1,diaryID);
                String getContent2 = diaryText.getText().toString();
                diaryDao.updatediaryinfo(getContent2,diaryID);
                personViewModel.updatePersonText(getContent1,diaryID);
                backBtn.performClick();
            }
        });
    }

    private Bitmap setImageByPath(String path){
        AtomicReference<Bitmap> getHighImage = new AtomicReference<>(BitmapFactory.decodeFile(path));

        float height = getHighImage.get().getHeight();
        float width = getHighImage.get().getWidth();

        float aspectRatio = width/height;
        float width_select = 480;
        float height_select = width_select / aspectRatio;

        Matrix matrix = new Matrix();
        matrix.postScale(width_select,height_select);
        matrix.setRotate(0f);//轉90度
        //設定大小
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(getHighImage.get(), (int)width_select, (int)height_select, true);

        return resizedBitmap;
    }

    class getDiaryInfo extends AsyncTask<Void, Void, Diary> {
        private Context context;//畫面
        private int id;
        private TextView diaryText;
        private TextView date;
        private ImageView weather;
        private ImageView emote;
        private TextView sentence;
        private ConstraintLayout people_layout;
        private int cloth;
        private int pant;
        private int face;
        private int accessories;
        private int hair;


        public getDiaryInfo(Context context, int id ,TextView DiaryText, TextView Date,ImageView weather,ImageView emote , TextView sentence,ConstraintLayout people_layout) {
            this.context = context;
            this.id = id;
            this.diaryText = DiaryText;
            this.date = Date;
            this.weather = weather;
            this.emote = emote;
            this.sentence = sentence;
            this.people_layout = people_layout;
        }

        @Override
        protected Diary doInBackground(Void... voids) {
            DiaryDatabase diaryDatabase = Room.databaseBuilder(context, DiaryDatabase.class, "diary").build(); //建置資料庫
            Diary diary = diaryDatabase.getDiaryDao().findDiaryByID(id); //取得日記

            PersonDao personDao = diaryDatabase.personDao();
            cloth = personDao.findClothByDiaryID(diary.getId());//找到cloth id
            pant = personDao.findPantByDiaryID(diary.getId());//找到cloth id
            face = personDao.findFaceByDiaryID(diary.getId());
            accessories = personDao.findAccessoriesByDiaryID(diary.getId());
            hair = personDao.findHairByDiaryID(diary.getId());


            return diary;
        }

        //更改UI介面
        @Override
        protected void onPostExecute(Diary diary) {
            super.onPostExecute(diary);
            //System.out.println("text = "+diary.getDiary_info()+" "+diary.getDate());
            diaryText.setText(diary.getDiary_info());
            date.setText(diary.getDate());
            sentence.setText(diary.getDiary_sentence());

            personBlock.setPerson_block_layout(cloth,pant,face,hair,accessories);//引入person.xml layout，設置圖片
            people_layout.addView(personBlock.getPerson_block_layout());

            personBlock.getPerson_block_layout().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("diaryID", diary.getId());//此篇日記的id
                    bundle.putInt("context", contextNum);//read回去的頁面偵測
                    bundle.putInt("intent", 1);//people回去的頁面偵測
                    intent.setClass(ReadDiary.this, people_create.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            String label = diary.getLabel();
            String[] label_split = label.split(",");//切割字串

            //weather
            switch (label_split[0]) {
                case "1":
                    weather.setImageResource(R.drawable.ic_baseline_wb_sunny_24);
                    break;
                case "2":
                    weather.setImageResource(R.drawable.ic_baseline_filter_drama_24);
                    break;
                case "3":
                    weather.setImageResource(R.drawable.ic_baseline_bedtime_24);
                    break;
            }

            //System.out.println("emote = "+label_split[1]);

            switch (label_split[1]) {
                case "1":
                    emote.setImageResource(R.drawable.notbad);
                    break;
                case "2":
                    emote.setImageResource(R.drawable.happy);
                    break;
                case "3":
                    emote.setImageResource(R.drawable.angry);
                    break;
                case "4":
                    emote.setImageResource(R.drawable.sad);
                    break;
            }
        }
    }

    class getImage extends AsyncTask<Void, Void, List<Picture>> {
        private int id;

        public getImage(int id) {
            this.id = id;
        }

        @Override
        protected List<Picture> doInBackground(Void... voids) {
            return pictureDao.getPictureByID(id);
        }

        //更改UI介面
        @Override
        protected void onPostExecute(List<Picture> pictures) {
            super.onPostExecute(pictures);

            for(int i = 0;i<pictures.size();i++){
                Picture p = pictures.get(i);

                //String url = p.getLink();
                if(p.getLink_type() == 0 || p.getLink_type() == 1) {
                    Bitmap b;
                    System.out.println("read圖片");

                    b = setImageByPath(p.getLink());

                    ImageView imageView = new ImageView(ReadDiary.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                    //imageView.setScaleType(ImageView.ScaleType.CENTER);

                    imageView.setImageBitmap(b);
                    imageView.setLayoutParams(params);
                    viewPager_List.add(imageView);

                    /**放大圖片的點擊事件
                     * 參考資料：https://blog.csdn.net/wxlSAMA/article/details/94355377*/
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = LayoutInflater.from(ReadDiary.this);
                            View imgEntryView = inflater.inflate(R.layout.show_pic_dialog,null);

                            final AlertDialog dialog = new AlertDialog.Builder(ReadDiary.this).create();

                            ImageView img = imgEntryView.findViewById(R.id.pic);
                            img.setImageBitmap(b);
                            Glide.with(ReadDiary.this).load(b).into(img);
                            dialog.setView(imgEntryView);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.show();

                            imgEntryView.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            });
                        }
                    });
                }
                else if(p.getLink_type() == 2) { //影片


                    ImageView imageView = new ImageView(ReadDiary.this);
                    imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(p.getLink(),
                            MediaStore.Video.Thumbnails.MICRO_KIND));//縮圖


                    viewPager_List.add(imageView);


                    LayoutInflater inflater = LayoutInflater.from(ReadDiary.this);
                    View imgEntryView = inflater.inflate(R.layout.show_video_layout,null);
                    final AlertDialog dialog = new AlertDialog.Builder(ReadDiary.this).create();

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            VideoView vv = imgEntryView.findViewById(R.id.videoview);

                            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                            vv.setVideoPath(p.getLink());
                            vv.setLayoutParams(param);


                            vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    // TODO Auto-generated method stub
                                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                        @Override
                                        public void onVideoSizeChanged(MediaPlayer mp,
                                                                       int width, int height) {
                                            /*
                                             * add media controller
                                             */
                                            MediaController mc = new MediaController(ReadDiary.this);
                                            vv.setMediaController(mc);
                                            /*
                                             * and set its position on screen
                                             */
                                            mc.setAnchorView(vv);

                                            ((ViewGroup) mc.getParent()).removeView(mc);
                                            FrameLayout f = imgEntryView.findViewById(R.id.videoViewWrapper);
                                            System.out.println("f = "+f);
                                            f.addView(mc);

                                            mc.setVisibility(View.VISIBLE);
                                            mc.show();

                                        }
                                    });
                                    vv.start();
                                }
                            });


                            dialog.setView(imgEntryView);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.show();
                        }
                    });

                    Button button = imgEntryView.findViewById(R.id.video_cancel);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                }



                RadioButton radioButton = new RadioButton(ReadDiary.this);
                radioButton.setBackgroundResource(R.drawable.radiobutton_bg_selector);
                RadioGroup.LayoutParams rlTable;
                rlTable = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT
                        , RadioGroup.LayoutParams.WRAP_CONTENT);
                rlTable.height = 15;
                rlTable.width = 15;

                radioGroup.addView(radioButton,rlTable);
                viewPager.setAdapter(new MyViewPagerAdapter(viewPager_List));
                radioButtonID.add(radioButton.getId());
            }
            int id = 1;
            radioGroup.check(id);

        }
    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
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
                //System.out.println("store = "+radioButtonID.get(viewPager.getCurrentItem()));
                radioGroup.check(radioButtonID.get(viewPager.getCurrentItem()));
            }
        }
    };
}