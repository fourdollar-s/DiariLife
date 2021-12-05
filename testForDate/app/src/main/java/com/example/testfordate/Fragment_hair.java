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

public class Fragment_hair extends Fragment {

    RadioGroup radioGroup;

    public interface MyListener_hair{ //寫一個接口 從fragment把值傳回去activity
        public void sendValue_hair(int num); //宣告
    }

    private MyListener_hair myListener;

    public Fragment_hair() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hair, container, false); //抓到fragment

        //System.out.println("cloth"+person.cloth);
        //關聯
        radioGroup = view.findViewById(R.id.radioGroup_select);
        //radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            int hair = bundle.getInt("hair");
            switch (hair) {
                case R.drawable.hair_1:
                    //System.out.println("choose 1");
                    radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項
                    break;
                case R.drawable.hair_2:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select2_fragment); //一開始選擇第一個選項
                    break;
                case R.drawable.hair_3:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select3_fragment);
                    break;
            }

        }
        else {
            radioGroup.check(R.id.select1_fragment); //一開始選擇第一個選項
        }
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
                            //System.out.println("衣服1");
                            myListener.sendValue_hair(0);
                            break;

                        case R.id.select2_fragment: //case mRadioButton1.getId():
                            //System.out.println("衣服2");
                            myListener.sendValue_hair(1);
                            break;
                        case R.id.select3_fragment:
                            myListener.sendValue_hair(2);
                            break;
                    }
                }
            };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myListener = (MyListener_hair) getActivity(); //接口要的東西
    }
}
