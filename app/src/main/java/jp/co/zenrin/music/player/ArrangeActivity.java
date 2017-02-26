package jp.co.zenrin.music.player;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jp.co.zenrin.music.controller.Song;
import jp.co.zenrin.music.helper.MediaControlHelper;
import jp.co.zenrin.music.service.MusicService;
import jp.co.zenrin.music.zdccore.ArrangeAdapter;
import jp.co.zenrin.music.zdccore.Track;

/**
 * Created by nguyenngocbinh on 2/26/17.
 */

public class ArrangeActivity extends ListActivity implements  View.OnClickListener{
    private final  static String TAG = ArrangeActivity.class.getName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private ArrangeAdapter mAdapter;

    private ArrayList<Song> mSongs = null;
    private Intent mPlayIntent;
    private MusicService mMusicService;
    private boolean mBound = false;
    private ImageView mBtnPlay;
    private ImageView mBtnPause;
    private ImageView mBtnNext;
    private ImageView mBtnPrevious;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.txt_android_music);
        setContentView(R.layout.activity_music_arrange);

        init();

        mBtnPlay = (ImageView) findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);

        mBtnPause = (ImageView) findViewById(R.id.btn_pause);
        mBtnPause.setOnClickListener(this);

        mBtnNext = (ImageView) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);

        mBtnPrevious = (ImageView) findViewById(R.id.btn_previous);
        mBtnPrevious.setOnClickListener(this);

        mMusicService = MusicService.getInstance();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // start music service
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MusicService.class);
            bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStateButtonControl();
    }

    @Override
    protected void onDestroy() {
        if (mBound) {
            unbindService(musicConnection);
            mMusicService = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupListView();
                }else {
                    Toast.makeText(getApplicationContext(), "Because limited about permission, you can't access to music screen!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                return;
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                resumePlay();
                break;
            case R.id.btn_pause:
                pause();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_previous:
                previous();
                break;
        }
        updateStateButtonControl();
    }

    private void updateStateButtonControl() {
        if (mMusicService == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mMusicService.isPlaying()) {
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);
                }else {
                    mBtnPause.setVisibility(View.GONE);
                    mBtnPlay.setVisibility(View.VISIBLE);
                }

                if (mMusicService.isFirstSong()) {
                    mBtnPrevious.setEnabled(false);
                }else {
                    mBtnPrevious.setEnabled(true);
                }

                if (mMusicService.isLastSong()) {
                    mBtnNext.setEnabled(false);
                }else {
                    mBtnNext.setEnabled(true);
                }
            }
        });
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissionReadExtStorage()) {
                setupListView();
            }else{
                requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }else {
            setupListView();
        }
        setupListView();
    }



    private void setupListView() {
        if (getListView() != null) {
            mSongs = getAllPhoneSong();
            mAdapter = new ArrangeAdapter(getApplicationContext(),mSongs);
            mAdapter.setOnClickItemListener(onClickItemListener);
            getListView().setAdapter(mAdapter);
        }
    }

    private ArrangeAdapter.OnClickItemListener onClickItemListener = new ArrangeAdapter.OnClickItemListener() {
        @Override
        public void onClickItem(View view, Song song, int position) {
            play(position);
            updateStateButtonControl();
        }
    };

    private ArrayList<Track> getAllSong() {
        ArrayList<Track> tracks = new ArrayList<>();

        Field[] fields=R.raw.class.getFields();
        try {
            for(int index=0; index < fields.length; index++){
                int id = fields[index].getInt(fields[index]);
                String name = fields[index].getName();
                String artist = "unknown";
                Track track = new Track(id, name, artist, null, null, null);
                tracks.add(track);
            }
        }catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        return tracks;
    }

    private ArrayList<Song> getAllPhoneSong() {
        return MediaControlHelper.get(getApplicationContext()).getAllMedias();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            //get service
            mMusicService = binder.getService();
            //pass list
            mMusicService.setList(mSongs);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private void play(int position) {
        mMusicService.setSong(position);
        mMusicService.playSong();
    }


    private void resumePlay() {
        mMusicService.resumeMedia();
    }

    private void pause() {
        mMusicService.pausePlayer();
    }

    private void next() {
        mMusicService.playNext();
    }

    private void previous() {
        mMusicService.playPrev();
    }

    // ------- PERMISSION FUNCTION FOR ANDROID API >=23 (>= ANDROID 6) ------------------------

    /**
     * check permission READ_EXTERNAL_STORAGE
     * */
    private boolean checkPermissionReadExtStorage() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(ArrangeActivity.this)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ArrangeActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage",
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    /**
     * display err and require to allow permission is necessary
     * */
    private void showDialog(final String msg, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ArrangeActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ArrangeActivity.this,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private boolean checkSelfPermission(Activity activity){
        String perReadExtStore = Manifest.permission.READ_EXTERNAL_STORAGE;
        int perGranted = PackageManager.PERMISSION_GRANTED;
        return (ContextCompat.checkSelfPermission(activity, perReadExtStore) != perGranted);
    }
}
