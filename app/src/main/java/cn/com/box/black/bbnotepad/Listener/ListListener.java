package cn.com.box.black.bbnotepad.Listener;

import java.util.List;

/**
 * Created by www44 on 2017/11/1.
 */

public interface ListListener<T> {
    void onResponse(List<T> t);
    void onFail(String msg);
}
