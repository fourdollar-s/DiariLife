package rain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.Delayed;


/**
 * 雨滴視圖, DELAY時間重繪, 繪製 NUM_SNOWFLAKES個雨滴
 */
public class RainView extends SurfaceView implements SurfaceHolder.Callback{

    private static final int NUM_SNOWFLAKES = 100; // 雨滴數量
    private static final int DELAY = 5; // 延遲
    private RainFlake[] mSnowFlakes; // 雨滴

    //private int w_saved,h_saved;

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    //子執行緒繪製標記
    private volatile boolean isDrawing;


    public RainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("changing");
        if (w != oldw || h != oldh) {
            initSnow(w, h);
            //w_saved = w;
            //h_saved = h;
        }
    }

    private void initSnow(int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // 抗鋸齒
        paint.setColor(Color.WHITE); // 雨滴的顏色
        paint.setStyle(Paint.Style.FILL); // 填充;
        mSnowFlakes = new RainFlake[NUM_SNOWFLAKES];
        //mSnowFlakes所有的雨滴都生成放到這裏面
        for (int i = 0; i < NUM_SNOWFLAKES; ++i) {
            mSnowFlakes[i] = RainFlake.create(width, height, paint);
        }
        System.out.println("start rn");
    }

    //當SurfaceView被建立的時候被呼叫
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        System.out.println("SurfaceCreated!");
        r.run();//draw
    }
    //當SurfaceView的檢視發生改變，比如橫豎屏切換時，這個方法被呼叫
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }

    //當SurfaceView被銷燬的時候，比如不可見了，會被呼叫
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;//停止繪畫
        surfaceHolder.removeCallback(this);
    }


    private void draw() {
        try {
            canvas = surfaceHolder.lockCanvas();//鎖定畫布
            //執行具體的繪製操作
            for (RainFlake s : mSnowFlakes) {
                //然後進行繪製
                s.draw(canvas);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);//結束鎖定畫布並提交改變
                getHandler().postDelayed(r, DELAY);//過一段時間再重畫一次
            }
        }
    }

    //畫畫
    final Runnable r = new Runnable(){ //runnable
        @Override
        public void run() {
            draw();
        }
    };

}
