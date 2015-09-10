package jatx.imageloader.lib;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import jatx.imageloader.lib.IBitmapCache.BitmapNotCachedException;
import jatx.imageloader.lib.ILoadQueue.NextNotReadyException;
import jatx.imageloader.lib.ImageEntry.ImageViewNotAliveException;
import jatx.imageloader.lib.NetworkingBitmapDownloader.CannotDownloadBitmapException;

public class ImageLoadWorker extends Thread {
	@Override
	public void run() {
		try {
			while (true) {
				try {
					ImageEntry imgEntry = ImageLoader.getQueue().getNextEntry();
				
					Bitmap bmp = null;
					try {
						bmp = ImageLoader.getCache().getBmp(imgEntry.getUrl());
					} catch (BitmapNotCachedException e) {
						e.printStackTrace();
						try {
							bmp = NetworkingBitmapDownloader.downloadBmp(imgEntry.getUrl());
						} catch (CannotDownloadBitmapException e1) {
							e1.printStackTrace();
						}
					}
					
					if (bmp==null) {
						Log.i("bmp", "null");
						continue;
					}
					
					ImageLoader.getCache().putBmp(imgEntry.getUrl(), bmp);
					
					final int W = bmp.getWidth();
					final int H = bmp.getHeight();
					
					Log.i("bmp size", imgEntry.getUrl() + ": " + Integer.toString(W) + "x"+ Integer.toString(H));
					
					try {
						final ImageView imgView = imgEntry.getImgView();
						final Bitmap bmpFinal = bmp;
						
						imgView.post(new Runnable() {
							@Override
							public void run() {
								ViewGroup.LayoutParams lp = imgView.getLayoutParams();
								lp.width = ImageLoader.getScreenWidth();
								lp.height = (ImageLoader.getScreenWidth() * H) / W;
								imgView.setLayoutParams(lp);
								
								imgView.setImageBitmap(bmpFinal);
								imgView.setVisibility(View.VISIBLE);
							}
						});
					} catch (ImageViewNotAliveException e) {
						e.printStackTrace();
					}
				} catch (NextNotReadyException e) {
					//e.printStackTrace();
				}
				
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			
		}
	}
}
