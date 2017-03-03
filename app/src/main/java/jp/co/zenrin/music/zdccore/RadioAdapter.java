package jp.co.zenrin.music.zdccore;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.dialog.PopupUtils;
import jp.co.zenrin.music.player.MusicPlayActivity;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.service.MediaPlayerService;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public class RadioAdapter extends ArrayAdapter<Track> implements  View.OnClickListener {

    // Logger
    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);

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

    public RadioAdapter(Context context, int resource, ArrayList<Track> mTrackList) {
        super(context, resource);
        this.trackList = mTrackList;
        this.layoutResourceId = resource;
        this.context = context;
        storage = new HondaSharePreference(context);
        popupUtils = new PopupUtils(context);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        View groupView = v.getRootView();
        Track track = trackList.get(position);
        switch (v.getId()) {
            case R.id.audio_title:
                Toast.makeText(context,"Title click", Toast.LENGTH_SHORT).show();
                TextView txtRadio = (TextView) v.findViewById(R.id.audio_title);
                txtRadio.setTextColor(ContextCompat.getColor(context,R.color.colorYellow));
                play(position);
                break;
        }

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
            result = convertView;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.trackTitle.setText(track.getTitle());
        viewHolder.trackTitle.setOnClickListener(this);
        viewHolder.trackTitle.setTag(position);

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



    private void resetTextColor() {

    }
//======= View Holder ======

    // View lookup cache
    private static class ViewHolder {
        TextView trackTitle;
    }


}
