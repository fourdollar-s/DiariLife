package DataBase.Person;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Person... persons);//when a diary created, new row of person add to database too

    @Update
    void update(Person person);//person move,then restore its location

    @Query("SELECT * FROM Person WHERE date LIKE :year_month")
    List<Person> getAllPerson(String year_month);//used to recover UI

    @Query("SELECT COUNT(*) FROM Person")
    int getPersonCount();//see how many data rows in database

    @Query("DELETE FROM Person")
    void delete();

    //更新人物上衣
    @Query("UPDATE Person SET personCloth =:personcloth ,personPant=:pantType,personFace=:faceType,personHair = :hairType,personAccessories = :accessoriesType WHERE DiaryID=:diaryID")
    void updatePersonCloth(int personcloth,int pantType,int faceType,int hairType,int accessoriesType,int diaryID);

    @Query("SELECT personCloth FROM Person WHERE DiaryID =:id")
    int findClothByDiaryID(int id);

    @Query("SELECT personPant FROM Person WHERE DiaryID =:id")
    int findPantByDiaryID(int id);

    @Query("SELECT personFace FROM Person WHERE DiaryID =:id")
    int findFaceByDiaryID(int id);

    @Query("SELECT personAccessories FROM Person WHERE DiaryID =:id")
    int findAccessoriesByDiaryID(int id);

    @Query("SELECT personHair FROM Person WHERE DiaryID =:id")
    int findHairByDiaryID(int id);

    //更新人物代表句
    @Query("UPDATE Person SET personText = :text WHERE DiaryID = :diaryID")
    void updatePersonText(String text,int diaryID);

}
