package jp.co.zenrin.music.player;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.BasePlayerActivity;
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
    private ListView songView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate");
        songView = (ListView) findViewById(R.id.song_list);
        // Get song list from device
        trackList = TrackUtil.getTrackList(getApplicationContext());
        TrackAdapter trackAdapter = new TrackAdapter(this, R.layout.song, trackList);
        songView.setAdapter(trackAdapter);
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
