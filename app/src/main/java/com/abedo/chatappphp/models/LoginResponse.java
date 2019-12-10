package com.abedo.chatappphp.models;

import com.google.gson.annotations.SerializedName;

/**
 * created by Abedo95 on 12/5/2019
 */
public class LoginResponse {
    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;
    @SerializedName("user")
    public User user;

    public static class User {
        @SerializedName("id")
        public String id;
        @SerializedName("user_name")
        public String user_name;
        @SerializedName("user_email")
        public String user_email;
        @SerializedName("is_user_admin")
        public String is_user_admin;
    }

}
