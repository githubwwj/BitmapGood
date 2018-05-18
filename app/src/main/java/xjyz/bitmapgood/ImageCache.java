package xjyz.bitmapgood;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class ImageCache {

    private static ImageCache imageCache;

    public static ImageCache getInstance(Context context) {
        if (imageCache == null) {
            synchronized (ImageCache.class) {
                imageCache = new ImageCache(context);
            }
        }
        return imageCache;
    }

    private ImageCache(Context context) {
        init(context);
    }

    private void init(Context context) {
        context = context.getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取APP内存大小 以 M 为单位
        int memoryClass = activityManager.getMemoryClass();
        int memorySize = memoryClass * 1024 * 1024;
        Log.e("tag", " -------memoryClass = " + memoryClass);
        lruCache = new LruCache<String, Bitmap>(memorySize / 8) {

            //计算图片大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                    return value.getAllocationByteCount();
                }
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (oldValue.isMutable()) {
                    //当GC扫到，通过引用队列，释放内存
                    //3.0 以前 内存是在 native
                    //3.0 以后  内存是在  JAVA
                    //8.0 开始   内存 native
                    WeakReference<Bitmap> weakReference = new WeakReference<Bitmap>(oldValue, getReferenceQueue());
                    reusePool.add(weakReference);
                } else {
                    oldValue.recycle();
                }

            }
        };

        reusePool = Collections.synchronizedSet(new HashSet<WeakReference<Bitmap>>());


        referenceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown) {
                    ReferenceQueue<Bitmap> referenceQueue = getReferenceQueue();
                    try {
                        Reference<? extends Bitmap> remove = referenceQueue.remove();
                        Bitmap bitmap = remove.get();
                        if (null != bitmap) {
                            bitmap.recycle();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        referenceThread.start();
    }

    private ReferenceQueue<Bitmap> getReferenceQueue() {
        if (referenceQueue == null) {
            referenceQueue = new ReferenceQueue<>();
        }
        return referenceQueue;
    }


    private LruCache<String, Bitmap> lruCache;
    private Set<WeakReference<Bitmap>> reusePool;
    private ReferenceQueue<Bitmap> referenceQueue;
    private Thread referenceThread;
    private boolean isShutdown = false;


    public void putBitmapMemory(String key, Bitmap bitmap) {
        lruCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemory(String key) {
        return lruCache.get(key);
    }

    //清除内存中的图片
    public void clear() {
        lruCache.evictAll();
    }



    public Bitmap getReuseBitmap(int width, int height, int sampleSize) {
        Bitmap bitmap = null;
        Iterator<WeakReference<Bitmap>> iterator = reusePool.iterator();
        while (iterator.hasNext()) {
            bitmap = iterator.next().get();
            iterator.remove();
            if (null != bitmap) {
                if(checkInBitmap(bitmap, width, height, sampleSize)){
                    return bitmap;
                }
            } else {
                break;
            }
        }
        return null;
    }

    /**
     * Android4.4(API 19)之前只有格式为jpg、png，同等宽高（要求苛刻），inSampleSize为1的Bitmap才可以复用；
     * Android4.4(API 19)之前被复用的Bitmap的inPreferredConfig会覆盖待分配内存的Bitmap设置的inPreferredConfig；
     * Android4.4(API 19)之后被复用的Bitmap的内存必须大于等于需要申请内存的Bitmap的内存；
     */
    private boolean checkInBitmap(Bitmap bitmap,int w,int h,int inSampleSize){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            return bitmap.getWidth() ==w&&bitmap.getHeight() ==h
                    && inSampleSize == 1;
        }
        //如果缩放系数大于1 获得缩放后的宽与高
        if (inSampleSize > 1){
            w /= inSampleSize;
            h /= inSampleSize;
        }
        int byteCout = w* h* getPixelsCout(bitmap.getConfig());
        return byteCout <= bitmap.getAllocationByteCount();
    }

    int getPixelsCout(Bitmap.Config config){
        if (config == Bitmap.Config.ARGB_8888){
            return 4;
        }
        return 2;
    }


}
