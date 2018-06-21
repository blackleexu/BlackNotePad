package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.LoginIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class LoginModel extends BaseModel implements LoginIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public LoginModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getUserList(String username, String password, final TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getLoginResult(username,password);
        callenqueue(call,tListener);
    }
}
