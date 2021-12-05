package com.example.testfordate;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import androidx.constraintlayout.widget.ConstraintSet;

public class PersonScrollView extends HorizontalScrollView {
    private boolean scrollable = true;

    public PersonScrollView(Context context) {
        super(context);
    }

    public PersonScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PersonScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PersonScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollable(boolean enable){
        scrollable=enable;
    }

    public boolean getScrollable(){
        return scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                return scrollable&&super.onTouchEvent(motionEvent);
            default:
                return super.onTouchEvent(motionEvent);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent){
        return scrollable&&super.onInterceptTouchEvent(motionEvent);
    }
}
