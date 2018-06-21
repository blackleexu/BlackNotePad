package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.UpdateIface;
import cn.com.box.black.bbnotepad.Iface.UploadsIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class UpdateModel extends BaseModel implements UpdateIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public UpdateModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getUpdateMsg(String uid, String nid, String tittle, String content, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getUpdtMsg(uid,nid,tittle,content);
        callenqueue(call,tListener);
    }
}
