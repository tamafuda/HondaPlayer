package jp.co.zenrin.music.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.player.TestFragment;
import jp.co.zenrin.music.util.SystemUtils;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.Logger;
import jp.co.zenrin.music.zdccore.RadioAdapter;
import jp.co.zenrin.music.zdccore.RadioRecyclerViewAdapter;
import jp.co.zenrin.music.zdccore.Track;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class IPodFragment extends Fragment implements View.OnClickListener{

    // Logger
    protected final Logger log = new Logger(IPodFragment.class.getSimpleName(), true);
    private ArrayList<Track> trackList;
    private ListView trackListView;
    private TextView mDeviceInfo;
    private TextView mAudioAmount;
    private RecyclerView mRecyclerView;
    RadioRecyclerViewAdapter mRadioRecyclerAdapter;

    TextView mPlaylist1;
    TextView mPlaylist2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ipod, container, false);
        //View v = inflater.inflate(R.layout.fragment_ipod_recycleview, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        //mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Get song list from device
        trackList = TrackUtil.getTrackList(getActivity());
        RadioAdapter trackAdapter = new RadioAdapter(getActivity(), R.layout.radio, trackList);
        //mRadioRecyclerAdapter = new RadioRecyclerViewAdapter(getActivity(), trackList);

        trackListView.setAdapter(trackAdapter);
        //mRecyclerView.setAdapter(mRadioRecyclerAdapter);
        //mRadioRecyclerAdapter.

        mDeviceInfo = (TextView) v.findViewById(R.id.id_device_info);
        mDeviceInfo.setText(SystemUtils.getDeviceInfo());

        mAudioAmount = (TextView) v.findViewById(R.id.id_count_audio);
        String amountAudio = String.valueOf(trackList.size()) + "曲";
        mAudioAmount.setText(amountAudio);

        mPlaylist1 = (TextView) v.findViewById(R.id.id_playlist_ipod_1);
        mPlaylist2 = (TextView) v.findViewById(R.id.id_playlist_ipod_2);
        mPlaylist1.setPaintFlags(mPlaylist1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist2.setPaintFlags(mPlaylist2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist1.setOnClickListener(this);
        mPlaylist2.setOnClickListener(this);
        return v;
    }

    private void resetColorItem() {

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void gotoInternetAudio() {
        Fragment fr = new InternetRadioFragment();
        // Internet Audio
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_playlist_ipod_1:
                //gotoInternetAudio();
                //((TestFragment)getActivity()).addItemsToSpinner();
                ((TestFragment)getActivity()).selectFrag(3);
                ((TestFragment)getActivity()).setSelection(3);
                break;
            case R.id.id_playlist_ipod_2:
                //gotoInternetAudio();
                //((TestFragment)getActivity()).addItemsToSpinner();
                ((TestFragment)getActivity()).selectFrag(3);
                ((TestFragment)getActivity()).setSelection(3);
                break;
        }
    }
}
