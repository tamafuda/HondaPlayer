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

import jp.co.honda.music.model.Media;
import jp.co.honda.music.model.TrackInfo;
import jp.co.honda.music.player.R;
import jp.co.honda.music.zdccore.HondaSharePreference;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public final class TrackUtil {

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

                Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");
                String albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId).getPath();
                Log.d("ABC", albumArtUri);

                //Bitmap bitmap = null;
                //bitmap = PlayerUtils.decodeBitmap(context,albumArtUri,4);
                /*try {
                    bitmap = PlayerUtils.decodeSampledBitmapFromUri(context, albumArtUri, 30, 30);
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.dark_default_album_artwork);
                }*/

                /*try
                {
                    if (albumArtUri != null)
                    {
                        bitmap = BitmapUtils.decodeSampledBitmapFromFile(albumArtUri.getPath(), 30, 30); // JH: Seems to be a reasonable limit for any fullscreen
// artwork
                    }
                    else
                    {
                        bitmap = BitmapUtils.decodeSampledBitmapFromResource(context.getResources(), R.drawable.dark_default_album_artwork, 30, 30);
                    }
                }
                catch (OutOfMemoryError e)
                {
                    log.e("Out of memory loading album artwork" *//* , e *//*);
                }*/
                mediaList.add(new Media(trackID, dataTrack, trackTitle, trackArtist, trackAlbum, trackDuration,albumArtUri));
            }
            while (musicCursor.moveToNext());
        }
        musicCursor.close();
        if(storage.loadTrackList() == null) {
            storage.storeTrackList(mediaList);
        }
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
                String albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId).getPath();
                Log.d("ABC", albumArtUri);
                /*
                Bitmap bitmap = null;
                //bitmap = PlayerUtils.decodeBitmap(context,albumArtUri,4);
                try {
                    bitmap = PlayerUtils.decodeSampledBitmapFromUri(context, albumArtUri, 30, 30);
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.dark_default_album_artwork);
                }*/
                /*try {
                    bitmap = MediaStore.Images.Media.getBitmap(
                            context.getContentResolver(), albumArtUri);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                    bitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.dark_default_album_artwork);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
