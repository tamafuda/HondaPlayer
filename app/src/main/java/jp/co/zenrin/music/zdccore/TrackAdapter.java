package jp.co.zenrin.music.zdccore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.zenrin.music.player.AIMixAudio;
import jp.co.zenrin.music.player.R;
import jp.co.zenrin.music.util.TrackUtil;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public class TrackAdapter extends ArrayAdapter<Track> implements  View.OnClickListener{

    private ArrayList<Track> trackList;
    private Context context;
    private int layoutResourceId;

    private int lastPosition = -1;

    public TrackAdapter(Context context, int resource, ArrayList<Track> mTrackList) {
        super(context, resource);
        this.trackList = mTrackList;
        this.layoutResourceId = resource;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Track track = trackList.get(position);
        switch (v.getId()) {
            case R.id.btn_share:
                Toast.makeText(context,"Share button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_arrange:
                Toast.makeText(context,"Arrange button", Toast.LENGTH_SHORT).show();
                Intent iController = new Intent(context, AIMixAudio.class);
                iController.putExtra("AIMixAudio", true);
                context.startActivity(iController);
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
            viewHolder.trackTitle = (TextView) convertView.findViewById(R.id.song_title);
            viewHolder.trackDuration = (TextView) convertView.findViewById(R.id.song_artist);
            viewHolder.share = (Button) convertView.findViewById(R.id.btn_share);
            viewHolder.arrange = (Button) convertView.findViewById(R.id.btn_arrange);

            result = convertView;
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.trackTitle.setText(track.getTitle());
        viewHolder.trackDuration.setText(TrackUtil.covertDuration(track.getDuration()));
        viewHolder.share.setOnClickListener(this);
        viewHolder.share.setTag(position);
        viewHolder.arrange.setOnClickListener(this);
        viewHolder.arrange.setTag(position);

        return convertView;

    }

    @Override
    public int getCount() {
        return trackList.size();
    }

//======= View Holder ======

    // View lookup cache
    private static class ViewHolder {
        TextView trackTitle;
        TextView trackDuration;
        Button share;
        Button arrange;
    }


}
