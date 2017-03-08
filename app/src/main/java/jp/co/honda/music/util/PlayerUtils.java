package jp.co.honda.music.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import jp.co.honda.music.model.Media;
import jp.co.honda.music.zdccore.HondaSharePreference;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class PlayerUtils
{

	public static String getTimeHoursMinutesSecondsString(long elapsedTime)
	{
		String time;
	    String format = String.format("%%0%dd", 2);
	    elapsedTime /= 1000;
	    String seconds = String.format(format, elapsedTime % 60);
	    String minutes = String.format(format, (elapsedTime % 3600) / 60);
	    long hoursInt = elapsedTime / 3600;
	    if( hoursInt > 0 )
	    {
	    	String hours = String.format(format, hoursInt );
	    	time =  hours + ":" + minutes + ":" + seconds;
	    }
	    else
	    {
	    	time =  minutes + ":" + seconds;
	    }

	    return time;
	}
	public static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = sampleSize;

		AssetFileDescriptor fileDescriptor = null;
		try {
			fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
				fileDescriptor.getFileDescriptor(), null, options);

		Log.d(TAG, options.inSampleSize + " sample method bitmap ... "
				+ actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

		return actuallyUsableBitmap;
	}

	private static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight
					&& (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromUri(Context context, Uri imageUri, int reqWidth, int reqHeight) throws FileNotFoundException {

		// Get input stream of the image
		final BitmapFactory.Options options = new BitmapFactory.Options();
		InputStream iStream = context.getContentResolver().openInputStream(imageUri);

		// First decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(iStream, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(iStream, null, options);
	}

	public static void addOneMediaArrange(Context context, String titleChange) {
		HondaSharePreference storage = new HondaSharePreference(context);
		// Case 1 : Send track index param from PlayMusic screen
		int index = 0;
        /*Bundle extras = getIntent().getExtras();
        int index = 0;
        if (extras != null) {
            index = extras.getInt(HondaConstants.INTENT_AIMIXAUDIO);
        }*/
		// Case 2: Get current track from Preference . It's also OK
		index = storage.loadTrackIndex();
		ArrayList<Media> list = storage.loadTrackList();
		Media m;
		if (list != null && list.size() > 0) {
			m = new Media(list.get(index).getID()
					,list.get(index).getData()
					,titleChange
					,list.get(index).getArtist()
					,list.get(index).getAlbum()
					,list.get(index).getDuration()
					,list.get(index).getAlbumArtUri());
			list.add(0,m);
			storage.storeTrackList(list);
		}
	}
}
