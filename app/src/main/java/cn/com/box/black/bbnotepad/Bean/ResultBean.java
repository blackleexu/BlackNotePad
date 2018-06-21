package cn.com.box.black.bbnotepad.Bean;

/**
 * Created by www44 on 2018/5/6.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultBean {

    @SerializedName("success")
    @Expose
    private String success;


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }
}