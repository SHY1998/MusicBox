package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.musicbox.MainActivity;
import com.example.musicbox.PlayActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import entity.Music;
import utils.AppConstantUtil;
import utils.MusicUtils;


public class PlayService extends Service {
    private MediaPlayer mediaPlayer;
    private MusicUtils musicUtils;
    private List<Music> music;
    private String path;
    private int mCurrentPosition;
    private int currentTime=0;
    private List<Music> list;
    private MyReceiver myReceiver;
    private int msg;
    private boolean isPlaying;
    private boolean isPause;
    private int songLength;
    public static final String UPDATE_ACTION = "com.example.action.UPDATE_ACTION";
    public static final String CTL_ACTION = "com.example.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if(mediaPlayer != null) {
                    currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                    Intent intent = new Intent();
                    intent.setAction(PlayActivity.MUSIC_CURRENT);
                    intent.putExtra("currentTime", currentTime);
                    sendBroadcast(intent); // 给PlayerActivity发送广播
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service","service created");
//        list=new ArrayList<Music>();
//        list=MusicUtils.getMusicData(this);
//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction(MainActivity.CTL_ACTION);
//        registerReceiver(myReceiver,intentFilter);
        mediaPlayer=new MediaPlayer();
        music= MusicUtils.getMusicData(PlayService.this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            mCurrentPosition++;
            Log.v("po",mCurrentPosition+"");
            if (mCurrentPosition<=music.size()-1)
            {
                Intent sendIntent =new Intent(UPDATE_ACTION);
                sendIntent.putExtra("current",mCurrentPosition);
                sendBroadcast(sendIntent);
                path=music.get(mCurrentPosition).getPath();
                play(0);
            }
            else
            {
                mediaPlayer.seekTo(0);
                mCurrentPosition=0;
                Intent sendIntent=new Intent(UPDATE_ACTION);
                sendIntent.putExtra("current",mCurrentPosition);
                sendBroadcast(sendIntent);
            }
            }
        });
    }
    @Override
    public void onStart(Intent intent,int startId)
        {
        if (intent==null)
        {
            stopSelf();
        }
        else
        {
            path=intent.getStringExtra("path");
            msg=intent.getIntExtra("MSG",0);
            mCurrentPosition=intent.getIntExtra("position",-1);
            if (msg == AppConstantUtil.PlayerMsg.PLAY_MSG)
            {
                play(0);
            }
            else if(msg == AppConstantUtil.PlayerMsg.PAUSE_MSG)
            {
                pause();
            }
            else if(msg == AppConstantUtil.PlayerMsg.CONTINUE_MSG)
            {
                resume();
            }
            else if (msg == AppConstantUtil.PlayerMsg.PROGRESS_CHANGE)
            {
                currentTime=intent.getIntExtra("progress",-1);
                System.out.println("第二输出："+currentTime);
                play(currentTime);
            }
            else if(msg == AppConstantUtil.PlayerMsg.PLAYING_MSG)
            {
                handler.sendEmptyMessage(1);
            }
            super.onStart(intent,startId);
        }
    }

    public class MyReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control=intent.getIntExtra("position",-1);
            System.out.println(control);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(list.get(control).getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void play(int currentTime) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void pause()
    {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            isPause=true;
        }
    }
    private void resume()
    {
        if(isPause)
        {
            mediaPlayer.start();
            isPause=false;
        }
    }
    private final class PreparedListener implements MediaPlayer.OnPreparedListener
    {
        private int currentTime;
        public PreparedListener(int currentTime)
        {
            this.currentTime = currentTime;
        }
        @Override
        public void onPrepared(MediaPlayer mp) {
            mediaPlayer.start();
            if (currentTime>0)
            {
                mediaPlayer.seekTo(currentTime);
            }
            Intent intent=new Intent();
            intent.setAction(MUSIC_DURATION);
            songLength=mediaPlayer.getDuration();
            intent.putExtra("songLength",songLength);
            sendBroadcast(intent);

        }
    }
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
