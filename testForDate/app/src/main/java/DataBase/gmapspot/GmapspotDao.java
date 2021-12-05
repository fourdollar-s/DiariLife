package DataBase.gmapspot;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GmapspotDao {
    @Query("Select * from gmapspot")
    List<Gmapspot> getGmapspotList();
    @Insert
    void insertGmapspot(Gmapspot... gmapspots);
    @Update
    void updateGmapspot(Gmapspot... gmapspots);
    @Delete
    void deleteGmapspot(Gmapspot... gmapspots);

    //--取得資料表總比數--//
    @Query("Select Count(*) from gmapspot")
    int countdb();

    //--取得緯度--//
    @Query("Select latitude from gmapspot where id_gmapspot=:id_gmapspot")
    double getlatitude(int id_gmapspot);
    //--取得經度--//
    @Query("Select longitude from gmapspot where id_gmapspot=:id_gmapspot")
    double getlongitude(int id_gmapspot);
    @Query("Select title from gmapspot where id_gmapspot=:id_gmapspot")
    String gettitle(int id_gmapspot);

    //--取得spot--
    @Query("Select spot from gmapspot where id_gmapspot=:id_gmapspot")
    String getspot(int id_gmapspot);
    //--取得info--
    @Query("Select info from gmapspot where id_gmapspot=:id_gmapspot")
    String getinfo(int id_gmapspot);
    //--取得date--
    @Query("Select date from gmapspot where id_gmapspot=:id_gmapspot")
    String getdate(int id_gmapspot);
}
