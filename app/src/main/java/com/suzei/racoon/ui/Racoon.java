package com.suzei.racoon.ui;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        setUpDatabaseDisconnection();
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

    private void setUpDatabaseDisconnection() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
                    userRef.child("users").child(mAuth.getCurrentUser().getUid()).child("online")
                            .onDisconnect().setValue(false);

                    if (connected) {
                        Timber.i("connected");
                        // Show snackbar says connected
                    } else {
                        Timber.i("disconnected");
                        // Show snackbar says disconnected
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
