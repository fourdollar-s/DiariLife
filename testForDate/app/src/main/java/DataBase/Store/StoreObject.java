package DataBase.Store;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "store")
public class StoreObject {

    //主鍵(自動生成)
    @PrimaryKey(autoGenerate = true)
    private int id_store_object;
    //各column的名稱以及屬性
    @ColumnInfo(name = "money_needed")
    private int money_needed;
    @ColumnInfo(name = "pic_id")
    private String pic_id;

    public StoreObject(int money_needed,String pic_id){
        this.money_needed = money_needed;
        this.pic_id = pic_id;
    }

    public int getId_store_object() {
        return id_store_object;
    }

    public void setId_store_object(int id_store_object) {
        this.id_store_object = id_store_object;
    }

    public int getMoney_needed() {
        return money_needed;
    }

    public void setMoney_needed(int money_needed) {
        this.money_needed = money_needed;
    }

    public String getPic_id() {
        return pic_id;
    }

    public void setPic_id(String pic_id) {
        this.pic_id = pic_id;
    }

}
