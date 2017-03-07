package jp.co.honda.music.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.PopupUtils;
import jp.co.honda.music.model.ChanelRadio;
import jp.co.honda.music.player.HomeBaseFragment;
import jp.co.honda.music.player.R;
import jp.co.honda.music.zdccore.AdapterInterface;
import jp.co.honda.music.zdccore.CheckSystemPermissions;
import jp.co.honda.music.util.TrackUtil;
import jp.co.honda.music.zdccore.HondaSharePreference;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.adapter.RadioAdapter;
import jp.co.honda.music.model.Media;

import static jp.co.honda.music.common.HondaConstants.PERMISSION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class AMFMFragment extends Fragment implements View.OnClickListener, AdapterInterface {
    protected final Logger log = new Logger(AMFMFragment.class.getSimpleName(), true);
    private ArrayList<Media> mediaList;
    private ListView trackListView;

    private TextView mPlaylist1;
    private TextView mPlaylist2;
    private TextView mRF;
    private TextView mChanel;

    private Button mBtnChanelUp;
    private Button mBtnChanelDown;
    private ImageView albumArt;

    boolean isPermission;
    private RadioAdapter trackAdapter;

    private ArrayList<ChanelRadio> listChanel;
    private HondaSharePreference storage;
    private Handler handler;
    private Dialog mDialog;
    private Thread thread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        log.d("onCreate");
        storage = new HondaSharePreference(getActivity());
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_amfm, container, false);
        trackListView = (ListView) v.findViewById(R.id.song_list);
        // Get song list from device
        //mediaList = TrackUtil.getTrackList(getActivity());
        if (Build.VERSION.SDK_INT < 23) {
            isPermission = true;
        }
        if (isPermission) {
            //TrackUtil.synTrackListDatabase(getActivity());
            //mediaList = storage.loadTrackList();
            mediaList = TrackUtil.getTrackList(getActivity());
        } else {
            mediaList = new ArrayList<Media>();
        }

        trackAdapter = new RadioAdapter(getActivity(), getActivity(), R.layout.radio
                , mediaList, HondaConstants.DETECT_FRAGMENT_FMAM, trackListView, this);
        //trackAdapter.notifyDataSetChanged();
        trackListView.setAdapter(trackAdapter);
        mPlaylist1 = (TextView) v.findViewById(R.id.id_playlist_fmam_1);
        mPlaylist2 = (TextView) v.findViewById(R.id.id_playlist_fmam_2);
        mPlaylist1.setOnClickListener(this);
        mPlaylist2.setOnClickListener(this);
        mPlaylist1.setPaintFlags(mPlaylist1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mPlaylist2.setPaintFlags(mPlaylist2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //ComponentUtils.setUnderlineTextView(mPlaylist1);

        albumArt = (ImageView) v.findViewById(R.id.album_art);

        // Implement chanel
        mRF = (TextView) v.findViewById(R.id.id_radio_way);
        mChanel = (TextView) v.findViewById(R.id.id_chanel_name);
        mBtnChanelUp = (Button) v.findViewById(R.id.chanel_up);
        mBtnChanelDown = (Button) v.findViewById(R.id.chanel_down);
        mBtnChanelDown.setOnClickListener(this);
        mBtnChanelUp.setOnClickListener(this);
        listChanel = initView();
        // Auto show popup notification
        //showAINofity();
        //customHandle();
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
                //((HomeBaseFragment)getActivity()).addItemsToSpinner();
                ((HomeBaseFragment) getActivity()).selectFrag(3);
                ((HomeBaseFragment) getActivity()).setSelection(3);
                break;
            case R.id.id_playlist_fmam_2:
                //gotoInternetAudio();
                //((HomeBaseFragment)getActivity()).addItemsToSpinner();
                ((HomeBaseFragment) getActivity()).selectFrag(3);
                ((HomeBaseFragment) getActivity()).setSelection(3);
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
                    //ArrayList<Media> list = storage.loadTrackList();
                    ArrayList<Media> list = TrackUtil.getTrackList(getActivity());
                    mediaList.clear();
                    mediaList.addAll(list);
                    //trackAdapter = new RadioAdapter(getActivity(),R.layout.radio,mediaList);
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
                if (isUpDown > 0) {
                    RFText = listChanel.get(0).getIdChanel();
                    chanelName = listChanel.get(0).getNameChanel();
                    listChanel.get(0).setPlay(true);
                } else {
                    RFText = listChanel.get(listChanel.size() - 1).getIdChanel();
                    chanelName = listChanel.get(listChanel.size() - 1).getNameChanel();
                    listChanel.get(listChanel.size() - 1).setPlay(true);
                }

            }

        }

        mRF.setText(RFText);
        mChanel.setText(chanelName);

    }

    private void showNotify() {
        PopupUtils popupUtils = new PopupUtils(getActivity());
        mDialog = popupUtils.notifyDialogCustom(getActivity(), R.string.popup_notify_content);
        mDialog.show();
    }

    private void customHandle() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        //Toast.makeText(getActivity(),"cool",Toast.LENGTH_SHORT).show();
                        showNotify();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void showAINofity() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(50000);
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                    thread.interrupt();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onDestroy() {
        log.d("Fragment destroy");
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        if (thread != null && thread.isAlive()) {
            log.d("Interrupt thread");
            thread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public void updateArtAlbum(int pos) {
        Media m = mediaList.get(pos);
        ((HomeBaseFragment)getActivity()).playMusicInList(pos);
//        if (m.getBitmap() != null) {
//            albumArt.setImageBitmap(m.getBitmap());
//        }else{
//            albumArt.setImageResource(R.drawable.dark_default_album_artwork);
//        }
        albumArt.setImageResource(R.drawable.dark_default_album_artwork);
    }
}
