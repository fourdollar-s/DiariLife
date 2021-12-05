package com.example.testfordate;

import android.content.Context;
import android.widget.TextView;

import androidx.room.Room;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import DataBase.Diary.DiaryDao;
import DataBase.Person.DiaryDatabase;

import static java.lang.Math.round;

/**折線圖的marker view
 * 用於顯示折線圖上entry的訊息*/

public class chartMarkerView extends MarkerView {

    private TextView sosoCount;
    private TextView happyCount;
    private TextView angryCount;
    private TextView sadCount;
    private String queryDate;

    private DiaryDatabase database;
    private DiaryDao diaryDao;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public chartMarkerView(Context context, int layoutResource,String date) {
        super(context, layoutResource);
        sosoCount=findViewById(R.id.sosoCount);
        happyCount=findViewById(R.id.happyCount);
        angryCount=findViewById(R.id.angryCount);
        sadCount=findViewById(R.id.sadCount);
        database=DiaryDatabase.getInstance(this.getContext());
        diaryDao=database.getDiaryDao();
        queryDate=date;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight){
        if(e.getX()<9){
            happyCount.setText(""+diaryDao.countEmoHappy(queryDate+"0"+(round(e.getX())+1)));
            sosoCount.setText(""+diaryDao.countEmoNotBad(queryDate+"0"+(round(e.getX())+1)));
            angryCount.setText(""+diaryDao.countEmoAngry(queryDate+"0"+(round(e.getX())+1)));
            sadCount.setText(""+diaryDao.countEmoSad(queryDate+"0"+(round(e.getX())+1)));
        }
        else{
            happyCount.setText(""+diaryDao.countEmoHappy(queryDate+(round(e.getX())+1)));
            sosoCount.setText(""+diaryDao.countEmoNotBad(queryDate+(round(e.getX())+1)));
            angryCount.setText(""+diaryDao.countEmoAngry(queryDate+(round(e.getX())+1)));
            sadCount.setText(""+diaryDao.countEmoSad(queryDate+(round(e.getX())+1)));
        }

        System.out.println(""+queryDate+e.getX());
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
