package cn.com.box.black.bbnotepad.Listener;

/**
 * Created by www44 on 2017/11/1.
 */

public interface TListener<T> {
    void onResponse(T t);
    void onFail(String msg);
}
