package DataBase.Picture;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import DataBase.Picture.Picture;


@Dao
public interface PictureDao {
    @Insert
    void insertPicture(Picture... pictures);

    //@Update
    //void updateMapObject(MapObject... mapObjects);

    @Delete
    void deletePic(Picture... pictures);


    @Query("DELETE FROM picture")
    void deleteAllPicture();

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name ='picture'")
    void deletePicID();

    @Query("SELECT * FROM PICTURE WHERE diary_id = :id")
    List<Picture> getPictureByID(int id);
}
