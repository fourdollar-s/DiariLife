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

public class Fragment_accessories extends Fragment {

    RadioGroup radioGroup;

    public interface MyListener_accessories{ //寫一個接口 從fragment把值傳回去activity
        public void sendValue_accessories(int num); //宣告
    }


    private MyListener_accessories myListener;

    public Fragment_accessories() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accessories, container, false); //抓到fragment
        radioGroup = view.findViewById(R.id.radioGroup_select);

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            int accessories = bundle.getInt("accessories");
            switch (accessories) {
                case R.drawable.accessories_1:
                    //System.out.println("choose 1");
                    radioGroup.check(R.id.select1_fragment);
                    break;
                case R.drawable.accessories_2:
                    //System.out.println("choose 2");
                    radioGroup.check(R.id.select2_fragment);
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
                            myListener.sendValue_accessories(0); //把值傳回去people_create去判斷要顯示哪件
                            break;
                        case R.id.select2_fragment:
                            myListener.sendValue_accessories(1); //把值傳回去people_create去判斷要顯示哪件
                            break;
                    }
                }
            };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        myListener = (MyListener_accessories) getActivity();
    }

}
