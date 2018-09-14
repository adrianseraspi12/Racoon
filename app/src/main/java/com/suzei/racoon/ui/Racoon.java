package com.suzei.racoon.ui;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.suzei.racoon.BuildConfig;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class Racoon extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initEmojiManager();
        initJodaTime();
        setUpCompatVector();
        initFirebaseDatabase();
        initTimber();
        initPicasso();
    }


    private void initEmojiManager() {
        EmojiManager.install(new EmojiOneProvider());
    }

    private void initJodaTime() {
        JodaTimeAndroid.init(this);
    }

    private void setUpCompatVector() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void initFirebaseDatabase() {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        instance.setPersistenceEnabled(true);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initPicasso() {
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
