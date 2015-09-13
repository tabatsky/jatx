package jatx.jNetworkingAudioClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * 
 * @author jatx
 *
 * This class makes get-request to servlet
 * every 90 seconds
 * to update your IP
 */
public class IPUpdater extends Thread {
	String serverUrl;
	String email;
	String password;
	
	public IPUpdater(String serverUrl, String email, String password) {
		setName("IPUpdater");
		this.serverUrl = serverUrl;
		this.email = email;
		this.password = password;
	}
	
	public void run() {
		try {
			while(true) {
				Thread.sleep(1000*90);
				/*
				 * get request to IP servlet:
				 */
				String url = serverUrl+"updateip?email="+email+"&password="+password;
				try {
					Scanner urlGetter = new Scanner(new URL(url).openStream(), "UTF-8");
					while (urlGetter.hasNextLine()) {
						urlGetter.nextLine();
					}
					urlGetter.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
