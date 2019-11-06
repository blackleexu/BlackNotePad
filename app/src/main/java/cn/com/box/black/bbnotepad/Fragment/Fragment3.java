package cn.com.box.black.bbnotepad.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.jph.takephoto.app.TakePhotoFragment;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import java.io.File;

import cn.com.box.black.bbnotepad.Activity.ChangePassActivity;
import cn.com.box.black.bbnotepad.Activity.FeedbackActivity;
import cn.com.box.black.bbnotepad.Activity.LoginActivity;
import cn.com.box.black.bbnotepad.Activity.Select;
import cn.com.box.black.bbnotepad.Activity.UserInfoActivity;
import cn.com.box.black.bbnotepad.Bean.ResultBean;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Bean.UserInfoBean;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.PortraitModel;
import cn.com.box.black.bbnotepad.Model.UserInfoModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Server;
import cn.com.box.black.bbnotepad.Service.NoteDB;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.blurry.Blurry;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static cn.com.box.black.bbnotepad.Server.AUTO_LOGIN;
import static cn.com.box.black.bbnotepad.Server.user_id_remember;

/**
 * Created by www44 on 2017/9/2.
 */

public class Fragment3 extends TakePhotoFragment {
    private TextView tvinfo;
    private View view;
    private Context context;
    private Button button_copy;
    private TextView tv_uname;
    private TextView tv_tel,btn_exit,btn_changepass,btn_author,btn_userInfo,btn_feedback;
    private ImageView portrait,portrait_back;
    private Uri imageUri;
    private String path;
    private String sPortrait;
    private String iPortrait;
    private String info_name,info_sex,info_email,info_tel;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    Glide.with(context).load(iPortrait).into(portrait);
//                    Glide.with(context).load(R.drawable.bg_login)
//                            .bitmapTransform(new BlurTransformation(context, 15), new CenterCrop(context)).override(portrait_back.getWidth(),200)
//                            .into(portrait_back);
                    final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.h_back);
                    Blurry.with(context)
                            .radius(25)
                            .sampling(2)
                            .async()
                            .from(bitmap)
                            .into(portrait_back);
                    break;
                default:
                    break;
            }
        };
    };
    TListener<UserInfoBean> tListener = new TListener<UserInfoBean>() {
        @Override
        public void onResponse(UserInfoBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(context,"获取基本资料失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                tv_uname.setText(Bean.getUname().toString());
                tv_tel.setText(Bean.getTelphone().toString());
                info_email=Bean.getEmail().toString();
                info_name=Bean.getUname().toString();
                info_sex=Bean.getGender().toString();
                info_tel=Bean.getTelphone().toString();
                iPortrait=Bean.getPic();

                new Thread(){
                    public void run(){
                        Message msg=new Message();
                        msg.what=1;
                        handler.sendMessage(msg);
                    };
                }.start();

            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(context,"获取失败",Toast.LENGTH_SHORT).show();
        }
    };

    TListener<SuccessBean> pListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean Bean) {
//            Toast.makeText(Select.this,""+Bean.getSuccess().toString(),Toast.LENGTH_SHORT).show();
            if(Bean.getSuccess().toString().equals("0")){
                Toast.makeText(context,"更新头像失败，请重试",Toast.LENGTH_SHORT).show();
            }
            else{
                Glide.with(context).load(sPortrait).into(portrait);
                show(context,"修改头像成功");
            }
        }
        @Override
        public void onFail(String msg) {
            Toast.makeText(context,"更新头像失败，请重试",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment31,container,false);//解析布局文件
        Log.e("info","Fragment3--onCreateView()");
        //裁剪图片临时存储路径
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        imageUri = Uri.fromFile(file);
//        button_copy=(Button)view.findViewById(R.id.button_copy);
        tv_uname=(TextView)view.findViewById(R.id.user_name);
        tv_tel=(TextView) view.findViewById(R.id.user_tel);
        btn_exit=view.findViewById(R.id.btn_exit);
        btn_changepass=view.findViewById(R.id.btn_changepass);
        btn_author=view.findViewById(R.id.author);
        btn_userInfo=view.findViewById(R.id.user_info);
        portrait=view.findViewById(R.id.h_head);
        portrait_back=view.findViewById(R.id.h_back);
        btn_feedback=view.findViewById(R.id.feedback);

        UserInfoModel model = new UserInfoModel();
        model.getUserinfo(""+user_id_remember,tListener);
//        button_copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setText("446634431");
//                Toast.makeText(context,"已复制作者QQ到粘贴板",Toast.LENGTH_SHORT).show();
//            }
//        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                AUTO_LOGIN=1;
                getActivity().finish();
            }
        });
        btn_changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChangePassActivity.class);
                startActivity(intent);
            }
        });
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropOptions cropOptions=new CropOptions.Builder().setOutputX(200).setOutputY(200).setWithOwnCrop(true).create();
                getTakePhoto().onPickFromGalleryWithCrop(imageUri,cropOptions);
            }
        });
        btn_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context)
                        .setTitleText("作者信息")
                        .setContentText("黑盒子:欢迎使用！如有bug，欢迎反馈")
                        .show();
            }
        });
        btn_userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserInfoActivity.class);
//                intent.putExtra("uname",info_name);
//                intent.putExtra("tel",info_tel);
//                intent.putExtra("email",info_email);
//                intent.putExtra("sex",info_sex);
                startActivity(intent);
            }
        });
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedbackActivity.class);
                startActivity(intent);
            }
        });
        return view;
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

        Call<ResultBean> call = service.uploadImage(body,uid);
        call.enqueue(new Callback<ResultBean>() {
            @Override
            public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getSuccess().toString().equals("0")) {
                        sPortrait=response.body().getSuccess().toString();
                        PortraitModel pModel = new PortraitModel();
                        pModel.getPorMsg(""+user_id_remember,sPortrait,pListener);
//                                Log.e("path",response.body().getSuccess().toString());
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

    private void showToast(String info){
        Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("info","Fragment3--onCreate()");
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("info","Fragment3--onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfoModel model = new UserInfoModel();
        model.getUserinfo(""+user_id_remember,tListener);
        Log.e("info","Fragment3--onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("info","Fragment3--onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("info","Fragment3--onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("info","Fragment3--onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("info","Fragment3--onDestroy()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("info","Fragment3--onActivityCreated()");
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e("info","Fragment3--onAttach()");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("info","Fragment3--onDetach()");
    }
}
