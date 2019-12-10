package com.abedo.chatappphp.models;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * created by Abedo95 on 12/3/2019
 */
public class User extends RealmObject {

    /*والـ SerializedName هو الاسم الموجود كـ key فى الجيسون لذلك اذا غيرت كلمة username الى userFristName مثلا فيظل الكلاس يعرف ان هذا هو المقصود به username فى الـ JSON*/

    @SerializedName("username")
    public String username;

    @SerializedName("password")
    public String password;

    @SerializedName("email")
    public String email;

    /*ولاحظ أنهم ليس لديهم serialized name ولا نحتاجه هنا لاننا عندما نستخدم الكلاس User فى تسجيل الدخول كباراميتر للميثود loginUser فى Retrofit نرسل الايميل والباسورد فقط ليتم التحقق منهم لذلك باقى الاشياء سترسل خالية وسيتم تجاهلها من قبل php لاننا لم نستدعيها ولم نستخدمها هناك فى ملف login-user.php*/
    public int id;
    public boolean isAdmin;


}
