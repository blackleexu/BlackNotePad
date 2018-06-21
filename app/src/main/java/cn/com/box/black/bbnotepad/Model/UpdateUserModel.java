package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.LoginIface;
import cn.com.box.black.bbnotepad.Iface.UpdateUserIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class UpdateUserModel extends BaseModel implements UpdateUserIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public UpdateUserModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getUpdateMsg(String uid, String name, String phone, String gender,String eamil, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getUUmessage(uid,name,phone,gender,eamil);
        callenqueue(call,tListener);
    }
}
