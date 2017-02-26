package jp.co.zenrin.music.zdccore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.util.TrackUtil;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public class TrackAdapter extends BaseAdapter{
    //song list and layout
    private ArrayList<Track> tracks;
    private LayoutInflater songInf;

    public TrackAdapter(Context c, ArrayList<Track> theTracks){
        tracks = theTracks;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
//		LinearLayout songLay = (LinearLayout)songInf.inflate
//				(R.layout.layout_listitem_item, parent, false);
        RelativeLayout songLay = (RelativeLayout)songInf.inflate
                (R.layout.track, parent, false);

        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.track_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.track_duration);
        //get song using position
        Track currTrack = tracks.get(position);
        //get title and artist strings
        songView.setText(currTrack.getTitle());
        //artistView.setText(currTrack.getArtist());
        artistView.setText(TrackUtil.covertDuration(currTrack.getDuration()));
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
