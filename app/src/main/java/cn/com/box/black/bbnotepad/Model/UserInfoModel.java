package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Bean.UserInfoBean;
import cn.com.box.black.bbnotepad.Iface.DeleteIface;
import cn.com.box.black.bbnotepad.Iface.UserInfoIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class UserInfoModel extends BaseModel implements UserInfoIface<UserInfoBean> {
    Service service;
    Retrofit retrofit;

    public UserInfoModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getUserinfo(String uid, TListener<UserInfoBean> tListener) {
        service=getService();
        Call<UserInfoBean> call = service.getUserInfo(uid);
        callenqueue(call,tListener);
    }
}
