package jatx.imageloader;

import jatx.imageloader.lib.DefaultBitmapCache;
import jatx.imageloader.lib.DefaultLoadQueue;
import jatx.imageloader.lib.ImageLoader;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ListView;

public class TestActivity extends Activity {
	public static final int THREAD_COUNT = 4;
	
	static long cacheMem;
	static int screenWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
	
		final Thread.UncaughtExceptionHandler androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		final Thread.UncaughtExceptionHandler customUEH = new Thread.UncaughtExceptionHandler() {
			 public void uncaughtException(final Thread thread, final Throwable ex) {
			     ex.printStackTrace();
			     androidDefaultUEH.uncaughtException(thread, ex);
			 }
		};
		Thread.setDefaultUncaughtExceptionHandler(customUEH);
		
		Runtime info = Runtime.getRuntime();
		cacheMem = info.freeMemory()/4;
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		screenWidth = metrics.widthPixels;
		
		ImageLoader.init(THREAD_COUNT, screenWidth, new DefaultLoadQueue(500), new DefaultBitmapCache(cacheMem, 50));
		
		try {			
			InputStream in = getResources().openRawResource(R.raw.flickr_data);
			Scanner sc = new Scanner(in);
			String jsonStr = sc.useDelimiter("\\A").next();
			sc.close();
			in.close();
			
			final List<String> urlList = FlickrJsonParser.parseURLs(jsonStr);
			final ListView listView = (ListView) findViewById(R.id.img_list_view);
			
			listView.setAdapter(new ImageListAdapter(urlList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		ImageLoader.destroy();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}
