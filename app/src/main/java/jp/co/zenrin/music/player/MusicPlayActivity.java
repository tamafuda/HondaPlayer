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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

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


public class MusicPlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{

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

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder)service;
            //get service
            mediaPlayerService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };



    //user song select
    public void songPicked(View view){
        mediaPlayerService.setSong(Integer.parseInt(view.getTag().toString()));
        mediaPlayerService.playSong();
        if(playBackPause){
            setController();
            playBackPause=false;
        }
        controller.show(0);
    }


    //method to retrieve song info from device
    public void getSongList(){
        //query external audio
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + "ASC";
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
                    (MediaStore.Audio.Media.ALBUM;
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


    //set the controller up
    private void setController(){
        controller = new MusicController(this);
        //set previous and next button listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        //set and show
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    private void playNext(){
        mediaPlayerService.playNext();
        if(playBackPause){
            setController();
            playBackPause=false;
        }
        controller.show(0);
    }

    private void playPrev(){
        mediaPlayerService.playPrev();
        if(playBackPause){
            setController();
            playBackPause=false;
        }
        controller.show(0);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null) {
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


}
