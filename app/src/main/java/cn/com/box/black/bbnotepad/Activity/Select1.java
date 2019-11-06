package cn.com.box.black.bbnotepad.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class Select1 extends TakePhotoActivity implements View.OnClickListener{

    private Button button_delt,button_bak;
    private EditText tv1,tv2;
    private ImageButton btn_update;
    private NoteDB noteDB;
    private SQLiteDatabase dbwriter;
    private String rowid;
    private int result;
    private String tvC,tvA;
    private RichEditor mEditor;
    private TextView mPreview;
    private Uri imageUri;
    private String path;

    private ImageButton [] btnView;
    private boolean btn_flag[]={false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};

    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(Select1.this,"删除失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Select1.this,"删除成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(Select1.this,"删除失败",Toast.LENGTH_SHORT).show();
        }
    };
    TListener<SuccessBean> ttListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(Select1.this,"修改失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Select1.this,"更新成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(Select1.this,"更新失败",Toast.LENGTH_SHORT).show();
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
        mEditor = (RichEditor) findViewById(R.id.editor);
        mPreview = (TextView) findViewById(R.id.preview);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        //    mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");
        button_delt=findViewById(R.id.button_delt);
        button_bak=findViewById(R.id.button_bak);

        final ImageButton btn[]={
                findViewById(R.id.action_undo),
                findViewById(R.id.action_redo),
                findViewById(R.id.action_bold),
                findViewById(R.id.action_italic),
                findViewById(R.id.action_strikethrough),
                findViewById(R.id.action_underline),
                findViewById(R.id.action_heading2),
                findViewById(R.id.action_heading3),
                findViewById(R.id.action_heading4),
                findViewById(R.id.action_heading5),
                findViewById(R.id.action_bg_color),
                findViewById(R.id.action_indent),
                findViewById(R.id.action_outdent),
                findViewById(R.id.action_align_left),
                findViewById(R.id.action_align_center),
                findViewById(R.id.action_align_right),
                findViewById(R.id.action_insert_bullets),
                findViewById(R.id.action_insert_numbers),
                findViewById(R.id.action_insert_image),
        };
        btnView=btn;

        tv1=findViewById(R.id.textView_select);
//        mEditor=findViewById(R.id.editor);
        btn_update=findViewById(R.id.btn_update);
//        noteDB = new NoteDB(this);
//        dbwriter = noteDB.getWritableDatabase();

        rowid=getIntent().getStringExtra(NoteDB.ID);
        tvC=getIntent().getStringExtra(NoteDB.CONTENT);
        tvA=getIntent().getStringExtra(NoteDB.ARTICLE);
        tv1.setText(tvC);
        mEditor.setHtml(tvA);

        mPreview.setText(tvA);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        btn[0].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        btn[1].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        btn[2].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBtnBackground(2);
                mEditor.setBold();
            }
        });

        btn[3].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBtnBackground(3);
                mEditor.setItalic();
            }
        });

//        view.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.setSubscript();
//            }
//        });
//
//        view.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.setSuperscript();
//            }
//        });

        btn[4].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBtnBackground(4);
                mEditor.setStrikeThrough();
            }
        });

        btn[5].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBtnBackground(5);
                mEditor.setUnderline();
            }
        });

//        view.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.setHeading(1);
//            }
//        });

        btn[6].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        btn[7].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        btn[8].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        btn[9].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

//        view.findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.setHeading(6);
//            }
//        });

//        view.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
//            private boolean isChanged;
//
//            @Override public void onClick(View v) {
//                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
//                isChanged = !isChanged;
//            }
//        });
//
        btn[10].setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                setBtnBackground(10);
                mEditor.setTextBackgroundColor(isChanged ? Color.WHITE : Color.YELLOW);
                isChanged = !isChanged;

            }
        });

        btn[11].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        btn[12].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        btn[13].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        btn[14].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        btn[15].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

//        view.findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.setBlockquote();
//            }
//        });

        btn[16].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBtnBackground(16);
                mEditor.setBullets();
            }
        });

        btn[17].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                setBtnBackground(17);
                mEditor.setNumbers();
            }
        });

        btn[18].setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                CropOptions cropOptions=new CropOptions.Builder().setOutputX(200).setOutputY(200).setWithOwnCrop(true).create();
                getTakePhoto().onPickFromGalleryWithCrop(imageUri,cropOptions);
            }
        });

//        view.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
//            }
//        });
//        view.findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                mEditor.insertTodo();
//            }
//        });
    }

    private void setBtnBackground(int num){
        if(mEditor.isFocused()){
            //已获得焦点
        }else{
            mEditor.setFocusable(true);
            mEditor.setFocusableInTouchMode(true);
            mEditor.requestFocus();
            mEditor.requestFocusFromTouch();//获得焦点
            InputMethodManager inputManager = (InputMethodManager)mEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mEditor, 0);
        }
        if(!btn_flag[num]){
            btnView[num].setBackgroundResource(R.drawable.gray_button_background);
            btn_flag[num]=true;
        }else{
            btnView[num].setBackgroundResource(0);
            btn_flag[num]=false;
        }
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
                if (!tv1.getText().toString().equals(""+tvC.trim()) || !mPreview.getText().toString().equals(""+tvA.trim())) {
                    Log.e("tittle",tvC+"+"+tv1.getText().toString());
                    Log.e("content",tvA+"+"+mPreview.getText().toString());
                    new AlertDialog.Builder(this)
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

                break;
            case R.id.btn_update:
                updateDatabase();
                break;
        }
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
                        mEditor.insertImage(response.body().getSuccess().toString(),"dachshund");
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

    public void deleteDatebase(){
        DeleteModel model = new DeleteModel();
        model.getDelMsg(""+rowid,tListener);
    }

    public void updateDatabase(){
        UpdateModel model=new UpdateModel();
        model.getUpdateMsg(""+user_id_remember,rowid,tv1.getText().toString(),mPreview.getText().toString(),ttListener);
    }

    private void showToast(String info){
        Toast.makeText(Select1.this,info,Toast.LENGTH_SHORT).show();
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
        if (!tv1.getText().toString().equals(""+tvC.trim()) || !mPreview.getText().toString().equals(""+tvA.trim())) {
            Log.e("tittle",tvC+"+"+tv1.getText().toString());
            Log.e("content",tvA+"+"+mPreview.getText().toString());
            new AlertDialog.Builder(this)
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
