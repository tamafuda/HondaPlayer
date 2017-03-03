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
import jp.co.zenrin.music.util.SystemUtils;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.RadioAdapter;
import jp.co.zenrin.music.zdccore.Track;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class IPodFragment extends Fragment {

    private ArrayList<Track> trackList;
    private ListView trackListView;
    private TextView mDeviceInfo;
    private TextView mAudioAmount;

    TextView mPlaylist1;
    TextView mPlaylist2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ipod, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        // Get song list from device
        trackList = TrackUtil.getTrackList(getActivity());
        RadioAdapter trackAdapter = new RadioAdapter(getActivity(), R.layout.radio, trackList);
        trackListView.setAdapter(trackAdapter);

        mDeviceInfo = (TextView) v.findViewById(R.id.id_device_info);
        mDeviceInfo.setText(SystemUtils.getDeviceInfo());

        mAudioAmount = (TextView) v.findViewById(R.id.id_count_audio);
        String amountAudio = String.valueOf(trackList.size()) + "曲";
        mAudioAmount.setText(amountAudio);

        mPlaylist1 = (TextView) v.findViewById(R.id.id_playlist_ipod_1);
        mPlaylist2 = (TextView) v.findViewById(R.id.id_playlist_ipod_2);
        mPlaylist1.setPaintFlags(mPlaylist1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist2.setPaintFlags(mPlaylist2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }




}
