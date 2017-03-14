package jp.co.honda.music.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.co.honda.music.model.SpinnerNavItem;
import jp.co.honda.music.player.R;
import jp.co.honda.music.zdccore.HondaSharePreference;

public class TitleNavigationAdapter extends BaseAdapter {

	private ImageView imgIcon;
	private TextView txtTitle;
	private ArrayList<SpinnerNavItem> spinnerNavItem;
	private Context context;
    private HondaSharePreference storage;

	public TitleNavigationAdapter(Context context,
								  ArrayList<SpinnerNavItem> spinnerNavItem) {
		this.spinnerNavItem = spinnerNavItem;
		this.context = context;
        storage = new HondaSharePreference(context);
	}

	@Override
	public int getCount() {
		return spinnerNavItem.size();
	}

	@Override
	public Object getItem(int index) {
		return spinnerNavItem.get(index);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        	LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_title_navigation, null);
        }
        
        imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

        imgIcon.setImageResource(R.drawable.ic_menu_bar);
        //imgIcon.setVisibility(View.GONE);
        txtTitle.setText(spinnerNavItem.get(position).getTitle());
        txtTitle.setTextColor(ContextCompat.getColor(context,R.color.color_text_white));
        convertView.setBackgroundResource(android.R.color.transparent);
        return convertView;
	}
	

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
        	LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.list_item_title_navigation, null);
        }
        
        imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
        imgIcon.setVisibility(View.GONE);
        //imgIcon.setImageResource(spinnerNavItem.get(position).getIcon());
        if(storage.loadSpinnerItemSelected() == position) {
            txtTitle.setTextColor(ContextCompat.getColor(context,R.color.colorYellow));
        }else{
            txtTitle.setTextColor(ContextCompat.getColor(context,R.color.color_text_white));
        }
        txtTitle.setText(spinnerNavItem.get(position).getTitle());

        //RelativeLayout rl = (RelativeLayout) txtTitle.getParent();
        //rl.setBackgroundColor(ContextCompat.getColor(context, R.color.holo_blue_bright));
        //convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        return convertView;
	}


}
