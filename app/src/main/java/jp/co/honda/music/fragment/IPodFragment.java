package jp.co.honda.music.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.honda.music.adapter.RadioAdapter;
import jp.co.honda.music.adapter.RadioRecyclerViewAdapter;
import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.player.HomeBaseFragment;
import jp.co.honda.music.player.R;
import jp.co.honda.music.util.BitmapUtils;
import jp.co.honda.music.zdccore.AdapterInterface;
import jp.co.honda.music.zdccore.HondaSharePreference;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/25
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class IPodFragment extends Fragment implements View.OnClickListener,AdapterInterface{

    // Logger
    protected final Logger log = new Logger(IPodFragment.class.getSimpleName(), true);
    private ArrayList<Media> mediaList;
    private ListView trackListView;
    /*private TextView mDeviceInfo;
    private TextView mAudioAmount;*/
    private RecyclerView mRecyclerView;
    RadioRecyclerViewAdapter mRadioRecyclerAdapter;

    TextView mPlaylist1;
    TextView mPlaylist2;

    ImageView mArtAlbum;

    private HondaSharePreference storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        log.d("onCreate");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ipod, container, false);
        //View v = inflater.inflate(R.layout.fragment_ipod_recycleview, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        //mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Get song list from device
        //mediaList = TrackUtil.getTrackList(getActivity());
        storage = new HondaSharePreference(getActivity());
        mediaList = storage.loadTrackList();
        if(mediaList == null) {
            mediaList = new ArrayList<Media>();
        }
        //mediaList = TrackUtil.getTrackList(getActivity());
        RadioAdapter trackAdapter = new RadioAdapter(getActivity(), getActivity(),R.layout.radio
                , mediaList, HondaConstants.DETECT_FRAGMENT_IPOD,trackListView,this);
        //mRadioRecyclerAdapter = new RadioRecyclerViewAdapter(getActivity(), mediaList);

        trackListView.setAdapter(trackAdapter);
        //mRecyclerView.setAdapter(mRadioRecyclerAdapter);
        //mRadioRecyclerAdapter.
/*
        mDeviceInfo = (TextView) v.findViewById(R.id.id_device_info);
        mDeviceInfo.setText(SystemUtils.getDeviceInfo());

        mAudioAmount = (TextView) v.findViewById(R.id.id_count_audio);
        String amountAudio = String.valueOf(mediaList.size()) + "曲";
        mAudioAmount.setText(amountAudio);*/
        mArtAlbum = (ImageView) v.findViewById(R.id.id_art_album_ipod);
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
                //((HomeBaseFragment)getActivity()).addItemsToSpinner();
                ((HomeBaseFragment)getActivity()).selectFrag(3);
                ((HomeBaseFragment)getActivity()).setSelection(3);
                break;
            case R.id.id_playlist_ipod_2:
                //gotoInternetAudio();
                //((HomeBaseFragment)getActivity()).addItemsToSpinner();
                ((HomeBaseFragment)getActivity()).selectFrag(3);
                ((HomeBaseFragment)getActivity()).setSelection(3);
                break;
        }
    }

    @Override
    public void updateArtAlbum(int pos) {
        Media m = mediaList.get(pos);
        ((HomeBaseFragment)getActivity()).playMusicInList(pos);
        if(m.getAlbum() != null && !m.getAlbum().isEmpty()){
            mPlaylist1.setText(m.getAlbum());
        }else{
            mPlaylist1.setText("Unknown");
        }
        if(m.getArtist() != null && !m.getArtist().isEmpty()){
            mPlaylist2.setText(m.getArtist());
        }else{
            mPlaylist2.setText("Unknown");
        }
        mArtAlbum.setImageBitmap(BitmapUtils.decodeBitmapFromFile(getActivity(),m.getAlbumArtUri()));
        //mArtAlbum.setImageBitmap(BitmapUtils.decodeBitmapHonda(getActivity(),m.getAlbumArtUri()));
    }

    @Override
    public void keepSrv(boolean isKeep) {

    }
}
