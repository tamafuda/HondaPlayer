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
 * @Author: Hoang Vu
 * @Date:   2017/02/23
 */


public class SongAdapter extends BaseAdapter {
	
	//song list and layout
	private ArrayList<Track> tracks;
	private LayoutInflater songInf;
	
	//constructor
	public SongAdapter(Context c, ArrayList<Track> theTracks){
		tracks = theTracks;
		songInf=LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return tracks.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//map to song layout
//		LinearLayout songLay = (LinearLayout)songInf.inflate
//				(R.layout.layout_listitem_item, parent, false);
		LinearLayout songLay = (LinearLayout)songInf.inflate
				(R.layout.song, parent, false);

		//get title and artist views
		TextView songView = (TextView)songLay.findViewById(R.id.song_title);
		TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
		//get song using position
		Track currTrack = tracks.get(position);
		//get title and artist strings
		songView.setText(currTrack.getTitle());
		artistView.setText(currTrack.getArtist());
		//set position as tag
		songLay.setTag(position);
		return songLay;
	}

}
