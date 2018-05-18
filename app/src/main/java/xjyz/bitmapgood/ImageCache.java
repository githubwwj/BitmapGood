package xjyz.bitmapgood;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import xjyz.bitmapgood.disk.DiskLruCache;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class ImageCache {

    private static ImageCache imageCache;

    public static ImageCache getInstance() {
        if (imageCache == null) {
            synchronized (ImageCache.class) {
                imageCache = new ImageCache();
            }
        }
        return imageCache;
    }


    public void init(Context context, String fileDir) {
        context = context.getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取APP内存大小 以 M 为单位
        int memoryClass = activityManager.getMemoryClass();
//        int memorySize = memoryClass * 1024 * 1024 ;
        Log.e("tag", " -------memoryClass = " + memoryClass);
        lruCache = new LruCache<String, Bitmap>(1024 * 1024 * 2) {

            //计算图片大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
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
                    Log.e("tag", "-------添加到复用池");
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
                    try {
                        Bitmap bitmap = getReferenceQueue().remove().get();
                        if (null != bitmap && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        referenceThread.start();

        try {
            diskLruCache = DiskLruCache.open(new File(fileDir), 1, 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private DiskLruCache diskLruCache;
    private BitmapFactory.Options options = new BitmapFactory.Options();

    public void putBitmapMemory(String key, Bitmap bitmap) {
        lruCache.put(key, bitmap);
    }

    public void putBitmapDisk(String key, Bitmap bitmap) {
        DiskLruCache.Snapshot snapshot = null;
        OutputStream outputStream = null;
        try {
            snapshot = diskLruCache.get(key);
            if (null == snapshot) {  //本地SDCard已经有文件,就不管它了
                DiskLruCache.Editor edit = diskLruCache.edit(key);
                outputStream = edit.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                outputStream.flush();
                edit.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap getBitmapFromDisk(String key, Bitmap reuseBitmap) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;
        try {
            snapshot = diskLruCache.get(key);
            if (null == snapshot) {  //磁盘中没有这个文件
                return null;
            }
            InputStream inputStream = snapshot.getInputStream(0);

            options.inMutable = true;
            options.inBitmap = reuseBitmap;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            putBitmapMemory(key, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
        }
        return bitmap;
    }

    public Bitmap getBitmapFromMemory(String key) {
        return lruCache.get(key);
    }

    //清除内存中的图片
    public void clear() {
        lruCache.evictAll();
    }


    /**
     * 可被复用的Bitmap必须设置inMutable为true；
     * Android4.4(API 19)之前只有格式为jpg、png，同等宽高（要求苛刻），
     * inSampleSize为1的Bitmap才可以复用；
     * Android4.4(API 19)之前被复用的Bitmap的inPreferredConfig
     * 会覆盖待分配内存的Bitmap设置的inPreferredConfig；
     * Android4.4(API 19)之后被复用的Bitmap的内存
     * 必须大于等于需要申请内存的Bitmap的内存；
     *
     * @param w
     * @param h
     * @param inSampleSize
     * @return
     */
    public Bitmap getReuseBitmap(int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return null;
        }
        Bitmap reusable = null;
        Iterator<WeakReference<Bitmap>> iterator = reusePool.iterator();
        //迭代查找符合复用条件的Bitmap
        while (iterator.hasNext()) {
            Bitmap bitmap = iterator.next().get();
            if (null != bitmap) {
                //可以被复用
                if (checkInBitmap(bitmap, w, h, inSampleSize)) {
                    reusable = bitmap;
                    //移出复用池
                    iterator.remove();
                    break;
                }
            } else {
                iterator.remove();
            }
        }
        return reusable;
    }

    boolean checkInBitmap(Bitmap bitmap, int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return bitmap.getWidth() == w && bitmap.getHeight() == h
                    && inSampleSize == 1;
        }
        //如果缩放系数大于1 获得缩放后的宽与高
        if (inSampleSize > 1) {
            w /= inSampleSize;
            h /= inSampleSize;
        }
        int byteCout = w * h * getPixelsCout(bitmap.getConfig());
        return byteCout <= bitmap.getAllocationByteCount();
    }

    int getPixelsCout(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        }
        return 2;
    }


}
