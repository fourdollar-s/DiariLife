package DataBase.gmapspot;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.Marker;

@Entity(tableName = "Gmapspot")
public class Gmapspot {
    @PrimaryKey(autoGenerate = true)
    private int id_gmapspot;

    //---緯度---
    @ColumnInfo(name = "latitude")
    private double latitude;

    //---經度---
    @ColumnInfo(name = "longitude")
    private double longitude;

    //---地點的標題名稱---
    @ColumnInfo(name = "title")
    private String title;

    //---日期---
    @ColumnInfo(name = "date")
    private String date;

    //---輸入的內容---
    @ColumnInfo(name = "info")
    private String info;
    //---地點---
    @ColumnInfo(name = "spot")
    private String spot;

    public Gmapspot(int id_gmapspot, double latitude, double longitude, String title, String date, String info, String spot){
        this.id_gmapspot = id_gmapspot;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.date = date;
        this.info = info;
        this.spot = spot;
    }

    @Ignore
    public Gmapspot(double latitude, double longitude, String title, String date, String info, String spot){
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.date = date;
        this.info = info;
        this.spot = spot;
    }

    public int getId_gmapspot(){
        return id_gmapspot;
    }
    public void setId_gmapspot(int id_gmapspot){
        this.id_gmapspot = id_gmapspot;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInfo(){return info;}

    public void setInfo(){this.info = info;}

    public String getSpot(){return spot;}

    public void setSpot(){this.spot = spot;}

}
