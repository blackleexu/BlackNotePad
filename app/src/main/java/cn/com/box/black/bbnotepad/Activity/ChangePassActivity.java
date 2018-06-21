package cn.com.box.black.bbnotepad.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Bean.UserInfoBean;
import cn.com.box.black.bbnotepad.ClearEditText;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.ChangePassModel;
import cn.com.box.black.bbnotepad.R;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;
import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

/**
 * Created by www44 on 2018/5/13.
 */

public class ChangePassActivity extends BaseActivity {
    private ClearEditText oldPass,newPass,checkPass;
    private Button btn_submit;
    private String NEW_PASS;
    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(ChangePassActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ChangePassActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                //保存新密码
                SharedPreferences sp;
                sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("userpass",NEW_PASS);
                editor.commit();
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(ChangePassActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
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

        setLayout_file(R.layout.activity_changepass);
        oldPass=findViewById(R.id.oldpass);
        newPass=findViewById(R.id.newpass);
        checkPass=findViewById(R.id.checkpass);
        btn_submit=findViewById(R.id.submit_changpass);
    }

    @Override
    void initEvent() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String oldpass=oldPass.getText().toString();
                final String newpass=newPass.getText().toString();
                final String checkpass=checkPass.getText().toString();
                Log.e("pass1",oldpass);
                Log.e("pass1",newpass);
                Log.e("pass1",checkpass);
                if(oldpass.equals("")||newpass.equals("")||checkpass.equals("")){
                    showToast("请填写表单全部信息");
                }else{
                    if(oldpass.equals(newpass)){
                        showToast("新密码不能和旧密码相同");
                    }else{
                        if(newpass.equals(checkpass)){
                            ChangePassModel model = new ChangePassModel();
                            model.getChangePass(""+user_id_remember,oldpass,newpass,tListener);
                            NEW_PASS=newpass;
                        }else{
                            showToast("确认密码有误");
                        }
                    }

                }
            }
        });
    }

    @Override
    void initData() {

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
