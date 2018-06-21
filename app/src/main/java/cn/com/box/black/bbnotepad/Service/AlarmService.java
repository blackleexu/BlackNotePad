package cn.com.box.black.bbnotepad.Service;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;

import cn.com.box.black.bbnotepad.Activity.MainActivity;
import cn.com.box.black.bbnotepad.R;

import static cn.com.box.black.bbnotepad.Fragment.Fragment1.mediaPlayer;

/**
 * Created by www44 on 2018/3/19.
 */

public class AlarmService extends Service {


    private NotificationManager notificationManager;
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        Log.e("TAG_ting","闹钟开启了");
        mediaPlayer = MediaPlayer.create(this, R.raw.boo);
        mediaPlayer.start();

        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("时间到了");
        builder.setIcon(R.drawable.alarm);
        builder.setMessage("点击停止闹钟");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.stop();
                stopSelf();
            }
        });
//        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                Intent intent;
//                PendingIntent pendingIntent;
//                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//                Log.e("current", "pkg:"+cn.getPackageName());
//                Log.e("currentclass", "cls:"+cn.getClassName());
//                if(!cn.getClassName().equals("cn.example.www44.memorandum.MainActivity")){
//                    intent = new Intent(getApplicationContext(), MainActivity.class);
//                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//                }else{
//                    intent = new Intent();  //需要跳转指定的页面
//                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//                }
//
//                Notification.Builder notification = new Notification.Builder(getApplicationContext());
//                notification.setContentIntent(pendingIntent);//点击后响应的事件
//                notification.setSmallIcon(R.drawable.alarm);
//                notification.setContentTitle("闹钟响了");
//                notification.setContentText("点我进入闹钟主界面");
//                notification.setWhen(System.currentTimeMillis());//出现notification的时间
//                notification.setAutoCancel(true);//点击后自动消失
//
//                Notification notification1= notification.build();
//                //  数字1为此app的唯一标识，这里为随意设置
//                notificationManager.notify(1,notification1);
//            }
//        });
//        builder.setCancelable(false);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mediaPlayer.stop();
                stopSelf();
                notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent;
                PendingIntent pendingIntent;
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                Log.e("current", "pkg:"+cn.getPackageName());
                Log.e("currentclass", "cls:"+cn.getClassName());
                if(!cn.getClassName().equals("com.example.www44.memorandum.MainActivity")){
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                }else{
                    intent = new Intent();  //需要跳转指定的页面
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                }

                Notification.Builder notification = new Notification.Builder(getApplicationContext());
                notification.setContentIntent(pendingIntent);//点击后响应的事件
                notification.setSmallIcon(R.drawable.alarm);
                notification.setContentTitle("闹钟响了");
                notification.setContentText("点我进入闹钟主界面");
                notification.setWhen(System.currentTimeMillis());//出现notification的时间
                notification.setAutoCancel(true);//点击后自动消失

                Notification notification1= notification.build();
                //  数字1为此app的唯一标识，这里为随意设置
                notificationManager.notify(1,notification1);

            }
        });
        Dialog dialog=builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
