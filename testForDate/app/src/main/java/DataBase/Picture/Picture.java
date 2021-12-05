package DataBase.Picture;

import android.content.Context;
import android.widget.TextView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import DataBase.Diary.Diary;
import DataBase.Store.StoreObject;

@Entity(tableName = "picture",foreignKeys = @ForeignKey(entity = Diary.class,
                            parentColumns = "id",
                            childColumns = "diary_id",
                            onDelete = ForeignKey.CASCADE))
public class Picture {
    //主鍵(自動生成)
    @PrimaryKey(autoGenerate = true)
    private int id;
    //各column的名稱以及屬性
    @ColumnInfo(name = "diary_id")
    private int diary_id;
    @ColumnInfo(name = "link_type")
    private int link_type;//uri or file
    @ColumnInfo(name = "link")
    private String link;


    public Picture(int diary_id, int link_type,String link) {
        this.diary_id = diary_id;
        this.link_type = link_type;
        this.link = link;
    }

    public int getDiary_id() {
        return diary_id;
    }

    public void setDiary_id(int diary_id) {
        this.diary_id = diary_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLink_type() {
        return link_type;
    }

    public void setLink_type(int link_type) {
        this.link_type = link_type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
