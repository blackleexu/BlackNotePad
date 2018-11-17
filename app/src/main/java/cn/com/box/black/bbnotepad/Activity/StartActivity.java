package cn.com.box.black.bbnotepad.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import cn.com.box.black.bbnotepad.Bean.ServerIPBean;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.IPModel;
import cn.com.box.black.bbnotepad.Model.LoginModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Server;

import static cn.com.box.black.bbnotepad.Server.AUTO_LOGIN;
import static cn.com.box.black.bbnotepad.Server.user_id_remember;
import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

/**
 * Created by gcj on 2017/11/28
 */
public class StartActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private LottieAnimationView mAnimationView;
    private String username,userpass;
    private CheckBox checkBox;
    private String userid;
    private String ip;
    private boolean check;

    TListener<ServerIPBean> ttListener = new TListener<ServerIPBean>() {
        @Override
        public void onResponse(ServerIPBean ipBean) {
            ip = ipBean.getIp().toString();
            //记录访问IP
            showToast(ip);
            Server.BASE_URL = ip;
            Toast.makeText(StartActivity.this,"登陆成功"+ip,Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(StartActivity.this,"登陆失败,IP错误"+ip,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean loginBean) {
            userid = loginBean.getSuccess().toString();
            //记录用户id
//            showToast(userid);
            user_id_remember= Integer.parseInt(userid);
            if(loginBean.getSuccess().toString().equals("0")){
                Toast.makeText(StartActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else{
//                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, MainActivity1.class);
                startActivity(intent);
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(StartActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT <= 19) {
            setStatusBarColor4_4(this, ContextCompat.getColor(this, R.color.back_red) );
        } else {
            setStatusBarColor(this,ContextCompat.getColor(this, R.color.back_red) );
        }
        // 注意：此处将setContentView()方法注释掉
         setContentView(R.layout.activity_start);

//        IPModel ipModul = new IPModel();
//        ipModul.getIP(ttListener);
        check=getUser_remember();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoLogin();
            }
        }, 2000);
    }

    /**
     * 前往注册、登录主页
     */
    private void gotoLogin() {
        username = getUser_name();
        userpass = getUser_password();
        if(check==true&&AUTO_LOGIN==0) {
            LoginModel loginModul = new LoginModel();
            loginModul.getUserList(username, userpass, tListener);
        }else{
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //取消界面跳转时的动画，使启动页的logo图片与注册、登录主页的logo图片完美衔接
        overridePendingTransition(0, 0);
    }

    /**
     * 屏蔽物理返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            //If token is null, all callbacks and messages will be removed.
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
    static void setStatusBarColor4_4(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

//First translucent status bar.
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int statusBarHeight = getStatusBarHeight(activity);

        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
            //如果已经为 ChildView 设置过了 marginTop, 再次调用时直接跳过
            if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
                //不预留系统空间
                ViewCompat.setFitsSystemWindows(mChildView, false);
                lp.topMargin += statusBarHeight;
                mChildView.setLayoutParams(lp);
            }
        }

        View statusBarView = mContentView.getChildAt(0);
        if (statusBarView != null && statusBarView.getLayoutParams() != null && statusBarView.getLayoutParams().height == statusBarHeight) {
            //避免重复调用时多次添加 View
            statusBarView.setBackgroundColor(statusColor);
            return;
        }
        statusBarView = new View(activity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarView.setBackgroundColor(statusColor);
//向 ContentView 中添加假 View
        mContentView.addView(statusBarView, 0, lp);
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
}
