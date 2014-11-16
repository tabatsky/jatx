package jatx.jNetworkingAudioClient;

/**
 * 
 * @author jatx
 *
 * This exception is thrown 
 * when you or remote user
 * declined or canceled call
 */
public class DeclinedException extends Exception {
	String message;
	
	public DeclinedException(String msg) {
		message = msg;
	}
	
	public String getMsg() {
		return message;
	}
}