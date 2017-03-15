package jp.co.honda.music.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.honda.music.common.HondaConstants;
import jp.co.honda.music.dialog.PopupUtils;
import jp.co.honda.music.logger.Logger;
import jp.co.honda.music.model.Media;
import jp.co.honda.music.player.HomeBaseFragment;
import jp.co.honda.music.player.R;
import jp.co.honda.music.service.MediaPlayerService;
import jp.co.honda.music.util.TrackUtil;
import jp.co.honda.music.zdccore.AdapterInterface;
import jp.co.honda.music.zdccore.HondaSharePreference;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/26
 */

public class RadioAdapter extends ArrayAdapter<Media> implements View.OnClickListener {

    // Logger
    protected final Logger log = new Logger(RadioAdapter.class.getSimpleName(), true);

    private ArrayList<Media> mediaList;
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
    private int savePreviousPos = -1;

    // Detect fragment
    private String detectFragment;
    private AdapterInterface mAdapterInterface;

    public RadioAdapter(Context context, Activity activity, int resource, ArrayList<Media> mMediaList, String fragment, ListView listView, AdapterInterface adapterInterface) {
        super(context, resource);
        this.mediaList = mMediaList;
        this.layoutResourceId = resource;
        this.context = context;
        storage = new HondaSharePreference(context);
        popupUtils = new PopupUtils(context);
        this.mActivity = activity;
        this.detectFragment = fragment;
        this.mListView = listView;
        this.mAdapterInterface = adapterInterface;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        log.d("Touch on item : " + position);
        log.d("Child count is : " + String.valueOf(mListView.getChildCount()));

        Media media = mediaList.get(position);
        media.setSelect(true);
        switch (v.getId()) {
            case R.id.radio_title:
            case R.id.radio_duration:
                //Toast.makeText(context, "Title click: " + String.valueOf(position), Toast.LENGTH_SHORT).show();
                TextView txtRadio = (TextView) v.findViewById(R.id.radio_title);
                txtRadio.setTextColor(ContextCompat.getColor(context, R.color.colorYellow));
                //setNotifyOnChange(true);
                log.d("Previous pos is : " +String.valueOf(previousPos));
                log.d("Current pos is : " +String.valueOf(position));
                if (previousPos != -1 && previousPos != position) {
                    //mListView.getChildAt(previousPos - mListView.getFirstVisiblePosition())

                    if (mListView.getChildAt(previousPos - mListView.getFirstVisiblePosition()) != null) {
                        TextView txt = (TextView) mListView.getChildAt(previousPos - mListView.getFirstVisiblePosition()).findViewById(R.id.radio_title);
                        txt.setTextColor(ContextCompat.getColor(context, R.color.color_text_white));
                    }
                    ((Media)mediaList.get(previousPos)).setSelect(false);
                    ((Media)mediaList.get(position)).setSelect(true);
                    previousPos = position;
                }
                log.d("After previous pos is : " +String.valueOf(previousPos));
                mAdapterInterface.updateArtAlbum(position);
                break;
            case R.id.radio_arrow:

                // Internet Audio
                /*Fragment fr = new InternetRadioFragment();
                FragmentManager fm = mActivity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fr);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                if (!detectFragment.equals(HondaConstants.DETECT_FRAGMENT_NETRADIO)) {
                    //Toast.makeText(context, "Arrow click : " + String.valueOf(position), Toast.LENGTH_SHORT).show();
                    ((HomeBaseFragment) mActivity).selectFrag(3);
                    ((HomeBaseFragment) mActivity).setSelection(3);
                }

                break;
        }
        previousPos = position;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Media media = mediaList.get(position);
        ViewHolder viewHolder;

        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutResourceId, parent, false);
            viewHolder.trackTitle = (TextView) convertView.findViewById(R.id.radio_title);
            viewHolder.duration = (TextView) convertView.findViewById(R.id.radio_duration);
            viewHolder.arrow = (TextView) convertView.findViewById(R.id.radio_arrow);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.id_album_ie);
            result = convertView;
            convertView.setTag(viewHolder);
            log.d("Position new load is : " + String.valueOf(position));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
            log.d("Position loaded is : " + String.valueOf(position));
        }

        String mTitle = String.valueOf(position + 1) + "    " + media.getTitle();
        if(detectFragment.equals(HondaConstants.DETECT_FRAGMENT_NETRADIO)) {
            viewHolder.trackTitle.setText(media.getTitle());
            viewHolder.image.setVisibility(View.VISIBLE);
            if(position%3 == 0){
                viewHolder.image.setImageResource(R.drawable.hikaru1);
            }else if(position%3 == 1){
                viewHolder.image.setImageResource(R.drawable.img_cover);
            }else if (position%3 == 2) {
                viewHolder.image.setImageResource(R.drawable.radio);
            }
        }else{
            viewHolder.trackTitle.setText(mTitle);
            viewHolder.image.setVisibility(View.GONE);
        }
        String duration = TrackUtil.covertDuration(media.getDuration());
        //viewHolder.trackTitle.setText(media.getTitle());
        viewHolder.trackTitle.setOnClickListener(this);
        viewHolder.trackTitle.setTag(position);
//        if (position == savePreviousPos) {
//            viewHolder.trackTitle.setTextColor(ContextCompat.getColor(context, R.color.holo_green_dark));
//            savePreviousPos = -1;
//        }

        if (media.isSelect()){
            log.d("Display color of text or not : YES ");
            viewHolder.trackTitle.setTextColor(ContextCompat.getColor(context, R.color.colorYellow));
        }else {
            log.d("Display color of text or not : NO ");
            viewHolder.trackTitle.setTextColor(ContextCompat.getColor(context, R.color.color_text_white));
        }
        if(!detectFragment.equals(HondaConstants.DETECT_FRAGMENT_FMAM)) {
            viewHolder.duration.setText(duration);
            viewHolder.duration.setTag(position);
        }
        viewHolder.arrow.setText(">");
        viewHolder.arrow.setTag(position);
        viewHolder.arrow.setOnClickListener(this);
        //log.d("Position loaded is : " + String.valueOf(position));
        return convertView;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

//======= View Holder ======

    // View lookup cache
    private static class ViewHolder {
        TextView trackTitle;
        TextView duration;
        TextView arrow;
        ImageView image;
    }


}
