package DataBase.Map;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import DataBase.Store.StoreObject;

@Entity(tableName = "map",foreignKeys = @ForeignKey(entity = StoreObject.class,
                                        parentColumns = "id_store_object",
                                        childColumns = "id_store_object_of_map",
                                        onDelete = ForeignKey.CASCADE))

/**
 * 商店中的商品
 * 於store介面購買後，跳轉到move object頁面放置
 * 在move object頁面放置完畢後點選儲存按鈕儲存*/
public class MapObject {

    //主鍵(自動生成)
    @PrimaryKey(autoGenerate = true)
    private int id_map_object;
    //各column的名稱以及屬性
    @ColumnInfo(name = "id_store_object_of_map", index = true)
    private int id_store_object_of_map;
    @ColumnInfo(name = "object_x")
    private int object_x;
    @ColumnInfo(name = "object_y")
    private int object_y;
    @ColumnInfo(name="object_use")
    private boolean object_use;//儲存地圖物件狀態，true代表已放置在畫面上，false代表尚未放置在畫面上(預設)
    @ColumnInfo(name = "object_date")
    private String object_date;//地圖物件產生的日期

    public MapObject(int id_store_object_of_map, int object_x,int object_y,String object_date){
        this.id_store_object_of_map = id_store_object_of_map;
        this.object_x = object_x;
        this.object_y = object_y;
        this.object_date=object_date;
        this.object_use=false;//預設為未放置在畫面上
    }

    public int getId_map_object() {
        return id_map_object;
    }

    public void setId_map_object(int id_map_object) {
        this.id_map_object = id_map_object;
    }

    public int getId_store_object_of_map() {
        return id_store_object_of_map;
    }

    public void setId_store_object_of_map(int id_store_object_of_map) {
        this.id_store_object_of_map = id_store_object_of_map;
    }

    public void setObject_x(int object_x) {
        this.object_x = object_x;
    }

    public void setObject_y(int object_y) {
        this.object_y = object_y;
    }

    public int getObject_x() {
        return object_x;
    }

    public int getObject_y() {
        return object_y;
    }

    public void setObject_use(boolean use){
        this.object_use=use;
    }

    public boolean getObject_use(){
        return this.object_use;
    }

    public void setObject_date(String date){
        this.object_date=date;
    }

    public String getObject_date(){
        return this.object_date;
    }
}
