package cn.com.box.black.bbnotepad.Iface;

import cn.com.box.black.bbnotepad.Listener.TListener;

/**
 * Created by www44 on 2017/11/1.
 */

public interface LoginIface<T> {
    void getUserList(String username, String password, TListener<T> loginListener);
}
