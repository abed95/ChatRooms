package com.abedo.chatappphp.webservices;

import com.abedo.chatappphp.models.ChatRoom;
import com.abedo.chatappphp.models.LoginResponse;
import com.abedo.chatappphp.models.MainResponse;
import com.abedo.chatappphp.models.Message;
import com.abedo.chatappphp.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * created by Abedo95 on 12/2/2019
 */

/*
* this file for using APIS requests
* */
public interface API {


    @POST("login-user.php")
    Call<LoginResponse> loginUser(@Body User user);

    @POST("register-user.php")
    Call<MainResponse> registerUser(@Body User user);

    @POST("add-chat-rooms.php")
    Call<MainResponse> addChatRoom(@Body ChatRoom chatRoom);

    @FormUrlEncoded
    @POST("delete-chat-room.php")
    Call<MainResponse> deleteChatRoom(@Field("id") int roomID);

    @POST("get-all-chat-rooms.php")
    Call<List<ChatRoom>> getAllChatRooms();

    @POST("add-message.php")
    Call<MainResponse> addMessage(@Body Message message);

    @FormUrlEncoded
    @POST("get-messages.php")
    Call<List<Message>> getMessages(@Field("room_id") int roomId);

}
