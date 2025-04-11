package com.example.patrimoin_dz;

import android.app.Application;
import androidx.emoji2.text.EmojiCompat;
import androidx.emoji2.text.DefaultEmojiCompatConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EmojiCompat.Config config = DefaultEmojiCompatConfig.create(this);
        if (config != null) {
            EmojiCompat.init(config);
        }
    }
}