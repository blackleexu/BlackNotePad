package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.LoginIface;
import cn.com.box.black.bbnotepad.Iface.RegisterIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class RegisterModel extends BaseModel implements RegisterIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public RegisterModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getRegMsg(String username, String password,String clientid, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getRegmessage(username,password,clientid);
        callenqueue(call,tListener);
    }
}
