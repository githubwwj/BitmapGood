package xjyz.bitmapgood;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(new ImageAdapter(this));

        printImageSize();

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
