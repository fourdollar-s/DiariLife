package com.example.testfordate;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

public class Fragment_face extends Fragment {

    RadioGroup radioGroup;

    public interface MyListener_face{ //寫一個接口 從fragment把值傳回去activity
        public void sendValue_face(int num); //宣告
    }

    private MyListener_face myListener;

    public Fragment_face() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container, false); //抓到fragment

        //System.out.println("cloth"+person.cloth);
        //關聯
        radioGroup = view.findViewById(R.id.radioGroup_select);
        //radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            int face = bundle.getInt("face");
            //System.out.println(face);
            switch (face) {
                case R.drawable.face_1:
                    //System.out.println("choose 1");
                    radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項
                    break;
                case R.drawable.face_2:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select2_fragment); //一開始選擇第一個選項
                    break;
                case R.drawable.face_3:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select3_fragment);
                    break;
                case R.drawable.face_4:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select4_fragment);
                    break;
                case R.drawable.face_5:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select5_fragment);
                    break;
            }

        }
        else {
            radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項
        }
        //radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項
        //System.out.println("create already");
        radioGroup.setOnCheckedChangeListener(radGrpRegionOnCheckedChange); //偵測有沒有被改選別的

        return view;
    }
    private RadioGroup.OnCheckedChangeListener radGrpRegionOnCheckedChange =
            new RadioGroup.OnCheckedChangeListener(){ //改選別的會跑來這裡判斷
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId_fragment)
                {
                    // TODO Auto-generated method stub
                    //String str = getString(R.string.select_region);

                    switch (checkedId_fragment)
                    {
                        case R.id.select1_fragment:
                            System.out.println("1");
                            myListener.sendValue_face(0); //把值傳回去people_create去判斷顯示
                            break;
                        case R.id.select2_fragment: //case mRadioButton1.getId():
                            System.out.println("2");
                            myListener.sendValue_face(1); //把值傳回去people_create去判斷顯示
                            break;
                        case R.id.select3_fragment:
                            myListener.sendValue_face(2); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select4_fragment:
                            myListener.sendValue_face(3); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select5_fragment:
                            myListener.sendValue_face(4); //把值傳回去people_create去判斷要顯示哪件
                            break;
                    }
                }
            };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myListener = (MyListener_face) getActivity(); //接口要的東西
    }
}
