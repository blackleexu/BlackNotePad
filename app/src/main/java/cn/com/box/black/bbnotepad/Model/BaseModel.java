package cn.com.box.black.bbnotepad.Model;

import java.util.List;

import cn.com.box.black.bbnotepad.Listener.ListListener;
import cn.com.box.black.bbnotepad.Listener.TListener;
import cn.com.box.black.bbnotepad.Server;
import cn.com.box.black.bbnotepad.Service.Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by www44 on 2017/11/8.
 */

public class BaseModel {
    Service service;
    Retrofit retrofit;
    public Retrofit getRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Server.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public Service getService(){
        service = retrofit.create(Service.class);
        return service;
    }
    public <T> void callenqueue(Call<T> call, final TListener<T> tListener){
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                tListener.onResponse(response.body());
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                tListener.onFail("获取错误...");
            }
        });
    }

    public <T> void callenqueueList(Call<List<T>> call, final ListListener<T> listListener){
        call.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                if (response.body()!=null&&response.body().size()!=0) {
                    listListener.onResponse(response.body());
                } else {
                    listListener.onFail("FAIL");
                }
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                listListener.onFail("获取失败,请检查网络...");
            }
        });
    }
}
