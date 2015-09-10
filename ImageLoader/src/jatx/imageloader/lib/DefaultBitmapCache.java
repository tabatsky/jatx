package jatx.imageloader.lib;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.util.Log;

public class DefaultBitmapCache implements IBitmapCache {
	private volatile ArrayBlockingQueue<String> mUrlQueue;
	private volatile ConcurrentHashMap<String,Bitmap> mCacheMap;
	private volatile long mCacheByteSizeCounter;
	private volatile int mCachedBmpCounter;
	private long mByteSizeLimit;
	private int mCachedBmpCountLimit;
	
	public DefaultBitmapCache(long byteSizeLimit, int cachedBmpCountLimit) {
		mByteSizeLimit = byteSizeLimit;
		mCachedBmpCountLimit = cachedBmpCountLimit;
		mCacheByteSizeCounter = 0;
		mCachedBmpCounter = 0;
		
		mUrlQueue = new ArrayBlockingQueue<String>(cachedBmpCountLimit);
		mCacheMap = new ConcurrentHashMap<String,Bitmap>();
	}
	
	public void clearBitmapCache() {
		for (Bitmap bmp: mCacheMap.values()) {
			bmp.recycle();
		}
		
		mCacheMap.clear();
		mUrlQueue.clear();
	}
	
	public void putBmp(String url, Bitmap bmp) {
		mCacheByteSizeCounter += bmp.getByteCount();
		mCachedBmpCounter += 1;
		
		while (mCachedBmpCounter>=mCachedBmpCountLimit || mCacheByteSizeCounter>=mByteSizeLimit) {
			String urlFromQueue = mUrlQueue.poll();
			if (urlFromQueue==null) break;
			
			Bitmap bmpFromCache = mCacheMap.remove(urlFromQueue);
			if (bmpFromCache==null) break;
			
			mCacheByteSizeCounter -= bmpFromCache.getByteCount();
			mCachedBmpCounter -= 1;
			
			bmpFromCache.recycle();
			
			Log.i("remove from cache", urlFromQueue);
		}
		
		try {
			mUrlQueue.put(url);
			mCacheMap.put(url, bmp);
			
			Log.i("put to cache", url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Bitmap getBmp(String url) throws IBitmapCache.BitmapNotCachedException {
		Bitmap bmp = mCacheMap.get(url);
		if (bmp==null || bmp.isRecycled()) throw new IBitmapCache.BitmapNotCachedException();
		return bmp;
	}
}
