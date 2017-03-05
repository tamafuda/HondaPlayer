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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.util.CheckSystemPermissions;
import jp.co.zenrin.music.util.TrackUtil;
import jp.co.zenrin.music.zdccore.RadioAdapter;
import jp.co.zenrin.music.zdccore.Track;

import static jp.co.zenrin.music.common.HondaConstants.PERMISSION_REQUEST_CODE;

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

    boolean isPermission;
    RadioAdapter trackAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_amfm, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        // Get song list from device
        //trackList = TrackUtil.getTrackList(getActivity());
        if(isPermission){
            trackList = TrackUtil.getTrackList(getActivity());
        }else {
            trackList = new ArrayList<Track>();
        }

        trackAdapter = new RadioAdapter(getActivity(), R.layout.radio, trackList);
        //trackAdapter.notifyDataSetChanged();
        trackListView.setAdapter(trackAdapter);
        mPlaylist1 = (TextView) v.findViewById(R.id.id_playlist_fmam_1);
        mPlaylist2 = (TextView) v.findViewById(R.id.id_playlist_fmam_2);
        mPlaylist1.setPaintFlags(mPlaylist1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist2.setPaintFlags(mPlaylist2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //ComponentUtils.setUnderlineTextView(mPlaylist1);


        return v;
    }

    @Override
    public void onAttach(Context context) {
        if (!CheckSystemPermissions.checkPermission(context, HondaConstants.READ_EXTERNAL_STORAGE)) {
            //CheckSystemPermissions.requestPermissionActivity(getActivity(),context,HondaConstants.READ_EXTERNAL_STORAGE);
            requestPermissionFragment(getActivity(),context,HondaConstants.READ_EXTERNAL_STORAGE);
            isPermission = false;
        }else{
            isPermission = true;
        }

        super.onAttach(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissionFragment(Activity activity, Context context, String permissionName){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)){

            Toast.makeText(context,"Android 6.0 needs permission allows us to access media data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {
            requestPermissions(new String[]{permissionName}, PERMISSION_REQUEST_CODE);
            //Fragment.requestPermissions(activity,new String[]{permissionName},PERMISSION_REQUEST_CODE);
        }
    }

    private void checkPermision() {

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
}
