package com.example.yaginuma.photomemo.utils;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by yaginuma on 15/09/21.
 */
public class RealmBuilder {
    public static RealmConfiguration getRealmConfiguration(Context context) {
        return new RealmConfiguration.Builder(context).build();
    }

    public static Realm getRealmInstance(Context context) {
        return Realm.getInstance(RealmBuilder.getRealmConfiguration(context));
    }

    public static Realm getRealmInstance(RealmConfiguration realmConfiguration) {
        return Realm.getInstance(realmConfiguration);
    }
}
