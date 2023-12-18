package com.wg.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

    private Bitmap mBitmap; // 用于双缓冲的位图对象
    private Canvas mBitmapCanvas; // 用于绘制的 Canvas 对象
    private SurfaceHolder mHolder;
    private boolean mIsDrawing;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint rectPaint;
    private Path mPath;
    private float x = 0;
    private float y = (float) (100*Math.sin(x*2*Math.PI/180)+400);
    private float T = 2f;


    public MySurfaceView(Context context) {
        this(context,null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(5f);
        mPath = new Path();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.BLACK);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mIsDrawing = true;

        // 创建位图对象
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);

        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void run() {
         while (mIsDrawing){
             try {
                 Thread.sleep(10);
                 drawToBuffer();
                 drawToCanvas();

             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
    }

    private void drawToBuffer() {


        if (x >= getWidth()) {
            x = 0;
            //mBitmapCanvas.drawColor(Color.BLACK); // 每次绘制前清除整个缓冲区
            mPath.reset(); // 重置路径
        }

        for (int i = 0; i < 100; i++) {
            x += 1;
            y = (float) (100 * Math.sin(x * T * Math.PI / 180) + 400);
            mPath.lineTo(x, y);
        }
        mBitmapCanvas.drawRect(new RectF(x, 0, x + 50f, getHeight()), rectPaint);
        mBitmapCanvas.drawPath(mPath, mPaint);
    }

    private void drawToCanvas() {
        if (mHolder.getSurface().isValid()) {
            try {
                mCanvas = mHolder.lockCanvas();
                if (mCanvas != null) {
                    mCanvas.drawBitmap(mBitmap, 0, 0, null); // 将缓冲区内容绘制到 SurfaceView
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mCanvas != null) {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }
    }
}
