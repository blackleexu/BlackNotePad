package cn.com.box.black.bbnotepad.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.com.box.black.bbnotepad.Fragment.Fragment1;
import cn.com.box.black.bbnotepad.Fragment.Fragment11;
import cn.com.box.black.bbnotepad.Fragment.Fragment111;
import cn.com.box.black.bbnotepad.Fragment.Fragment2;
import cn.com.box.black.bbnotepad.Fragment.Fragment3;
import cn.com.box.black.bbnotepad.R;
import cn.com.box.black.bbnotepad.Service.FastBlur;

import static com.githang.statusbar.StatusBarTools.getStatusBarHeight;

public class MainActivity1 extends AppCompatActivity {

    private Fragment[] views;
    private ViewPager viewPager;

    private long exitTime = 0;
    private LinearLayout layout1,layout2,layout3;
    private ImageButton imageButton1,imageButton2,imageButton3;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    List<Fragment> listfragment;
    FragmentPagerAdapter fragmentpageradapter;
    private String TAGID="-1";
    private Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main1);
        init();
        selectTab(0);
        if (Build.VERSION.SDK_INT <= 19) {
            setStatusBarColor4_4(this,ContextCompat.getColor(this, R.color.colorLogin) );
        } else {
            setStatusBarColor(this,ContextCompat.getColor(this, R.color.colorLogin) );
        }
//        final Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg1);
//        findViewById(R.id.content).getViewTreeObserver().addOnPreDrawListener(
//                new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        blur(bmp1, findViewById(R.id.bottom));
////                        applyBlur();
//                        return true;
//                    }
//                });
//        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimaryDark), false);

    }

    //layout点击事件
    //点击对应的页面按钮选择页面
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            resetImage();
            switch (view.getId()){
                case R.id.layout1:
                    selectTab(0);
                    break;
                case R.id.layout2:
                    selectTab(1);
                    break;
                case R.id.layout3:
                    selectTab(2);
                    break;
            }
        }
    };

    private void init(){
        imageButton1 = (ImageButton)findViewById(R.id.imageButton);
        imageButton2 = (ImageButton)findViewById(R.id.imageButton2);
        imageButton3 = (ImageButton)findViewById(R.id.imageButton3);

        layout1 = (LinearLayout)findViewById(R.id.layout1);
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        layout3 = (LinearLayout)findViewById(R.id.layout3);

        final ImageButton tv_fragment[]={imageButton1,imageButton2,imageButton3};
        final LinearLayout tv_tab[]={layout1,layout2,layout3};

        layout1.setOnClickListener(onClickListener);
        layout2.setOnClickListener(onClickListener);
        layout3.setOnClickListener(onClickListener);

        views=new Fragment[3];
        views[0]= new Fragment111();
        views[1]= new Fragment2();
        views[2]= new Fragment3();


        fragmentpageradapter=new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public Fragment getItem(int position) {
                //返回对应的fragment对象
                return views[position];
            }
            @Override
            public int getCount() {
                return views.length;
            }

//            @Override
//            public int getItemPosition(Object object) {
//                //如果时列表界面（fragment2）需要重绘界面来刷新数据
//                Log.i("tagchange", "getitem");
//                if(TAGID.equals("1"))
//                    return PagerAdapter.POSITION_NONE;
//                else
//                    return POSITION_UNCHANGED;
//            }
        };

        viewPager= (ViewPager) findViewById(R.id.viewPager);
        viewPager .setOffscreenPageLimit(3);
        viewPager.setAdapter(fragmentpageradapter);
        fragmentpageradapter.notifyDataSetChanged();

        //监听viewpager的状态：滑动、被选中页、滑动状态改变
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetImage();
                selectTab(position);
                //得到当前fragment的tagid
                TAGID= String.valueOf(position);
                if(TAGID.equals("1")){
                    views[position].onStart();
                }
//                Log.i("tagname", "getItem:position=" + position + ",fragment:"
//                        + views[position].getClass().getName() + ",fragment.tag="
//                        + views[position].getTag().substring(views[position].getTag().length()-1));
                fragmentpageradapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void selectTab(int i) {
        switch (i)
        {
            case 0:imageButton1.setImageResource(R.drawable.ic_write_pressed);
                break;
            case 1:
                imageButton2.setImageResource(R.drawable.ic_check_pressed);
                break;
            case 2:imageButton3.setImageResource(R.drawable.ic_setting_pressed);
                break;
        }
        viewPager.setCurrentItem(i);
    }


    private void resetImage(){
        imageButton1.setImageResource(R.drawable.ic_write_normal);
        imageButton2.setImageResource(R.drawable.ic_check_normal);
        imageButton3.setImageResource(R.drawable.ic_setting_normal);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
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
    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(fragment1!=null){
            fragmentTransaction.hide(fragment1);
        }
        if (fragment2!=null){
            fragmentTransaction.hide(fragment2);
        }
        if (fragment3!=null){
            fragmentTransaction.hide(fragment3);
        }
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
}
