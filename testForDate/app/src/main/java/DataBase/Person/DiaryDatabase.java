package DataBase.Person;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import DataBase.Building.DataUao;
import DataBase.Judge.JudgeDiary;
import DataBase.Judge.JudgeDao;
import DataBase.Map.MapObject;
import DataBase.Map.MapObjectDao;
import DataBase.Person.PersonDao;
import DataBase.Picture.Picture;
import DataBase.Picture.PictureDao;
import DataBase.Store.StoreObject;
import DataBase.Store.StoreObjectDao;
import DataBase.Building.DataUao;
import DataBase.Building.MyData;
import DataBase.Diary.Diary;
import DataBase.Diary.DiaryDao;

import android.content.Context;
import android.provider.ContactsContract;

@Database(entities = {Diary.class,StoreObject.class,MapObject.class,Person.class,MyData.class, JudgeDiary.class, Picture.class},version = 1,exportSchema = false)
public abstract class DiaryDatabase extends RoomDatabase {
    public abstract DiaryDao getDiaryDao();
    public abstract StoreObjectDao getStoreObjectDao();
    public abstract MapObjectDao getMapObjectDao();
    public abstract PersonDao personDao();
    public abstract DataUao getDataUao();
    public abstract JudgeDao getJudgeDao();
    public abstract PictureDao getPictureDao();

    private static volatile DiaryDatabase instance;
    public static final String DB_NAME = "diary";//資料庫名稱

    private  static final int NUMBER_OF_THREADS=15;
    static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized DiaryDatabase getInstance(Context context){
        if(instance == null){
            instance = create(context);//創立新的資料庫
        }
        return instance;
    }
    private static DiaryDatabase create(final Context context){
        return Room.databaseBuilder(context,DiaryDatabase.class,DB_NAME).allowMainThreadQueries().build();
    }

}
