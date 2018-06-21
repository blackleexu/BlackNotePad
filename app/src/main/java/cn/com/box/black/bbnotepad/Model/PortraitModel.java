package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.LoginIface;
import cn.com.box.black.bbnotepad.Iface.PortraitIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 * 修改头像
 */

public class PortraitModel extends BaseModel implements PortraitIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public PortraitModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getPorMsg(String uid, String imgpath, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getPorMsg(uid,imgpath);
        callenqueue(call,tListener);
    }
}
