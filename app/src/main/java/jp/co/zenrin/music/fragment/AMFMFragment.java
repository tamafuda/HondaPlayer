package jp.co.zenrin.music.fragment;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.RadioAdapter;
import jp.co.zenrin.music.zdccore.Track;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class AMFMFragment extends Fragment {

    private ArrayList<Track> trackList;
    private ListView trackListView;

    TextView mPlaylist1;
    TextView mPlaylist2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_amfm, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        // Get song list from device
        trackList = TrackUtil.getTrackList(getActivity());
        RadioAdapter trackAdapter = new RadioAdapter(getActivity(), R.layout.radio, trackList);
        trackListView.setAdapter(trackAdapter);
        mPlaylist1 = (TextView) v.findViewById(R.id.id_playlist_fmam_1);
        mPlaylist2 = (TextView) v.findViewById(R.id.id_playlist_fmam_2);
        mPlaylist1.setPaintFlags(mPlaylist1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist2.setPaintFlags(mPlaylist2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //ComponentUtils.setUnderlineTextView(mPlaylist1);


        return v;
    }

}
