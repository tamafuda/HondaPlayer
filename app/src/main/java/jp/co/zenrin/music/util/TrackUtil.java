package jp.co.zenrin.music.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.zdccore.Track;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public final class TrackUtil {

    public static ArrayList<Track> getTrackList(Context context) {
        ArrayList<Track> trackList = new ArrayList<Track>();
        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, sortOrder);

        //iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int dataColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long trackID = musicCursor.getLong(idColumn);
                String dataTrack = musicCursor.getString(dataColumn);
                String trackTitle = musicCursor.getString(titleColumn);
                String trackArtist = musicCursor.getString(artistColumn);
                String trackAlbum = musicCursor.getString(albumColumn);
                long trackDuration = musicCursor.getLong(durationColumn);
                trackList.add(new Track(trackID, dataTrack, trackTitle, trackArtist, trackAlbum, trackDuration));
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();

        return trackList;
    }

    /**
     * Convert duration long to time
     *
     * @param duration
     * @return
     */
    public static String covertDuration(long duration) {

        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;
    }

    public static ArrayList<Track> getRawMediaList(Context context) {
        ArrayList<MediaMetadataRetriever> metadataRetrievers = new ArrayList<MediaMetadataRetriever>();
        Uri mediaPath = null;
        MediaMetadataRetriever mmr = null;
        // Hard code

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a01);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a02);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a03);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a04);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a05);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a05);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a06);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a07);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a08);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.a09);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context,mediaPath);
        metadataRetrievers.add(mmr);

        ArrayList<Track> trackList = new ArrayList<Track>();
        for (MediaMetadataRetriever obj:metadataRetrievers) {
            String title = obj.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String duration = obj.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Track track = new Track(1, "", title, "","",Long.valueOf(duration));
            trackList.add(track);
        }

        return trackList;
    }
}
