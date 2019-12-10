package com.abedo.chatappphp.utils;

import android.app.Application;

import io.realm.Realm;

/**
 * created by Abedo95 on 12/4/2019
 */

/*يجب عليك تهيئة Realm للعمل او ما يعرف بشكل شائع بالـ config  لكن قبل ذلك نحتاج لعمل init او ابتداء لل Realm وهو عباره عن استخدام الميثود init واعطائها الكونتكست والطريقة الافضل لعمل ذلك هو استخدام الـ custom application class اى نقوم بعمل class يرث الـ application لنستطيع تنفيذ كود به*/
/*ويجب علينا تسجيل هذا الامر فى  المنيفست الذى يعمل كرئيس مجلس ادارة فى كل تطبيق بموضع هذا الكلاس وأن هذا الكلاس APP سيتم تنفيذه كـ Application class وذلك عن طريق الخاصية name
*/
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
