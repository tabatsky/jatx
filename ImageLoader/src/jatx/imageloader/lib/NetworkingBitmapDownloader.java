package jatx.imageloader.lib;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class NetworkingBitmapDownloader {
	public static Bitmap downloadBmp(String url) throws CannotDownloadBitmapException {
		URL _url;
		Bitmap bmp;
		try {
			Log.i("try download", url);
			_url = new URL(url);
			bmp = BitmapFactory.decodeStream(_url.openConnection().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new CannotDownloadBitmapException();
		}
		if (bmp==null) {
			throw new CannotDownloadBitmapException();
		} else {
			Log.i("success download", url);
		}
		return bmp;
	}
	
	public static class CannotDownloadBitmapException extends Exception {
		private static final long serialVersionUID = -7497714923814625159L;
	}
}
