package cn.com.box.black.bbnotepad.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
import android.widget.Toast;

import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Bean.UserInfoBean;
import cn.com.box.black.bbnotepad.ClearEditText;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.ChangePassModel;
import cn.com.box.black.bbnotepad.Model.UpdateUserModel;
import cn.com.box.black.bbnotepad.Model.UserInfoModel;
import cn.com.box.black.bbnotepad.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;
import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

/**
 * Created by www44 on 2018/5/13.
 */

public class UserInfoActivity extends BaseActivity {
    private ClearEditText uname,tel,email;
    private Button btn_submit;
    private RadioButton sex1,sex0;
    private String NEW_NAME;
    private String sexres,telphone,mail;
    private SweetAlertDialog pDialog;

//    private Handler handler=new Handler(){
//        public void handleMessage(Message msg){
//            switch (msg.what){
//                case 5:
////                    UpdateUserModel model = new UpdateUserModel();
////                    model.getUpdateMsg(""+user_id_remember,""+NEW_NAME,telphone,sexres,""+mail,tListener);
//                    pDialog.dismiss();
//                    break;
//
//                default:
//                    break;
//            }
//        };
//    };
    TListener<UserInfoBean> ttListener = new TListener<UserInfoBean>() {
        @Override
        public void onResponse(UserInfoBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(UserInfoActivity.this,"获取基本资料失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                uname.setText(Bean.getUname().toString());
                tel.setText(Bean.getTelphone().toString());
                email.setText(Bean.getEmail().toString());
//                info_email=Bean.getEmail().toString();
//                info_name=Bean.getUname().toString();
//                info_sex=Bean.getGender().toString();
//                info_tel=Bean.getTelphone().toString();
                if(Bean.getGender().toString().equals("1")){
                    sex1.setChecked(true);
                }else{
                    sex0.setChecked(true);
                }
//                iPortrait="http://39.105.20.169/notepad/Uploads/"+Bean.getPic();

//                new Thread(){
//                    public void run(){
//                        Message msg=new Message();
//                        msg.what=1;
//                        handler.sendMessage(msg);
//                    };
//                }.start();

            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(UserInfoActivity.this,"获取失败",Toast.LENGTH_SHORT).show();
        }
    };
    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(UserInfoActivity.this,"修改失败:"+Bean.getMsg().toString(),Toast.LENGTH_SHORT).show();
            }
            else{
                show(UserInfoActivity.this,"修改成功");
//                Toast.makeText(UserInfoActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                //保存新密码
                SharedPreferences sp;
                sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username",NEW_NAME);
                editor.commit();
                finish();
//                restartApplication();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(UserInfoActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
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

        setLayout_file(R.layout.activity_userinfo);
        uname=findViewById(R.id.upname);
        tel=findViewById(R.id.uptel);
        email=findViewById(R.id.upemail);
        sex0=findViewById(R.id.sex0);
        sex1=findViewById(R.id.sex1);
        btn_submit=findViewById(R.id.submit_changinfo);

//        new Thread(){
//            public void run(){
//                Message msg=new Message();
//                msg.what=5;
//                handler.sendMessage(msg);
//                pDialog = new SweetAlertDialog(UserInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                pDialog.setTitleText("Loading");
//                pDialog.setCancelable(false);
//                pDialog.show();
                UserInfoModel model = new UserInfoModel();
                model.getUserinfo(""+user_id_remember,ttListener);
//            };
//        }.start();
    }

    @Override
    void initEvent() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name= uname.getText().toString();
                telphone=tel.getText().toString();
                mail=email.getText().toString();
                if(telphone.equals("")){
                    telphone="未填写";
                }
                if(mail.equals("")){
                    mail="未填写";
                }
                Log.e("telphone","-"+telphone+"-");
                if(sex1.isChecked()){
                    sexres="1";
                }else{
                    sexres="0";
                }

                if(name.equals("")){
                    showToast("用户名不能为空");
                }else{
                    NEW_NAME=name;
//                    Log.e("nihao","shijie");
                    UpdateUserModel model = new UpdateUserModel();
                    model.getUpdateMsg(""+user_id_remember,""+NEW_NAME,telphone,sexres,""+mail,tListener);
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
