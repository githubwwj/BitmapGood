package xjyz.bitmapgood;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermission()){
            return;
        }

        setAdapter();

    }

    private void setAdapter() {
        ImageCache.getInstance().init(this,(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"good"));
        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(new ImageAdapter(this));

        printImageSize();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        //判断当前Activity是否已经获得了该权限
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || !(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                ) {
            requestSDCardPermission();
            return false;
        } else {
            return true;
        }
    }

    private static final int SDCARD_CODE = 23;

    private void requestSDCardPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, SDCARD_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SDCARD_CODE) {
            if (null != grantResults && grantResults.length > 0) {
                boolean granted = true;
                for (int g = 0; g < grantResults.length; g++) {
                    if (grantResults[g] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        break;
                    }
                }
                if (!granted) {
                    checkPermission();
                }else{
                    setAdapter();
                }
            }
        }
    }


    //计算一张图片在内存中的大小
    public void printImageSize() {

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.house);
//
//        int size = bitmap.getWidth() * bitmap.getHeight() * 4 / 1024 /1024; //计算图片大小 M
//        Log.e("tag", "width=" + bitmap.getWidth() + "----height=" + bitmap.getHeight() + "-----getByteCount=" + bitmap
//                .getByteCount() + "---getConfig=" + bitmap.getConfig().name() + "-----density=" + bitmap.getDensity()
//        +"----图片大小="+size);
//        width=1440----height=2560-----getByteCount=14745600---getConfig=ARGB_8888-----density=320----图片大小=14


//        BitmapFactory.Options options = new BitmapFactory.Options();
//
//        //获取图片信息，不需要把图片加载到内存，获取图片宽高等信息
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.mipmap.house, options);
//
//        int size = options.outWidth * options.outHeight * 4 / 1024 / 1024; //计算图片大小 M
//        int byteCount = options.outWidth * options.outHeight * 4; //计算图片大小 字节数
//
////        options.inDensity  在不同目录下它的密度是不同的
//
//        Log.e("tag", "width=" + options.outWidth + "----height=" + options.outHeight + "-----byteCount=" + byteCount
//                + "---getConfig=" + options.inPreferredConfig.name() + "-----density=" + options.inDensity
//                + "----图片大小=" + size);



    }

}
