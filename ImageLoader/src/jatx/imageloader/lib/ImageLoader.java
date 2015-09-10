package jatx.imageloader.lib;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;

public class ImageLoader {
	private static volatile ILoadQueue sQueue;
	private static volatile IBitmapCache sCache;
	
	private static int sScreenWidth;
	
	private static List<ImageLoadWorker> sWorkerList;
	
	public static void init(int threadsCount, int screenWidth, ILoadQueue queue, IBitmapCache cache) {
		sQueue = queue;
		sCache = cache;
		
		sScreenWidth = screenWidth;
		
		sWorkerList = new ArrayList<ImageLoadWorker>();
		
		for (int i=0; i<threadsCount; i++) {
			ImageLoadWorker worker = new ImageLoadWorker();
			sWorkerList.add(worker);
			worker.start();
		}
	}
	
	public static void destroy() {
		sCache.clearBitmapCache();
		sQueue.clearQueue();
		for (ImageLoadWorker worker: sWorkerList) {
			worker.interrupt();
		}
		sWorkerList.clear();
	}
	
	public static int getScreenWidth() {
		return sScreenWidth;
	}
	
	public static ILoadQueue getQueue() {
		return sQueue;
	}
	
	public static IBitmapCache getCache() {
		return sCache;
	}
	
	public static void load(String url, ImageView imgView) {
		try {
			sQueue.putEntry(new ImageEntry(url, imgView));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
