package gws.grottworkshop.gwsholmeswatson.cache;



import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

// TODO: Auto-generated Javadoc
/**
 * Derived fromAOSP copy of LruCache.
 * 
 * Priro to Android 3.0 bitmaps were stored on boht native heap and Dalvik VM heap with
 * the largest amount of data on the Dalvik VM. Thus the use case to use softreference for
 * both android 2.x and android past 2.x is about making sure not only that bitmaps do not stay
 * around in Dalvik VM longer than they should but also to make sure that smal part of data
 * in the native heap is not staying around in long time periods.
 * 
 * 
 * 
 * Reference Background:
 * 
 * Weak reference is not strong enough to rmeain inmemory and gets removed on next
 * GC cycle. Its enqueued when VM knows it a weak reference.
 * 
 * Soft reference sticks aroud more than one GC cycle. Its enqueued when Vmreaches low memory.
 * 
 * So for performance to span both Android 2.x and Android 3.x devices in terms of UI
 * speed, etc we should be using a two level cache with both a SoftLruCache and a SoftDiskLruCache.
 * 
 * Because we are using a soft reference on bitmaps that other data in native heap
 * will eventually be de-referenced(no not the right word but hey) at one of the
 * GC cycles and thus than native heapstuff will be able to free it up at that point.
 * Still run an ondetachedfromWindow() method handler to handle or hint to
 * GC to do that though.
 * 
 * Note:
 * 
 * This still does not handel the other performance hit ie inflating full xmls instead of say using
 * custom view factories. Yes I amsure FB is not using this trick in the their home app:).
 * 
 * And obiovusly the other perfromacne trick is to in the ImageView:
 * <code>
 *
 * @param <K> the key type
 * @param <V> the value type
 * @Override
 * protected void onDetachedFromWindow () {
 * setImageDrawable(null);
 * setBackgroundDrawable(null);
 * System.gc();
 * }
 * </code>
 * @author fredgrott
 */
public class LruSoftCache<K, V> {
	
	/** The map. */
	private final LinkedHashMap<K, SoftReference<V>> map;
	
	/** The gwslog. */
	private Logger GWSLOG = LoggerFactory.getLogger(LruSoftCache.class);

	/** Size of this cache in units. Not necessarily the number of elements. */
	private int size;
	
	/** The max size. */
	private int maxSize;

	/** The put count. */
	private int putCount;
	
	/** The create count. */
	private int createCount;
	
	/** The eviction count. */
	private int evictionCount;
	
	/** The hit count. */
	private int hitCount;
	
	/** The miss count. */
	private int missCount;

	/**
	 * Instantiates a new lru soft cache.
	 *
	 * @param maxSize the max size
	 */
	public LruSoftCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<K, SoftReference<V>>(0, 0.75f, true);
	}
	
	/**
	 * Gets the.
	 *
	 * @param context the context
	 * @param key the key
	 * @return the v
	 */
	public final V get(Context context, K key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		SoftReference<V> mapValue;
		V mapReferent = null;
		synchronized (this) {
			mapValue = map.get(key);
			if (mapValue != null) {
				mapReferent = mapValue.get();
			}
			if (mapReferent != null) {
				hitCount++;
				return mapReferent;
			}
			if (mapValue != null) {
				size -= safeSizeOf(key, null);
			}
			map.remove(key);
			missCount++;
		}

		/*
		 * Attempt to create a value. This may take a long time, and the map may
		 * be different when create() returns. If a conflicting value was added
		 * to the map while create() was working, we leave that value in the map
		 * and release the created value.
		 */

		V createdReferent = create(context, key);
		if (createdReferent == null) {
			return null;
		}

		synchronized (this) {
			createCount++;
			mapValue = map.put(key, new SoftReference<V>(createdReferent));
			mapReferent = mapValue.get();

			if (mapValue != null && mapReferent != null) {
				// There was a conflict so undo that last put
				map.put(key, mapValue);
			} else {
				size += safeSizeOf(key, createdReferent);
			}
		}

		if (mapValue != null && mapReferent != null) {
			entryRemoved(false, key, createdReferent, mapReferent);
			return mapReferent;
		} else {
			trimToSize(maxSize);
			return createdReferent;
		}
	}

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param referent the referent
	 * @return the v
	 */
	public final V put(K key, V referent) {
		if (key == null || referent == null) {
			throw new NullPointerException("key == null || value == null");
		}
		SoftReference<V> value = new SoftReference<V>(referent);

		SoftReference<V> previousValue;
		V previousReferent = null;
		synchronized (this) {
			putCount++;
			size += safeSizeOf(key, referent);
			previousValue = map.put(key, value);
			if (previousValue != null) {
				previousReferent = previousValue.get();
				size -= safeSizeOf(key, previousReferent);
			}
		}

		if (previousValue != null) {
			entryRemoved(false, key, previousReferent, referent);
		}

		trimToSize(maxSize);

		return previousReferent;
	}

	/**
	 * Trim to size.
	 *
	 * @param maxSize the max size
	 */
	private void trimToSize(int maxSize) {
		while (true) {
			K key;
			SoftReference<V> value;
			V referent;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0)) {
					size = 0;
					map.clear();
					GWSLOG.error( ".sizeOf() is reporting inconsistent results! size: "
									+ size + ", maxSize: " + maxSize);
					break;
				}

				if (size <= maxSize || map.isEmpty()) {
					break;
				}

				Map.Entry<K, SoftReference<V>> toEvict = map.entrySet()
						.iterator().next();
				key = toEvict.getKey();
				value = toEvict.getValue();
				referent = (value != null) ? value.get() : null;
				map.remove(key);
				size -= safeSizeOf(key, referent);
				evictionCount++;
			}

			entryRemoved(true, key, referent, null);
		}
	}

	/**
	 * Removes the entry for {@code key} if it exists.
	 *
	 * @param key the key
	 * @return the previous value mapped by {@code key}.
	 */
	public final V remove(K key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		SoftReference<V> previousValue;
		V previousReferent = null;
		synchronized (this) {
			previousValue = map.remove(key);
			if (previousValue != null) {
				previousReferent = previousValue.get();
				size -= safeSizeOf(key, previousReferent);
			}
		}

		if (previousValue != null) {
			entryRemoved(false, key, previousReferent, null);
		}

		return previousReferent;
	}

	/**
	 * Entry removed.
	 *
	 * @param evicted the evicted
	 * @param key the key
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
	}

	/**
	 * Creates the.
	 *
	 * @param context the context
	 * @param key the key
	 * @return the v
	 */
	protected V create(Context context, K key) {
		return null;
	}

	/**
	 * Safe size of.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the int
	 */
	private int safeSizeOf(K key, V value) {
		int result = sizeOf(key, value);
		if (result < 0) {
			throw new IllegalStateException("Negative size: " + key + "="
					+ value);
		}
		return result;
	}
	
	/**
	 * Size of.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the int
	 */
	private final int sizeOf(K key, V value) {
		return 1;
	}

	/**
	 * Evict all.
	 */
	public final void evictAll() {
		trimToSize(-1); // -1 will evict 0-sized elements
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public synchronized final int size() {
		return size;
	}
	
	/**
	 * Max size.
	 *
	 * @return the int
	 */
	public synchronized final int maxSize() {
		return maxSize;
	}
	
	/**
	 * Hit count.
	 *
	 * @return the int
	 */
	public synchronized final int hitCount() {
		return hitCount;
	}
	
	/**
	 * Miss count.
	 *
	 * @return the int
	 */
	public synchronized final int missCount() {
		return missCount;
	}
	
	/**
	 * Creates the count.
	 *
	 * @return the int
	 */
	public synchronized final int createCount() {
		return createCount;
	}
	
	/**
	 * Put count.
	 *
	 * @return the int
	 */
	public synchronized final int putCount() {
		return putCount;
	}
	
	/**
	 * Eviction count.
	 *
	 * @return the int
	 */
	public synchronized final int evictionCount() {
		return evictionCount;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized final String toString() {
		int accesses = hitCount + missCount;
		int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
		return String
				.format("LruCache[size=%d,mapSize=%d,maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",
						size, map.size(), maxSize, hitCount, missCount,
						hitPercent);
	}
}
