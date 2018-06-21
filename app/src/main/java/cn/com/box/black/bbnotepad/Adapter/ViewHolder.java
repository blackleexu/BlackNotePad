package cn.com.box.black.bbnotepad.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.com.box.black.bbnotepad.R;

/**
 * Created by www44 on 2017/12/3.
 */

public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView contenttv;
    public TextView articletv;
    public TextView replace;
    public TextView timetv;
    public Button btn_del;

    public ViewHolder(View view) {
        super(view);

        contenttv = view.findViewById(R.id.list_content);
        articletv = view.findViewById(R.id.list_article);
        replace = view.findViewById(R.id.list_replace);
        timetv = view.findViewById(R.id.list_time);
    }
}
