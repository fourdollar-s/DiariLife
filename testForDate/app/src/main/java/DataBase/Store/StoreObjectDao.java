package DataBase.Store;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface StoreObjectDao {
    @Insert
    void insertStoreObject(StoreObject... storeObjects);

    @Query("DELETE FROM store")
    void deleteAllStoreObject();

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name ='store'")
    void deleteStoreID();

    @Query("SELECT money_needed FROM store WHERE id_store_object = 1")
    int getMyMoney();

    @Query("UPDATE store SET money_needed = :money WHERE id_store_object = 1")
    void updateMyMoney(int money);

    @Query("SELECT * FROM store")
    List<StoreObject> getAllStoreObject();

    @Query("SELECT COUNT (*) FROM store")
    int getStoreNumber();

    @Query("SELECT money_needed FROM store WHERE pic_id = :id")
    int getObjectMoney(String id);

    @Query("SELECT id_store_object FROM store WHERE pic_id = :id")
    int getObjectID(String id);

    @Query("SELECT pic_id FROM store WHERE id_store_object = :id")
    String getObjectPicID(int id);

    @Query("SELECT * FROM store WHERE id_store_object == 1")
    LiveData<List<StoreObject>> getMyMoney_live();
}