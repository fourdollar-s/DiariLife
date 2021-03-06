package com.example.testfordate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import DataBase.Building.DataUao;
import DataBase.Building.MyData;
import DataBase.Diary.Diary;
import DataBase.Diary.DiaryDao;
import DataBase.Judge.JudgeDao;
import DataBase.Person.Person;
import DataBase.Person.PersonDao;
import DataBase.Picture.Picture;
import DataBase.Picture.PictureDao;
import DataBase.Store.StoreObjectDao;
import DataBase.Person.DiaryDatabase;
//import DataBase.Person.PersonViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.testfordate.guide.MyViewPagerAdapter;
import com.example.testfordate.guide.guide_writediary;


class image_db{
    int type;
    String path;
}

public class writeDiary extends AppCompatActivity {
    DiaryDatabase diaryDatabase;
    DiaryDao diaryDao;
    StoreObjectDao storeObjectDao;
    PersonDao personDao;
    DataUao dataUao;
    JudgeDao judgeDao;
    PictureDao pictureDao;

    Button back_diary;
    Button Insert;
    ImageButton camera;
    ImageButton album;
    TextView diary;
    TextView sentence;
    ImageButton guide_write;

    String weather = "sunny";
    String emote = "notbad";

    private Spinner weather_spinner = null;
    private Spinner emote_spinner = null;

    private ArrayList<spinner_item_weather> weather_list;
    private ArrayList<spinner_item_weather> emote_list;
    private spinnerAdapter spinnerAdapter_weather;
    private spinnerAdapter spinnerAdapter_emote;

    ConstraintLayout constraintLayout = null;

    private String mPath;//??????????????????
    private String select_path;
    //private Uri uriPath;//???????????????uri
    public static final int CAMERA_PERMISSION = 100;//?????????????????????

    //???????????????view?????????pager
    private ViewPager viewPager; //??????
    private ArrayList<View> viewPager_List; //??????????????????list
    private RadioGroup radioGroup; //????????????radio
    private List<Integer> radioButtonID = new ArrayList<>(); //???????????????button???id???list
    private List<image_db> image_dbs = new ArrayList<>(); //???????????????image????????????????????????path??????uri?????????List

    MediaPlayer mPlayer;

    /**???????????????????????????*/
    private Intent intent;
    private Bundle bundle;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_diary);

        diaryDatabase = Room.databaseBuilder(this, DiaryDatabase.class, "diary").allowMainThreadQueries().build();
        diaryDao = diaryDatabase.getDiaryDao();
        storeObjectDao = diaryDatabase.getStoreObjectDao();
        personDao = diaryDatabase.personDao();
        dataUao = diaryDatabase.getDataUao();
        judgeDao = diaryDatabase.getJudgeDao();
        pictureDao = diaryDatabase.getPictureDao();


        //textView = findViewById(R.id.textView);
        diary = findViewById(R.id.diary);
        Insert = (Button) findViewById(R.id.Insert);
        back_diary = findViewById(R.id.back_diary);
        constraintLayout = findViewById(R.id.root);
        weather_spinner = findViewById(R.id.spinner_weather);
        emote_spinner = findViewById(R.id.spinner_emote);
        sentence = findViewById(R.id.sentence);
        camera = findViewById(R.id.camera);
        album = findViewById(R.id.album);
        guide_write = (ImageButton) findViewById(R.id.guide_write);



        viewPager = findViewById(R.id.vpager);
        radioGroup = findViewById(R.id.radioGroup);
        viewPager_List = new ArrayList<View>();

        viewPager.setCurrentItem(0); //??????????????????
        viewPager.addOnPageChangeListener(listener); //????????????listener

        /**???????????????????????????*/
        intent = new Intent();
        bundle = new Bundle();
        //insertDiary=false;

        initList();

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        spinnerAdapter_weather = new spinnerAdapter(this,weather_list);
        weather_spinner.setAdapter(spinnerAdapter_weather);

        spinnerAdapter_emote = new spinnerAdapter(this,emote_list);
        emote_spinner.setAdapter(spinnerAdapter_emote);

        weather_spinner.setSelection(0,false);
        emote_spinner.setSelection(0,false);

        //py = Python.getInstance();

        guide_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_guide = new Intent();
                intent_guide.setClass(writeDiary.this, guide_writediary.class);
                startActivity(intent_guide);
            }
        });

        weather_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_item_weather clickedItem = (spinner_item_weather) parent.getItemAtPosition(position);
                String clickedWeatherName = clickedItem.getWeatherName();

                weather = clickedWeatherName; //????????????
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        emote_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_item_weather clickedItem = (spinner_item_weather) parent.getItemAtPosition(position);
                String clickedWeatherName = clickedItem.getWeatherName();

                emote = clickedWeatherName; //????????????

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getContent = diary.getText().toString();
                String getSentence = sentence.getText().toString();
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());

                String label = "0";

                if(getContent.isEmpty())
                    Toast.makeText(writeDiary.this,"error_didn't_Write_Diary",Toast.LENGTH_SHORT).show();
                else if(getSentence.isEmpty())
                    Toast.makeText(writeDiary.this,"error_didn't_Write_Sentence",Toast.LENGTH_SHORT).show();
                else {
                    switch (weather) {
                        case "sunny":
                            label = "1";
                            break;
                        case "cloud":
                            label = "2";
                            //System.out.println("??????2_back");
                            break;
                        case "moon":
                            label = "3";
                            break;
                    }
                    switch (emote) {
                        case "notbad":
                            label += ",1";
                            break;
                        case "happy":
                            label += ",2";
                            break;
                        case "angry":
                            label += ",3";
                            break;
                        case "sad":
                            label += ",4";
                            break;
                    }

                    Diary get = new Diary(getContent, timeStamp, label ,getSentence);

                    new insertDiary(diaryDao, storeObjectDao, personDao, dataUao,pictureDao).execute(get);

                    int judge_type;

                    Python py = Python.getInstance();
                    PyObject result = py.getModule("AImodule").callAttr("judge", getContent);//??????python
                    judge_type = result.toJava(Integer.class);//??????????????????(int)????????????java???integer??????


                    Random ran = new Random();
                    Integer num = 0;//???????????????
                    String show_content = "";
                    if(judge_type == 0) {
                        num = (ran.nextInt(10)+1);//1~10??????????????? ????????????10???
                        show_content = judgeDao.findDataByIdAndType_judge(num,judge_type);//?????????????????????
                    }
                    if(judge_type == 1) {
                        num = (ran.nextInt(10)+11);//11~20??????????????? ????????????10???
                        show_content = judgeDao.findDataByIdAndType_judge(num,judge_type);//?????????????????????
                    }
                    if(judge_type == 2){//?????????????????????
                        num = 21;
                        show_content = judgeDao.findDataByIdAndType_judge(num,judge_type);//?????????????????????
                    }

                    new updateType(diaryDao,judge_type,get.getId());

                    AlertDialog.Builder goLogin = new AlertDialog.Builder(writeDiary.this);
                    goLogin.setMessage(show_content);
                    goLogin.setCancelable(false);
                    goLogin.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            bundle.putInt("DiaryID", get.getId());//?????????????????????id
                            bundle.putInt("intent", 0);//???????????????????????????
                            bundle.putInt("type",judge_type);
                            intent.setClass(writeDiary.this, people_create.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alertLogin = goLogin.create();
                    alertLogin.show();

                    //System.out.println("num = " + num);
                    //System.out.println("type = " + judge_type);
                    //System.out.println("content = " + show_content);
                    //new selectJudgeContent(judgeDao,writeDiary.this,getContent).execute();
                }
            }
        });

        back_diary.setOnClickListener(new View.OnClickListener() {//????????????
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(writeDiary.this, MainActivity.class);
                startActivity(intent);
            }
        });

        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent highIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //???????????????intent
                //???????????????????????????
                if (highIntent.resolveActivity(getPackageManager()) == null) {
                    System.out.println("???????????????");
                    ActivityCompat.requestPermissions(writeDiary.this,new String[]{Manifest.permission.CAMERA},1);
                }
                //?????????????????????URI???????????????????????????
                File imageFile = getImageFile();
                if (imageFile == null) {
                    System.out.println("???????????????????????????");
                    return;
                }
                //?????????????????????URI??????
                Uri imageUri = FileProvider.getUriForFile(
                        writeDiary.this,
                        "com.example.testfordate.CameraEx",//????????????AndroidManifest.xml??????authorities ??????
                        imageFile
                );
                highIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                someActivityResultLauncher.launch(highIntent); //????????????
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(writeDiary.this, album);
                popupMenu.getMenuInflater().inflate(R.menu.picture_or_video_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.picture:
                                Intent photoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                photoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                photoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);//????????????????????????
                                photoIntent.setType("image/*");
                                getPicFromPhoneLauncher.launch(photoIntent); //????????????
                                break;
                            case R.id.video:
                                Intent videoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                videoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                videoIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);//????????????????????????
                                videoIntent.setType("video/*");
                                getVideoFromPhoneLauncher.launch(videoIntent); //????????????
                                break;

                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

    }

    private void initList() {
        weather_list = new ArrayList<>();
        weather_list.add(new spinner_item_weather("sunny", R.drawable.ic_baseline_wb_sunny_24));
        weather_list.add(new spinner_item_weather("cloud", R.drawable.ic_baseline_filter_drama_24));
        weather_list.add(new spinner_item_weather("moon", R.drawable.ic_baseline_bedtime_24));

        emote_list = new ArrayList<>();
        emote_list.add(new spinner_item_weather("notbad",R.drawable.notbad));
        emote_list.add(new spinner_item_weather("happy",R.drawable.happy));
        emote_list.add(new spinner_item_weather("angry",R.drawable.angry));
        emote_list.add(new spinner_item_weather("sad",R.drawable.sad));
    }

    class insertDiary extends AsyncTask<Diary,Void,Integer> {
        private DiaryDao diaryDao;
        private StoreObjectDao storeObjectDao;
        private PersonDao personDao;
        private DataUao dataUao;
        private PictureDao pictureDao;
        public insertDiary(DiaryDao diaryDao,StoreObjectDao storeObjectDao,PersonDao personDao , DataUao dataUao,PictureDao pictureDao){
            this.diaryDao = diaryDao;
            this.storeObjectDao = storeObjectDao;
            this.personDao = personDao;
            this.dataUao = dataUao;
            this.pictureDao = pictureDao;
        }

        @Override
        protected Integer doInBackground(Diary... diaries) {
            //??????????????????????????????????????????????????????
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
            List<Diary> list = diaryDao.findDiaryByDate(timeStamp);//???????????????????????????????????????????????????ID?????????list
            //List<MyData> getBuilding = dataUao.findBuildingByDate(timeStamp);
            if(list.size()==0){
                MyData buildData = new MyData(0,500,timeStamp);
                //diaryDatabase = Room.databaseBuilder(context, DiaryDatabase.class,"diary").build();//???????????????
                dataUao.insertBuildingObject(buildData);//??????
                //idCount++;
                //viewId++;
            }

            diaryDao.insertDiaries(diaries); //????????????
            int myMoney = storeObjectDao.getMyMoney();
            myMoney += 50; //??????
            storeObjectDao.updateMyMoney(myMoney); //?????????????????????????????????
            int diaryID = diaryDao.getNewestDiaryID();
            //String sentence = diaries[0].getDiary_sentence();
            //System.out.println("sentence = "+sentence+diaryID);

            //????????????
            Person person = new Person(diaryID,0,0,sentence.getText().toString(),R.drawable.cloth_1,R.drawable.pants_1,R.drawable.face_1,R.drawable.hair_1,R.drawable.accessories_1,timeStamp);//???????????????????????????????????????
            //insertDiary=true;//???insert diary??????true?????????????????????????????????
            personDao.insert(person);

            for(int i = 0;i<image_dbs.size();i++){
                Picture p;
                p = new Picture(diaryID, image_dbs.get(i).type, image_dbs.get(i).path);
                pictureDao.insertPicture(p);
            }

            return null;
        }
    }

    class updateType extends AsyncTask<Void,Void,Void>{
        private DiaryDao diaryDao;
        private Integer judge_type;
        private int id;

        public updateType(DiaryDao diaryDao,int type,int id) {
            this.judge_type = type;
            this.diaryDao = diaryDao;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            diaryDao.updateType(judge_type,id);
            return null;
        }
    }

    /**?????????????????????URI???????????????????????????*/
    private File getImageFile()  {
        String time = new SimpleDateFormat("yyMMdd").format(new Date());
        String fileName = time+"_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            //?????????????????????????????????
            File imageFile = File.createTempFile(fileName,".jpg",dir);
            //???????????????????????????????????????????????????????????????
            mPath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            return null;
        }
    }


    private File getFileByUri(Uri uri,Context context)  {
        //String time = new SimpleDateFormat("yyMMdd").format(new Date());

        File videoFile = null;
        if(uri.getScheme().equals(ContentResolver.SCHEME_FILE))
            videoFile = new File(uri.getPath());
        else if(uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver resolver = context.getContentResolver();
            String fileName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000) + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(resolver.getType(uri));

            try {
                InputStream inputStream = resolver.openInputStream(uri);
                File cache = new File(context.getCacheDir().getAbsolutePath(), fileName);
                FileOutputStream fos = new FileOutputStream(cache);
                FileUtils.copy(inputStream,fos);

                videoFile = cache;
                fos.close();
                inputStream.close();

            } catch (IOException e) {
                return null;
            }
        }
        return videoFile;
    }

    //?????????????????????
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        new Thread(()->{

                            Bitmap resizedBitmap = setImageByPath(mPath);

                            new setPic(diary.getText().toString(),resizedBitmap).execute();
                        }).start();

                    }
                }
            });

    //???????????????????????????
    ActivityResultLauncher<Intent> getPicFromPhoneLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        for(int i = 0;i<data.getClipData().getItemCount();i++) { //????????????getClipData
                            Uri uri = data.getClipData().getItemAt(i).getUri();

                            //uriPath = uri;
                            Bitmap resizedBitmap = setImageByUri(uri);
                            if (resizedBitmap != null)
                                new setPic(diary.getText().toString(), resizedBitmap).execute();
                            else
                                System.out.println("error happened when set bitmap by uri");


                        }
                    }


                }
            });

    //???????????????????????????
    ActivityResultLauncher<Intent> getVideoFromPhoneLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        for(int i = 0;i<data.getClipData().getItemCount();i++) { //????????????getClipData
                            Uri uri = data.getClipData().getItemAt(i).getUri();

                            //uriPath = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                            new setVideo(uri).execute();

                        }
                    }


                }
            });

    private Bitmap setImageByUri(Uri uri){

        //content_temp=diary.getText().toString();
        //ContentResolver resolver = getContentResolver();
        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);

        File file = getFileByUri(uri,writeDiary.this);
        String bitmap_path = file.getPath();
        select_path = bitmap_path;
        Bitmap bitmap = BitmapFactory.decodeFile(bitmap_path);

        float height = bitmap.getHeight();
        float width = bitmap.getWidth();

        float aspectRatio = width/height;
        float width_select = 480;
        float height_select = width_select / aspectRatio;

        //System.out.println(width_select+","+height_select+","+aspectRatio);

        Matrix matrix = new Matrix();
        matrix.postScale(width_select,height_select);
        matrix.setRotate(0f);
        //????????????
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int)width_select, (int)height_select, true);
        //getHighImage.get().recycle();

        //getHighImage.set(resizedBitmap);
        //addPic(diary.getText().toString(),resizedBitmap);
        return resizedBitmap;

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
        matrix.setRotate(0f);//???90???
        //????????????
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(getHighImage.get(), (int)width_select, (int)height_select, true);

        return resizedBitmap;
    }

    class setPic extends AsyncTask<Void,Void,Integer>{
        private String content;//????????????
        private Bitmap bitmap;//??????????????????

        public setPic(String content,Bitmap bitmap) {
            this.bitmap = bitmap;
            this.content = content;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            return 0;
        }

        @Override
        protected void onPostExecute(Integer num) {
            super.onPostExecute(num);

            ImageView imageView = new ImageView(writeDiary.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            //imageView.setScaleType(ImageView.ScaleType.CENTER);

            imageView.setImageBitmap(bitmap);
            imageView.setLayoutParams(params);

            viewPager_List.add(imageView);

            /**???????????????????????????
             * ???????????????https://blog.csdn.net/wxlSAMA/article/details/94355377*/
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(writeDiary.this);
                    View imgEntryView = inflater.inflate(R.layout.show_pic_dialog,null);

                    final AlertDialog dialog = new AlertDialog.Builder(writeDiary.this).create();

                    ImageView img = imgEntryView.findViewById(R.id.pic);
                    img.setImageBitmap(bitmap);
                    Glide.with(writeDiary.this).load(bitmap).into(img);
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

            RadioButton radioButton = new RadioButton(writeDiary.this);
            radioButton.setBackgroundResource(R.drawable.radiobutton_bg_selector);
            //radioButton.setChecked(true);

            RadioGroup.LayoutParams rlTable;
            rlTable = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT
                    , RadioGroup.LayoutParams.WRAP_CONTENT);
            rlTable.height = 15;
            rlTable.width = 15;

            radioGroup.addView(radioButton,rlTable);
            viewPager.setAdapter(new MyViewPagerAdapter(viewPager_List));
            //1picNum++;
            radioButtonID.add(radioButton.getId());
            //System.out.println("get = "+radioButton.getId());
            int id = 1;
            radioGroup.check(id);

            image_db temp = new image_db();
            if (mPath != null) {
                temp.path = mPath;
                temp.type = 0;
                image_dbs.add(temp);
            }
            else if(select_path != null) {
                temp.path = select_path;
                temp.type = 1;
                image_dbs.add(temp);
            }
            //System.out.println("path = "+temp.path);
        }
    }


    class setVideo extends AsyncTask<Void,Void,Integer>{
        private Uri uri;//??????????????????

        public setVideo(Uri uri) {
            this.uri = uri;
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            return 0;
        }

        @Override
        protected void onPostExecute(Integer num) {
            super.onPostExecute(num);

            File file = getFileByUri(uri,writeDiary.this);

            ImageView imageView = new ImageView(writeDiary.this);
            imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(file.getPath(),
                    MediaStore.Video.Thumbnails.MICRO_KIND));//??????

            viewPager_List.add(imageView);

            LayoutInflater inflater = LayoutInflater.from(writeDiary.this);
            View imgEntryView = inflater.inflate(R.layout.show_video_layout,null);
            final AlertDialog dialog = new AlertDialog.Builder(writeDiary.this).create();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    VideoView vv = imgEntryView.findViewById(R.id.videoview);

                    RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);


                    vv.setVideoPath(file.getAbsolutePath());
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
                                    MediaController mc = new MediaController(writeDiary.this);
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

            Button b = imgEntryView.findViewById(R.id.video_cancel);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });



            RadioButton radioButton = new RadioButton(writeDiary.this);
            radioButton.setBackgroundResource(R.drawable.radiobutton_bg_selector);
            //radioButton.setChecked(true);

            RadioGroup.LayoutParams rlTable;
            rlTable = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT
                    , RadioGroup.LayoutParams.WRAP_CONTENT);
            rlTable.height = 15;
            rlTable.width = 15;

            radioGroup.addView(radioButton,rlTable);
            viewPager.setAdapter(new MyViewPagerAdapter(viewPager_List));
            //1picNum++;
            radioButtonID.add(radioButton.getId());
            //System.out.println("get = "+radioButton.getId());
            int id = 1;
            radioGroup.check(id);

            image_db temp = new image_db();
            temp.path = file.getAbsolutePath();
            temp.type = 2;//??????
            image_dbs.add(temp);

            System.out.println("video path = "+uri.toString());
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
            //state?????????????????????0????????????????????????1???????????????2????????????
            if (state == 2) {
                //System.out.println("store = "+radioButtonID.get(viewPager.getCurrentItem()));
                radioGroup.check(radioButtonID.get(viewPager.getCurrentItem()));
            }
        }
    };



}
