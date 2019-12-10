package com.abedo.chatappphp.models;

import com.google.gson.annotations.SerializedName;

/**
 * created by Abedo95 on 12/4/2019
 */
public class ChatRoom {
    @SerializedName("id")
    public String id;
    @SerializedName("room_name")
    public String room_name;
    @SerializedName("room_desc")
    public String room_desc;

}
