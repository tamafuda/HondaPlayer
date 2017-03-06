package jp.co.zenrin.music.fragment;

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

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.HondaSharePreference;
import jp.co.zenrin.music.zdccore.RadioAdapter;
import jp.co.zenrin.music.zdccore.Track;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class InternetRadioFragment extends Fragment {

    private ArrayList<Track> trackList;
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
        //trackList = TrackUtil.getTrackList(getActivity());
        storage = new HondaSharePreference(getActivity());
        trackList = storage.loadTrackList();
        RadioAdapter trackAdapter = new RadioAdapter(getActivity(),getActivity(), R.layout.radio
                , trackList, HondaConstants.DETECT_FRAGMENT_NETRADIO,trackListView);
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


}
