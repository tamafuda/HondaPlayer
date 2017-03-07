package jp.co.honda.music.model;

import java.io.Serializable;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/23
 */


public class Media implements Serializable {

    private long id;
    private String data;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private String albumArtUri;
    private boolean isSelect;

    public Media(long trackID
            , String trackData
            , String trackTitle
            , String trackArtist
            , String trackAlbum
            , long trackDuration
            , String albumArtUri) {
        this.id = trackID;
        this.data = trackData;
        this.title = trackTitle;
        this.artist = trackArtist;
        this.album = trackAlbum;
        this.duration = trackDuration;
        this.albumArtUri = albumArtUri;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

}
