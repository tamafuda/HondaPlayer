package jp.co.honda.music.adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.PopupUtils;
import jp.co.honda.music.player.R;
import jp.co.honda.music.player.HomeBaseFragment;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.zdccore.HondaSharePreference;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Track;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public class RadioAdapter extends ArrayAdapter<Track> implements  View.OnClickListener {

    // Logger
    protected final Logger log = new Logger(RadioAdapter.class.getSimpleName(), true);

    private ArrayList<Track> trackList;
    private Context context;
    private int layoutResourceId;

    private int lastPosition = -1;
    // Service
    private MediaPlayerService player;
    private Intent playIntent;

    // Binding
    private boolean serviceBound = false;
    //Check is service is active
    HondaSharePreference storage;
    PopupUtils popupUtils;
    private int indexPlayBefore;
    Activity mActivity;
    private ListView mListView;
    private int previousPos = -1;

    // Detect fragment
    private String detectFragment;

    public RadioAdapter(Context context, Activity activity, int resource, ArrayList<Track> mTrackList, String fragment, ListView listView) {
        super(context, resource);
        this.trackList = mTrackList;
        this.layoutResourceId = resource;
        this.context = context;
        storage = new HondaSharePreference(context);
        popupUtils = new PopupUtils(context);
        this.mActivity = activity;
        this.detectFragment = fragment;
        this.mListView = listView;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        log.d("Child count is : " + String.valueOf(mListView.getChildCount()));

        Track track = trackList.get(position);
        switch (v.getId()) {
            case R.id.audio_title:
                Toast.makeText(context,"Title click: " + String.valueOf(position), Toast.LENGTH_SHORT).show();
                TextView txtRadio = (TextView) v.findViewById(R.id.audio_title);
                txtRadio.setTextColor(ContextCompat.getColor(context,R.color.colorYellow));
                //setNotifyOnChange(true);
                play(position);
                if (previousPos != -1 && previousPos != position) {
                    if (mListView.getChildAt(previousPos) != null) {
                        TextView txt = (TextView) mListView.getChildAt(previousPos).findViewById(R.id.audio_title);
                        log.d("Tag of textview is : " + String.valueOf(txt.getTag()));
                        txt.setTextColor(ContextCompat.getColor(context,R.color.holo_green_dark));
                    }
                    trackList.get(previousPos).setSelect(false);
                }
                track.setSelect(true);
                break;
            case R.id.arrow:

                // Internet Audio
                /*Fragment fr = new InternetRadioFragment();
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fr);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                if (!detectFragment.equals(HondaConstants.DETECT_FRAGMENT_NETRADIO)) {
                    Toast.makeText(context,"Arrow click : " + String.valueOf(position), Toast.LENGTH_SHORT).show();
                    ((HomeBaseFragment)mActivity).selectFrag(3);
                    ((HomeBaseFragment)mActivity).setSelection(3);
                }

                break;
        }
        previousPos = position;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = trackList.get(position);
        ViewHolder viewHolder;

        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutResourceId, parent, false);
            viewHolder.trackTitle = (TextView) convertView.findViewById(R.id.audio_title);
            viewHolder.arrow = (TextView) convertView.findViewById(R.id.arrow);
            result = convertView;
            convertView.setTag(viewHolder);
            log.d("Position new load is : " + String.valueOf(position));
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
            log.d("Position loaded is : " + String.valueOf(position));
        }

        viewHolder.trackTitle.setText(track.getTitle());
        viewHolder.trackTitle.setOnClickListener(this);
        viewHolder.trackTitle.setTag(position);
        if (!track.isSelect()) {
            viewHolder.trackTitle.setTextColor(ContextCompat.getColor(context,R.color.holo_green_dark));
        }

        viewHolder.arrow.setTag(position);
        viewHolder.arrow.setOnClickListener(this);
        //log.d("Position loaded is : " + String.valueOf(position));
        return convertView;

    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            log.d("ServiceBound is true");
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            player = binder.getService();
            serviceBound = true;
            storage.storeMPLServiceStatus(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            log.d("ServiceBound is false");
            serviceBound = false;
            storage.storeMPLServiceStatus(false);
        }
    };

    private void play(int trackID) {

        //boolean serviceBound = storage.loadMPLServiceStatus();
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            //HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackList(trackList);
            storage.storeTrackIndex(trackID);

            Intent playerIntent = new Intent(context,MediaPlayerService.class);
            context.startService(playerIntent);
            context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            //HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackIndex(trackID);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEW_TRACK);
            context.sendBroadcast(broadcastIntent);
        }

    }

    @Override
    public int getCount() {
        return trackList.size();
    }

//======= View Holder ======

    // View lookup cache
    private static class ViewHolder {
        TextView trackTitle;
        TextView arrow;
    }


}
