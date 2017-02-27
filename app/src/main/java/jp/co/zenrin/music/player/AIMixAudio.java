package jp.co.zenrin.music.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.TrackInfo;

public class AIMixAudio extends AppCompatActivity implements MediaPlayer.OnCompletionListener{

    Button btnGhita;
    Button btnBass;
    Button btnJazz;
    Button btnPop;
    ArrayList<TrackInfo> trackInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aimix_audio);
        btnGhita = (Button) findViewById(R.id.id_ghita);
        btnBass = (Button) findViewById(R.id.id_bass);
        btnJazz = (Button) findViewById(R.id.id_jazz);
        btnPop = (Button) findViewById(R.id.id_pop);
        btnGhita.setOnClickListener(mOnclick);
        btnBass.setOnClickListener(mOnclick);
        btnJazz.setOnClickListener(mOnclick);
        btnPop.setOnClickListener(mOnclick);
        trackInfoList = TrackUtil.getRawToMix(getApplicationContext());
    }

    /**
     *
     * @param v
     */
    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            String tag = "";
            switch (id) {
                case R.id.id_ghita:
                    tag = btnGhita.getTag().toString();
                    break;
                case R.id.id_bass:
                    tag = btnBass.getTag().toString();
                    break;
                case R.id.id_jazz:
                    tag = btnJazz.getTag().toString();
                    break;
                case R.id.id_pop:
                    tag = btnPop.getTag().toString();
                    break;
                // even more buttons here
            }
            if(!tag.isEmpty()) {
                playAudio(getApplicationContext(), tag);
            }
        }
    };

    private void playAudio(Context ctx, String tag){
        TrackInfo trackInfo = getTrackByTag(tag);
        if(trackInfo != null){
            MediaPlayer mp = trackInfo.getMp();
            if(mp.isPlaying()){
                mp.pause();
                trackInfo.setPosition(mp.getCurrentPosition());
                trackInfo.setPause(true);
            }else {
                if (trackInfo.isPause()) {
                    mp.start();
                    mp.seekTo(trackInfo.getPosition());
                }else {
                    try {
                        mp.setDataSource(ctx, trackInfo.getSong());
                        mp.setOnCompletionListener(this);
                        trackInfo.setPause(false);
                        mp.prepare();
                        mp.start();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     *
     * @param tag
     * @return
     */
    private TrackInfo getTrackByTag(String tag) {
        if (trackInfoList.size() == 0) {
            return null;
        }
        for (TrackInfo t : trackInfoList) {
            if (t.getId().equals(tag)){
                return t;
            }
        }
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(this.getClass().getName(), "Debug");
        mediaPlayer.reset();
    }


}
