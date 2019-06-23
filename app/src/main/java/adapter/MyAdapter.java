package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.musicbox.MainActivity;
import com.example.musicbox.R;

import java.util.List;

import entity.Music;
import utils.MusicUtils;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Music> list;
    private int position_flag=0;
    public MyAdapter(MainActivity mainActivity,List<Music> list)
    {
        this.context=mainActivity;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder  holder=null;
        if(view==null)
        {
            holder=new ViewHolder();
            view=View.inflate(context, R.layout.selectitem,null);
            holder.song=(TextView) view.findViewById(R.id.item_music_song);
            holder.singer=(TextView) view.findViewById(R.id.it_music_singer);
            holder.songLength=(TextView) view.findViewById(R.id.item_music_songLength);
            holder.position=(TextView) view.findViewById(R.id.item_music_position);
            view.setTag(holder);

        }
        else
        {
            holder= (ViewHolder) view.getTag();
        }
        String string_song =list.get(position).getSong();
        if (string_song.length() >= 5
                && string_song.substring(string_song.length() - 4,
                string_song.length()).equals(".mp3")) {
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 4).trim());
        } else {
            holder.song.setText(string_song.trim());
        }
        holder.singer.setText(list.get(position).getSinger().toString().trim());

//        holder.song.setText(list.get(position).song.toString());
//        holder.singer.setText(list.get(position).singer.toString());
        int songLength = list.get(position_flag).getSonLength();
        String time= MusicUtils.formaTime(songLength);
        holder.songLength.setText(time);
        holder.position.setText(position+1+"");
        return view;
    }

    class ViewHolder
    {
        TextView song;
        TextView singer;
        TextView songLength;
        TextView position;
    }
}
