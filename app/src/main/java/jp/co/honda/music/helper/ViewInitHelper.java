package jp.co.honda.music.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

import jp.co.honda.music.logger.Logger;


public class ViewInitHelper
{
	public interface OnInitAction<T>
	{
		public void init(T view);
	}

	private final static Logger log = new Logger(ViewInitHelper.class.getSimpleName(), true);

	public static <T extends View> T init(Context context, View parent, int id, OnInitAction<T> initAction)
	{
		T v = (T) parent.findViewById(id);
		if (v != null && initAction != null)
		{
			initAction.init(v);
		}

		if (v == null)
		{
			try
			{
				log.w("View " + context.getResources().getResourceEntryName(id) + " cannot be found in " + context.getResources().getResourceEntryName(parent.getId()));
			}
			catch (Exception e)
			{
				log.e("View not found. Resource with ID " + id + " cannot be found in " + parent.getClass().getSimpleName() + " (" + parent.getId() + ")");
			}
		}

		return v;
	}

	public static <T extends View> T init(Activity parent, int id, OnInitAction<T> initAction)
	{
		T v = (T) parent.findViewById(id);
		if (v != null && initAction != null)
		{
			initAction.init(v);
		}

		if (v == null)
		{
			try
			{
				log.w("View " + parent.getResources().getResourceEntryName(id) + " cannot be found in " + parent.getTitle());
			}
			catch (Exception e)
			{
				log.e("View not found. Resource with ID " + id + " cannot be found in " + parent.getTitle());
			}
		}

		return v;
	}

	public static View initOnClick(Activity parent, int id, View.OnClickListener onClickListener)
	{
		return initOnClicks(parent, id, onClickListener, null);
	}

	public static View initOnClicks(Activity parent, int id, View.OnClickListener onClickListener, OnLongClickListener onLongClickListener)
	{
		View v = parent.findViewById(id);
		if (v != null && onClickListener != null)
		{
			if (onClickListener != null)
			{
				v.setOnClickListener(onClickListener);
			}

			if (onLongClickListener != null)
			{
				v.setOnLongClickListener(onLongClickListener);
			}
		}

		if (v == null)
		{
			try
			{
				log.w("View " + parent.getResources().getResourceEntryName(id) + " cannot be found in " + parent.getTitle());
			}
			catch (Exception e)
			{
				log.e("View not found. Resource with ID " + id + " cannot be found in " + parent.getTitle());
			}
		}

		return v;
	}

	public static View initOnClick(Context context, View parent, int id, View.OnClickListener onClickListener)
	{
		return initOnClicks(context, parent, id, onClickListener, null);
	}

	public static View initOnClick(Context context, View parent, int id, View.OnClickListener onClickListener, boolean isResourceRequired)
	{
		return initOnClicks(context, parent, id, onClickListener, null, isResourceRequired);
	}

	public static View initOnClicks(Context context, View parent, int id, View.OnClickListener onClickListener, OnLongClickListener onLongClickListener)
	{
		return initOnClicks(context, parent, id, onClickListener, onLongClickListener, true);
	}

	public static View initOnClicks(Context context, View parent, int id, View.OnClickListener onClickListener, OnLongClickListener onLongClickListener, boolean isResourceRequierd)
	{
		View v = parent.findViewById(id);
		if (v != null && onClickListener != null)
		{
			if (onClickListener != null)
			{
				v.setOnClickListener(onClickListener);
			}

			if (onLongClickListener != null)
			{
				v.setOnLongClickListener(onLongClickListener);
			}
		}

		if (v == null)
		{
			if (isResourceRequierd)
			{
				try
				{
					log.w("View " + context.getResources().getResourceEntryName(id) + " cannot be found in " + context.getResources().getResourceEntryName(parent.getId()));
				}
				catch (Exception e)
				{
					log.e("View not found. Resource with ID " + id + " cannot be found in " + parent.getClass().getSimpleName() + " (" + parent.getId() + ")");
				}
			}
		}

		return v;
	}

	public static <T extends View> T init(Context context, View parent, int id, Class<T> c)
	{
		T v = (T) parent.findViewById(id);

		if (v == null)
		{
			try
			{
				log.w(c.getSimpleName() + " " + context.getResources().getResourceEntryName(id) + " cannot be found in " + context.getResources().getResourceEntryName(parent.getId()));
			}
			catch (Exception e)
			{
				log.e("View not found. Resource with ID " + id + " cannot be found in " + parent.getClass().getSimpleName() + " (" + parent.getId() + ")");
			}
		}

		return v;
	}

	public static <T extends View> T init(Activity parent, int id, Class<T> c)
	{
		T v = (T) parent.findViewById(id);

		if (v == null)
		{
			try
			{
				log.w(c.getSimpleName() + " " + parent.getResources().getResourceEntryName(id) + " cannot be found in " + parent.getTitle());
			}
			catch (Exception e)
			{
				log.e("View not found. Resource with ID " + id + " cannot be found in " + parent.getTitle());
			}
		}

		return v;
	}

	public static OnLongClickListener getLongClickToastDisplayer(final Context context, final int stringId)
	{
		return new OnLongClickListener()
		{

			@Override
			public boolean onLongClick(View v)
			{
				Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
				return true;
			}
		};
	}

	public static OnLongClickListener getLongClickToastDisplayer(final Context context, final CharSequence text)
	{
		return new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				return true;
			}
		};
	}
}
