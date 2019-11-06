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
import android.webkit.WebView;
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
import com.github.mr5.icarus.button.FontScaleButton;
import com.github.mr5.icarus.button.TextViewButton;
import com.github.mr5.icarus.entity.Options;
import com.github.mr5.icarus.popover.FontScalePopoverImpl;
import com.github.mr5.icarus.popover.HtmlPopoverImpl;
import com.github.mr5.icarus.popover.ImagePopoverImpl;
import com.github.mr5.icarus.popover.LinkPopoverImpl;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

public class Fragment111 extends TakePhotoFragment implements View.OnClickListener{
    private View view;
    private Context context;
    private EditText editText_title;
    private Button savebtn,deletebtn;
    private Uri imageUri;
    private String path;

    private ProgressDialog pd;
    private Dialog mCameraDialog;

    private View root;
    private ImageButton [] btnView;
    private boolean btn_flag[]={false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    
    WebView webView;
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
                show(context,"保存失败",2);
//                Toasty.error(context,"插入图片失败", Toast.LENGTH_SHORT,true).show();
            }
            else{
                show(context,"保存成功",1);
//                Toasty.success(context,"保存成功",Toast.LENGTH_SHORT,true).show();
                icarus.render();
                editText_title.setText("");
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
        view = inflater.inflate(R.layout.fragment111,container,false);//解析布局文件
        webView = view.findViewById(R.id.editor);
        savebtn=view.findViewById(R.id.button_save);
        deletebtn=view.findViewById(R.id.button_cancel);
        editText_title=view.findViewById(R.id.editText_title);
        savebtn.setOnClickListener(this);
        deletebtn.setOnClickListener(this);
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

        view.findViewById(R.id.button_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
            }
        });
        editText_title.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText_title.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void ininEvent(){

    }

    private Toolbar prepareToolbar(TextViewToolbar toolbar, Icarus icarus) {
        Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "Simditor.ttf");
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
            TextView textView = view.findViewById(generalButtons.get(name));
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
//                                Log.e("path",response.body().getSuccess().toString());
//                        ../images/31/15261292911526129248539.jpg结果
// http://39.105.20.169/notepad/Uploads/images/31/15261292911526129248539.jpg
                                String Src=response.body().getSuccess().toString();
                                icarus.insertHtml("<img src=\""+Src+"\" />");
//                                mEditor.insertImage("http://39.105.20.169/notepad/Uploads/"+response.body().getSuccess().toString().substring(3),"dachshund");
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                if(editText_title.getText().toString().equals("")){
//                    showToast("请填写标题");
                    show(context,"请填写标题",2);
//                    Toasty.error(context,"请填写标题",Toast.LENGTH_SHORT,true).show();
                }else {
                    //先到服务器判断用户名密码信息是否存在再决定是否保存
                    final String tittle = editText_title.getText().toString();

                    icarus.getContent(new com.github.mr5.icarus.Callback() {
                        @Override
                        public void run(String params) {
                            Log.d("content", params);
                            try {
                                JSONObject jsonObject = new JSONObject(params);
                                params = jsonObject.optString("content");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            UploadsModel model = new UploadsModel();
                            model.getUploadMsg(user_id_remember + "", tittle, params, tListener);
                        }
                    });

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
                                icarus.render();
//                                mEditor.setHtml("");
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
        webView.destroy();
        super.onDestroy();
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
