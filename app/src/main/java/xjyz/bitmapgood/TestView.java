package xjyz.bitmapgood;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class TestView extends View {

    private int mViewWidth;
    private int mViewHeight;
    private InputStream mInputStream;
    private BitmapRegionDecoder mBitmapRegionDecoder;
    private BitmapFactory.Options mOption = new BitmapFactory.Options();
    private int mImageWidth;
    private int mImageHeight;
    private float mScale;
    private Rect mRect = new Rect();
    private Bitmap mBitmap;
    private Paint mPaint=new Paint();

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 1  获取图片宽高
     * @param mInputStream
     */
    public void setInputStream(InputStream mInputStream) {
        this.mInputStream = mInputStream;

        try {
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(mInputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOption.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(mInputStream, null, mOption);
        mImageWidth = mOption.outWidth;
        mImageHeight = mOption.outHeight;


        Log.e("tag", "----mImageWidth=" + mImageWidth + "---mImageHeight" + mImageHeight);


    }

    /**
     * 2  测量控件的宽高，确定要画的区域
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        Log.e("tag", "---mViewWidth=" + mViewWidth + "-----mViewHeight" + mViewHeight);

        //如果图片区域解码对象为空，不在往下走
        if (null == mBitmapRegionDecoder) {
            return;
        }

        mRect.set(0, 0, 0, 0);

        mRect.top = 0;
        mRect.left = 0;
        mRect.right = mImageWidth;

        //计算缩放因子
        mScale = mViewWidth * 1.0f / mImageWidth;     //  720 / 360 = 2  这个地方是float
        mRect.bottom = (int) (mImageHeight / mScale);

        mPaint.setAntiAlias(true);  //消除画笔的锯齿
    }

    /**
     * 3  绘制要显示的区域
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("tag", "---onDraw=====");

        //如果图片区域解码对象为空，不在往下走
        if (null == mBitmapRegionDecoder) {
            return;
        }

        mOption.inJustDecodeBounds = false;

        mOption.inMutable = true;
        mOption.inBitmap = mBitmap;
        mBitmap = mBitmapRegionDecoder.decodeRegion(mRect, mOption);

        Matrix matrix=new Matrix();
        matrix.postScale(mScale,mScale);
        canvas.drawBitmap(mBitmap,matrix,mPaint);

    }




}
