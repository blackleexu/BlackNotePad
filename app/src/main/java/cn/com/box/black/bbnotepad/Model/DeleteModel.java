package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.DeleteIface;
import cn.com.box.black.bbnotepad.Iface.UploadsIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class DeleteModel extends BaseModel implements DeleteIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public DeleteModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getDelMsg(String nid, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getDelMsg(nid);
        callenqueue(call,tListener);
    }
}
