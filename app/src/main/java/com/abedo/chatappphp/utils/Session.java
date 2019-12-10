package com.abedo.chatappphp.utils;

import android.app.Activity;
import android.content.Intent;
import com.abedo.chatappphp.LoginActivity;
import com.abedo.chatappphp.models.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * created by Abedo95 on 12/2/2019
 */
public class Session {
    // define single instance
    private static Session instance;
    // define realm
    private Realm realm;

    // Session constructor
    private Session() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(realmConfig);
    }

    // get singletone from session
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // get new Instance (new Object) from this class
    public static Session newInstance() {
        return new Session();
    }

    // login user take user and add it to realm
    public void loginUser(final User user) {
        if (realm.where(User.class).findFirst() == null) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(user);
                }
            });

        } else {
            logout();
            loginUser(user);
        }


    }

    // logout
    public void logout() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(User.class);
            }
        });
    }

    public boolean isUserLoggedIn() {
        return realm.where(User.class).findFirst() != null;
    }

    public User getUser() {
        return realm.where(User.class).findFirst();
    }

    public void logoutAndGoToLogin(Activity activity) {
        logout();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }



/*
    //get one user first
    User uf=realm.where(User.class).findFirst();
    //get all users
    List<User> userList = realm.where(User.class).findAll();
    //get the one user with name
    User u =realm.where(User.class).equalTo("username","Abed").findFirst();
    //get all users with name abed
    List<User> users = realm.where(User.class).equalTo("username","abed").findAll();

    // update user
    User uupdate = realm.where(User.class).equalTo("username","ace").findFirst();
     realm.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            uupdate.username="abed";
        }
    });

     //delete user
     User deleteuser = realm.where(User.class).equalTo("username","ace").findFirst();
     realm.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            deleteuser.deleteFromRealm();
        }
    });

    //delete all users with name ali
    final RealmResults<User> deleteAllUser = realm.where(User.class).equalTo("username","ace").findAll();
     realm.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            deleteAllUser.deleteFromRealm();
        }
    });

     //delet all users
    realm.executeTransaction(new Realm.Transaction() {
        @Override
        public void execute(Realm realm) {
            realm.delete(User.class);
        }
    });*/

}
