package jp.co.zenrin.music.player;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.co.zenrin.music.service.MediaPlayerService;
import jp.co.zenrin.music.zdccore.MusicController;
import jp.co.zenrin.music.zdccore.SongAdapter;
import jp.co.zenrin.music.zdccore.Track;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */


public class MusicPlayActivity extends AppCompatActivity {

    // Track list variables
    private ArrayList<Track> trackList;
    private ListView songView;

    // Service
    private MediaPlayerService mediaPlayerService;
    private Intent playIntent;

    // Binding
    private boolean serviceBound = false;

    // Controller
    private MusicController controller;

    // Activity and playback pause flags
    private boolean pause = false;
    private boolean playBackPause = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        // Retrieve list view
        songView = (ListView) findViewById(R.id.song_list);

        // Instantiate list
        trackList = new ArrayList<Track>();

        // Get song list from device
        getSongList();

        //Sort alphabetically by title
        Collections.sort(trackList, new Comparator<Track>() {
            @Override
            public int compare(Track lhs, Track rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        // Create and set adapter
        SongAdapter songAdt = new SongAdapter(this, trackList);
        songView.setAdapter(songAdt);

        // Setup controller

    }

    //method to retrieve song info from device
    public void getSongList(){
        //query external audio
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, sortOrder);

        //iterate over results if valid
        if(musicCursor!=null && musicCursor.moveToFirst()){
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
                String trackDuration = musicCursor.getString(durationColumn);
                trackList.add(new Track(trackID, dataTrack, trackTitle, trackArtist, trackAlbum, trackDuration));
            }
            while (musicCursor.moveToNext());
        }
    }

}
