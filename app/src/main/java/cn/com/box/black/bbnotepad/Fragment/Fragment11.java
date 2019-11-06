package cn.com.box.black.bbnotepad.Fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mr5.icarus.Icarus;
import com.github.mr5.icarus.TextViewToolbar;
import com.github.mr5.icarus.Toolbar;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.popover.HtmlPopoverImpl;
import com.github.mr5.icarus.popover.LinkPopoverImpl;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.com.box.black.bbnotepad.Bean.ResultBean;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.UploadsModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Server;
import cn.com.box.black.bbnotepad.Service.FastBlur;
import cn.com.box.black.bbnotepad.Service.NoteDB;
import es.dmoral.toasty.Toasty;
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

/**
 * Created by www44 on 2017/9/2.
 */

public class Fragment11 extends TakePhotoFragment implements View.OnClickListener{
    private View view;
    private Context context;
    private EditText editText_title;
    private Button savebtn,deletebtn;
    private Uri imageUri;
    private String path;
    private ImageButton clockbtn;
    private String date;
    private long rowId;
    public static AlarmManager aManager;
    public static MediaPlayer mediaPlayer;
    private ProgressDialog pd;
    private Dialog mCameraDialog;

    private View root;
    private ImageButton [] btnView;
    private boolean btn_flag[]={false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};

    @DrawableRes
    private int btn_drawable=R.drawable.gray_button_background;

    private RichEditor mEditor;
    private TextView mPreview;
    protected Icarus icarus;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 3:
                    pd.dismiss();
                    break;
                default:
                    break;
            }
        };
    };
    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean loginBean) {

            if(loginBean.getSuccess().toString().equals("0")){
                show(context,"保存成功",1);
//                Toasty.error(context,"插入图片失败", Toast.LENGTH_SHORT,true).show();
//                Toast.makeText(getActivity(),"保存失败",Toast.LENGTH_SHORT).show();
            }
            else{
                show(context,"保存成功",1);
//                Toasty.success(context,"保存成功",Toast.LENGTH_SHORT,true).show();
                mEditor.setHtml("");
                editText_title.setText("");
                mPreview.setText("");
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(getActivity(),"保存失败",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        Log.e("Fragment1","onCreateView");
        view = inflater.inflate(R.layout.fragment11,container,false);//解析布局文件
        savebtn=view.findViewById(R.id.button_save);
        deletebtn=view.findViewById(R.id.button_cancel);
        editText_title=view.findViewById(R.id.editText_title);
        savebtn.setOnClickListener(this);
        deletebtn.setOnClickListener(this);
        //裁剪图片临时存储路径
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        imageUri = Uri.fromFile(file);

        ininView();
        return view;
    }

    private void ininView(){
        mEditor = (RichEditor) view.findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(18);
        mEditor.setEditorFontColor(Color.BLACK);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(5, 5, 5, 5);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("请输入正文. PS:支持富文本...");
        //mEditor.setInputEnabled(false);
        root = view.findViewById(R.id.content);


        final ImageButton btn[]={
                view.findViewById(R.id.action_undo),
                view.findViewById(R.id.action_redo),
                view.findViewById(R.id.action_bold),
                view.findViewById(R.id.action_italic),
                view.findViewById(R.id.action_strikethrough),
                view.findViewById(R.id.action_underline),
                view.findViewById(R.id.action_heading2),
                view.findViewById(R.id.action_heading3),
                view.findViewById(R.id.action_heading4),
                view.findViewById(R.id.action_heading5),
                view.findViewById(R.id.action_bg_color),
                view.findViewById(R.id.action_indent),
                view.findViewById(R.id.action_outdent),
                view.findViewById(R.id.action_align_left),
                view.findViewById(R.id.action_align_center),
                view.findViewById(R.id.action_align_right),
                view.findViewById(R.id.action_insert_bullets),
                view.findViewById(R.id.action_insert_numbers),
                view.findViewById(R.id.action_insert_image),
        };
        btnView=btn;

        mPreview = view.findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });
//        mEditor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

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

//                showToast("Bold");
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
                setDialog();
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

    private void ininEvent(){

    }

    private void setBtnBackground(int num){
        if(mEditor.isFocused()){
            //已获得焦点
            if(getKeybordStatus()){

            }else{
                InputMethodManager inputManager = (InputMethodManager)mEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mEditor, 0);
            }
        }else{
            mEditor.focusEditor();
//            mEditor.setFocusable(true);
//            mEditor.setFocusableInTouchMode(true);
//            mEditor.requestFocus();
//            mEditor.requestFocusFromTouch();//获得焦点
            InputMethodManager inputManager = (InputMethodManager)mEditor.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(mEditor, 0);
        }
        if(!btn_flag[num]){
            btnView[num].setBackgroundResource(R.drawable.gray_button_background);
            btn_flag[num]=true;
//            mPreview.append("&nbsp;");

            Log.e("selection","postion:"+mPreview.getSelectionStart());

            mEditor.focusEditor();
        }else{
            btnView[num].setBackgroundResource(0);
            btn_flag[num]=false;
//            mPreview.append("&nbsp;");
            mEditor.focusEditor();
        }
    }

    public boolean getKeybordStatus(){
        final int[] screenHeight = new int[1];
        final int[] myHeight = new int[1];
        final int[] heightDiff = new int[1];
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                screenHeight[0] = root.getRootView().getHeight();
                myHeight[0] = root.getHeight();
                heightDiff[0] = screenHeight[0] - myHeight[0];
                Log.e("onGlobalLayout", "screenHeight=" + screenHeight[0]);
                Log.e("onGlobalLayout", "myHeight=" + myHeight[0]);
            }
        });
        if (heightDiff[0] > 100) {
            Log.e("onGlobalLayout", "Soft keyboard showing"+heightDiff[0]);
            return true;
        } else {
            Log.e("onGlobalLayout", "Soft keyboard hidden"+heightDiff[0]);
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                if(editText_title.getText().toString().equals("")){
//                    showToast("请填写标题");
                    show(context,"请填写标题",2);
//                    Toasty.error(context,"请填写标题",Toast.LENGTH_SHORT,true).show();
                }else {
                    //符合保存条件之后恢复富文本按钮的颜色
                    mEditor.clearFocus();
                    InputMethodManager imm = ( InputMethodManager ) mEditor.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
                    if ( imm.isActive( ) ) {
                        imm.hideSoftInputFromWindow( mEditor.getApplicationWindowToken( ) , 0 );
                    }
                    for(int i=0;i<19;i++){
                        btnView[i].setBackgroundResource(0);
                        btn_flag[i]=false;
                    }
                    //先到服务器判断用户名密码信息是否存在再决定是否保存
                    String content = mPreview.getText().toString();
                    String tittle = editText_title.getText().toString();
                    UploadsModel model = new UploadsModel();
                    model.getUploadMsg(user_id_remember + "", tittle, content, tListener);
                }
                break;
            case R.id.button_cancel:
                new AlertDialog.Builder(context)
                        .setTitle("取消")
                        .setMessage("确认取消编辑并清空编辑框？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                editText_title.setText("");
                                mEditor.setHtml("");
                                mPreview.setText("");
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
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

    private void setDialog() {
        mCameraDialog = new Dialog(context, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(
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

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        System.out.println(System.currentTimeMillis() - startMs + "ms");
    }
    private int getOtherHeight() {
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }
    private void applyBlur() {
        DisplayMetrics dm = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;

        int screenHeight = dm.heightPixels;
        View view = getActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        /**
         * 获取当前窗口快照，相当于截屏
         */
//        Bitmap bmp1 = view.getDrawingCache();
        final Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.backbule1);
        int height = getOtherHeight();
        /**
         * 除去状态栏和标题栏
         */
        Bitmap bmp2 = Bitmap.createBitmap(bmp1, 0, height,screenWidth, screenHeight - height);
        blur(bmp2, view.findViewById(R.id.content));
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

//        Call<Result> call = service.uploadImage(body,uid);
        pd = new ProgressDialog(context);
        pd.setMessage("正在调用图片资源...");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                //dosomething
                Call<ResultBean> call = service.uploadImage(body,uid);
                call.enqueue(new Callback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().getSuccess().toString().equals("0")) {
//                        ../images/31/15261292911526129248539.jpg结果
// http://39.105.20.169/notepad/Uploads/images/31/15261292911526129248539.jpg
                                showToast(response.body().getSuccess().toString());
                                mEditor.insertImage(response.body().getSuccess().toString(),"dachshund");
                                // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
                                if (file.exists() && file.isFile()) {
                                    file.delete();//操作完成后删除临时文件
                                }
                            }else{
//                                showToast("插入图片失败");
                                Toasty.error(context,"插入图片失败", Toast.LENGTH_SHORT,true).show();
                            }
                        }else{
//                            showToast("插入图片失败");
                            Toasty.error(context,"插入图片失败", Toast.LENGTH_SHORT,true).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        showToast("插入图片失败");
                    }
                });

                Message msg = new Message();
                msg.what=3;
                handler.sendMessage(msg);
            };
        }.start();
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
                break;
            case 2:
                tvMessage.setBackgroundResource(R.drawable.red_button_background);
                break;
            default:
                break;
        }
        tvMessage.setBackgroundResource(R.drawable.red_button_background);
        //设置文本
        tvMessage.setText(msg);
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();
    }

    private void showToast(String info){
        Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getUser_id(){
        SharedPreferences sp;
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sp.getString("id","");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("info","Fragment1--onCreate()");
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("info","Fragment1--onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("info","Fragment1--onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("info","Fragment1--onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("info","Fragment1--onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("info","Fragment1--onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("info","Fragment1--onDestroy()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("info","Fragment1--onActivityCreated()");
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("info","Fragment1--onAttach()");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("info","Fragment1--onDetach()");
    }
}
