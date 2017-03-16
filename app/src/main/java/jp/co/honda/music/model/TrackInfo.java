package jp.co.honda.music.model;

import android.media.MediaPlayer;
import android.net.Uri;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/28
 */

public class TrackInfo {
    String id;
    Uri song;
    int position;
    boolean isPause;
    MediaPlayer mp;

    public TrackInfo(String _id, Uri _song) {
        this.id = _id;
        this.song = _song;
        this.mp = new MediaPlayer();
    }

    public String getId() {
        return id;
    }

    public Uri getSong() {
        return song;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

}
