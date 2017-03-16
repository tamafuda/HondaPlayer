package jp.co.honda.music.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.model.TrackInfo;
import jp.co.honda.music.player.R;
import jp.co.honda.music.zdccore.HondaSharePreference;

import static android.content.ContentUris.withAppendedId;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public final class TrackUtil {

    protected static final Logger log = new Logger(TrackUtil.class.getSimpleName(), true);

    public static ArrayList<Media> synTrackListDatabaseWithAlbum(Context context) {
        HondaSharePreference storage = new HondaSharePreference(context);
        ArrayList<Media> mediaList = new ArrayList<Media>();
        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String selection = MediaStore.Audio.Media.DATA
                + " LIKE '/storage/emulated/0/Music/%' AND " + MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION
        };

        Cursor musicCursor = musicResolver.query(musicUri, projection, selection, null, sortOrder);
        while((musicCursor != null && musicCursor.moveToNext())) {
            long trackID = musicCursor.getLong(0);
            String dataTrack = musicCursor.getString(1);
            String trackTitle = musicCursor.getString(2);
            String trackArtist = musicCursor.getString(3);
            String trackAlbum = musicCursor.getString(4);
            Long albumId = musicCursor.getLong(5);
            long trackDuration = musicCursor.getLong(6);
            log.d("AlbumID Value : " + String.valueOf(albumId));
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            String albumArtUri = ContentUris.withAppendedId(sArtworkUri,albumId).toString();
            albumArtUri = getAlbumArt(context,albumId);
            log.d("Album Art URI Value : " + albumArtUri);
            mediaList.add(new Media(trackID, dataTrack, trackTitle, trackArtist, trackAlbum, trackDuration,albumArtUri));

        }
        musicCursor.close();
        storage.storeTrackList(mediaList);
        return mediaList;
    }

    private static String getAlbumArt(Context c, long albumID) {
        String path = "";
        Cursor cursor = c.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[] {String.valueOf(albumID)},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            // do whatever you need to do
        }
        cursor.close();
        return path;
    }

    public static ArrayList<Media> synTrackListDatabase(Context context) {
        HondaSharePreference storage = new HondaSharePreference(context);
        ArrayList<Media> mediaList = new ArrayList<Media>();
        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String selection = MediaStore.Audio.Media.DATA
                + " LIKE '/storage/emulated/0/Music/%' AND " + MediaStore.Audio.Media.IS_MUSIC + "!= 0";
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
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            log.d("AlbumID column : " + String.valueOf(albumColumn));
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);


            //add songs to list
            do {
                long trackID = musicCursor.getLong(idColumn);
                String dataTrack = musicCursor.getString(dataColumn);
                String trackTitle = musicCursor.getString(titleColumn);
                String trackArtist = musicCursor.getString(artistColumn);
                String trackAlbum = musicCursor.getString(albumColumn);
                long trackDuration = musicCursor.getLong(durationColumn);
                Long albumId = musicCursor.getLong(albumIdColumn);
                log.d("AlbumID Value : " + String.valueOf(albumId));

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                String albumArtUri = withAppendedId(sArtworkUri, albumId).getPath();
                albumArtUri = ContentUris.withAppendedId(sArtworkUri,albumId).toString();
                //Uri mAlbumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
                log.d("Artwork albumId is : ", albumArtUri);
                mediaList.add(new Media(trackID, dataTrack, trackTitle, trackArtist, trackAlbum, trackDuration,albumArtUri));
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();
        storage.storeTrackList(mediaList);
        return mediaList;
    }

    public static ArrayList<Media> getTrackList(Context context) {
        HondaSharePreference storage = new HondaSharePreference(context);
        ArrayList<Media> mediaList = new ArrayList<Media>();
        //query external audio
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String selection = MediaStore.Audio.Media.DATA
                + " LIKE '/storage/emulated/0/Music/%' AND " + MediaStore.Audio.Media.IS_MUSIC + "!= 0";
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
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long trackID = musicCursor.getLong(idColumn);
                String dataTrack = musicCursor.getString(dataColumn);
                String trackTitle = musicCursor.getString(titleColumn);
                String trackArtist = musicCursor.getString(artistColumn);
                String trackAlbum = musicCursor.getString(albumColumn);
                long trackDuration = musicCursor.getLong(durationColumn);
                Long albumId = musicCursor.getLong(albumIdColumn);

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                String albumArtUri = withAppendedId(sArtworkUri, albumId).getPath();
                Log.d("ABC", albumArtUri);
                mediaList.add(new Media(trackID, dataTrack, trackTitle, trackArtist, trackAlbum, trackDuration,albumArtUri));
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();
        return mediaList;

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

    /**
     *
     * @param context
     * @return
     */
    public static ArrayList<Media> getRawMediaList(Context context) {
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

        ArrayList<Media> mediaList = new ArrayList<Media>();
        for (MediaMetadataRetriever obj:metadataRetrievers) {
            String title = obj.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String duration = obj.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Media media = new Media(1, "", title, "","",Long.valueOf(duration),null);
            mediaList.add(media);
        }

        return mediaList;
    }

    public static ArrayList<Media> getRadioStationList(Context context) {
        HondaSharePreference storage = new HondaSharePreference(context);
        ArrayList<Media> listRadioStation = new ArrayList<Media>();
        Media media = null;
        ArrayList<Media> listMedias = storage.loadTrackList();
        if (listMedias == null || listMedias.size() == 0) {
            return null;
        }
        String data = "";
        String album = "";
        String artist = "";
        long duration = 0;
        String albumArtID = "";
        media = new Media(1, listMedias.get(0).getData(), "TOKYO FM 80.0Mhz", listMedias.get(0).getAlbum(),listMedias.get(0).getArtist(),listMedias.get(0).getDuration(),listMedias.get(0).getAlbumArtUri());
        listRadioStation.add(media);
        if(listMedias.size() > 1 && listMedias.get(1) != null) {
            data = listMedias.get(1).getData();
            album = listMedias.get(1).getAlbum();
            artist = listMedias.get(1).getArtist();
            duration = listMedias.get(1).getDuration();
            albumArtID = listMedias.get(1).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(2, data , "J-WAVE   76.1Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 2 && listMedias.get(2) != null) {
            data = listMedias.get(2).getData();
            album = listMedias.get(2).getAlbum();
            artist = listMedias.get(2).getArtist();
            duration = listMedias.get(2).getDuration();
            albumArtID = listMedias.get(2).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(3, data, "bayFM    78.0Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 3 && listMedias.get(3) != null) {
            data = listMedias.get(3).getData();
            album = listMedias.get(3).getAlbum();
            artist = listMedias.get(3).getArtist();
            duration = listMedias.get(3).getDuration();
            albumArtID = listMedias.get(3).getAlbumArtUri();

        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(4, data, "RADIO BERRY 76.4Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 4 && listMedias.get(4) != null) {
            data = listMedias.get(4).getData();
            album = listMedias.get(4).getAlbum();
            artist = listMedias.get(4).getArtist();
            duration = listMedias.get(4).getDuration();
            albumArtID = listMedias.get(4).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(5, data, "FM yokohama 84.7Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 5 && listMedias.get(5) != null) {
            data = listMedias.get(5).getData();
            album = listMedias.get(5).getAlbum();
            artist = listMedias.get(5).getArtist();
            duration = listMedias.get(5).getDuration();
            albumArtID = listMedias.get(5).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(6, data, "FM-FUJI     83.0Mhz",album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 6 && listMedias.get(6) != null) {
            data = listMedias.get(6).getData();
            album = listMedias.get(6).getAlbum();
            artist = listMedias.get(6).getArtist();
            duration = listMedias.get(6).getDuration();
            albumArtID = listMedias.get(6).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(7, data, "FMぐんま    86.3Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 8 && listMedias.get(7) != null) {
            data = listMedias.get(7).getData();
            album = listMedias.get(7).getAlbum();
            artist = listMedias.get(7).getArtist();
            duration = listMedias.get(7).getDuration();
            albumArtID = listMedias.get(7).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(8, data, "TBSラジオ   95.4Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 9 && listMedias.get(8) != null) {
            data = listMedias.get(8).getData();
            album = listMedias.get(8).getAlbum();
            artist = listMedias.get(8).getArtist();
            duration = listMedias.get(8).getDuration();
            albumArtID = listMedias.get(8).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(9, data, "ニッポン放送 1242Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 10 && listMedias.get(9) != null) {
            data = listMedias.get(9).getData();
            album = listMedias.get(9).getAlbum();
            artist = listMedias.get(9).getArtist();
            duration = listMedias.get(9).getDuration();
            albumArtID = listMedias.get(9).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(10, data, "ラジオ日本  1422Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 11 && listMedias.get(10) != null) {
            data = listMedias.get(10).getData();
            album = listMedias.get(10).getAlbum();
            artist = listMedias.get(10).getArtist();
            duration = listMedias.get(10).getDuration();
            albumArtID = listMedias.get(10).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(11, data, "栃木放送    864Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 12 && listMedias.get(11) != null) {
            data = listMedias.get(11).getData();
            album = listMedias.get(11).getAlbum();
            artist = listMedias.get(11).getArtist();
            duration = listMedias.get(11).getDuration();
            albumArtID = listMedias.get(11).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(12, data, "NHK東京第１放送 594Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 13 && listMedias.get(12) != null) {
            data = listMedias.get(12).getData();
            album = listMedias.get(12).getAlbum();
            artist = listMedias.get(12).getArtist();
            duration = listMedias.get(12).getDuration();
            albumArtID = listMedias.get(12).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(13, data, "NHK山梨第２放送 1602Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        if(listMedias.size() > 14 && listMedias.get(13) != null) {
            data = listMedias.get(13).getData();
            album = listMedias.get(13).getAlbum();
            artist = listMedias.get(13).getArtist();
            duration = listMedias.get(13).getDuration();
            albumArtID = listMedias.get(13).getAlbumArtUri();
        }else{
            data = listMedias.get(0).getData();
            album = listMedias.get(0).getAlbum();
            artist = listMedias.get(0).getArtist();
            duration = listMedias.get(0).getDuration();
            albumArtID = listMedias.get(0).getAlbumArtUri();
        }
        media = new Media(14, data, "茨城放送  1197Mhz", album,artist,duration,albumArtID);
        listRadioStation.add(media);

        return listRadioStation;

    }

    /**
     *
     * @param context
     * @return
     */
    public static ArrayList<TrackInfo> getRawToMix(Context context) {
        Uri mediaPath = null;
        TrackInfo trackInfo = null;
        ArrayList<TrackInfo> trackList = new ArrayList<TrackInfo>();
        // Hard code
        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ghita);
        trackInfo = new TrackInfo("ghita", mediaPath);
        trackList.add(trackInfo);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.bazz);
        trackInfo = new TrackInfo("bass", mediaPath);
        trackList.add(trackInfo);

        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.jazz);
        trackInfo = new TrackInfo("jazz", mediaPath);
        trackList.add(trackInfo);
        mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.pop);
        trackInfo = new TrackInfo("pop", mediaPath);
        trackList.add(trackInfo);

        return trackList;
    }
}
