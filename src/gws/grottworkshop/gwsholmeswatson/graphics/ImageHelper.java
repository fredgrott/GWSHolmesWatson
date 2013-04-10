package gws.grottworkshop.gwsholmeswatson.graphics;

import gws.grottworkshop.gwsholmeswatson.GWSApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.SyncStateContract.Constants;

public class ImageHelper {

	static float DENSITY;
	
	private static Logger GWSLOG = LoggerFactory.getLogger(ImageHelper.class);

	public static int dipToPixel(final Context ctx, final int dips) {
		if (DENSITY == 0)
			DENSITY = ctx.getResources().getDisplayMetrics().density;
		return (int) (DENSITY * dips + 0.5f);
	}

	public static int pixelToDip(final Context ctx, final int pixels) {
		if (DENSITY == 0)
			DENSITY = ctx.getResources().getDisplayMetrics().density;
		return (int) ( pixels / DENSITY - 0.5f);
	}
	
	//decodes image and scales it to reduce memory consumption
		public static Bitmap decodeFile(final File f, final int requiredSize) {
			try {
				//Decode image size
				final BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(new FileInputStream(f),null,o);

				//Find the correct scale value. It should be the power of 2.
				int width_tmp=o.outWidth, height_tmp=o.outHeight;
				int scale=1;
				while(true){
					if(width_tmp/2<requiredSize || height_tmp/2<requiredSize)
						break;
					width_tmp/=2;
					height_tmp/=2;
					scale*=2;
				}

				//Decode with inSampleSize
				final BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize=scale;
				return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			} catch (final FileNotFoundException e) {
				GWSLOG.error( "Error FileNotfound");
			}
			return null;
		}

		//decodes image and scales it to reduce memory consumption
		public static Bitmap decodeResource(final Resources res, final int id, final int requiredWidth, final int requiredHeight) {
			try {
				//Decode image size
				final BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(res, id, o);

				//Find the correct scale value. It should be the power of 2.
				int width_tmp=o.outWidth, height_tmp=o.outHeight;
				int scale=1;
				while(true){
					if(width_tmp/2<requiredWidth || height_tmp/2<requiredHeight)
						break;
					width_tmp/=2;
					height_tmp/=2;
					scale*=2;
				}

				//Decode with inSampleSize
				final BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize=scale;

				
					o2.inScaled = false;

					//o2.inPurgeable = true;
				
				o2.outWidth = requiredWidth;
				o2.outHeight = requiredHeight;
				o2.inDither = true;
				//o2.inPreferQualityOverSpeed = true;
				final Bitmap bit = BitmapFactory.decodeResource(res, id, o2);

				final Bitmap scaled = Bitmap.createScaledBitmap(bit, requiredWidth, requiredHeight, false);

				bit.recycle();

				return scaled;
			} catch (final Exception e) {
				GWSLOG.error( "Error with resource ");
			}
			return null;
		}
}
