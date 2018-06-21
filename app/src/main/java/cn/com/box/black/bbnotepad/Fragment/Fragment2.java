package cn.com.box.black.bbnotepad.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.com.box.black.bbnotepad.Activity.Select;
import cn.com.box.black.bbnotepad.Adapter.MyAdapter;
import cn.com.box.black.bbnotepad.Adapter.NotesAdapter;
import cn.com.box.black.bbnotepad.Bean.NotesBean;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Listener.ListListener;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.DeleteModel;
import cn.com.box.black.bbnotepad.Model.NotesModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Service.NoteDB;
import cn.com.box.black.bbnotepad.Utils.MyDividerItemDecoration;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;


/**
 * Created by www44 on 2017/9/2.implements AbsListView.OnScrollListener
 */

public class Fragment2 extends Fragment {
    private View view;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_noitem;
    private static int notesExist=0;
    private List<NotesBean> list;
    private NotesAdapter notesAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:if (swipeRefreshLayout.isRefreshing()){
                    notesAdapter.notifyDataSetChanged();
                    selectDB();
                    swipeRefreshLayout.setRefreshing(false);//设置不刷新
                }
                    break;
                case 2:if (swipeRefreshLayout.isRefreshing()){
                    notesAdapter.notifyDataSetChanged();
                    selectDB();
                    swipeRefreshLayout.setRefreshing(false);//设置不刷新
                }
                    break;
                default:
                    break;
            }
        };
    };

    TListener<SuccessBean> dtListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(context,"删除失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                selectDB();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context=getActivity();
        view = inflater.inflate(R.layout.fragment2,container,false);//解析布局文件
        initView();
        initEvent();
        selectDB();
        Log.e("info","Fragment2--onCreateView()");
        return view;
    }
    //初始化界面，绑定控件
    private void initView(){
        swipeRefreshLayout = view.findViewById(R.id.main_srl);
        tv_noitem=view.findViewById(R.id.tv_noitem);
    }
    //监听事件以及点击事件
    private void initEvent(){
        // 设置刷新时进度动画变换的颜色，接收参数为可变长度数组。也可以使用setColorSchemeColors()方法。
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                Log.e("infoswiperefreshlayout","newthread");
                new Thread(){
                    public void run(){
                        Message msg=new Message();
                        msg.what=2;
                        handler.sendMessage(msg);
                    };
                }.start();
            }
        });
    }
    //数据库查询
    public void selectDB(){
        context=this.getActivity();
        ListListener<NotesBean> listListener=new ListListener<NotesBean>() {
            @Override
            public void onResponse(List<NotesBean> listbean) {
                list=listbean;
                notesAdapter.setList(list);
                notesExist=list.size();
                if(notesExist==0){
                    tv_noitem.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.INVISIBLE);
                }else {
                    tv_noitem.setVisibility(View.GONE);
                    tv_noitem.setHeight(0);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail(String msg) {
                tv_noitem.setVisibility(View.VISIBLE);
//                tv_noitem.setText("出错了...");
                swipeRefreshLayout.setVisibility(View.GONE);
            }
        };

        recyclerView=view.findViewById(R.id.rv_notelist);
        //创建默认的线性LayoutManager
        layoutManager =new LinearLayoutManager(context);
        //list添加分隔线
//        recyclerView.addItemDecoration(new MyDividerItemDecoration(context,LinearLayoutManager.VERTICAL));
        //设置布局
        recyclerView.setLayoutManager(layoutManager);
        //创建适配器
        notesAdapter=new NotesAdapter(getActivity(),list,R.layout.cell);
        NotesAdapter.OnItemClickListener onItemClickListener= new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(getActivity(), Select.class);
                intent.putExtra(NoteDB.ID,list.get(position).getNid());
                intent.putExtra(NoteDB.CONTENT,list.get(position).getTittle());
                intent.putExtra(NoteDB.ARTICLE,list.get(position).getContent());
                intent.putExtra(NoteDB.TIME,list.get(position).getCreated_at());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
//                Toast.makeText(context, "您长按打开了", Toast.LENGTH_SHORT).show();
                initPopWindow(view,position);
            }
        };

        NotesModel Model =new NotesModel();
        Model.getNoteList(""+user_id_remember,listListener);
        notesAdapter.setOnItemClickListener(onItemClickListener);
        notesAdapter.setList(list);

        recyclerView.setAdapter(notesAdapter);
    }
    //气泡弹窗
    private void initPopWindow(View v, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popup, null, false);
        Button btn_delitem = (Button) view.findViewById(R.id.btn_item_del);
        Button btn_checkitem = (Button) view.findViewById(R.id.btn_item_check);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        /*
         *  测量popwindow
         *  由于contentView还未绘制，这时候的width、height都是0。因此需要通过measure测量出contentView的大小，才能进行计算
         */
        View contentView = popWindow.getContentView();
        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(popWindow.getWidth()),
                makeDropDownMeasureSpec(popWindow.getHeight()));

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效


        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
//        int x=btn_show.getWidth();
//        popWindow.showAsDropDown(v, 0, 0, Gravity.CENTER);
        int offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-v.getWidth()) / 2;
        int offsetY = 0;
        PopupWindowCompat.showAsDropDown(popWindow, v, offsetX, offsetY, Gravity.START);


        //设置popupWindow里的按钮的事件
        btn_delitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "你点击了删除~"+list.get(position).getNid(), Toast.LENGTH_SHORT).show();
                DeleteModel model = new DeleteModel();
                model.getDelMsg(""+list.get(position).getNid(),dtListener);
                popWindow.dismiss();//点击执行操作后将气泡窗体关闭
            }
        });
        btn_checkitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "你点击了查看~"+list.get(position).getNid(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), Select.class);
                intent.putExtra(NoteDB.ID,list.get(position).getNid());
                intent.putExtra(NoteDB.CONTENT,list.get(position).getTittle());
                intent.putExtra(NoteDB.ARTICLE,list.get(position).getContent());
                intent.putExtra(NoteDB.TIME,list.get(position).getCreated_at());
                startActivity(intent);
                popWindow.dismiss();
            }
        });
    }
    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }
//    private int visibleLastIndex;//用来可显示的最后一条数据的索引
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if(adapter.getCount() == visibleLastIndex && scrollState == SCROLL_STATE_IDLE){
//            new Thread(){
//                public void run(){
//                    Log.e("scroll","newthread");
////                    Toast.makeText(context,"调用scrollstatechange更新",Toast.LENGTH_SHORT).show();
//                    Message msg=new Message();
//                    msg.what=1;
//                    handler.sendMessage(msg);
//                };
//            }.start();
//        }
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;//减去最后一个加载中那条
//    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("info","Fragment2--onCreate()");
    }
    @Override
    public void onStart() {
        super.onStart();
        notesAdapter.notifyDataSetChanged();
        selectDB();
        Log.e("info","Fragment2--onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("info","Fragment2--onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("info","Fragment2--onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("info","Fragment2--onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("info","Fragment2--onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("info","Fragment2--onDestroy()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("info","Fragment2--onActivityCreated()");
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("info","Fragment2--onAttach()");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("info","Fragment2--onDetach()");
    }
}
