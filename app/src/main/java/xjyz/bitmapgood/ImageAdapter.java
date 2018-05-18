package xjyz.bitmapgood;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018/5/17 0017.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 666;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.house);

        Bitmap bitmap = ImageCache.getInstance().getBitmapFromMemory(String.valueOf(position));
        Log.e("tag", "-----------复用内存=" + bitmap);
        if (null == bitmap) {
            Bitmap reuseBitmap = ImageCache.getInstance().getReuseBitmap(200, 200, 1);
            Log.e("tag", "-----------复用池=" + reuseBitmap);
            bitmap = ImageCache.getInstance().getBitmapFromDisk(String.valueOf(position), reuseBitmap);
            if(null!=bitmap){
                Log.e("tag", "-----------磁盘中的图片=" + bitmap);
            }else{
                bitmap = ImageReszie.resizeBitmap(context, R.mipmap.house, 200, 200, true, reuseBitmap);
                Log.e("tag", "----------创建一张新图片大小是=" + bitmap.getByteCount());
                ImageCache.getInstance().putBitmapMemory(String.valueOf(position), bitmap);
                ImageCache.getInstance().putBitmapDisk(String.valueOf(position), bitmap);
            }
        }
        holder.imageView.setImageBitmap(bitmap);
        return convertView;
    }


    class ViewHolder {
        ImageView imageView;

        ViewHolder(View view) {
            imageView = view.findViewById(R.id.imageView);
        }
    }


}
