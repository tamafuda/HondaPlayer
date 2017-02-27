package jp.co.zenrin.music.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.service.MediaPlayerService;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.HondaSharePreference;
import jp.co.zenrin.music.zdccore.Logger;
import jp.co.zenrin.music.zdccore.MusicController;
import jp.co.zenrin.music.zdccore.Track;
import jp.co.zenrin.music.zdccore.TrackAdapter;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */


public class MusicPlayActivity extends AppCompatActivity {

    // Logger
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);

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
        log.d("onCreate");
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

        // Get song list from device
        trackList = TrackUtil.getTrackList(getApplicationContext());

        //Sort alphabetically by title
//        Collections.sort(trackList, new Comparator<Track>() {
//            @Override
//            public int compare(Track lhs, Track rhs) {
//                return lhs.getTitle().compareTo(rhs.getTitle());
//            }
//        });

        // Create and set adapter
        //SongAdapter songAdt = new SongAdapter(this, trackList);
        TrackAdapter trackAdapter = new TrackAdapter(this, R.layout.song, trackList);
        songView.setAdapter(trackAdapter);
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            log.d("ServiceConnected Binder");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.d("ServiceBound is false");
            serviceBound = false;
        }
    };


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
        log.d("onDestroy");
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
        super.onDestroy();

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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
