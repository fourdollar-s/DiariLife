package DataBase.Building;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import DataBase.Diary.Diary;
import DataBase.Map.MapObject;

@Dao
public interface DataUao {
    String tableName = "MyTable";

    // 簡易新增資料
    //@Insert(onConflict = OnConflictStrategy.REPLACE)//預設若執行出錯的辦法，REPLACE為覆蓋
    //void insertData(MyData myData);

    @Insert
    void insertBuildingObject(MyData... myData);

    //複雜新增資料
    //@Query("INSERT INTO "+tableName+"(x,y) VALUES(:x,:y)")
    //void insertData(int x,int y);

    //撈取全部資料
    @Query("SELECT * FROM "+tableName+" WHERE date LIKE :year_month")
    List<MyData> displayAll(String year_month);

    //撈取指定名稱資料
    @Query("SELECT * FROM "+tableName +" WHERE id = :id")
    List<MyData> findBuildingDataByID(int id);

    //簡易更新資料
//    @Update
//    void updateData(MyData myData);

    //複雜更薪資料
    @Query("UPDATE "+tableName+" SET x = :x,y = :y WHERE id = :id")
    void updateData(int id,int x,int y);

    @Query("DELETE FROM MyTable")
    void delete();

    //簡易刪除資料
    @Delete
    void deleteData(MyData myData);

    @Query("DELETE FROM "+tableName+" WHERE id = :id")
    void deleteData(int id);

    //used to count number of buildings , for setting person ID
    @Query("SELECT COUNT(*) FROM Mytable")
    int countBuilding();

    @Query("SELECT date FROM "+tableName+" WHERE id = :id")
    String findDateByID(int id);

    /**物件碰撞*/
    @Query("SELECT COUNT(*) FROM MyTable WHERE x=:locationX AND y=:locationY")//計算(X,Y)座標上房子的數量
    int countBuilding(int locationX,int locationY);
}
