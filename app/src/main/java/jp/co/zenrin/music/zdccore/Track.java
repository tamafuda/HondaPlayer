package jp.co.zenrin.music.zdccore;

import java.io.Serializable;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/23
 */


public class Track implements Serializable {

    private long id;
    private String data;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private boolean isSelect;

    public Track(long trackID, String trackData, String trackTitle, String trackArtist, String trackAlbum, long trackDuration) {
        id = trackID;
        data = trackData;
        title = trackTitle;
        artist = trackArtist;
        album = trackAlbum;
        duration = trackDuration;
    }

    public long getID() {
        return id;
    }

    public String getData() {
        return data;
    }


    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
