package DataBase.Judge;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface JudgeDao {
    @Insert
    void insertWord(JudgeDiary... contents);//點點代表傳入多個

    @Query("SELECT content FROM judge_content WHERE id = :id AND type = :type")
    String findDataByIdAndType_judge(int id,int type);

    @Query("SELECT * FROM judge_content")
    LiveData<List<JudgeDiary>> getAllContentlive();

    @Query("DELETE FROM judge_content")
    void deleteAllContent();

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name ='judge_content'")
    void deleteID();

    @Query("Select Count(*) from 'judge_content'")
    int dataNumber();

}
