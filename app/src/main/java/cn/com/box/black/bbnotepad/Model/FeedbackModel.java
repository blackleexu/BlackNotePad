package cn.com.box.black.bbnotepad.Model;


import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Iface.FeedbackIface;
import cn.com.box.black.bbnotepad.Iface.UpdateIface;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class FeedbackModel extends BaseModel implements FeedbackIface<SuccessBean> {
    Service service;
    Retrofit retrofit;

    public FeedbackModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getFBMsg(String uid, String content, TListener<SuccessBean> tListener) {
        service=getService();
        Call<SuccessBean> call = service.getFeedbackMsg(uid,content);
        callenqueue(call,tListener);
    }
}
