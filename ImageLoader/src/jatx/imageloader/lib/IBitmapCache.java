package jatx.imageloader.lib;

import android.graphics.Bitmap;

public interface IBitmapCache {
	public void putBmp(String url, Bitmap bmp);
	public Bitmap getBmp(String url) throws BitmapNotCachedException;
	public void clearBitmapCache();
	
	public static class BitmapNotCachedException extends Exception {
		private static final long serialVersionUID = 6879606956133407258L;
	}
}
