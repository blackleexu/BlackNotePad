package cn.com.box.black.bbnotepad.Iface;

import cn.com.box.black.bbnotepad.Listener.ListListener;

/**
 * Created by www44 on 2017/11/1.
 */

public interface NotesIface<T> {
    void getNoteList(String uid, ListListener<T> listListener);
}
