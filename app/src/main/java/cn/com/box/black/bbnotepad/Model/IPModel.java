package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.ServerIPBean;
import cn.com.box.black.bbnotepad.Bean.UserInfoBean;
import cn.com.box.black.bbnotepad.Iface.IPIface;
import cn.com.box.black.bbnotepad.Iface.UserInfoIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class IPModel extends BaseModel implements IPIface<ServerIPBean> {
    Service service;
    Retrofit retrofit;

    public IPModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getIP(TListener<ServerIPBean> tListener) {
        service=getService();
        Call<ServerIPBean> call = service.getIP();
        callenqueue(call,tListener);
    }
}
