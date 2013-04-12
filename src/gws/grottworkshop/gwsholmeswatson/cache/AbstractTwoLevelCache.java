package gws.grottworkshop.gwsholmeswatson.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

import android.support.v4.util.LruCache;

import org.apache.commons.io.IOUtils;

/**
 * AbstracTwoLevelCache using LruCache  and DiskLruCache. 
 * 
 * LruCache parameters tocreate are:
 * final int maxSize
 * 
 * DIskLruCache paramateres to create are:
 * 
 * File directory, int appVersion, int valueCount, long maxSize
 * 
 * 
 * 
 * @author fredgrott
 *
 * 
 */
public  class AbstractTwoLevelCache<V> {
	
	private final LruCache<V> mMemCache;
	private final DiskLruCache mDiskCache;
	private final Converter<V> mConverter;
	
	
   

	
	
	public static interface Converter<T> {
        /** Converts bytes to an object. */
        T from(byte[] bytes) throws IOException;

        /** Converts o to bytes written to the specified stream. */
        void toStream(T o, OutputStream bytes) throws IOException;
    }

	
}
