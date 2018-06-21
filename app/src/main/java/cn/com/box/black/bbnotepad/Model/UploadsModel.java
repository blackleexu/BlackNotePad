package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.LoginIface;
import cn.com.box.black.bbnotepad.Iface.UploadsIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class UploadsModel extends BaseModel implements UploadsIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public UploadsModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getUploadMsg(String uid, String tittle, String content, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getUploadResult(uid,tittle,content);
        callenqueue(call,tListener);
    }
}
