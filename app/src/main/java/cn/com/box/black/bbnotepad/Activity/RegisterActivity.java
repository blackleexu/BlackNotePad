package cn.com.box.black.bbnotepad.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.igexin.sdk.PushManager;

import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.ClearEditText;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.ChangePassModel;
import cn.com.box.black.bbnotepad.Model.RegisterModel;
import cn.com.box.black.bbnotepad.R;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;
import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

/**
 * Created by www44 on 2018/5/13.
 */

public class RegisterActivity extends BaseActivity {
    private ClearEditText uname,pass,checkPass;
    private Button btn_submit;
    private String NAME,PASS;
    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(RegisterActivity.this,"注册失败，该用户名可能已被注册",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                //保存新密码
                SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("userpass",PASS);
                editor.putString("username",NAME);
                editor.putBoolean("remember",true);
                editor.commit();
                finish();
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
//                restartApplication();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(RegisterActivity.this,"祖册失败",Toast.LENGTH_SHORT).show();
        }
    };
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    void initView() {
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT <= 19) {
            setStatusBarColor4_4(this, ContextCompat.getColor(this, R.color.colorLogin) );
        } else {
            setStatusBarColor(this,ContextCompat.getColor(this, R.color.colorLogin) );
        }

        setLayout_file(R.layout.activity_register);
        uname=findViewById(R.id.reg_name);
        pass=findViewById(R.id.reg_pass);
        checkPass=findViewById(R.id.reg_checkpass);
        btn_submit=findViewById(R.id.submit_register);
    }

    @Override
    void initEvent() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=uname.getText().toString();
                final String password=pass.getText().toString();
                final String checkpass=checkPass.getText().toString();
                if(name.equals("")||password.equals("")||checkpass.equals("")){
                    showToast("请填写表单全部信息");
                }else{
                    if(password.equals(checkpass)){
                        RegisterModel model = new RegisterModel();
                        model.getRegMsg(name,password,getCid(),tListener);
                        PASS=password;
                        NAME=name;
                    }else{
                        showToast("两次密码输入不一致");
                    }
                }
            }
        });
    }

    @Override
    void initData() {

    }

    //获取当前手机的cid
    private String getCid() {
        String cid = PushManager.getInstance().getClientid(this);
//        Log.d("BNP", getResources().getString(R.string.show_cid) + cid);
        return cid;
    }
    private void restartApplication() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
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
}
