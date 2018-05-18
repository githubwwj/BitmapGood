package xjyz.bitmapgood;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/12 0012.
 */

public class ImageReszie {

    /**
     *  缩放bitmap
     * @param context
     * @param id
     * @param maxW
     * @param maxH
     * @return
     */
    public static Bitmap resizeBitmap(Context context,int id,int maxW,int maxH,boolean hasAlpha,Bitmap reusable){
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只解码出 outxxx参数 比如 宽、高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources,id,options);
        //根据宽、高进行缩放
        int w = options.outWidth;
        int h = options.outHeight;
        //设置缩放系数
        options.inSampleSize = calcuteInSampleSize(w,h,maxW,maxH);
        if (!hasAlpha){
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        options.inJustDecodeBounds = false;
        // 需要设置为异变的才能够被复用内存
        options.inMutable = true;
        options.inBitmap = reusable;
        return BitmapFactory.decodeResource(resources,id,options);
    }

    /**
     * 计算缩放系数
     *
     * @param w
     * @param h
     * @param maxW
     * @param maxH
     * @return 缩放的系数
     */
    private static int calcuteInSampleSize(int w, int h, int maxW, int maxH) {
        int inSampleSize = 1;
        while (w / inSampleSize >= maxW && h / inSampleSize >= maxH) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
