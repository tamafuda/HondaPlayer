package jp.co.honda.music.util;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.honda.music.logger.Logger;

public class Utils
{
	public static final Logger log = new Logger(Utils.class.getSimpleName(), true);

	public static final String DATA = "_uri";
	public static final String ACTION = "_action";
	public static final String NO_ALBUMS = "_no_albums";
	public static final String TYPE = "type";
	public static final String BACK_TITLE = "back_title";
	public static final String UNKNOWN_ALBUM = "unknown_album";
	public static final String RUN_FROM_SYNC_OPTIONS = "run_from_sync_options";

	public static final String SEARCH = "search";
	public static final String NEWS = "news";
	public static final String NEWS_URL = "news_url";

	public static final String QUERY = SearchManager.QUERY;

	public static final int API_9_GB = 9;
	public static final int API_10_GM_MR1 = 10;
	public static final int API_11_HC = 11;
	public static final int API_16_JB = 16;
	public static final int API_17_JB = 17;

	public static boolean replacePopAnimation = false;

	public static Boolean sIsOtherAppInstalled = null;

	public static final String START_FROM_BROWSER_KEY = "start_from_browser";
	public static final String START_FROM_OPENING_NOTICATION = "start_from_opening_notification";
	public static final String START_FROM_OPENING_NEWS_NOTICATION = "start_from_opening_news_notification";
	public static final String START_FROM_OPENING_KNOCK_SETTING = "start_from_opening_knock_setting";
	public static final String START_KNOCK_SETTING_FRAGMENT = "start_knock_setting_fragment";
	public static final String ACTION_CHECK_TO_OPEN_KNOCK_SETTING = "jp.co.mytrax.traxcore.player.Utils.ACTION_CHECK_OPEN_KNOCK_SETTING";

	public static final String HINT_LONG_CLICK_KEY = "hint_long_click_key";
	public static final String HINT_CONTEXT_MENU_KEY = "hint_context_menu_key";
	public static final String HINT_FLICK_KEY = "hint_flick_key";
	public static final String HINT_MENU_SETTING_KEY = "hint_menu_setting_key";
	public static final String MDJ_PREFERENCE = "mdjHomeScreen";

	public static final String CHATHEAD_SETTING_KEY = "chathead_setting_key";

	private static final String REDISPLAY_HINTS_KEY = "redisplay_hint_key";

	public static final String HIRES_SONG_EQ_SETTING_KEY = "hires_song_eq_key";

	public static final String NON_DISPLAYED_SETTING_KEY = "non_displayed_setting";

	public static final String NOTIFY_ALL_KEY = "notify_all";

	public static final String VIEW_NON_DISPLAYED_TRACK_LIST_KEY = "view_non_displayed_track_list";

	public static final String SEARCH_MDJ = "search_mdj";

	private static boolean isHomeResumeFromChild = false;
	private static boolean isSettingResumeFromChild = false;

	/**
	 * The requestCode with which the storage access framework is triggered for input folder.
	 */
	public static final int DELETION_REQUEST_CODE_STORAGE_ACCESS_INPUT = 44;
	public static final int DELETION_MEDIA = 1;
	public static final int DELETION_ALBUM = 2;
	public static final int DELETION_ARTIST = 3;
	public static final int DELETION_VIDEO = 4;
	public static final int DELETION_SEARCH = 5;
	public static final int DELETION_ALBUM_MEDIA = 6;
	public static final int DELETION_MDJ_MEDIA = 7;
	public static final int DELETION_MDJ_BUNDLE = 8;
	public static final int DELETION_ARTIST_MEDIA = 9;
	public static final int DELETION_MDJ_MEDIA_SEARCH = 10;

	public static final String REQUEST_CODE_STORAGE_ACCESS_INPUT_STRING = "REQUEST_CODE_STORAGE_ACCESS_INPUT_STRING";
	private static int mDeletionScreen = 0;
	private static int[] mDeletionPos ;

	private static boolean checkIfOtherAppInstalled(Context context)
	{
		List<String> packages = new ArrayList<String>();
		packages.add("jp.co.mytrax.traxcore");

		int pos = packages.indexOf(Utils.class.getPackage().getName());
		if (pos != -1)
		{
			packages.remove(pos);
		}

		for (String p : packages)
		{
			try
			{
				context.getPackageManager().getApplicationInfo(p, 0);
				log.d("Another instance of application found: " + p);
				return true;
			}
			catch (NameNotFoundException e)
			{
				continue;
			}
		}

		log.d("Another instance of application not found");
		return false;
	}

	public static boolean isOtherAppInstalled(Context context)
	{
		if (sIsOtherAppInstalled == null)
		{
			sIsOtherAppInstalled = checkIfOtherAppInstalled(context);
		}

		return sIsOtherAppInstalled;
	}

	public static int safeLongToInt(long l)
	{
		int i = (int) l;
		if (i != l)
		{
			throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
		}
		return i;
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
	 */
	public static Bundle intentToFragmentArguments(Intent intent)
	{
		Bundle arguments = new Bundle();
		if (intent == null)
		{
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null)
		{
			arguments.putParcelable(DATA, data);
		}

		final String action = intent.getAction();
		if (action != null)
		{
			arguments.putString(ACTION, action);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null)
		{
			arguments.putAll(extras);
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments)
	{
		Intent intent = new Intent();
		if (arguments == null)
		{
			return intent;
		}

		final Uri data = arguments.getParcelable(DATA);
		if (data != null)
		{
			intent.setData(data);
		}

		final String action = arguments.getString(ACTION);
		if (action != null)
		{
			intent.setAction(action);
		}

		intent.putExtras(arguments);
		intent.removeExtra(DATA);
		intent.removeExtra(ACTION);
		return intent;
	}

	public static long[] longListToLongArray(List<Long> list)
	{
		long[] array = new long[list.size()];

		int i = 0;
		for (long id : list)
		{
			array[i++] = id;
		}

		return array;
	}

	public static int[] integerListToIntArray(List<Integer> list)
	{
		int[] array = new int[list.size()];

		int i = 0;
		for (int id : list)
		{
			array[i++] = id;
		}

		return array;
	}

	public static <T> T[] concat(T[] first, T[] second)
	{
		final T[] result = resizeArray(first, first.length + second.length);

		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public static <T> T[] resizeArray(T[] array, int newSize)
	{
		if (Utils.isApiLevelAtLeast(API_9_GB))
		{
			return Arrays.copyOf(array, newSize);
		}
		else
		{
			@SuppressWarnings("unchecked")
			T[] result = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), newSize);
			System.arraycopy(array, 0, result, 0, array.length);
			return result;
		}
	}

	public static byte[] resizeByteArray(byte[] array, int newSize)
	{
		if (Utils.isApiLevelAtLeast(API_9_GB))
		{
			return Arrays.copyOf(array, newSize);
		}
		else
		{
			byte[] result = (byte[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), newSize);
			System.arraycopy(array, 0, result, 0, array.length);
			return result;
		}
	}

	public static boolean isCheckedRealPosition(SparseBooleanArray sparseArray, int position)
	{
		return sparseArray.get(position);
	}


	public static int[] getTruesFromSparseBooleanArray(SparseBooleanArray sparseArray, int positionDiff)
	{
		List<Integer> checkedItems = new ArrayList<Integer>();

		for (int i = 0; i < sparseArray.size(); i++)
		{
//			log.d( "Item " + (sparseArray.keyAt(i) + positionDiff) + ":" + (sparseArray.valueAt(i) ? "true" : "false"));
			if (sparseArray.valueAt(i))
			{
				checkedItems.add(sparseArray.keyAt(i) + positionDiff);
			}
		}

		return integerListToIntArray(checkedItems);
	}

	public static boolean isPortraitOrientation(Context context)
	{
		boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		log.d("isPortraitOrientation: " + isPortrait);
		return isPortrait;
	}

	public static void fullscreenOn(Activity activity)
	{
		// Remove title bar
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public static boolean isVideoActivity(Context activity)
	{
		// TODO Auto-generated method stub
		// return true;
		return !isPortraitOrientation(activity) && activity.getClass().getSimpleName().equals("NowVideoPlayingActivity");
	}

	public static boolean isLandscapePlaying(Context activity)
	{
		return !isPortraitOrientation(activity) && activity.getClass().getSimpleName().equals("NowPlayingActivity");
	}


	public static boolean canBeTransactionNonExclusive()
	{
		return isApiLevelGreaterThanGingerbread();
	}

	/**
	 * Api level greather or equal 11
	 * 
	 * @return
	 */
	public static boolean isApiLevelGreaterThanGingerbread()
	{
		int sdkVersion = Build.VERSION.SDK_INT;
		return sdkVersion >= 11;
	}

	/**
	 * Api level greather or equal specified level
	 * 
	 * @return
	 */
	public static boolean isApiLevelAtLeast(int apiLevel)
	{
		int sdkVersion = Build.VERSION.SDK_INT;
		return sdkVersion >= apiLevel;
	}

	public static Set<String> keySet(ContentValues values)
	{
		Set<String> keys = new HashSet<String>();
		Set<Entry<String, Object>> s = values.valueSet();

		if (values != null)
		{
			Iterator<Entry<String, Object>> itr = s.iterator();
			while (itr.hasNext())
			{
				Entry<String, Object> me = itr.next();
				keys.add(me.getKey());
			}
		}

		return keys;
	}

	public static String getDeviceName()
	{
		String brand = Build.BRAND.substring(0, 1).toUpperCase() + Build.BRAND.substring(1);

		return brand + " " + Build.MODEL;
	}

	public static Uri[] parcelableToUriArray(Parcelable[] parcelables)
	{
		if (parcelables == null)
		{
			return null;
		}

		Uri[] uris = new Uri[parcelables.length];
		int i = 0;
		for (Parcelable p : parcelables)
		{
			uris[i++] = (Uri) p;
		}

		return uris;
	}

	/**
	 * //*\/*
	 * 
	 * @return
	 */
	public static boolean isGeneralMimeType(String type)
	{
		return "*/*".equals(type);
	}

	public static String getExtensionFromUri(Uri uri)
	{
		if (!uri.getScheme().equals("file"))
		{
			return null;
		}

		return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
	}

	public static String getMimeTypeFromExtension(String extension)
	{
		return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}

	public static String getMimeTypeFromUri(Uri uri)
	{
		String extension = getExtensionFromUri(uri);
		if (extension != null)
		{
			return getMimeTypeFromExtension(extension);
		}
		return null;
	}

	public static boolean isMimeType(Uri uri, String mimeType, String type)
	{
		if (mimeType == null || uri == null)
		{
			log.d("Uri or mimeType is null");
			return false;
		}

		boolean isGeneral = Utils.isGeneralMimeType(mimeType);

		if (isGeneral)
		{// mime type is */*
			mimeType = getMimeTypeFromUri(uri);
		}

		return mimeType != null && mimeType.startsWith(type);

	}

	public static boolean isMimeTypeAudio(Uri uri, String mimeType)
	{
		return isMimeType(uri, mimeType, "audio");
	}

	public static boolean isMimeTypeVideo(Uri uri, String mimeType)
	{
		return isMimeType(uri, mimeType, "video");
	}

	public static boolean isDebuggable(Context ctx)
	{
		PackageManager pm = ctx.getPackageManager();
		try
		{
			return ((pm.getApplicationInfo(ctx.getPackageName(), 0).flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0);
		}
		catch (NameNotFoundException e)
		{
			return false;
		}
	}

	public static boolean compare(Object o1, Object o2)
	{
		if (o1 != null)
		{
			return o1.equals(o2);
		}
		else if (o2 == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static long[] mergeArrayWithList(long[] mediaIds, List<Long> playlistMediaIds)
	{
		int size = mediaIds.length + playlistMediaIds.size();
		long[] mergedArray = new long[size];
		for (int i = 0; i < size; i++)
		{
			if (i < mediaIds.length)
			{
				mergedArray[i] = mediaIds[i];
			}
			else
			{
				mergedArray[i] = playlistMediaIds.get(i - mediaIds.length);
			}
		}
		return mergedArray;
	}

	public static String toString(String value)
	{

		return (value != null) ? value : "";
	}

	public static int[] getCheckedPositions(SparseBooleanArray checkedItemPositions, int i)
	{
		if (checkedItemPositions != null)
		{
			return Utils.getTruesFromSparseBooleanArray(checkedItemPositions, 0);
		}
		return new int[0];
	}

	/**
	 * check if the hint is shown or not
	 * 
	 * @param key
	 *            : type of hint
	 * @return
	 */
	public static boolean isHintShown(Context context, String key)
	{
		final SharedPreferences prefs = context.getSharedPreferences(MDJ_PREFERENCE, 0);
		return prefs.getBoolean(key, false);
	}

	/**
	 * mark the hint is shown
	 * 
	 * @param key
	 *            : type of hint
	 * @return
	 */
	public static void markHintShown(Context context, String key)
	{
		final SharedPreferences prefs = context.getSharedPreferences(MDJ_PREFERENCE, 0);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(key, true);
		edit.commit();
	}

	/**
	 * check chathead setting is on or off
	 */
	public static boolean isChatheadSettingOn(Context context)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean(Utils.CHATHEAD_SETTING_KEY, true);
	}

	/**
	 * check knock setting is on or off
	 */
	public static boolean isOnKnockSetting(Context context)
	{
		if (context == null)
		{
			return false;
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getBoolean("kinesics_setting_" + "enabled", false);
	}

	/**
	 * Set resume home when on back press now playing
	 */
	public static void setHomeResumeOnBackPress(boolean isSet)
	{
		isHomeResumeFromChild = isSet;
	}

	/**
	 * check resume home when on back press now playing
	 */
	public static boolean isHomeResumeOnBackPress()
	{
		return isHomeResumeFromChild;
	}

	/**
	 * Set resume setting screen is on or off
	 */
	public static void setSettingResumeOnBackPress(boolean isSet)
	{
		isSettingResumeFromChild = isSet;
	}

	/**
	 * check resume setting screen is on or off
	 */
	public static boolean isSettingResumeOnBackPress()
	{
		return isSettingResumeFromChild;
	}

	/**
	 * check redisplay hint is set or not
	 */
	public static boolean isRedisplayHints(Context context)
	{
		final SharedPreferences prefs = context.getSharedPreferences(MDJ_PREFERENCE, 0);
		return prefs.getBoolean(REDISPLAY_HINTS_KEY, false);
	}

	/**
	 * reset all hint keys to show them again
	 */
	public static void resetAllHintKeys(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences(MDJ_PREFERENCE, 0);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(REDISPLAY_HINTS_KEY, true);
		edit.putBoolean(HINT_CONTEXT_MENU_KEY, false);
		edit.putBoolean(HINT_LONG_CLICK_KEY, false);
		edit.putBoolean(HINT_FLICK_KEY, false);
		edit.putBoolean(HINT_MENU_SETTING_KEY, false);
		edit.commit();
	}

	/**
	 * reset all hint keys to show them again
	 */
	public static void resetHintKey(Context context, String key)
	{
		SharedPreferences prefs = context.getSharedPreferences(MDJ_PREFERENCE, 0);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(key, false);
		edit.commit();
	}

	public static boolean isLargeScreen(Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE ||
				(context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}


	public static String getAppFirstInstallTime(Context context)
	{
		PackageInfo packageInfo = null;
		try
		{
			packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (packageInfo != null)
			{
				Date date = new Date(packageInfo.firstInstallTime);
				SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.JAPAN);
				String dateString = format.format(date);
				return dateString;
			}
		}
		catch (NameNotFoundException e)
		{
		}
		return "";
	}

	/**
	 * The time at which the app was last updated. Units are as per currentTimeMillis().
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppLastUpdateTime(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			if (packageInfo != null)
			{
				Date date = new Date(packageInfo.lastUpdateTime);
				SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.JAPAN);
				String dateString = format.format(date);
				return dateString;
			}

		}
		catch (NameNotFoundException e)
		{

		}
		return "";
	}

	public static void enableDisableView(View view, boolean enabled)
	{
		if (view != null)
		{
			view.setEnabled(enabled);
			if (view != null && view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup) view;

				for (int idx = 0; idx < group.getChildCount(); idx++)
				{
					enableDisableView(group.getChildAt(idx), enabled);
				}
			}
		}
	}

	/**
	 * Get information if Android version is Lollipop (5.0) or higher.
	 *
	 * @return true if Lollipop or higher.
	 */
	public static boolean isAndroid5()
	{
		return isApiLevelAtLeast(Build.VERSION_CODES.LOLLIPOP);
	}

	/**
	 * Get information if Android version is KitKat.
	 *
	 * @return true if KitKat or not.
	 */
	public static boolean isAndroidKitKat()
	{
		int sdkVersion = Build.VERSION.SDK_INT;
		return sdkVersion == Build.VERSION_CODES.KITKAT || sdkVersion == Build.VERSION_CODES.KITKAT_WATCH;
	}

	/**
	 * Get information if Android version is ice cream sandwich (4.0) or older.
	 *
	 * @return true if Lollipop or higher.
	 */
	public static boolean isAndroid_4_0()
	{
		return !isApiLevelAtLeast(Build.VERSION_CODES.JELLY_BEAN);
	}



	/**
	 * Get screen to show deletion dialog
	 */
	public static int getScreenTypeToDelete() {
		return mDeletionScreen;
	}

	/**
	 * Set screen type
	 */
	public static void setScreenTypeToDelete(int screen) {
		mDeletionScreen = screen;
	}

	/**
	 * Get positions
	 */
	public static int[] getPositionsToDelete() {
		return mDeletionPos;
	}

	/**
	 * Set positions
	 */
	public static void setPositionsToDelete(int[] pos) {
		mDeletionPos = pos;
	}

	public static void setOnTouchAnimation(View targetView, final float scaleX,
										   final float scaleY) {
		if (targetView == null) return;
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onTouchAnimate(v, event.getAction(), scaleX, scaleY);
				return false;
			}
		});
	}

	public static void onTouchAnimate(View targetView, int action,
									  float scaleX, float scaleY) {
		if (action == MotionEvent.ACTION_DOWN) {
			scaleAnimation(targetView, scaleX, scaleY, true);
		} else if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			scaleAnimation(targetView, scaleX, scaleY, false);
		}
	}

	public static void scaleAnimation(View targetView, float scaleX, float scaleY, boolean isScale) {
		ScaleAnimation anim;
		if (isScale) {
			anim = new ScaleAnimation(1.0f, scaleX, 1.0f,
					scaleY, targetView.getWidth() / 2,
					targetView.getHeight() / 2);
			anim.setDuration(0);
			anim.setFillEnabled(true);
			anim.setFillAfter(true);
			targetView.startAnimation(anim);
		} else {
			anim = new ScaleAnimation(scaleX, 1.0f, scaleY,
					1.0f, targetView.getWidth() / 2, targetView.getHeight() / 2);
			anim.setDuration(0);
			targetView.startAnimation(anim);
		}
	}

	public static void setOnTouchButtonEffect(View targetView) {
		if (targetView == null) return;
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				buttonEffect(v, event.getAction());
				return false;
			}
		});
	}

	public static void buttonEffect(View v, final int action) {
		if (v instanceof ImageButton) {
			switch (action) {
				case MotionEvent.ACTION_DOWN: {
					((ImageButton) v).getDrawable().setColorFilter(Color.parseColor("#c3c3c3"), PorterDuff.Mode.SRC_ATOP);
					v.invalidate();
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL: {
					((ImageButton) v).getDrawable().clearColorFilter();
					v.invalidate();
					break;
				}
			}
		}

	}
}
