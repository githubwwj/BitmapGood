package xjyz.bitmapgood;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class ImageReszie {

    public static Bitmap resizeBitmap(Resources resources, int id, int maxWidth, int maxHeight, Bitmap bitmap) {

        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        //获取图片信息，不需要把图片加载到内存，获取图片宽高等信息
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, options);

        int sampleSize = calculateSampleSize(options, maxWidth, maxHeight);
        //设置图片的缩放比例
        options.inSampleSize = sampleSize;

        options.inJustDecodeBounds = false;
        options.inMutable = true;
        options.inBitmap = bitmap;
        return BitmapFactory.decodeResource(resources, id, options);
    }

    private static int calculateSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int sampleSize = 1; //设置默认缩放比例
        while (true) {
            if (options.outWidth / sampleSize > maxWidth && options.outHeight / sampleSize > maxHeight) {
                sampleSize *= 2;
            } else {
                break;
            }
        }
        return sampleSize;
    }
}
