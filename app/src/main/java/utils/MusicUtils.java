package utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.example.musicbox.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import entity.Music;

public class MusicUtils {
    public static List<Music> getMusicData(Context context)
    {
        List<Music> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                Music music = new Music();
                //获取音乐资源并置入实体
                music.setSong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                music.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                music.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                music.setSonLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                music.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                music.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                music.setAlbum_id(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                music.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                if (music.getSize() > 1000 * 800) //过滤短音频
                {
                    if (music.getSong().contains("-")) {//分离歌曲名和歌手  例如江南-林俊杰
                        String[] str = music.getSong().split("-");
                        music.setSinger(str[0]);
                        music.setSong(str[1]);
                    }
                    list.add(music);
                }
            }
            cursor.close();//释放资源
        }
        return list;
        }
        //格式化时间
        public static String formaTime(int time)
        {
            if(time/1000%60<10)
            {
                return time/1000/60+".0"+time/1000%60;
            }
            else
            {
                return time/1000/60+":"+time/1000%60;
            }
        }

        private static  final Uri sArtworkUri=Uri.parse("content://media/external/audio/albumart");
        private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

        public static Bitmap getArtwork(Context context,long song_id,long album_id,
                                        boolean allowdefault)
        {
            if (album_id<0)
            {
                if(song_id>=0)
                {
                    Bitmap bm=getArtworkFromFile(context,song_id,-1);
                    if (bm!=null)
                    {
                        return bm;
                    }
                }
                if(allowdefault)
                {
                    return getDefaultArtwork(context);
                }
                return null;
            }
            ContentResolver res=context.getContentResolver();
            Uri uri= ContentUris.withAppendedId(sArtworkUri,album_id);
            if(uri!=null)
            {
                InputStream in=null;
                try
                {
                    in=res.openInputStream(uri);
                    Bitmap bmp=BitmapFactory.decodeStream(in,null,sBitmapOptions);
                    if (bmp==null)
                    {
                        bmp=getDefaultArtwork(context);
                    }
                    return bmp;
                } catch (FileNotFoundException e) {
                    Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                    if (bm != null) {
                        if (bm.getConfig() == null) {
                            bm = bm.copy(Bitmap.Config.RGB_565, false);
                            if (bm == null && allowdefault) {
                                return getDefaultArtwork(context);
                            }
                        }
                    } else if (allowdefault) {
                        bm = getDefaultArtwork(context);
                    }
                    return bm;
                }
                finally
                {
                    try
                    {
                        if (in!=null)
                        {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private static Bitmap getArtworkFromFile(Context context,long songid,long albumid)
        {
            Bitmap bm=null;
            if(albumid<0&&songid<0)
            {
                throw new IllegalArgumentException("Must specify an album or a song id");
            }
            try
            {
                if (albumid<0)
                {
                    Uri uri=Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                    ParcelFileDescriptor pfd=context.getContentResolver()
                            .openFileDescriptor(uri,"r");
                    if(pfd!=null)
                    {
                        FileDescriptor fd=pfd.getFileDescriptor();
                        bm=BitmapFactory.decodeFileDescriptor(fd);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return bm;
        }
        @SuppressLint("ResourceType")
        private static Bitmap getDefaultArtwork(Context context)
        {
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inPreferredConfig=Bitmap.Config.RGB_565;
            return BitmapFactory.decodeStream(
                    context.getResources().openRawResource(R.drawable.default_album),null,opts);

        }

}
