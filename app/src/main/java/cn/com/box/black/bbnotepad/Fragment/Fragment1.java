package cn.com.box.black.bbnotepad.Fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.box.black.bbnotepad.Activity.LoginActivity;
import cn.com.box.black.bbnotepad.Activity.MainActivity;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Model.LoginModel;
import cn.com.box.black.bbnotepad.Model.UploadsModel;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Service.FastBlur;
import cn.com.box.black.bbnotepad.Service.NoteDB;

import static cn.com.box.black.bbnotepad.Server.user_id_remember;

/**
 * Created by www44 on 2017/9/2.
 */

public class Fragment1 extends Fragment implements View.OnClickListener {
    private View view;
    private Context context;
    private EditText editText_title,editText_article;
    private Button savebtn,deletebtn;
    private ImageButton clockbtn;
    private NoteDB noteDB;
    private SQLiteDatabase dbwriter;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private String date;
    private long rowId;
    public static AlarmManager aManager;
    public static MediaPlayer mediaPlayer;
    public static int i=230,j=230;
    TListener<SuccessBean> tListener = new TListener<SuccessBean>() {
        @Override
        public void onResponse(SuccessBean loginBean) {

            if(loginBean.getSuccess().toString().equals("0")){
                Toast.makeText(getActivity(),"保存失败",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(),"保存成功",Toast.LENGTH_SHORT).show();
                editText_title.setText("");
                editText_article.setText("");
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
        view = inflater.inflate(R.layout.fragment11,container,false);//解析布局文件
        ininView();
        return view;
    }

    private void ininView(){
        editText_title = view.findViewById(R.id.editText_title);
        editText_article = view.findViewById(R.id.editText_article);
        savebtn=view.findViewById(R.id.button_save);
        deletebtn=view.findViewById(R.id.button_cancel);
        datePicker=view.findViewById(R.id.datePicker);
        timePicker=view.findViewById(R.id.timePicker) ;
        timePicker.setIs24HourView(true);
        clockbtn=view.findViewById(R.id.imageButton_clock);
        datePicker.setCalendarViewShown(false);
        savebtn.setOnClickListener(this);
        deletebtn.setOnClickListener(this);
        clockbtn.setOnClickListener(this);

        //以写的方式打开database
        noteDB = new NoteDB(context);
        dbwriter=noteDB.getWritableDatabase();
        final Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.backbule1);


        view.findViewById(R.id.content).getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
//                        blur(bmp1, view.findViewById(R.id.content));
//                        applyBlur();
                        return true;
                    }
                });
    }

    private void ininEvent(){

    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.button_save:
                if(editText_title.getText().toString().trim().equals("")||editText_article.getText().toString().trim().equals("")){
                    Toast.makeText(context,"标题或正文不能为空",Toast.LENGTH_SHORT).show();
                }
                else{
                    //先到服务器判断用户名密码信息是否存在再决定是否保存
                    String content = editText_article.getText().toString();
                    String tittle = editText_title.getText().toString();
                    UploadsModel model = new UploadsModel();
                    model.getUploadMsg(user_id_remember+"",tittle,content,tListener);
                }
//                addDB();
//                if(rowId!=-1) {
//                    editText_title.setText("");
//                    editText_article.setText("");
//                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.button_cancel:
                new AlertDialog.Builder(context)
                        .setTitle("取消")
                        .setMessage("确认取消编辑并清空编辑框？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                editText_title.setText("");
                                editText_article.setText("");
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

                break;
            case R.id.imageButton_clock:
//                clockbtn.setVisibility(View.VISIBLE);
                // 获取AlarmManager对象
                aManager = (AlarmManager) getActivity().getSystemService(Service.ALARM_SERVICE);
                Calendar currentTime = Calendar.getInstance();
                // 创建一个TimePickerDialog实例，并把它显示出来。绑定监听器
                new TimePickerDialog(getActivity(), 0 ,new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp,int hourOfDay, int minute){
                                Toast.makeText(context,""+hourOfDay+":"+minute,Toast.LENGTH_SHORT).show();
                                // 指定启动AlarmActivity组件
                                Intent intent = new Intent();
                                intent.setAction("Alarm.Service");
                                // 创建PendingIntent对象
                                PendingIntent pi = PendingIntent.getService(getActivity(), j, intent, 0);
                                i++;
                                j++;
                                Calendar c = Calendar.getInstance();
                                // 根据用户选择时间来设置Calendar对象
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND,0);
                                // 设置AlarmManager将在Calendar对应的时间启动指定组件
                                //set(@AlarmType int type, long triggerAtMillis, PendingIntent operation) 1\唤醒方式，2、规定时间，3、操作
                                aManager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(), pi);
                                Toast.makeText(context,":"+c.getTimeInMillis(),Toast.LENGTH_SHORT).show();
                                String str = String.format("%tF %<tT", c.getTimeInMillis());
                                Log.e("time",""+str);
//                                aManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),6000*60*24, pi);
//                                aManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000, pi);
                                // 显示闹铃设置成功的提示信息
                                Toast.makeText(getActivity(), "闹铃设置成功啦", Toast.LENGTH_SHORT).show();
//                                finish();
                                Log.e("TAG",""+c.getTimeInMillis());
                            }
                        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true){
                    @Override
                    protected void onStop() {
                        //super.onStop();
                    }
                }.show();
                break;
        }
    }

    public void addDB(){
        dbwriter=noteDB.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NoteDB.CONTENT,editText_title.getText().toString());
        cv.put(NoteDB.ARTICLE,editText_article.getText().toString());
        cv.put(NoteDB.TIME,getTime());

        /*
         * insert方法得参数列表：
         *              第一个为数据库表名，
         *              第二个为希望插入值为空的列名（如果插入值没有为空得列，全是有数据得就写null），
         *              第三个为一个ContentValues对象   键值对
         *  插入操作会返回以一个行id值提示是否插入成功
         */
        rowId=dbwriter.insert(NoteDB.TABLE_NAME,null,cv);

        //操作完成关闭数据库
        dbwriter.close();
    }
/*
    获取当前时间作为储存是的时间设置
 */
    public String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }
/*
    控制日期的显示格式
 */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int huor=calendar.get(Calendar.HOUR_OF_DAY);
        int minutes=calendar.get(Calendar.MINUTE);

        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int year, int month, int day) {
                date=year+"年"+(++month)+"月"+day;//将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
            }
        };

        TimePickerDialog.OnTimeSetListener listener1=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int huor, int minutes) {
//                clockbtn.setText(date+"日 "+huor+":"+minutes);
                Toast.makeText(context,date+"日 "+huor+":"+minutes,Toast.LENGTH_SHORT).show();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, 0, listener, year, month, day);
        datePickerDialog.setCancelable(true);
        DatePicker dp = datePickerDialog.getDatePicker();
        TimePickerDialog timePickerDialog=new TimePickerDialog(context,0,listener1,huor,minutes, DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.setCancelable(true);

        //--------------------限制日期的选择范围---------------------------------
        //设置当天为最小值
//        dp.setMinDate(calendar.getTimeInMillis());
        //设置最大值是７天
//        calendar.set(Calendar.DAY_OF_MONTH, day + 6);
//        dp.setMaxDate(calendar.getTimeInMillis());
        //-----------------------------------------------------------------------
        try {
            //获取指定的字段
            Field field = dp.getClass().getDeclaredField("mYearSpinner");//---------不让年显示
            //Field field1 = dp.getClass().getDeclaredField("mMonthSpinner");//-------不让月显示
            //解封装
            field.setAccessible(true);
            //field1.setAccessible(true);
            //获取当前实例的值
            NumberPicker np = ((NumberPicker) field.get(dp));
            np.setVisibility(View.GONE);
            //NumberPicker np1 = ((NumberPicker) field1.get(dp));
            //np1.setVisibility(View.GONE);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        timePickerDialog.show();
        datePickerDialog.show();
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
}
