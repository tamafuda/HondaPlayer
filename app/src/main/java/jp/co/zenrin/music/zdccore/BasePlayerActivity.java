package jp.co.zenrin.music.zdccore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.player.MusicPlayActivity;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.service.MediaPlayerService;
import jp.co.zenrin.music.util.TrackUtil;

public abstract class BasePlayerActivity extends AppCompatActivity {
    // Logger
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);
    private ImageButton btnPrevious;
    private ImageButton btnPlay;
    private ImageButton btnPause;
    private ImageButton btnNext;

    // Track list variables
    private ArrayList<Track> trackList;

    // Service
    private MediaPlayerService player;
    private Intent playIntent;

    // Binding
    private boolean serviceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        btnPrevious = (ImageButton) findViewById(R.id.btn_previous);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnPause = (ImageButton) findViewById(R.id.btn_pause);
        btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnPrevious.setOnClickListener(mOnclick);
        btnPlay.setOnClickListener(mOnclick);
        btnPause.setOnClickListener(mOnclick);
        btnNext.setOnClickListener(mOnclick);
        // Get song list from device
        trackList = TrackUtil.getTrackList(getApplicationContext());
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
                    playAudio();
                    break;
                case R.id.btn_pause:
                    break;
                case R.id.btn_next:
                    break;
            }
        }
    };


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

    private void playAudio() {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackList(trackList);
            storage.storeTrackIndex(getAudioIndex());

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackIndex(getAudioIndex());

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


    // === ABSTRACT METHOD ====
    protected abstract int getLayoutResourceId();
    protected abstract int getAudioIndex();

}
