package jatx.imageloader.lib;

import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;

public class DefaultLoadQueue implements ILoadQueue {
	private volatile ArrayBlockingQueue<ImageEntry> mQueue;
	
	public DefaultLoadQueue(int capacity) {
		mQueue = new ArrayBlockingQueue<ImageEntry>(capacity);
	}
	
	public void putEntry(ImageEntry entry) throws InterruptedException {
		mQueue.put(entry);
		Log.i("queue size put", Integer.toString(mQueue.size()));
	}
	
	public void clearQueue() {
		mQueue.clear();
	}
	
	public ImageEntry getNextEntry() throws ILoadQueue.NextNotReadyException {
		//Log.i("queue size poll", Integer.toString(mQueue.size()));
		ImageEntry entry = mQueue.poll();
		if (entry==null) throw new ILoadQueue.NextNotReadyException();
		return entry;
	}
}
