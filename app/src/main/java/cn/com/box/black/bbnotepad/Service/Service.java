package cn.com.box.black.bbnotepad.Service;

import java.util.List;

import cn.com.box.black.bbnotepad.Bean.NotesBean;
import cn.com.box.black.bbnotepad.Bean.ResultBean;
import cn.com.box.black.bbnotepad.Bean.ServerIPBean;
import cn.com.box.black.bbnotepad.Bean.SuccessBean;
import cn.com.box.black.bbnotepad.Bean.UserInfoBean;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

//import cn.edu.neusoft.lixu524.foodorder.Bean.UserBean;

/**
 * Created by www44 on 2017/11/1.
 */

public interface Service {
    @GET("login")
    Call<SuccessBean> getLoginResult(@Query("uname") String username, @Query("password") String userpass);
    @GET("reg")
    Call<SuccessBean> getRegmessage(@Query("uname") String username, @Query("password") String userpass, @Query("clientid") String clientid);
    @GET("updtuser")
    Call<SuccessBean> getUUmessage(@Query("uid") String uid,@Query("uname") String username,
                                     @Query("telphone") String tel,@Query("gender") String gender,@Query("email") String email);
    @GET("uploads")
    Call<SuccessBean> getUploadResult(@Query("uid") String uid, @Query("tittle") String tittle, @Query("content") String content);

    @GET("feedback")
    Call<SuccessBean> getFeedbackMsg(@Query("uid") String uid,@Query("feedback") String content);

    @GET("index")
    Call<List<NotesBean>> getAllNotes(@Query("uid") String uid);

    @GET("getuser")
    Call<UserInfoBean> getUserInfo(@Query("uid") String uid);

    @GET("del")
    Call<SuccessBean> getDelMsg(@Query("nid") String nid);

    @GET("updtportrait")
    Call<SuccessBean> getPorMsg(@Query("uid") String uid,@Query("portrait") String imgpath);

    @GET("changepass")
    Call<SuccessBean> getChangePass(@Query("uid") String uid, @Query("password") String password,@Query("newpass") String newpass);

    @GET("update")
    Call<SuccessBean> getUpdtMsg(@Query("uid") String uid,@Query("nid") String nid,@Query("tittle") String tittle,@Query("content") String content);

    @Multipart
    @POST("upload.php")
    Call<ResultBean> uploadImage(@Part MultipartBody.Part file, @Part("uid") RequestBody uid);

    @GET("getip")
    Call<ServerIPBean> getIP();
//    @GET("getAllShops.do")
//    Call<List<ShopListBean>> getShoplist();
//
//    @GET("getFoodByShop.do")
//    Call<List<FoodListBean>> getFoodlist(@Query("shop_id") int shop_id);
//
//    @GET("getFoodById.do")
//    Call<FoodDetailBean> getFooddetail(@Query("food_id") int food_id);
//
//    @GET("insertOrder.do")
//    Call<PurchaseBean> getPurchase(@Query("user_id") int user_id, @Query("food_id") int food_id,
//                                   @Query("num") int num, @Query("sum") double sum, @Query("suggesttime") String suggesttime);
//
//    @GET("isCollected.do")
//    Call<IsCollectBean> getIscollected(@Query("user_id") int user_id, @Query("shop_food_id") int shop_food_id, @Query("flag") int flag);
//
//    @GET("userCollectShop.do")
//    Call<RegisterBean> getShopcollect(@Query("user_id") int user_id, @Query("shop_id") int shop_id);
//
//    @GET("userCollectFood.do")
//    Call<RegisterBean> getFoodcollect(@Query("user_id") int user_id, @Query("food_id") int food_id);
//
//    @GET("getAllUserCollection.do")
//    Call<List<GetCollectBean>> getCollect(@Query("user_id") int user_id, @Query("flag") int flag);
//
//    @GET("getFoodBySearch.do")
//    Call<List<FoodListBean>> getSearchMsg(@Query("search") String search);
//
//    @GET("getUserById.do")
//    Call<UserBean> getUserMeg(@Query("user_id") int user_id);
//
//    @GET("getAllUserOrder.do")
//    Call<List<OrderOrCommentBean>> getOrderMeg(@Query("user_id") int user_id);
//
//    @GET("getAllUserComment.do")
//    Call<List<OrderOrCommentBean>> getCommentMeg(@Query("user_id") int user_id);
//
//    @GET("getAllUserFoodOrder.do")
//    Call<List<OrderOrCommentBean>> getfoodCommentMeg(@Query("food_id") int food_id);
//
//    @GET("updateUserById.do")
//    Call<RegisterBean> getchangeMeg(@Query("user_id") int user_id, @Query("username") String username, @Query("userpass") String userpass,
//                                    @Query("mobilenum") String mobilenum, @Query("address") String address);
//
//    @GET("deleteComment.do")
//    Call<RegisterBean> getdelCommentMeg(@Query("order_id") int order_id);
//
//    @GET("insertComment.do")
//    Call<RegisterBean> getinCommentMeg(@Query("order_id") int order_id, @Query("content") String content);
//
//    @GET("updateComment.do")
//    Call<RegisterBean> getupdtCommentMeg(@Query("order_id") int order_id, @Query("content") String content);

}
