package jp.co.honda.music.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jp.co.honda.music.player.R;

import static android.graphics.BitmapFactory.decodeResource;

public class BitmapUtils
{

	// public static getDefaultBitmapOptions(){
	// BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inJustDecodeBounds = true;
	// BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
	// int imageHeight = options.outHeight;
	// int imageWidth = options.outWidth;
	// //String imageType = options.outMimeType;
	// }

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth)
		{
			if (width > height)
			{
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}
			else
			{
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
			int reqWidth, int reqHeight)
	{

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return decodeResource(res, resId, options);
	}

	public static Bitmap decodeSampledBitmapFromFile(String path,
			int reqWidth, int reqHeight)
	{

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		return BitmapFactory.decodeFile(path, options);
	}

	public static Bitmap decodeSampledBitmapFromStream(InputStream stream,
			int reqWidth, int reqHeight) throws IOException
	{

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;

		final int CHUNKSIZE = 50 * 1024;

		byte buffer[] = new byte[CHUNKSIZE];
		int len = 0;

		while (true)
		{
			int chunklen = stream.read(buffer, len, CHUNKSIZE);
			if (chunklen <= 0)
				break;
			len += chunklen;
			buffer = Utils.resizeByteArray(buffer, len + CHUNKSIZE);
		}

		ByteArrayInputStream mystream = new ByteArrayInputStream(buffer, 0, len);
		BitmapFactory.decodeStream(mystream, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		mystream = new ByteArrayInputStream(buffer, 0, len);
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		return BitmapFactory.decodeStream(mystream, null, options);
	}

	public static void setImageBitmap(ImageView imageView, String path)
	{
		imageView.setImageBitmap(decodeSampledBitmapFromFile(path, 100, 100));
	}

	/**
	 * Support scale bitmap to expect size and keep the correct ratio as original size.
	 * 
	 * @param src
	 *            original bitmap
	 * @param expectWidth
	 *            the width of new bitmap that want to scale
	 * @param expectHeight
	 *            the height of new bitmap that want to scale.
	 * @param recycle
	 *            true: auto recycle after finish. false: don't recycle
	 * @return new scaled Bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap src, int expectWidth, int expectHeight, boolean recycle)
	{
		if (src == null)
		{
			return null;
		}
		int width = src.getWidth();
		int height = src.getHeight();
		float sx = (float) expectWidth / (float) width;
		float sy = (float) expectHeight / (float) height;
		float dd = Math.min(sx, sy);
		Bitmap newBm = null;
		try
		{
			int dstWidth = (int) (width * dd);
			int dstHeight = (int) (height * dd);
			newBm = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
			if (recycle && src != newBm)
			{
				src.recycle();
				src = null;
			}
		}
		catch (Exception e)
		{
			// can not scale, return original bitmap
			return src;
		}
		catch (OutOfMemoryError e)
		{
			// can not scale, return original bitmap
			return src;
		}
		return newBm;
	}

	public static Bitmap decodeBitmapHonda(Context context, String uri) {
        Bitmap bitmap = null;
        if(uri != null) {
            Uri artAlbumID = Uri.parse(uri);
            try{
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),artAlbumID);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dark_default_album_artwork);
            }catch (IOException e) {
                e.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dark_default_album_artwork);
            }
        }else{
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dark_default_album_artwork);
        }
        return bitmap;
    }
}
