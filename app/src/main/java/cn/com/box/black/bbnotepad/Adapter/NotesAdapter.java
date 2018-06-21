package cn.com.box.black.bbnotepad.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.com.box.black.bbnotepad.Bean.NotesBean;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;

/**
 * Created by www44 on 2017/9/25.
 */

public class NotesAdapter extends BaseAdapter<NotesBean>{
    private OnItemClickListener mOnItemClickListener=null;
    private Context context;
    public NotesAdapter(Context context, List<NotesBean> items, int layoutResource) {
        super(context, items, layoutResource);
        this.context=context;
    }

    public void setList(List<NotesBean> l){
        items=l;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final NotesBean bean = items.get(position);
        if(bean==null) return;
        final ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.contenttv.setText(bean.getTittle());
        String replace=bean.getContent().replace("<img src="+"\""+"http://39.105.20.169/notepad/Uploads/images/"+user_id_remember,"图片");
        viewHolder.replace.setText(replace.replace("\""+" alt="+"\""+"dachshund"+"\"",""));
        viewHolder.articletv.setText(bean.getContent());
        viewHolder.timetv.setText(bean.getCreated_at());
//        Picasso.with(context).load(items.get(position).getPic()).into(viewHolder.img);
        if (mOnItemClickListener!=null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    String strid=String.valueOf(bean.getNid());
//                Toast.makeText(context, "您打开了第"+strid+"项", Toast.LENGTH_SHORT).show();
                    int position = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(viewHolder.itemView,position);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    String strid=String.valueOf(bean.getNid());
//                  Toast.makeText(context, "您长按打开了第"+strid+"项", Toast.LENGTH_SHORT).show();
                    int position = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(viewHolder.itemView,position);
                    return true;
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }
}
