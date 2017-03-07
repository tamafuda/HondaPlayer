package jp.co.honda.music.player;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.honda.music.util.TrackUtil;
import jp.co.honda.music.zdccore.AdapterInterface;
import jp.co.honda.music.zdccore.HondaSharePreference;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.adapter.TrackAdapter;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */


public class MusicPlayActivity extends BasePlayerActivity implements AdapterInterface {

    // Logger
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);

    // Media list variables
    private ArrayList<Media> mediaList;
    private ListView trackListView;

    private TextView mTitle;
    private HondaSharePreference storage;
    private boolean isTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        //isTrans = this.checkTransitionFromNotify();
        mTitle = (TextView) findViewById(R.id.id_common_title);
        mTitle.setText("AI レコメンド");
        log.d("onCreate");
        trackListView = (ListView) findViewById(R.id.song_list);
        // Get song list from device
        //mediaList = TrackUtil.getTrackList(getApplicationContext());
        storage = new HondaSharePreference(this);
        //mediaList = storage.loadTrackList();
        mediaList = TrackUtil.getTrackList(getApplicationContext());
        TrackAdapter trackAdapter = new TrackAdapter(this, R.layout.song, mediaList,this, this);
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

    @Override
    protected boolean isNeedKeepMediaSrv() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if(this.checkTransitionFromNotify()) {
            Intent iPlay = new Intent(getBaseContext(), HomeBaseFragment.class);
            startActivity(iPlay);
            finish();
        }else{
            Intent iPlay = new Intent(getBaseContext(), RadarMusicActivity.class);
            startActivity(iPlay);
            finish();
        }
        storage.storeTransitionNotifyToPlay(false);

    }

    private boolean checkTransitionFromNotify() {
        /*Bundle extras = getIntent().getExtras();
        boolean isTransition = false;
        if (extras != null) {
            isTransition = extras.getBoolean(HondaConstants.INTENT_NOTIFY_TO_MUSICPLAY_SRC,false);
        }
        return isTransition;*/
        boolean isTransition = storage.loadTransitionNotifyToPlay();
        return isTransition;
    }

    @Override
    public void updateArtAlbum(int pos) {
        storage.storeTrackIndex(pos);
        super.playFromAdapter();
    }

}
