package cn.com.box.black.bbnotepad.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.box.black.bbnotepad.R;


public abstract class BaseActivity extends AppCompatActivity {
    protected int layout_file = R.layout.activity_main;

    abstract void initView();
    abstract void initEvent();
    abstract void initData();
    void setLayout_file(int layout_file){
        setContentView(layout_file);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout_file(layout_file);
        initView();
        initEvent();
        initData();
    }
    public String getUser_id(){
        SharedPreferences sp;
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sp.getString("id","");
    }
    //获取保存的用户名
    public String getUser_name(){
        SharedPreferences sp;
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sp.getString("username","");
    }
    //获取保存的密码
    public String getUser_password(){
        SharedPreferences sp;
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sp.getString("userpass","");
    }
    public boolean getUser_remember(){
        SharedPreferences sp;
        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sp.getBoolean("remember",false);
    }
    public void showToast(String content){
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }
    public static void show(Context context, String msg) {
        Toast toast = new Toast(context);
        //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message_toast);
        //设置文本
        tvMessage.setText(msg);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();
    }
}
