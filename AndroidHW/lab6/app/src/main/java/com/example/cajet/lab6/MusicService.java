package com.example.cajet.lab6;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by cajet on 2016/10/27.
 */

public class MusicService extends Service{

    public MediaPlayer mplayer= new MediaPlayer();
    public final IBinder binder = new MyBinder();
    public String mp_state="empty";
    public int mp_degree= 1;

    public MusicService() {
        try {
            mplayer.setDataSource("/data/K.Will-Melt.mp3");
            mplayer.prepare();
            mplayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCreate()
    {
        super.onCreate();
    }


    public void stop()
    {
        mplayer.pause();
        mp_state= "isstopped";
    }

    public void play() {
        mplayer.start();
        mp_state = "isplaying";

    }

    public void pause() {
        mplayer.pause();
        mp_state= "ispaused";
    }

    public void onDestroy()
    {
        if (mplayer != null)
        {
            mplayer.stop();
            mplayer.release();
        }
        super.onDestroy();
    }


    public IBinder onBind(Intent intent)
    {
        return this.binder;
    }

    public class MyBinder extends Binder
    {

        MusicService getService()
        {
            return MusicService.this;
        }
    }
    

}
