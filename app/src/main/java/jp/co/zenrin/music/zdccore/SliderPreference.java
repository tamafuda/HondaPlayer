package jp.co.zenrin.music.zdccore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import jp.co.zenrin.music.player.R;

public class SliderPreference extends Preference implements OnSeekBarChangeListener
{
	private SeekBar seekBar;
	//private TextView valueText;
	private int currentValue;
	private int maxValue = 100;
	private int minValue = 0;
	private int interval = 1;
	//private boolean showValue = false;

	public SliderPreference(Context ctx, AttributeSet attrs)
	{
		super(ctx, attrs);

		init(ctx, attrs);
	}

	public SliderPreference(Context ctx, AttributeSet attrs, int defStyle)
	{
		super(ctx, attrs, defStyle);

		init(ctx, attrs);
	}

	private void init(Context ctx, AttributeSet attrs)
	{
		maxValue = attrs.getAttributeIntValue(null, "max", 100);
		minValue = attrs.getAttributeIntValue(null, "min", 0);
		interval = attrs.getAttributeIntValue(null, "interval", 1);
		//showValue = attrs.getAttributeBooleanValue(null, "showValue", false);
	}

	@Override
	protected View onCreateView(ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.viewgroup_play_control, parent, false);
		seekBar = (SeekBar) view.findViewById(R.id.seekBar);
		seekBar.setProgressDrawable(getContext().getDrawable(R.drawable.seekbar_progress));
		seekBar.setMax(maxValue - minValue);
		seekBar.setOnSeekBarChangeListener(this);
//		valueText = (TextView) view.findViewById(R.id.value);
//		if (showValue)
//		{
//			valueText.setVisibility(View.VISIBLE);
//		}
		update();
		return view;
	}

	private void update()
	{
//		if (showValue)
//		{
//			valueText.setText(String.valueOf(currentValue));
//		}
		seekBar.setProgress(currentValue - minValue);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		int val = Math.max(Math.min(progress + minValue, maxValue), minValue);

		if (interval != 1 && val % interval != 0)
		{
			val = Math.round((float) val / interval) * interval;
		}

		if (!callChangeListener(val))
		{
			seekBar.setProgress(currentValue - minValue);
			return;
		}

		currentValue = val;
		persistInt(val);
		update();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		notifyChanged();
	}

	@Override
	protected Object onGetDefaultValue(TypedArray ta, int index)
	{
		return ta.getInt(index, minValue);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{

		if (restoreValue)
		{
			currentValue = getPersistedInt(currentValue);
		}
		else
		{
			int val = (Integer) defaultValue;
			persistInt(val);
			currentValue = val;
		}
	}

	@Override
	protected void onBindView(View view)
	{
		super.onBindView(view);
		TextView summaryView = (TextView) view.findViewById(android.R.id.title);
		summaryView.setMaxLines(10);
		summaryView.setTextColor(Color.BLACK);
	}
}
