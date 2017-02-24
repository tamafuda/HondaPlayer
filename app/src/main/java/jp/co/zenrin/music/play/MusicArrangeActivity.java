package jp.co.zenrin.music.play;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jp.co.zenrin.music.controller.Song;
import jp.co.zenrin.music.controller.SongAdapter;

/**
 * Created by nguyenngocbinh on 2/23/17.
 */

public class MusicArrangeActivity extends ListActivity {
    private static final String TAG = "MusicArrangeActivity";
    private SongAdapter mAdapter;
    private ArrayList<Song> mSongs = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.txt_android_music);
        setContentView(R.layout.activity_music_arrange);
        mSongs = getAllSong();
        setupListView();
    }

    private void setupListView() {
        if (getListView() != null) {
            mAdapter = new SongAdapter(getApplicationContext(),mSongs);
            getListView().setAdapter(mAdapter);
        }
    }

    private ArrayList<Song> getAllSong() {
        ArrayList<Song> songs = new ArrayList<>();

        Field[] fields=R.raw.class.getFields();
        try {
            for(int index=0; index < fields.length; index++){
                int id = fields[index].getInt(fields[index]);
                String name = fields[index].getName();
                String artist = "unknown";
                Song song = new Song(id, name, artist);
                songs.add(song);
            }
        }catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        return songs;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getApplicationContext(), id + " - " + mSongs.get(position), Toast.LENGTH_SHORT).show();

    }
}
