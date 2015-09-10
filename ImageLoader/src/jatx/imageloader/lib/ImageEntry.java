package jatx.imageloader.lib;

import java.lang.ref.WeakReference;

import android.widget.ImageView;

public class ImageEntry {
	private String mUrl;
	private WeakReference<ImageView> mViewRef;
	
	public ImageEntry(String url, ImageView imgView) {
		mUrl = url;
		mViewRef = new WeakReference<ImageView>(imgView);
	}

	public String getUrl() {
		return mUrl;
	}
	
	public ImageView getImgView() throws ImageViewNotAliveException {
		ImageView imgView = mViewRef.get();
		if (imgView==null) throw new ImageViewNotAliveException();
		return imgView;
	}
	
	public static class ImageViewNotAliveException extends Exception {
		private static final long serialVersionUID = 4232686429184431532L;
	}
}
