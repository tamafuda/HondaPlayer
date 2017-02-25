package jp.co.zenrin.music.player;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.service.MediaPlayerService;
import jp.co.zenrin.music.zdccore.HondaSharePreference;
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
    private MediaPlayerService player;
    private Intent playIntent;

    // Binding
    private boolean serviceBound = false;

    // Controller
    private MusicController controller;

    // Activity and playback pause flags
    private boolean pause = false;
    private boolean playBackPause = false;

    private Button btnPrevious;
    private Button btnPlay;
    private Button btnPause;
    private Button btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        btnPrevious = (Button) findViewById(R.id.btn_previous);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnPrevious.setOnClickListener(mOnclick);
        btnPlay.setOnClickListener(mOnclick);
        btnPause.setOnClickListener(mOnclick);
        btnNext.setOnClickListener(mOnclick);
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
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

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
        musicCursor.close();
    }

    /**
     *
     * @param v
     */
    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.btn_previous:
                    break;
                case R.id.btn_play:
                    playAudio(1);
                    break;
                case R.id.btn_pause:
                    break;
                case R.id.btn_next:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
    }


    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackList(trackList);
            storage.storeTrackIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEW_TRACK);
            sendBroadcast(broadcastIntent);
        }
    }
}
