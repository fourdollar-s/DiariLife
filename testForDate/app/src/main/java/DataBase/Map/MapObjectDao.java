package DataBase.Map;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface MapObjectDao {
    @Insert
    void insertMapObject(MapObject... mapObjects);

    //@Update
    //void updateMapObject(MapObject... mapObjects);

    @Delete
    void deleteMapObject(MapObject... mapObjects);

    @Query("SELECT * FROM map WHERE object_date LIKE :date")
    List<MapObject> getAllMapObject(String date);

    @Query("SELECT COUNT (*) FROM map")
    int getMapNumber();

    @Query("DELETE FROM map")
    void deleteAllMapObject();

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name ='map'")
    void deleteMapID();

    @Query("UPDATE map SET object_x = :x,object_y = :y WHERE id_map_object = :id")
    void updateMapData(int id,int x,int y);

    /**地圖物件列表相關*/
    @Query("SELECT COUNT(*) FROM map WHERE object_use=:use AND id_store_object_of_map =:object_type AND object_date LIKE :date")//計算使用者持有的地圖物件數量(指定地圖物件類別與放置狀態)
    int getNotUseObjectNumber(boolean use,int object_type,String date);

    @Query("SELECT id_map_object FROM map WHERE object_use=:use AND id_store_object_of_map =:object_type AND object_date LIKE :date ORDER BY id_map_object")//選擇未放置地圖物件中id最小者(默認排序為由小到大)，因為會取出一串id，因此回傳值用list
    List<Integer> getPostedObjectNumber(boolean use,int object_type,String date);

    @Query("SELECT * FROM map WHERE id_map_object =:id")//透過id取得指定的地圖物件
    MapObject getObjectById(int id);

    @Update
    void updateSingleMapObject(MapObject mapObject);

    @Query("UPDATE map SET object_use =:use WHERE id_map_object=:id")
    void updateMapData(boolean use,int id);

    /**偵測擺放數量變動*/
    @Query("SELECT COUNT (*) FROM map WHERE object_use= 0 AND id_store_object_of_map = 2 AND object_date LIKE :date")//尚未擺放的tree數量
    LiveData<Integer> getUnPostTreeCount(String date);

    @Query("SELECT COUNT (*) FROM map WHERE object_use= 0 AND id_store_object_of_map = 3 AND object_date LIKE :date")//尚未擺放的little tree數量
    LiveData<Integer> getUnPostLittleTreeCount(String date);

    @Query("SELECT COUNT (*) FROM map WHERE object_use= 0 AND id_store_object_of_map = 4 AND object_date LIKE :date")//尚未擺放的yellow cat數量
    LiveData<Integer> getUnPostYellowCatCount(String date);

    /**回傳位於(x,y)上的地圖物件數量*/
    @Query("SELECT COUNT (*) FROM map WHERE object_x=:postX AND object_y=:postY AND object_date LIKE :date")
    int getPostedProductCount(int postX,int postY,String date);
}