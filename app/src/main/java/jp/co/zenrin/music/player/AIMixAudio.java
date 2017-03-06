package jp.co.zenrin.music.player;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.BasePlayerActivity;
import jp.co.zenrin.music.zdccore.HondaSharePreference;
import jp.co.zenrin.music.zdccore.Track;
import jp.co.zenrin.music.zdccore.TrackInfo;

public class AIMixAudio extends BasePlayerActivity implements MediaPlayer.OnCompletionListener{

    Button btnGhita;
    Button btnBass;
    Button btnJazz;
    Button btnPop;
    Button btnCancel;
    ArrayList<TrackInfo> trackInfoList;
    TextView mTitle;

    Button btnSave;
    final Context mContext = this;
    String titleOriginIntent;
    private HondaSharePreference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_aimix_audio);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        storage = new HondaSharePreference(this);
        mTitle = (TextView) findViewById(R.id.id_common_title);
        mTitle.setText("Music アレンジ");

        btnGhita = (Button) findViewById(R.id.id_ghita);
        btnBass = (Button) findViewById(R.id.id_bass);
        btnJazz = (Button) findViewById(R.id.id_jazz);
        btnPop = (Button) findViewById(R.id.id_pop);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnGhita.setOnClickListener(mOnclick);
        btnBass.setOnClickListener(mOnclick);
        btnJazz.setOnClickListener(mOnclick);
        btnPop.setOnClickListener(mOnclick);
        btnSave.setOnClickListener(mOnclick);
        btnCancel.setOnClickListener(mOnclick);

        trackInfoList = TrackUtil.getRawToMix(getApplicationContext());
        //titleOriginIntent = getArrangeMusicIntent();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_aimix_audio;
    }

    @Override
    protected int getAudioIndex() {
        return 0;
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
                case R.id.btn_save:
                    // Save audio after mixed
                    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                    View view = layoutInflater.inflate(R.layout.input_dialog_mix,null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setView(view);

                    final EditText musicChangeName = (EditText) view.findViewById(R.id.input_dialog_edittext);
                    musicChangeName.setHint("アレンジ");
                    alertDialogBuilder.setCancelable(false)
                                      .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialogInterface, int i) {
                                              String arrangeMusic = musicChangeName.getText().toString();
                                              if(arrangeMusic.isEmpty()) {
                                                  arrangeMusic = musicChangeName.getHint().toString();
                                              }
                                              //synTrackListChanged(arrangeMusic);
                                              Intent iController = new Intent(getBaseContext(), MusicPlayActivity.class);
                                              startActivity(iController);
                                              finish();
                                          }
                                      })
                                      .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                          }
                                      });
                    AlertDialog alertDialogAndroid = alertDialogBuilder.create();
                    alertDialogAndroid.show();
                    break;
                case R.id.btn_cancel:
                    Intent iController = new Intent(getBaseContext(), MusicPlayActivity.class);
                    startActivity(iController);
                    finish();
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


    private void synTrackListChanged(String titleChanged) {
        Bundle extras = getIntent().getExtras();
        int indexTrack = -1;
        if (extras != null) {
            indexTrack = extras.getInt(HondaConstants.INTENT_AIMIXAUDIO);
        }
        if(indexTrack != -1) {
            ArrayList<Track> list = storage.loadTrackList();
            Track t = list.get(indexTrack);
            t.setTitle(t.getTitle() + titleChanged);
            storage.storeTrackList(list);
        }
    }


    @Override
    public void onBackPressed() {
        Intent iPlay = new Intent(getBaseContext(), MusicPlayActivity.class);
        startActivity(iPlay);
        finish();
    }
}
