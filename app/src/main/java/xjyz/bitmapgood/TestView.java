package xjyz.bitmapgood;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class TestView extends View implements GestureDetector.OnGestureListener {

    private int mImageWidth, mImageHeight;
    private BitmapRegionDecoder mBitmapRegionDecoder;
    private int mViewWidth, mViewHeight;
    private Rect mRect = new Rect();
    private float mScale;
    private BitmapFactory.Options mOptions = new BitmapFactory.Options();
    private Bitmap mBitmap;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, this);
        mScroller = new Scroller(context);
    }


    //1 先得到图片的宽高
    public void setInputSteam(InputStream inputSteam) {

        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputSteam, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        //显示图片的某一块矩形区域
        try {
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputSteam, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("tag", "---imageWidth=" + mImageWidth + "---imageHeight=" + mImageHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        Log.e("tag", "---viewWidth=" + mViewWidth + "---viewHeight=" + mViewHeight);


        mRect.top = 0;
        mRect.left = 0;
        mRect.right = mImageWidth;
        mScale = mViewWidth * 1.0f / mImageWidth;  // 720/360 = 2  屏幕宽度缩放因子
        mRect.bottom = (int) (mViewHeight / mScale);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mBitmapRegionDecoder) {
            return;
        }
        mOptions.inJustDecodeBounds = false;
        mOptions.inMutable = true;
        mOptions.inBitmap = mBitmap;
        mBitmap = mBitmapRegionDecoder.decodeRegion(mRect, mOptions);
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);

        canvas.drawBitmap(mBitmap, matrix, null);
    }

    /**
     * 手机按下事件  可以得到  x y 坐标
     *
     * @param e
     * @return
     */
    @Override
    public boolean onDown(MotionEvent e) {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * 单击事件
     *
     * @param e
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * 手指在屏幕上拖动
     *
     * @param e1        手指按下事件
     * @param e2        手指当前事件
     * @param distanceX 手指横向滑动距离
     * @param distanceY 手指纵向滑动距离
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        手指从上往下滑动图片，顶部距离增加，底部的距离也增加
//        手指从下往上滑动图片，顶部距离介绍，顶部距离也介绍
        mRect.offset(0, (int) distanceY);
        if (mRect.bottom > mImageHeight) {
            mRect.bottom = mImageHeight;
            mRect.top = mImageHeight - (int) (mViewHeight / mScale);
        } else if (mRect.top < 0) {
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight / mScale);
        }
        invalidate();
        return false;
    }

    /**
     * 长点击时间
     *
     * @param e
     */
    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * 手指离开屏幕滑动时间
     *
     * @param e1        手指按下事件
     * @param e2        手指当前事件
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //开始的 x y
        //x y 速度
        //不管横向滑动
//        int minY, int maxY
        Log.e("tag", "------velocityY=" + velocityY);
        mScroller.fling(0, mRect.top, 0, (int) -velocityY, 0, 0, 0, mImageHeight - (int) (mViewHeight / mScale));
        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //如果已经滑动完毕,不用往下走
        if (mScroller.isFinished()) {
            return;
        }
        //如果还在滚动中
        if (mScroller.computeScrollOffset()) {
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top + (int) (mViewHeight / mScale);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
