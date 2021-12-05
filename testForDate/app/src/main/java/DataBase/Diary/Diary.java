package DataBase.Diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import com.example.testfordate.ReadDiary;

import java.util.List;

import DataBase.Person.DiaryDatabase;

import static java.lang.Integer.parseInt;

@Entity
public class Diary {
    //主鍵(自動生成)
    @PrimaryKey(autoGenerate = true)
    private int id;
    //各column的名稱以及屬性
    @ColumnInfo(name = "diary_info")
    private String diary_info;
    @ColumnInfo(name = "diary_sentence")
    private String diary_sentence;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "label")
    private String label;
    @ColumnInfo(name = "type")
    private int type;

    @Ignore
    private TextView diary_sentence_text;
    @Ignore
    private Context calendar_context;

    public Diary(String diary_info, String date,String label,String diary_sentence) {
        this.diary_info = diary_info;
        this.date = date;
        this.label=label;
        this.diary_sentence = diary_sentence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiary_info() {
        return diary_info;
    }

    public void setDiary_info(String diary_info) {
        this.diary_info = diary_info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLabel(){ return label; }

    public void setLabel(String label){ this.label=label; }

    public String getDiary_sentence(){ return diary_sentence; }

    public void setDiary_sentence(String sentence){ this.diary_sentence = sentence; }

    public int getType(){ return type; }

    public void setType(int type){ this.type = type; }

}
