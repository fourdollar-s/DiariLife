package DataBase.Diary;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DiaryDao {
    @Insert
    void insertDiaries(Diary... diaries);

    @Update
    void updateDiaries(Diary... diaries);
    @Delete
    void deleteDiaries(Diary... diaries);

    @Query("DELETE FROM Diary")
    void deleteAllDiaries();

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name ='Diary'")
    void deleteDiaryID();

    @Query("SELECT * FROM Diary ORDER BY ID DESC")
    List<Diary> getAllDiaries();

    @Query("SELECT date FROM Diary WHERE id=:id")
    String findDateById(int id);

    @Query("SELECT * FROM Diary WHERE date = :date")
    List<Diary> findDiaryByDate(String date);

    @Query("SELECT * FROM Diary WHERE id = :id")
    Diary findDiaryByID(int id);

    //@Query("SELECT date FROM Diary WHERE id = 1")
    //String findMinDate();

    @Query("SELECT id FROM Diary ORDER BY id DESC LIMIT 0,1")
    int getNewestDiaryID();


    /*更新日記內容，用在更新label那邊*/
    @Query("UPDATE Diary SET label=:newlabel WHERE id=:newid")
    void update(int newlabel,int newid);

    /*刪除日記*/
    @Query("DELETE FROM Diary WHERE id=:id")
    void deleteDiaryByID(int id);

    /*get到最新日記的id，有刪除動作之後在新寫日記才不會存錯地方*/
    @Query("SELECT MAX(id) FROM Diary")
    int getId();

    @Query("UPDATE Diary SET diary_info=:newdiary_info WHERE id=:select_id")
    void updatediaryinfo(String newdiary_info,int select_id);
    @Query("UPDATE Diary SET diary_sentence=:newdiary_sentence WHERE id=:select_id")
    void updatediarysentence(String newdiary_sentence,int select_id);

    @Query("UPDATE Diary SET type =:new_type WHERE id =:select_id")
    void updateType(int new_type,int select_id);

    /**計算情緒標籤數量*/
    @Query("SELECT COUNT(*) FROM Diary WHERE label LIKE '%1' AND date = :d")
    int countEmoNotBad(String d);

    @Query("SELECT COUNT(*) FROM Diary WHERE label LIKE '%2' AND date = :d")
    int countEmoHappy(String d);

    @Query("SELECT COUNT(*) FROM Diary WHERE label LIKE '%3' AND date = :d")
    int countEmoAngry(String d);

    @Query("SELECT COUNT(*) FROM Diary WHERE label LIKE '%4' AND date = :d")
    int countEmoSad(String d);

    /**透過內容獲得日記*/
    @Query("SELECT * FROM Diary WHERE diary_info LIKE :s1 OR diary_info LIKE :s2")
    List<Diary> getAllDiaryWithString(String s1,String s2);
}