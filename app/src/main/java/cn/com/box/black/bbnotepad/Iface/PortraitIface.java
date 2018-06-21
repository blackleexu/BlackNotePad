package cn.com.box.black.bbnotepad.Iface;

import cn.com.box.black.bbnotepad.Listener.TListener;

/**
 * Created by www44 on 2017/11/1.
 */

public interface PortraitIface<T> {
    void getPorMsg(String uid, String imgpath, TListener<T> tListener);
}
