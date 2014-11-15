package jatx.jNetworkingAudioClient;

public class DeclinedException extends Exception {
	String message;
	
	public DeclinedException(String msg) {
		message = msg;
	}
	
	public String getMsg() {
		return message;
	}
}