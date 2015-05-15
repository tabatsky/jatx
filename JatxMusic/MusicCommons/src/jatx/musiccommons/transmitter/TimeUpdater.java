package jatx.musiccommons.transmitter;

import java.lang.ref.WeakReference;

public class TimeUpdater extends Thread {
	private volatile WeakReference<UI> ref;
	private volatile Mp3Decoder mDecoder;
	
	public TimeUpdater(UI ui, Mp3Decoder decoder) {
		ref = new WeakReference<UI>(ui);
		mDecoder = decoder;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				UI ui = ref.get();
				
				if (ui!=null) {
					ui.setCurrentTime(mDecoder.currentMs, mDecoder.trackLengthSec*1000f);
				}
				
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			System.err.println("time updater interrupted");
		}
	}
}
