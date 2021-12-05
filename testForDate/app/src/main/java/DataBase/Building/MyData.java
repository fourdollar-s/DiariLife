package DataBase.Building;

import android.widget.Toast;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/// 需先取好table名稱
@Entity(tableName = "MyTable")
public class MyData {

    // ID 是否自動累加
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int x;
    public int y;
    private String date;

    public MyData(int x, int y ,String date){
        this.x = x;
        this.y = y;
        this.date = date;
    }
    @Ignore
    public MyData(int id,int x,int y,String date){
        this.id = id;
        this.x = x;
        this.y = y;
        this.date = date;
    }

    public int getId() { return this.id; }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() { return this.x; }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDate(String date){ this.date = date;}

    public String getDate(){ return this.date;}
}
