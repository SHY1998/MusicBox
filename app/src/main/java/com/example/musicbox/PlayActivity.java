package com.example.musicbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entity.Music;
import service.PlayService;
import utils.AppConstantUtil;
import utils.MusicUtils;

public class PlayActivity extends Activity implements View.OnClickListener {
    TextView nubmer;
    private static int mCurrentPosition=-1;//当前播放序号
    private boolean isPlaying;
    private boolean isPasue;
    private boolean flag;
    private List<Music> music;
    private Music musicEntity;
    private TextView song;
    private TextView singer;
    private SeekBar music_process;
    private ImageButton front;
    private ImageButton pause;
    private ImageButton next;
    private int currentTime;
    private String path;
    private Bitmap mResource;
    private TextView currentProcess,finalProgress,nameText,singerText;
    private PactivityReceiver pactivityReceiver;
    public static final String UPDATE_ACTION="com.example.action.UPDATE_ACTION";
    public static final String CTL_ACTION="com.example.action.CTL_ACTION";
    public static final String MUSIC_CURRENT = "com.example.action.MUSIC_CURRENT";
    public static final String MUSIC_DURATION = "com.example.action.MUSIC_DURATION";
    public static final String MUSIC_PLAYING = "com.example.action.MUSIC_PLAYING";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        Intent intent=getIntent();
        int position=(intent.getIntExtra("position",-1));
        if(position!=mCurrentPosition)
        {
            mCurrentPosition=position;
            flag=true;
            isPasue=false;
            isPlaying=true;
        }
        else
        {
            flag=false;
            isPlaying=false;
            isPasue=true;
        }
        music= MusicUtils.getMusicData(this);
        musicEntity=music.get(mCurrentPosition);
        pause=findViewById(R.id.IB_pause);
        front=findViewById(R.id.IB_front);
        next=findViewById(R.id.IB_next);
        music_process=findViewById(R.id.SB_length);
        nameText=findViewById(R.id.TV_songName);
        singerText=findViewById(R.id.TV_singer);
        song=findViewById(R.id.TV_songName);
        currentProcess=findViewById(R.id.current_progress);
        finalProgress=findViewById(R.id.final_progress);
        front.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        mResource= (Bitmap) MusicUtils.getArtwork(this,musicEntity.getId(),musicEntity.getAlbum_id(),true);
      //  setViewContent(mResource);
        singer=findViewById(R.id.TV_singer);
        song.setText(musicEntity.getSong());
        singer.setText(musicEntity.getSinger());
        music_process.setMax(musicEntity.getSonLength());
        music_process.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    isPlaying=true;
                    isPasue=false;
                    audioTrackChange(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pactivityReceiver=new PactivityReceiver();
        IntentFilter filter= new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        registerReceiver(pactivityReceiver,filter);
        if(flag)
        {
            play();
            isPlaying=true;
            isPasue=false;
        }
        else
        {
            Intent intent1=new Intent(this,PlayService.class);
            intent1.putExtra("MSG", AppConstantUtil.PlayerMsg.PLAYING_MSG);
            intent1.putExtra("position", mCurrentPosition);
            intent1.setAction("com.example.action.MUSIC_SERVICE");
            startService(intent1);
            isPlaying=true;
            isPlaying=false;
        }

    }
    public void play()
    {
        Intent intent=new Intent();
        intent.setAction("com.example.action.MUSIC_SERVICE");
        intent.setClass(this,PlayService.class);
        intent.putExtra("path",musicEntity.getPath());
    //    System.out.println(mCurrentPosition);
        intent.putExtra("position",mCurrentPosition);
        intent.putExtra("MSG",AppConstantUtil.PlayerMsg.PLAY_MSG);
        startService(intent);
        isPlaying=true;
        isPasue=false;
    }
    public class PactivityReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MUSIC_CURRENT))
            {
                currentTime=intent.getIntExtra("currentTime",-1);
                currentProcess.setText(MusicUtils.formaTime(currentTime));
                music_process.setProgress(currentTime);
            }
            else if(action.equals(MUSIC_DURATION))
            {
                int songLength=intent.getIntExtra("songLength",-1);
                music_process.setMax(songLength);
                finalProgress.setText(MusicUtils.formaTime(songLength));
            }
            else if (action.equals(UPDATE_ACTION))
            {
                mCurrentPosition=intent.getIntExtra("current",-1);
                musicEntity=music.get(mCurrentPosition);
                path=musicEntity.getPath();
                if(mCurrentPosition>=0)
                {
                    song.setText(musicEntity.getSong());
                    singer.setText(musicEntity.getSinger());
                }
                if (mCurrentPosition==0)
                {
                    finalProgress.setText(MusicUtils.formaTime(music.get(mCurrentPosition).getSonLength()));
                    pause.setBackgroundResource(R.drawable.play);
                    isPasue=true;
                }
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.IB_pause:
                if(isPlaying)
                {
                    pause();
                }
                else if(isPasue)
                {
                    resume();
                }
                else
                {
                    play();
                }
                break;
            case R.id.IB_front:
                mCurrentPosition--;
                if (mCurrentPosition>=0)
                {
                    musicEntity=music.get(mCurrentPosition);
                    song.setText(musicEntity.getSong());
                    singerText.setText(musicEntity.getSinger());
                    music_process.setProgress(0);
                    play();
                }
                else
                {
                    mCurrentPosition=0;
                    Toast.makeText(PlayActivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.IB_next:
                mCurrentPosition++;
                mCurrentPosition=mCurrentPosition%(music.size());
                musicEntity=music.get(mCurrentPosition);
                song.setText(musicEntity.getSong());
                singerText.setText(musicEntity.getSinger());
                music_process.setProgress(0);
                    play();
                    break;
            default:
                        break;
//
        }

    }

    public void pause()
    {
        Intent intent=new Intent();
        intent.setClass(PlayActivity.this,PlayService.class);
        intent.setAction("com.example.action.MUSIC_SERVICE");
        intent.putExtra("MSG", AppConstantUtil.PlayerMsg.PAUSE_MSG);
        startService(intent);
        isPlaying=false;
        isPasue=true;
    }
    private void resume()
    {
        Intent intent=new Intent();
        intent.setAction("com.example.action.MUSIC_SERVICE");
        intent.setClass(PlayActivity.this,PlayService.class);
        intent.putExtra("MSG",AppConstantUtil.PlayerMsg.CONTINUE_MSG);
        startService(intent);
        isPlaying=true;
        isPasue=false;

    }
    public void audioTrackChange( int progress)
    {
        Intent intent=new Intent();
        intent.setClass(this,PlayService.class);
        intent.setAction("com.example.action.MUSIC_SERVICE");
        intent.putExtra("path",path);
        intent.putExtra("MSG",AppConstantUtil.PlayerMsg.PROGRESS_CHANGE);
        intent.putExtra("position",mCurrentPosition);
        intent.putExtra("progress",progress);
        System.out.println("时间"+progress);
        startService(intent);
    }
//    public void setViewContent(Bitmap bitmap)
//    {
//        setBackgroundBitmap(bitmap);
//        setAvatarBitmap(bitmap);
//    }
//
//    private void setAvatarBitmap(Bitmap bitmap) {
//        mAvatar.setImageBitmap(bitmap);
//    }
//
//    private void setBackgroundBitmap(Bitmap bitmap) {
//        mBackground.setBackgroundDrawable(GaussianBlurUtil.BoxBlurFilter(bitmap));
//
//    }
}
