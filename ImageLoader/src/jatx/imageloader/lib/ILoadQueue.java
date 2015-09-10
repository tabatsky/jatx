package jatx.imageloader.lib;


public interface ILoadQueue {
	public void putEntry(ImageEntry imgEntry) throws InterruptedException;
	public ImageEntry getNextEntry() throws NextNotReadyException;
	public void clearQueue();
	
	public static class NextNotReadyException extends Exception {
		private static final long serialVersionUID = 5633608051734517150L;
	}
}
