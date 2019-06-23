package entity;

public class Music {
    private String singer;  //歌手
    private String song;    //歌曲名
    private String path;    //歌曲地址
    private int sonLength;  //歌曲长度
    private long size;      //歌曲大小
    private long id;
    private String album;   //歌曲专辑
    private Long album_id;//实际存储为音乐专辑图片

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getSonLength() {
        return sonLength;
    }

    public void setSonLength(int sonLength) {
        this.sonLength = sonLength;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Long album_id) {
        this.album_id = album_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
