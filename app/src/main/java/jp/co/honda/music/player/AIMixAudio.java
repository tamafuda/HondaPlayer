package jp.co.honda.music.player;

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

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.ProgressDialogTask;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.model.TrackInfo;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.util.PlayerUtils;
import jp.co.honda.music.util.TrackUtil;
import jp.co.honda.music.zdccore.AIMixInterface;
import jp.co.honda.music.zdccore.AdapterInterface;
import jp.co.honda.music.zdccore.HondaSharePreference;

/**
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 *
 * AIMixAudio class is executed mixing audio file then save into device
 * We are confusing about extends of audio file that is supported by mix
 * Note : On this version, do a hard code for mixing media file
 *
 */
public class AIMixAudio extends BasePlayerActivity implements MediaPlayer.OnCompletionListener,AdapterInterface,AIMixInterface{

    private Button btnGhita;
    private Button btnBass;
    private Button btnJazz;
    private Button btnPop;
    private Button btnCancel;
    private ArrayList<TrackInfo> trackInfoList;
    private ArrayList<TrackInfo> mediaPlayList;
    private TextView mTitle;
    private Button btnSave;
    private final Context mContext = this;
    private String titleOriginIntent;
    private HondaSharePreference storage;
    private MediaPlayer mediaPlayer;
    private boolean isNeedKeepSrc = false;

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
        btnGhita.setOnClickListener(mOnclick);
        btnBass.setOnClickListener(mOnclick);
        btnJazz.setOnClickListener(mOnclick);
        btnPop.setOnClickListener(mOnclick);
        trackInfoList = TrackUtil.getRawToMix(getApplicationContext());
        mediaPlayList = new ArrayList<TrackInfo>();
        super.setAIInterface(this);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_aimix_audio;
    }

    @Override
    protected int getAudioIndex() {
        return 0;
    }

    @Override
    protected boolean isNeedKeepMediaSrv() {
        return isNeedKeepSrc;
    }

    @Override
    protected String detectScreenID() {
        return HondaConstants.DETECTED_SCREEN_ARRANGE;
    }

    /**
     * Listener for control onClick event of all button in this screen
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
                    btnGhita.setSelected(!btnGhita.isSelected());
                    break;
                case R.id.id_bass:
                    tag = btnBass.getTag().toString();
                    btnBass.setSelected(!btnBass.isSelected());
                    break;
                case R.id.id_jazz:
                    tag = btnJazz.getTag().toString();
                    btnJazz.setSelected(!btnJazz.isSelected());
                    break;
                case R.id.id_pop:
                    tag = btnPop.getTag().toString();
                    btnPop.setSelected(!btnPop.isSelected());
                    break;
                case R.id.btn_save:
                    AIMixAudio.super.stopFromChild();
                    isNeedKeepSrc = false;
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
                                              PlayerUtils.addOneMediaArrange(getBaseContext(),arrangeMusic);
                                              Intent iController = new Intent(getBaseContext(), RadarMusicActivity.class);
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
                    break;
                // even more buttons here
            }
            if(!tag.isEmpty()) {
                playAudio(getApplicationContext(), tag);
            }
        }
    };

    /**
     * Play mixing audio file
     * These files is stored at raw folder of project
     * @param ctx
     * @param tag
     */
    private void playAudio(Context ctx, String tag){
        TrackInfo trackInfo = getTrackByTag(tag);
        if(trackInfo != null){
            mediaPlayer = trackInfo.getMp();
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                trackInfo.setPosition(mediaPlayer.getCurrentPosition());
                trackInfo.setPause(true);
            }else {
                if (trackInfo.isPause()) {
                    mediaPlayer.start();
                    mediaPlayer.seekTo(trackInfo.getPosition());
                }else {
                    try {
                        mediaPlayer.setDataSource(ctx, trackInfo.getSong());
                        mediaPlayer.setOnCompletionListener(this);
                        trackInfo.setPause(false);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * Get mix audio by tag name
     * @param tag
     * @return
     */
    private TrackInfo getTrackByTag(String tag) {
        if (trackInfoList.size() == 0) {
            return null;
        }
        for (TrackInfo t : trackInfoList) {
            if (t.getId().equals(tag)){
                mediaPlayList.add(t);
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

    /**
     * This function is store mixed audio file into current mediaList and save to Preference
     * @param titleChanged
     */
    private void synTrackListChanged(String titleChanged) {
        Bundle extras = getIntent().getExtras();
        int indexTrack = -1;
        if (extras != null) {
            indexTrack = extras.getInt(HondaConstants.INTENT_AIMIXAUDIO);
        }
        if(indexTrack != -1) {
            ArrayList<Media> list = storage.loadTrackList();
            Media t = list.get(indexTrack);
            t.setTitle(t.getTitle() + titleChanged);
            storage.storeTrackList(list);
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        releaseMediaPlayer();
        super.stopUpdateSeekbar();
        Intent iController = new Intent(getBaseContext(), RadarMusicActivity.class);
        startActivity(iController);
        finish();
        super.onBackPressed();
    }

    @Override
    public void updateArtAlbum(int pos) {
        storage.storeTrackIndex(pos);
        super.playFromAdapter();
    }

    @Override
    public void keepSrv(boolean isKeep) {

    }

    /**
     * Release a media player when change screen
     */
    private void releaseMediaPlayer() {
        // Release the media player

        for (TrackInfo t : mediaPlayList) {
            if (t.getMp() != null) {
                log.d("Mix Audio mediaplayer release !");
                t.getMp().reset();
                t.getMp().release();
                t.setMp(null);
            }
        }
    }

    @Override
    public void shareMedia() {
        log.d("Share media");
        releaseMediaPlayer();
        AIMixAudio.super.stopUpdateSeekbar();
        stopService(new Intent(AIMixAudio.this, MediaPlayerService.class));
        ProgressDialogTask task1 = new ProgressDialogTask(AIMixAudio.this, false, R.string.popup_sharing);
        task1.execute(0);

    }

    @Override
    public void saveMedia() {
        log.d("Save media");
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
                        AIMixAudio.super.stopUpdateSeekbar();
                        String arrangeMusic = musicChangeName.getText().toString();
                        if(arrangeMusic.isEmpty()) {
                            arrangeMusic = musicChangeName.getHint().toString();
                        }
                        PlayerUtils.addOneMediaArrange(getBaseContext(),arrangeMusic);
                        releaseMediaPlayer();
                        stopService(new Intent(AIMixAudio.this, MediaPlayerService.class));
                        Intent iController = new Intent(getBaseContext(), RadarMusicActivity.class);
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

    }
}
