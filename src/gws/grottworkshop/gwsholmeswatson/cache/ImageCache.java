package gws.grottworkshop.gwsholmeswatson.cache;

import java.io.File;
import java.io.IOException;

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

	
}
