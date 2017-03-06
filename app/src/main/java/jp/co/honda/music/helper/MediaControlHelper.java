package jp.co.honda.music.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

import jp.co.honda.music.model.Song;

/**
 * Created by nguyenngocbinh on 2/25/17.
 */

public class MediaControlHelper {
    private static final String TAG = "MediaControlHelper";

    public final static int TYPE_MEDIA = 1;
    public final static int TYPE_ALBUM = TYPE_MEDIA + 1;
    private static final int TYPE_PLAYLIST = TYPE_ALBUM + 1;
    private static final int TYPE_ARTIST = TYPE_PLAYLIST + 1;
    private static final String URI_EXTERNAL = "external";
    private Context mContext;
    private ContentResolver mResolver;

    public static MediaControlHelper get(Context context) {
        return new MediaControlHelper(context);
    }

    public static MediaControlHelper get() {
        return new MediaControlHelper();
    }

    public MediaControlHelper(Context context) {
        mContext = context;
        mResolver = context.getContentResolver();
    }

    public MediaControlHelper() {
    }

    /**
     * get all of media in your device
     * */
    public ArrayList<Song> getAllMedias() {
        ArrayList<Song> mediaModels = new ArrayList<>();
        Cursor songCursor = getList(mContext, TYPE_MEDIA);
        if (songCursor != null && songCursor.moveToFirst()) {
            do {
                mediaModels.add(new Song(songCursor));
            } while (songCursor.moveToNext());
        }
        return mediaModels;
    }



    /**
     * get data by type: MEIDA, ALBUM, PLAYLIST, VIDEO, ...
     * */
    private Cursor getList(Context context, int type) {
        Cursor cursor = null;
        String sortOrder;
        ContentResolver contentResolver = context.getContentResolver();
        switch (type) {
            case TYPE_MEDIA:
                sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
                cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null, null, null, sortOrder);
                break;
            case TYPE_ALBUM:
                sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";
                cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        null, null, null, sortOrder);
                break;
            case TYPE_PLAYLIST:
                sortOrder = MediaStore.Audio.Playlists.NAME + " ASC";
                cursor = contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        null, null,null,sortOrder);
                break;
            case TYPE_ARTIST:
                sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";
                cursor = mResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                        null, null, null, sortOrder);
                break;
        }
        return cursor;
    }
}
