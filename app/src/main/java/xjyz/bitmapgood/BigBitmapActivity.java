package xjyz.bitmapgood;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class BigBitmapActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_bitmap);

        TestView imageView = findViewById(R.id.testView);
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("big.png");
            imageView.setInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
