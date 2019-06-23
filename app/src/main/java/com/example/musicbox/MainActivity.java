package com.example.musicbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.MyAdapter;
import entity.Music;
//import service.MusicService;
import service.PlayService;
import utils.MusicUtils;

public class MainActivity extends Activity
{
    private ListView mListView;
    private List<Music> list;
    private MyAdapter adapter;
    private MediaPlayer mplayer=new MediaPlayer();
    private int currentposition;
    public static final String CTL_ACTION="com.example.action.CTL_ACTION";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);
        setListView();
        Intent intent=new Intent(this,PlayService.class);
        startService(intent);
    }
    //初始化适配器
    //播放音乐
    private void musicplay(int positoin)
    {
        try
        {
            mplayer.reset();
            mplayer.setDataSource(list.get(positoin).getPath());
            mplayer.prepare();
            mplayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setListView()
    {
     mListView=findViewById(R.id.main_listview);
     list=new ArrayList<Music>();
     list=MusicUtils.getMusicData(this);
     adapter=new MyAdapter(this,list);
     mListView.setAdapter(adapter);
     mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           // musicplay(position);
             Intent intent =new Intent(MainActivity.this,PlayActivity.class);
             intent.putExtra("position",position);
             startActivity(intent);
             Intent intent1=new Intent(CTL_ACTION);
             intent1.putExtra("position",position);
             sendBroadcast(intent);
         }
     });

    }


}



