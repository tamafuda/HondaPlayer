package jp.co.zenrin.music.zdccore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.zenrin.music.player.R;

/**
 * Created by nguyenngocbinh on 2/26/17.
 */

public class ArrangeAdapter extends BaseAdapter {
    //song list and layout
    private Context mContext;
    private OnClickItemListener mOnClickItemListener;
    private ArrayList<Song> mSongs;

    //constructor
    public ArrangeAdapter(Context context, ArrayList<Song> songs){
        mContext = context;
        mSongs = songs;
    }

    public interface OnClickItemListener {
        void onClickItem(View view, Song song, int position);
    }

    public OnClickItemListener getOnClickItemListener() {
        return mOnClickItemListener;
    }

    public void setOnClickItemListener(OnClickItemListener mOnClickItemListener) {
        this.mOnClickItemListener = mOnClickItemListener;
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return mSongs.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //map to song layout
        final LinearLayout songLay = (LinearLayout) LayoutInflater.from(mContext).inflate
                (R.layout.layout_listitem_item, parent, false);

        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //get song using position
        Song song = mSongs.get(position);
        //get title and artist strings
        songView.setText(song.getTitle());
        artistView.setText(song.getArtist());
        //set position as tag
        songLay.setTag(position);

        songLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song song = mSongs.get(position);
                if (mOnClickItemListener != null) {
                    mOnClickItemListener.onClickItem(songLay, song, position);
                }
            }
        });
        return songLay;
    }
}
