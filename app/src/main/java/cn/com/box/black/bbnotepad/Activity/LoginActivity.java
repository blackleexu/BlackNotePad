package cn.com.box.black.bbnotepad.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.mob.MobSDK;

import com.igexin.sdk.PushManager;

import java.util.HashMap;

import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.LoginModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Service.DemoIntentService;
import cn.com.box.black.bbnotepad.Service.FastBlur;

import static cn.com.box.black.bbnotepad.Server.AUTO_LOGIN;
import static cn.com.box.black.bbnotepad.Server.BOOL_FLAG;
import static cn.com.box.black.bbnotepad.Server.user_id_remember;
import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

//import cn.edu.neusoft.lixu524.foodorder.Bean.LoginBean;
//import cn.edu.neusoft.lixu524.foodorder.Bean.RegisterBean;
//import cn.edu.neusoft.lixu524.foodorder.Listener.TListener;
//import cn.edu.neusoft.lixu524.foodorder.Modul.LoginModul;
//import cn.edu.neusoft.lixu524.foodorder.Modul.RegisterModel;
//import cn.edu.neusoft.lixu524.foodorder.R;
//import cn.smssdk.EventHandler;
//import cn.smssdk.SMSSDK;
//import cn.smssdk.gui.RegisterPage;


public class LoginActivity extends BaseActivity {
    private EditText et_user,et_password;
    private Button btn_login;
    private TextView tv_register,tv_phoneReg;
    private String username,userpass;
    private CheckBox checkBox;
    private String userid;
//    EventHandler eventHandler;
    private String country,phone,randomPass;

    TListener<SuccessBean> ttListener=new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean registerBean) {
            String success=registerBean.getSuccess().toString();
            if(success.equals("0")){
                Toast.makeText(LoginActivity.this,"注册失败,用户名可能已注册",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                showToast("初始密码为电话后4位数："+randomPass);
                finish();
            }
        }

        @Override
        public void onFail(String msg) {
            Toast.makeText(LoginActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LoginActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
            }
            else{
//                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity1.class);
                startActivity(intent);
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(LoginActivity.this,"登陆失败",Toast.LENGTH_SHORT).show();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initView(){
//        MobSDK.init(this, "218423de33b70", "f5352147728d3b6d775b4aa306be2962");
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT <= 19) {
            setStatusBarColor4_4(this,ContextCompat.getColor(this, R.color.colorLogin) );
        } else {
            setStatusBarColor(this,ContextCompat.getColor(this, R.color.colorLogin) );
        }

        setLayout_file(R.layout.activity_login1);

        PushManager.getInstance().initialize(this.getApplicationContext(), cn.com.box.black.bbnotepad.Service.DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
//        final Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_login1);
//        findViewById(R.id.content_login).getViewTreeObserver().addOnPreDrawListener(
//                new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
////                        blur(bmp1, findViewById(R.id.content_login));
//                        applyBlur();
//                        return true;
//                    }
//                });

        et_user = (EditText)findViewById(R.id.editText_user);
        et_password = (EditText)findViewById(R.id.editText_password);
        btn_login = (Button)findViewById(R.id.button_login);
        tv_register = (TextView)findViewById(R.id.register);
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        tv_phoneReg= (TextView) findViewById(R.id.tv_phoneReg);

        boolean check=getUser_remember();
//        String receive = getIntent().getStringExtra("username");

        if(check){
            username = getUser_name();
            userpass = getUser_password();
            et_user.setText(username);
            et_password.setText(userpass);
            if(AUTO_LOGIN==0) {
                LoginModel loginModul = new LoginModel();
                loginModul.getUserList(username, userpass, tListener);
            }
        }
        //用户注册账号成功后会把用户名传过来，用户直接输密码即可
//        if(BOOL_FLAG) {
//            et_user.setText(receive);
//            et_password.setText("");
//            BOOL_FLAG=false;
//        }
        //光标移到最后
        et_user.setSelection(et_user.getText().length());
        et_password.setSelection(et_password.getText().length());
    }

    @Override
    void initEvent() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_user.getText().toString().trim().equals("")||et_password.getText().toString().trim().equals("")){
                    Toast.makeText(LoginActivity.this,"用户名和密码不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    //先到服务器判断用户名密码信息是否存在再决定是否保存
                    String username = et_user.getText().toString();
                    String password = et_password.getText().toString();
                    LoginModel loginModul = new LoginModel();
                    loginModul.getUserList(username,password,tListener);
                    AUTO_LOGIN=0;
                    if(checkBox.isChecked()){
                        saveUser();
                    }
                }
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
//
//        tv_phoneReg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                RegisterPage registerPage = new RegisterPage();
//                registerPage.setRegisterCallback(new EventHandler() {
//                    public void afterEvent(int event, int result, Object data) {
//                        // 解析注册结果
//                        if (result == SMSSDK.RESULT_COMPLETE) {
//                            @SuppressWarnings("unchecked")
//                            HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
////                            Random rand=new Random();
////                            int i=(int)(Math.random()*100);
//                            country = (String) phoneMap.get("country");
//                            phone = (String) phoneMap.get("phone");
//                            randomPass=phone.substring(phone.length()-4);
//
//                            // 提交用户信息
//                            RegisterModel Modul = new RegisterModel();
//                            Modul.getReglist(phone,randomPass,phone,"东软","",ttListener);
//
//                        }
//                    }
//                });
//                registerPage.show(LoginActivity.this);
//            }
//        });
    }

    @Override
    void initData() {

    }

    //保存用户名和密码
    public void saveUser(){
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username",et_user.getText().toString());
        editor.putString("userpass",et_password.getText().toString());
        editor.putBoolean("remember",checkBox.isChecked());
        editor.putString("id",userid);
        editor.commit();
    }

    /**
     * 判断网络是否连接.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo ni : info) {
                    if (ni.getState() == NetworkInfo.State.CONNECTED) {
                        Log.d("BNP", "type = " + (ni.getType() == 0 ? "mobile" : ((ni.getType() == 1) ? "wifi" : "none")));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void applyBlur() {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        /**
         * 获取当前窗口快照，相当于截屏
         */
        Bitmap bmp1 = view.getDrawingCache();
//        final Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_login);
        int height = getOtherHeight();
        /**
         * 除去状态栏和标题栏
         */
        Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height,bmp1.getWidth(), bmp1.getHeight() - height);
        blur(bmp2, findViewById(R.id.content_login));
    }

    @SuppressLint("NewApi")
    private void blur(Bitmap bkg, View view) {
//        long startMs = System.currentTimeMillis();
        float scaleFactor = 20;//图片缩放比例；
        float radius = 20;//模糊程度

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()/ scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);


        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        /**
         * 打印高斯模糊处理时间，如果时间大约16ms，用户就能感到到卡顿，时间越长卡顿越明显，如果对模糊完图片要求不高，可是将scaleFactor设置大一些。
         */
//        Log.i("jerome", "blur time:" + (System.currentTimeMillis() - startMs));
    }

    /**
     * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置；
     * @return
     */
    private int getOtherHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
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
