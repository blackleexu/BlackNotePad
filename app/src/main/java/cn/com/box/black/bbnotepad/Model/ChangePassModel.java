package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.ChangePassIface;
import cn.com.box.black.bbnotepad.Iface.LoginIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class ChangePassModel extends BaseModel implements ChangePassIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public ChangePassModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getChangePass(String uid, String password, String newpass, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getChangePass(uid,password,newpass);
        callenqueue(call,tListener);
    }
}
