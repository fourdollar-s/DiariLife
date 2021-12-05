package com.example.testfordate;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Fragment_pants extends Fragment {

    RadioGroup radioGroup;

    public interface MyListener_pants{ //寫一個接口 從fragment把值傳回去activity
        public void sendValue_pants(int num); //宣告
    }


    private MyListener_pants myListener;

    public Fragment_pants() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pants, container, false); //抓到fragment
        radioGroup = view.findViewById(R.id.radioGroup_select);

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            int pant = bundle.getInt("pant");
            switch (pant) {
                case R.drawable.pants_1:
                    //System.out.println("choose 1");
                    radioGroup.check(R.id.select1_fragment);
                    break;
                case R.drawable.pants_2:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select2_fragment);
                    break;
                case R.drawable.pants_3:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select3_fragment);
                    break;
                case R.drawable.pants_4:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select4_fragment);
                    break;
                case R.drawable.pants_5:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select5_fragment);
                    break;
            }

        }
        else {
            radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項
        }


        //select1 = view.findViewById(R.id.select1_fragment);

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
                            myListener.sendValue_pants(0); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select2_fragment:
                            myListener.sendValue_pants(1); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select3_fragment:
                            myListener.sendValue_pants(2); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select4_fragment:
                            myListener.sendValue_pants(3); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select5_fragment:
                            myListener.sendValue_pants(4); //把值傳回去people_create去判斷要顯示哪件
                            break;
                    }
                }
            };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myListener = (MyListener_pants) getActivity();
    }

}
