package cn.com.box.black.bbnotepad.Model;

import java.util.List;

import cn.com.box.black.bbnotepad.Bean.NotesBean;
import cn.com.box.black.bbnotepad.Iface.NotesIface;
import cn.com.box.black.bbnotepad.Listener.ListListener;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by www44 on 2017/11/1.
 */

public class NotesModel extends BaseModel implements NotesIface<NotesBean> {
    Service service;
    Retrofit retrofit;

    public NotesModel(){
        retrofit=getRetrofit();
    }

    @Override
    public void getNoteList(String uid, ListListener<NotesBean> listListener) {
        service=getService();
        Call<List<NotesBean>> call = service.getAllNotes(uid);
        callenqueueList(call,listListener);
    }
}
