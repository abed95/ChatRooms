package com.abedo.chatappphp.models;

import com.google.gson.annotations.SerializedName;

/**
 * created by Abedo95 on 12/3/2019
 */
public class MainResponse {
    /*وبالمثل الـ Response الخاص بالـ api يكون كالتالى {“status”: 1,”message”: “Welcome !”} والمهم هو الهيكل نفسه status و message سواء نجح وكانت status ب 1 او فشل وكانت ب0 وكذلك الmessage*/

    @SerializedName("status")
    public int status;
    @SerializedName("message")
    public String message;

}
