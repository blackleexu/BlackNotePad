package cn.com.box.black.bbnotepad.Interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AppendUrlParamIntercepter implements Interceptor {
    private final String KEY = "IbSJT6NkV0rnQKfQu3lqXPOh85ag30reHR";
    @Override
    public Response intercept(Chain chain) throws IOException {
        String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
        String salt = "bnp12345";
        String sign = md5(salt+timeStamp+KEY);
        //偷天换日
        Request oldRequest = chain.request();
        //拿到拥有以前的request里的url的那些信息的builder
        HttpUrl.Builder builder = oldRequest
                .url()
                .newBuilder();

        //得到新的url（已经追加好了参数）
        HttpUrl newUrl = builder
                .addQueryParameter("salt", salt)
                .addQueryParameter("sign", sign)
                .addQueryParameter("sign_time", timeStamp)
                .build();

        //利用新的Url，构建新的request，并发送给服务器
        Request newRequest = oldRequest
                .newBuilder()
                .url(newUrl)
                .build();

        return chain.proceed(newRequest);
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
