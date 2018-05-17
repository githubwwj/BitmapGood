package xjyz.bitmapgood;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        return 999;
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
        Bitmap bitmap=ImageReszie.resizeBitmap(context.getResources(),R.mipmap.house,200,200);
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
