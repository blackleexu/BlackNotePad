package cn.com.box.black.bbnotepad.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.Toolbar;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import cn.com.box.black.bbnotepad.Bean.ResultBean;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.DeleteModel;
import cn.com.box.black.bbnotepad.Model.UpdateModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Server;
import cn.com.box.black.bbnotepad.Service.NoteDB;
import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;
import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

/**
 * Created by www44 on 2017/9/8.
 */

public class Select extends TakePhotoActivity implements View.OnClickListener{

    private Button button_delt,button_bak;
    private EditText tv1,tv2;
//    private ImageButton btn_update;
    private FloatingActionButton btn_update;
    private String rowid;
    private int result;
    private String tvC,tvA;
    private RichEditor mEditor;
    private TextView mPreview;
    private Uri imageUri;
    private String path,res;
    private ProgressDialog pd;
    private Dialog mCameraDialog;

    private ImageButton [] btnView;
    private boolean btn_flag[]={false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    WebView webView;
    protected Icarus icarus;
    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(Select.this,"删除失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Select.this,"删除成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(Select.this,"删除失败",Toast.LENGTH_SHORT).show();
        }
    };
    TListener<SuccessBean> ttListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(Select.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Select.this,"更新成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(Select.this,"更新失败",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        //裁剪图片临时存储路径
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        imageUri = Uri.fromFile(file);
        initView();
        initEvent();
        Log.e("info", "select==onCreate");
        if (Build.VERSION.SDK_INT <= 19) {
            setStatusBarColor4_4(this, ContextCompat.getColor(this, R.color.colorLogin) );
        } else {
            setStatusBarColor(this,ContextCompat.getColor(this, R.color.colorLogin) );
        }
    }

    private void initView(){
        webView = findViewById(R.id.editor);
        TextViewToolbar toolbar = new TextViewToolbar();
        Options options = new Options();
        options.setPlaceholder("请输入正文...");
        //  img: ['src', 'alt', 'width', 'height', 'data-non-image']
        // a: ['href', 'target']
        options.addAllowedAttributes("img", Arrays.asList("data-type", "data-id", "class", "src", "alt", "width", "height", "data-non-image"));
        options.addAllowedAttributes("iframe", Arrays.asList("data-type", "data-id", "class", "src", "width", "height"));
        options.addAllowedAttributes("a", Arrays.asList("data-type", "data-id", "class", "href", "target", "title"));

        icarus = new Icarus(toolbar, options, webView);
        prepareToolbar(toolbar, icarus);
        icarus.loadCSS("file:///android_asset/editor.css");
        icarus.loadJs("file:///android_asset/test.js");
        icarus.render();
        button_delt=findViewById(R.id.button_delt);
        button_bak=findViewById(R.id.button_bak);
        findViewById(R.id.button_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
            }
        });

        tv1=findViewById(R.id.textView_select);
//        mEditor=findViewById(R.id.editor);
        btn_update=findViewById(R.id.btn_update);
//        noteDB = new NoteDB(this);
//        dbwriter = noteDB.getWritableDatabase();

        rowid=getIntent().getStringExtra(NoteDB.ID);
//        showToast(""+rowid);
        tvC=getIntent().getStringExtra(NoteDB.CONTENT);
        tvA=getIntent().getStringExtra(NoteDB.ARTICLE);
        tv1.setText(tvC);
        icarus.setContent(tvA);
//        icarus.render();
    }


    private void initEvent(){
        button_delt.setOnClickListener(this);
        button_bak.setOnClickListener(this);
        btn_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_delt:
                new AlertDialog.Builder(this)
                        .setTitle("提示！")
                        .setMessage("确认删除？")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDatebase();
                                finish();
                            }
                        }).create().show();

                break;
            case R.id.button_bak:
                icarus.getContent(new com.github.mr5.icarus.Callback() {
                    @Override
                    public void run(String params) {
//                        Log.d("geteditorcontent", params);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(params);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params = jsonObject.optString("content");
                        if (!tv1.getText().toString().equals(""+tvC.trim()) || !params.toString().equals(""+tvA.trim())) {
                            Log.e("tittle",tv1.getText().toString());
                            Log.e("content1",tvA);
                            Log.e("content",params.toString());
                            new AlertDialog.Builder(Select.this)
                                    .setTitle("提示！")
                                    .setMessage("内容有变化,是否放弃修改内容")
                                    .setPositiveButton("返回保存", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).create().show();
                        }else{
                            finish();
                        }
                    }
                });


                break;
            case R.id.btn_update:
                updateDatabase();
                break;
            case R.id.btn_choose_img:
                //选择照片按钮
                mCameraDialog.dismiss();
                CropOptions cropOptions=new CropOptions.Builder().setOutputX(200).setOutputY(200).setWithOwnCrop(true).create();
                getTakePhoto().onPickFromGalleryWithCrop(imageUri,cropOptions);
                break;
            case R.id.btn_open_camera:
                //拍照按钮
                mCameraDialog.dismiss();
                CropOptions cropOptions1=new CropOptions.Builder().setOutputX(200).setOutputY(200).setWithOwnCrop(true).create();
                getTakePhoto().onPickFromCaptureWithCrop(imageUri,cropOptions1);
                break;
            case R.id.btn_cancel:
                //取消按钮
                mCameraDialog.dismiss();
                break;
        }
    }

    private Toolbar prepareToolbar(TextViewToolbar toolbar, Icarus icarus) {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "Simditor.ttf");
        HashMap<String, Integer> generalButtons = new HashMap<>();
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_BOLD, R.id.button_bold);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_OL, R.id.button_list_ol);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_IMAGE, R.id.button_image);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_BLOCKQUOTE, R.id.button_blockquote);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_HR, R.id.button_hr);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_UL, R.id.button_list_ul);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_ALIGN_LEFT, R.id.button_align_left);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_ALIGN_CENTER, R.id.button_align_center);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_ALIGN_RIGHT, R.id.button_align_right);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_ITALIC, R.id.button_italic);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_INDENT, R.id.button_indent);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_OUTDENT, R.id.button_outdent);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_CODE, R.id.button_math);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_UNDERLINE, R.id.button_underline);
        generalButtons.put(com.github.mr5.icarus.button.Button.NAME_STRIKETHROUGH, R.id.button_strike_through);

        for (String name : generalButtons.keySet()) {
            TextView textView = findViewById(generalButtons.get(name));
            if (textView == null) {
                continue;
            }
            textView.setTypeface(iconfont);
            TextViewButton button = new TextViewButton(textView, icarus);
            button.setName(name);
            toolbar.addButton(button);
        }

        return toolbar;
    }
    public static void show(Context context, String msg,int showtype) {
        Toast toast = new Toast(context);
        //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取自定义视图
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message_toast);
        switch (showtype){
            case 1:
                tvMessage.setBackgroundResource(R.drawable.bg_btn_login);
                break;
            case 2:
                tvMessage.setBackgroundResource(R.drawable.red_button_background);
                break;
            default:
                break;
        }
//        tvMessage.setBackgroundResource(R.drawable.red_button_background);
        //设置文本
        tvMessage.setText(msg);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();
    }
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        path=result.getImage().getOriginalPath().toString();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Server.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final cn.com.box.black.bbnotepad.Service.Service service = retrofit.create(cn.com.box.black.bbnotepad.Service.Service.class);
        final File file = new File(path);//访问手机端的文件资源，保证手机端sdcdrd中必须有这个文件
        if(!file.exists()){
            Log.e("fail","not exist");
            return;
        }else{
            Log.e("success","createIMG");
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final RequestBody uid = RequestBody.create(MediaType.parse("multipart/form-data"), ""+user_id_remember);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        //dosomething
        Call<ResultBean> call = service.uploadImage(body,uid);
        call.enqueue(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getSuccess().toString().equals("0")) {
//                                Log.e("path",response.body().getSuccess().toString());
//                        ../images/31/15261292911526129248539.jpg结果
// http://39.105.20.169/notepad/Uploads/images/31/15261292911526129248539.jpg
                        String Src=response.body().getSuccess().toString();
                        icarus.insertHtml("<img src=\""+Src+"\" />");
//                        mEditor.insertImage("http://39.105.20.169/notepad/Uploads/"+response.body().getSuccess().toString().substring(3),"dachshund");
                        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
                        if (file.exists() && file.isFile()) {
                            file.delete();//操作完成后删除临时文件
                        }
                    }else{
                        showToast("插入图片失败");
                    }
                }else{
                    showToast("插入图片失败");
                }
            }
            @Override
            public void onFailure(Call<ResultBean> call, Throwable t) {
                showToast("插入图片失败");
            }
        });
    }

    private void setDialog() {
        mCameraDialog = new Dialog(Select.this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(Select.this).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    public void deleteDatebase(){
        DeleteModel model = new DeleteModel();
        model.getDelMsg(""+rowid,tListener);
    }

    public void updateDatabase(){
        final UpdateModel model=new UpdateModel();
        icarus.getContent(new com.github.mr5.icarus.Callback() {
            @Override
            public void run(String params) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params = jsonObject.optString("content");
                model.getUpdateMsg(""+user_id_remember,rowid,tv1.getText().toString(),params,ttListener);
            }
        });

    }

    private void showToast(String info){
        Toast.makeText(Select.this,info,Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.e("info", "select==onStart");
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log.e("info", "select==onRestart");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("info", "select==onResume");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.e("info", "select==onPause");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.e("info", "select==onStop");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("info", "select==onDestroy");
    }
    public String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(statusColor);
        }
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
    @Override
    public void onBackPressed() {
        icarus.getContent(new com.github.mr5.icarus.Callback() {
            @Override
            public void run(String params) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params = jsonObject.optString("content");
                if (!tv1.getText().toString().equals(""+tvC.trim()) || !params.toString().equals(""+tvA.trim())) {
                    new AlertDialog.Builder(Select.this)
                            .setTitle("提示！")
                            .setMessage("内容有变化,是否放弃修改内容")
                            .setPositiveButton("返回保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).create().show();
//            super.onBackPressed();
                }else{
                    finish();
                }
            }
        });

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
