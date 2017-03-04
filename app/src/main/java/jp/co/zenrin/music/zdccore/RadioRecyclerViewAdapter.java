package jp.co.zenrin.music.zdccore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;
import jp.co.zenrin.music.player.MusicPlayActivity;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.service.MediaPlayerService;

/**
 * @Author: Hoang Vu
 * @Date: 2017/03/04
 */

public class RadioRecyclerViewAdapter extends RecyclerView.Adapter<RadioRecyclerViewAdapter.RadioViewHolder>
        implements View.OnClickListener, View.OnAttachStateChangeListener{


    protected final Logger log = new Logger(MusicPlayActivity.class.getSimpleName(), true);
    private ArrayList<Track> mRadioList;
    private Context mContext;
    private SparseBooleanArray sparseBooleanArray;
    private int previousPos = -1;

    private int lastPosition = -1;
    // Service
    private MediaPlayerService player;
    private Intent playIntent;

    // Binding
    private boolean serviceBound = false;
    //Check is service is active
    HondaSharePreference storage;

    public RadioRecyclerViewAdapter(Context context, ArrayList<Track> radioList) {
        this.mContext = context;
        this.mRadioList = radioList;
        sparseBooleanArray = new SparseBooleanArray();
        storage = new HondaSharePreference(context);
    }

    @Override
    public RadioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_card_radio_row,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT
               , RecyclerView.LayoutParams.WRAP_CONTENT));
        RadioViewHolder radioViewHolder = new RadioViewHolder(view);

        return radioViewHolder;
    }

    @Override
    public void onBindViewHolder(RadioViewHolder holder, int position) {
        Track r = mRadioList.get(position);
        Log.d("onBindViewHolder", String.valueOf(position));
        holder.textView.setText(r.getTitle());
        holder.textView.setTag(position);
        holder.textView.setOnClickListener(this);
        holder.textView.setTextColor(ContextCompat.getColor(mContext,R.color.holo_green_dark));
        holder.textView.addOnAttachStateChangeListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT - 20, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.textView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return (null != mRadioList ? mRadioList.size() : 0);
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        if (previousPos != -1 && previousPos != pos) {
            notifyItemChanged(previousPos);
        }
        Log.d("RadioRecycler", String.valueOf(pos));
        if (sparseBooleanArray.get(pos, false)) {
            sparseBooleanArray.delete(pos);
            v.setSelected(false);
        }else {
            sparseBooleanArray.put(pos,true);
            v.setSelected(true);
        }
        switch (v.getId()) {
            case R.id.audio_title:
                TextView audioTitle = (TextView) v.findViewById(R.id.audio_title);
                audioTitle.setTextColor(ContextCompat.getColor(mContext,R.color.holo_orange_dark));
                play(pos);
                break;
        }
        previousPos = pos;
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        v.getTag();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        v.getTag();
    }

    class RadioViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected TextView arrow;

        public RadioViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.audio_title);
            this.arrow = (TextView) view.findViewById(R.id.arrow);
        }

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
            storage.storeTrackList(mRadioList);
            storage.storeTrackIndex(trackID);

            Intent playerIntent = new Intent(mContext,MediaPlayerService.class);
            mContext.startService(playerIntent);
            mContext.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            //HondaSharePreference storage = new HondaSharePreference(getApplicationContext());
            storage.storeTrackIndex(trackID);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(HondaConstants.BROADCAST_PLAY_NEW_TRACK);
            mContext.sendBroadcast(broadcastIntent);
        }

    }
}
