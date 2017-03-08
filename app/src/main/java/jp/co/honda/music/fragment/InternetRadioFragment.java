package jp.co.honda.music.fragment;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.player.HomeBaseFragment;
import jp.co.honda.music.player.R;
import jp.co.honda.music.util.TrackUtil;
import jp.co.honda.music.zdccore.AdapterInterface;
import jp.co.honda.music.zdccore.HondaSharePreference;
import jp.co.honda.music.adapter.RadioAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class InternetRadioFragment extends Fragment implements AdapterInterface{

    private ArrayList<Media> mediaList;
    private ListView trackListView;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;

    private ImageView artSinger;

    private TextView radioDomain;
    private HondaSharePreference storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_internet_radio, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        // Get song list from device
        mediaList = TrackUtil.getTrackList(getActivity());
        storage = new HondaSharePreference(getActivity());
        //mediaList = storage.loadTrackList();
        //mediaList = TrackUtil.getTrackList(getActivity());
        RadioAdapter trackAdapter = new RadioAdapter(getActivity(),getActivity(), R.layout.radio
                , mediaList, HondaConstants.DETECT_FRAGMENT_NETRADIO,trackListView,this);
        trackListView.setAdapter(trackAdapter);

        artSinger = (ImageView) v.findViewById(R.id.bgr_singer);
        image1 = (ImageView) v.findViewById(R.id.horizontal_image1);
        image2 = (ImageView) v.findViewById(R.id.horizontal_image2);
        image3 = (ImageView) v.findViewById(R.id.horizontal_image3);
        image4 = (ImageView) v.findViewById(R.id.horizontal_image4);
        image1.setOnClickListener(mOnclick);
        image2.setOnClickListener(mOnclick);
        image3.setOnClickListener(mOnclick);
        image4.setOnClickListener(mOnclick);

        radioDomain = (TextView) v.findViewById(R.id.id_radio_domain);
        radioDomain.setPaintFlags(radioDomain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        return v;
    }

    private View.OnClickListener mOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            int resoureId = 0;
            switch (id) {
                case R.id.horizontal_image1:
                    resoureId = R.drawable.hikaru1;
                    break;
                case R.id.horizontal_image2:
                    resoureId = R.drawable.hikaru2;
                    break;
                case R.id.horizontal_image3:
                    resoureId = R.drawable.nhuquynh3;
                    break;
                case R.id.horizontal_image4:
                    resoureId = R.drawable.ikimonogakiri;
                    break;
                // even more buttons here
            }
            //artSinger.setBackgroundResource(resoureId);
            artSinger.setImageDrawable(getActivity().getDrawable(resoureId));
        }
    };


    @Override
    public void updateArtAlbum(int pos) {
        ((HomeBaseFragment)getActivity()).playMusicInList(pos);
    }

    @Override
    public void keepSrv(boolean isKeep) {

    }
}
