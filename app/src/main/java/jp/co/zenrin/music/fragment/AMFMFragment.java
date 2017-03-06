package jp.co.zenrin.music.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.model.ChanelRadio;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.player.TestFragment;
import jp.co.zenrin.music.util.CheckSystemPermissions;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.Logger;
import jp.co.zenrin.music.zdccore.RadioAdapter;
import jp.co.zenrin.music.zdccore.Track;

import static jp.co.zenrin.music.common.HondaConstants.PERMISSION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class AMFMFragment extends Fragment implements View.OnClickListener {
    protected final Logger log = new Logger(AMFMFragment.class.getSimpleName(), true);
    private ArrayList<Track> trackList;
    private ListView trackListView;

    TextView mPlaylist1;
    TextView mPlaylist2;

    TextView mRF;
    TextView mChanel;

    Button mBtnChanelUp;
    Button mBtnChanelDown;

    boolean isPermission;
    RadioAdapter trackAdapter;

    ArrayList<ChanelRadio> listChanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        log.d("onCreate");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_amfm, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        // Get song list from device
        //trackList = TrackUtil.getTrackList(getActivity());
        if (isPermission) {
            trackList = TrackUtil.getTrackList(getActivity());
        } else {
            trackList = new ArrayList<Track>();
        }

        trackAdapter = new RadioAdapter(getActivity(), getActivity(), R.layout.radio
                , trackList, HondaConstants.DETECT_FRAGMENT_FMAM, trackListView);
        //trackAdapter.notifyDataSetChanged();
        trackListView.setAdapter(trackAdapter);
        mPlaylist1 = (TextView) v.findViewById(R.id.id_playlist_fmam_1);
        mPlaylist2 = (TextView) v.findViewById(R.id.id_playlist_fmam_2);
        mPlaylist1.setOnClickListener(this);
        mPlaylist2.setOnClickListener(this);
        mPlaylist1.setPaintFlags(mPlaylist1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist2.setPaintFlags(mPlaylist2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //ComponentUtils.setUnderlineTextView(mPlaylist1);

        // Implement chanel
        mRF = (TextView) v.findViewById(R.id.id_radio_way);
        mChanel = (TextView) v.findViewById(R.id.id_chanel_name);
        mBtnChanelUp = (Button) v.findViewById(R.id.chanel_up);
        mBtnChanelDown = (Button) v.findViewById(R.id.chanel_down);
        mBtnChanelDown.setOnClickListener(this);
        mBtnChanelUp.setOnClickListener(this);
        listChanel = initView();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        if (!CheckSystemPermissions.checkPermission(context, HondaConstants.READ_EXTERNAL_STORAGE)) {
            //CheckSystemPermissions.requestPermissionActivity(getActivity(),context,HondaConstants.READ_EXTERNAL_STORAGE);
            requestPermissionFragment(getActivity(), context, HondaConstants.READ_EXTERNAL_STORAGE);
            isPermission = false;
        } else {
            isPermission = true;
        }

        super.onAttach(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionFragment(Activity activity, Context context, String permissionName) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)) {

            Toast.makeText(context, "Android 6.0 needs permission allows us to access media data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {
            requestPermissions(new String[]{permissionName}, PERMISSION_REQUEST_CODE);
            //Fragment.requestPermissions(activity,new String[]{permissionName},PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_playlist_fmam_1:
                //gotoInternetAudio();
                //((TestFragment)getActivity()).addItemsToSpinner();
                ((TestFragment) getActivity()).selectFrag(3);
                ((TestFragment) getActivity()).setSelection(3);
                break;
            case R.id.id_playlist_fmam_2:
                //gotoInternetAudio();
                //((TestFragment)getActivity()).addItemsToSpinner();
                ((TestFragment) getActivity()).selectFrag(3);
                ((TestFragment) getActivity()).setSelection(3);
                break;
            case R.id.chanel_up:
                setChanel(1);
                break;

            case R.id.chanel_down:
                setChanel(-1);
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Snackbar.make(mView,"Permission Granted, Now you can access location data.", Snackbar.LENGTH_LONG).show();
                    isPermission = true;
                    ArrayList<Track> list = TrackUtil.getTrackList(getActivity());
                    trackList.clear();
                    trackList.addAll(list);
                    //trackAdapter = new RadioAdapter(getActivity(),R.layout.radio,trackList);
                    trackAdapter.notifyDataSetChanged();


                } else {
                    isPermission = false;
                    //Snackbar.make(mView,"Permission Denied, You cannot access location data.",Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    private ArrayList<ChanelRadio> initView() {
        ArrayList<ChanelRadio> listChanel = new ArrayList<ChanelRadio>();
        ChanelRadio cr;
        cr = new ChanelRadio("80.0 MHz", "TOKYO FM", true);
        listChanel.add(cr);
        cr = new ChanelRadio("100.15 MHz", "CHIBA FM", false);
        listChanel.add(cr);
        cr = new ChanelRadio("90.0 MHz", "YOKOHAMA FM", false);
        listChanel.add(cr);
        cr = new ChanelRadio("70.05 MHz", "CHOFU FM", false);
        listChanel.add(cr);
        cr = new ChanelRadio("95.27 MHz", "CHIYODA FM", false);
        listChanel.add(cr);
        cr = new ChanelRadio("100.0 MHz", "TAMA FM", false);
        listChanel.add(cr);
        return listChanel;
    }

    /**
     * isUpDown +1 when click button Up
     * isUpDown -1 when click button Down
     *
     * @param isUpDown
     */
    private void setChanel(int isUpDown) {
        String RFText = "";
        String chanelName = "";
        for (int i = 0; i < listChanel.size(); i++) {
            try {
                if (listChanel.get(i).isPlay()) {
                    listChanel.get(i).setPlay(false);
                    RFText = listChanel.get(i + isUpDown).getIdChanel();
                    chanelName = listChanel.get(i + isUpDown).getNameChanel();
                    listChanel.get(i + isUpDown).setPlay(true);
                    break;
                }
            } catch (IndexOutOfBoundsException e) {
                if(isUpDown > 0) {
                    RFText = listChanel.get(0).getIdChanel();
                    chanelName = listChanel.get(0).getNameChanel();
                    listChanel.get(0).setPlay(true);
                }else {
                    RFText = listChanel.get(listChanel.size() -1 ).getIdChanel();
                    chanelName = listChanel.get(listChanel.size() -1).getNameChanel();
                    listChanel.get(listChanel.size() -1 ).setPlay(true);
                }

            }

        }

        mRF.setText(RFText);
        mChanel.setText(chanelName);

    }
}
