package DataBase.Person;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.testfordate.PersonBlock;
import com.example.testfordate.ReadDiary;
import com.xujiaji.happybubble.BubbleDialog;

import org.w3c.dom.Text;

import java.util.jar.Attributes;

import DataBase.Diary.Diary;
import DataBase.Store.StoreObject;

@Entity(tableName = "Person", foreignKeys = @ForeignKey(entity = Diary.class,
        parentColumns = "id",
        childColumns = "DiaryID",
        onDelete = ForeignKey.CASCADE))
public class Person {//make class be a subclass of View
    @PrimaryKey(autoGenerate = true)
    private int id;

    //need to store in database
    @ColumnInfo(index = true)
    private int DiaryID;
    private int personX;
    private int personY;
    private String personText;//每日代表句
    private int personCloth;//人物服裝，為image的id
    private int personPant;//人物服裝，為image的id
    private int personFace;//人物服裝，為image的id
    private int personHair;//人物服裝，為image的id
    private int personAccessories;//人物服裝，為image的id
    private String date;//人物產生的日期

    //no need to store in database
    @Ignore
    private PersonBlock personBlock;//替代image view
    @Ignore
    private BubbleDialog personBubbleDialog;
    @Ignore
    private TextView personTextView;
    @Ignore
    private Rect personRect;
    @Ignore
    private Intent personIntent;
    @Ignore
    private boolean personMove;
    @Ignore
    private int personDirection;
    @Ignore
    private int personStep;
    @Ignore
    private Context personContext;

    //--------------data member over------------------------------------------------

    public Person(int DiaryID, int personX, int personY, String personText,int personCloth,int personPant,int personFace,int personHair,int personAccessories,String date) {
        //修改成這樣方便執行new person
        this.DiaryID = DiaryID;
        this.personX = personX;
        this.personY = personY;
//        this.personImageID = personImageID;
        this.personText = personText;
        this.personCloth=personCloth;
        this.personPant = personPant;
        this.personFace = personFace;
        this.personAccessories = personAccessories;
        this.personHair = personHair;
        this.date=date;
    }

    @Ignore
    public void initializePerson(Context context) {
        personBlock = new PersonBlock(context);
//        personImageView=new ImageView(context);
        personBubbleDialog = new BubbleDialog(context);
        personTextView = new TextView(context);
        personRect = new Rect();
        personIntent = new Intent();
        personIntent.setClass(context, ReadDiary.class);
        personMove = true;
        personDirection = -1;
        personStep = 0;
        personContext = context;
    }

    @Ignore
    public void decidePersonDirection() {
        if (personStep <= 0) {
            personStep = 1;
            int direction = (int) (Math.random() * 10);
            if (direction % 2 == 0) {//horizontal
                int leftOrRight = (int) (Math.random() * 10);
                if (leftOrRight % 2 == 0) {//right
                    setPersonDirection(1);
                } else {//left
                    setPersonDirection(2);
                }
            } else {//vertical
                int upOrDown = (int) (Math.random() * 10);
                if (upOrDown % 2 == 0) {//down
                    setPersonDirection(3);
                } else {//up
                    setPersonDirection(4);
                }
            }
        } else {
            personStep--;
        }
    }

    @Ignore
    private final View.OnTouchListener showBubble = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setPersonMove(false);
                    setPersonBubbleDialog();
                    personBubbleDialog.show();
                    break;
                case MotionEvent.ACTION_UP:
                    setPersonMove(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    setPersonMove(true);
                    Bundle bundle = new Bundle();
                    bundle.putInt("DiaryID", DiaryID);
                    bundle.putInt("context", 1);//選擇回來頁面
                    personIntent.putExtras(bundle);
                    personContext.startActivity(personIntent);
                default:
                    break;
            }
            return true;
        }
    };

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPersonPant(int id){this.personPant = id;}

    public int getPersonPant(){return this.personPant;}

    public void setPersonFace(int id){this.personFace = id;}

    public int getPersonFace(){return this.personFace;}

    public void setPersonCloth(int personCloth) {
        this.personCloth = personCloth;
    }

    public int getPersonCloth(){
        return this.personCloth;
    }

    public void setPersonAccessories(int personAccessories) {
        this.personAccessories = personAccessories;
    }
    public int getPersonAccessories() {
        return personAccessories;
    }

    public void setPersonHair(int personHair) {
        this.personHair = personHair;
    }
    public int getPersonHair() {
        return personHair;
    }



    //----diary ID--------------------------
    public void setDiaryID(int id) {
        this.DiaryID = id;
    }

    public int getDiaryID() {
        return this.DiaryID;
    }

    //----personX---------------------------
    public void setPersonX(int x) {
        this.personX = x;
    }

    public int getPersonX() {
        return this.personX;
    }

    //----personY---------------------------
    public void setPersonY(int y) {
        this.personY = y;
    }

    public int getPersonY() {
        return this.personY;
    }

    //----personText-------------------------
    public void setPersonText(String sentence) {
        this.personText = sentence;
    }

    public String getPersonText() {
        return this.personText;
    }



    //---------------------------------------
    public void setDate(String d){
        this.date=d;
    }

    public String getDate(){
        return this.date;
    }

    //----image view-------------------------
    @Ignore
    public void setPersonImageView() {
        personBlock.setPerson_block_layout(this.personCloth,this.personPant,this.personFace,this.personHair,this.personAccessories);//引入person.xml layout，設置圖片
        personBlock.getPerson_block_layout().setOnTouchListener(showBubble);//點擊觸發氣泡框
    }

    @Ignore
    public PersonBlock getPersonImageView() {
        return this.personBlock;
    }


    //----text view---------------------------
    @Ignore
    public void setPersonTextView() {
        this.personTextView.setText(this.personText);
        this.personTextView.setId(this.DiaryID);
    }

    @Ignore
    public TextView getPersonTextView() {
        return this.personTextView;
    }

    //----rect-----------------------------------
    @Ignore
    public void setPersonRect() {
        personRect.set(personBlock.getPerson_block_layout().getLeft(), personBlock.getPerson_block_layout().getTop(), personBlock.getPerson_block_layout().getRight(), personBlock.getPerson_block_layout().getBottom());
    }

    @Ignore
    public void setNextPersonRect() {
        if (this.personDirection == 1) {
            personRect.set(personBlock.getPerson_block_layout().getLeft() + 400, personBlock.getPerson_block_layout().getTop(), personBlock.getPerson_block_layout().getRight() + 400, personBlock.getPerson_block_layout().getBottom());
        } else if (this.personDirection == 2) {
            personRect.set(personBlock.getPerson_block_layout().getLeft() - 400, personBlock.getPerson_block_layout().getTop(), personBlock.getPerson_block_layout().getRight() - 400, personBlock.getPerson_block_layout().getBottom());
        } else if (this.personDirection == 3) {
            personRect.set(personBlock.getPerson_block_layout().getLeft(), personBlock.getPerson_block_layout().getTop() + 400, personBlock.getPerson_block_layout().getRight(), personBlock.getPerson_block_layout().getBottom() + 400);
        } else if (this.personDirection == 4) {
            personRect.set(personBlock.getPerson_block_layout().getLeft(), personBlock.getPerson_block_layout().getTop() - 400, personBlock.getPerson_block_layout().getRight(), personBlock.getPerson_block_layout().getBottom() - 400);
        }
    }

    @Ignore
    public Rect getPersonRect() {
        return personRect;
    }

    //----bubble-----------------------------------
    @Ignore
    public void setPersonBubbleDialog() {
        personBubbleDialog.setClickedView(this.personBlock.getPerson_block_layout());
        personBubbleDialog.setBubbleContentView(this.personTextView);
        personBubbleDialog.setPosition(BubbleDialog.Position.TOP);
    }

    //----move or not------------------------------
    @Ignore
    public void setPersonMove(boolean move) {
        personMove = move;
    }

    @Ignore
    public boolean getPersonMove() {
        return personMove;
    }

    //----step-------------------------------------
    @Ignore
    public void setPersonStep(int step) {
        personStep = step;
    }

    @Ignore
    public int getPersonStep() {
        return this.personStep;
    }

    //----direction--------------------------------
    @Ignore
    public void setPersonDirection(int direction) {
        personDirection = direction;
    }

    @Ignore
    public int getPersonDirection() {
        return personDirection;
    }


}
