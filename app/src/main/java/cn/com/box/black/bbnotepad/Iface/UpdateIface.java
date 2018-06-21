package cn.com.box.black.bbnotepad.Iface;

import cn.com.box.black.bbnotepad.Listener.TListener;

/**
 * Created by www44 on 2017/11/1.
 */

public interface UpdateIface<T> {
    void getUpdateMsg(String uid,String nid, String tittle, String content, TListener<T> tListener);
}
