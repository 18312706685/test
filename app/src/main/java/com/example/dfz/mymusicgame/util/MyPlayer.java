package com.example.dfz.mymusicgame.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.example.dfz.mymusicgame.R;

import java.io.IOException;

/**
 * Created by dfz on 2017/4/30.
 */

public class MyPlayer {

    private static MediaPlayer mMusicMediaPlayer;
//    private static MediaPlayer[] mSoundEffectsPlayer=new MediaPlayer[3];
    public static void playSong(Context context,String filename) {
        if(mMusicMediaPlayer==null){
            mMusicMediaPlayer=new MediaPlayer();
        }
        mMusicMediaPlayer.reset();
        AssetManager assetManager=context.getAssets();
        try {
            AssetFileDescriptor fileDescriptor=assetManager.openFd(filename);
            mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
            mMusicMediaPlayer.prepare();
            mMusicMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mMusicMediaPlayer=MediaPlayer.create(context, R.raw.__00000);
//        mMusicMediaPlayer.start();
    }
    public static void stopTheSong() {
        if (mMusicMediaPlayer != null) {
            mMusicMediaPlayer.stop();
        }
    }
    public static void playSoundEffects(Context context,String filename){

    }
}
