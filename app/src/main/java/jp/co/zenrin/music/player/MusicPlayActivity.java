package jp.co.zenrin.music.player;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.zenrin.music.zdccore.BasePlayerActivity;
import jp.co.zenrin.music.zdccore.HondaSharePreference;
import jp.co.zenrin.music.zdccore.Logger;
import jp.co.zenrin.music.zdccore.Track;
import jp.co.zenrin.music.zdccore.TrackAdapter;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */


public class MusicPlayActivity extends BasePlayerActivity {

    // Logger
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);

    // Track list variables
    private ArrayList<Track> trackList;
    private ListView trackListView;

    private TextView mTitle;
    private HondaSharePreference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        mTitle = (TextView) findViewById(R.id.id_common_title);
        mTitle.setText("AI レコメンド");
        log.d("onCreate");
        trackListView = (ListView) findViewById(R.id.song_list);
        // Get song list from device
        //trackList = TrackUtil.getTrackList(getApplicationContext());
        storage = new HondaSharePreference(this);
        trackList = storage.loadTrackList();
        TrackAdapter trackAdapter = new TrackAdapter(this, R.layout.song, trackList,this);
        trackListView.setAdapter(trackAdapter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_music_play;
    }

    @Override
    protected int getAudioIndex() {
        return 0;
    }



}
