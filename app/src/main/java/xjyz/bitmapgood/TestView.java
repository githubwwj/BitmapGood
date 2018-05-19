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
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class TestView extends View {

    private int mImageWidth, mImageHeight;
    private BitmapRegionDecoder mBitmapRegionDecoder;
    private int mViewWidth, mViewHeight;
    private Rect mRect = new Rect();
    private float mScale;
    private BitmapFactory.Options mOptions = new BitmapFactory.Options();
    private Bitmap mBitmap;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        mRect.bottom = (int) (mImageHeight / mScale);
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
        Matrix matrix=new Matrix();
        matrix.setScale(mScale,mScale);

        canvas.drawBitmap(mBitmap,matrix,null);
    }
}
