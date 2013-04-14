package gws.grottworkshop.gwsholmeswatson.cache;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageCache extends TwoLevelLruCache<String, byte[]> {

	public ImageCache(
			File directory,
			int appVersion,
			int maxSizeMem,
			long maxSizeDisk,
			gws.grottworkshop.gwsholmeswatson.cache.TwoLevelLruCache.Converter<byte[]> converter)
			throws IOException {
		super(directory, appVersion, maxSizeMem, maxSizeDisk, converter);
		// TODO Auto-generated constructor stub
	}

	public synchronized Bitmap getBitmap(String elementKey) {
        byte[] imageData = super.get(elementKey);
        if (imageData == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }
	
}
