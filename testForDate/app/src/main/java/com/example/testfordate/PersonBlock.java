package com.example.testfordate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 顯示於畫面上到處走動的人物，替代main activity中原本的image view物件
 * 人物包含：身體、衣服、褲子（頭髮部分位置需要調整，故先不加入）
 * 初階：此block包含人物個部分，並且可於main activity上主移動(尚不考慮碰撞與動畫)
 */

public class PersonBlock {
    //放置人物的layout
    private View person_block_layout;

    //main activity context
    private Context main_activity_context;

    //constructor，先配置空間給這個人物的身體、衣服與褲子
    public PersonBlock(Context context) {
        main_activity_context = context;
    }

    public void setPerson_block_layout(int clothType,int pantType,int faceType,int hairType,int accessoriesType) {
        LayoutInflater inflater = LayoutInflater.from(main_activity_context);
        person_block_layout = inflater.inflate(R.layout.person, null);

        /**設定人物各部分的圖片顯示，尚未加入頭髮(位置需作調整)*/
        //取得設定在person.xml中的圖片
        ImageView person_body = person_block_layout.findViewById(R.id.person_body);
        ImageView person_cloth = person_block_layout.findViewById(R.id.person_cloth);
        ImageView person_pant = person_block_layout.findViewById(R.id.person_pant);
        ImageView person_hair = person_block_layout.findViewById(R.id.person_hair_1);
        ImageView person_accessories = person_block_layout.findViewById(R.id.person_accessories);
        //ImageView person_accessories_head = person_block_layout.findViewById(R.id.person_accessories);
        ImageView person_face = person_block_layout.findViewById(R.id.person_face);

        //設定圖片id
        person_body.setImageResource(R.drawable.body);
        person_cloth.setImageResource(clothType);
        person_pant.setImageResource(pantType);
        person_hair.setImageResource(hairType);
        person_accessories.setImageResource(accessoriesType);
        person_face.setImageResource(faceType);
    }

    public View getPerson_block_layout() {
        return person_block_layout;
    }
}