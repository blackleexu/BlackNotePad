package cn.com.box.black.bbnotepad.Iface;

import cn.com.box.black.bbnotepad.Listener.TListener;

/**
 * Created by www44 on 2017/11/1.
 */

public interface UpdateUserIface<T> {
    void getUpdateMsg(String uid, String name, String phone,String gender,String email, TListener<T> tListener);
}
