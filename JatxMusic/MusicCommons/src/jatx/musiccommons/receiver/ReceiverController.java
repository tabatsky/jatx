/*******************************************************************************
 * Copyright (c) 2015 Evgeny Tabatsky.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Evgeny Tabatsky - initial API and implementation
 ******************************************************************************/
package jatx.musiccommons.receiver;

import jatx.musiccommons.receiver.UI;
import jatx.musiccommons.util.Debug;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ReceiverController extends Thread {
	public static final int CONNECT_PORT_CONTROLLER = 7172;
	
	public static final int SOCKET_TIMEOUT = 1500;
	
	public static final byte COMMAND_EMPTY = (byte)255;
	public static final byte COMMAND_STOP = (byte)127;
	public static final byte COMMAND_PAUSE = (byte)126;
	public static final byte COMMAND_PLAY = (byte)125;
	
	volatile WeakReference<UI> ref;
	
	volatile boolean finishFlag;
	
	String host;
	
	public ReceiverController(String hostname, UI ui) {
		host = hostname;
		finishFlag = false;
		ref = new WeakReference<UI>(ui);
	}
	
	public void setFinishFlag() {
		if (finishFlag) return;
		finishFlag = true;
		interrupt();
	}
	
	public void play() {
		System.out.println("(controller) " + "play command received");
		
		final UI ui = ref.get();
		if (ui!=null) {
			ui.play();
		}
	}

	public void pause() {
		System.out.println("(controller) " + "pause command received");
		
		final UI ui = ref.get();
		if (ui!=null) {
			ui.pause();
		}
	}
	
	public void setVolume(final int vol) {
		System.out.println("(controller) " + "volume command received");
		System.out.println("(controller) " + "volume: " + Integer.valueOf(vol).toString());
		
		final UI ui = ref.get();
		if (ui!=null) {
			ui.setVolume(vol);
		}
	}
	
	public void run() {
		Socket s = null;
		InputStream is = null;
		
		try {
			System.out.println("(controller) " + "thread start");
			
			InetAddress ipAddr = InetAddress.getByName(host);
		
			s = new Socket();
			s.connect(new InetSocketAddress(ipAddr, CONNECT_PORT_CONTROLLER), SOCKET_TIMEOUT);
			is = s.getInputStream();
			
			int numBytesRead;
			byte[] data = new byte[1];
			
			int cmdSkipped = 0;
			
			while (!finishFlag) {
				if (is.available()>0) {
					cmdSkipped = 0;
					
					numBytesRead = is.read(data, 0, 1);
					
					if (numBytesRead==1) {
						byte cmd = data[0];
						
						if (cmd>=0&&cmd<=100) {
							setVolume(cmd);
						} else if (cmd==COMMAND_PLAY) {
							play();
						} else if (cmd==COMMAND_PAUSE) {
							pause();
						} else if (cmd==COMMAND_STOP) {
							System.out.println("(controller) " + "stop command received");
							
							final UI ui = ref.get();
							if (ui!=null) {
								ui.stopJob();
							}
						} else if (cmd==COMMAND_EMPTY) {
							//System.out.println("(controller) " + "empty command received");
						}
					}
				} else {
					Thread.sleep(50);
					cmdSkipped++;
					if (cmdSkipped>7) {
						finishFlag = true;
					}
				}
			}
		} catch (InterruptedException e) {
			System.err.println("(controller) thread interrupted");
		} catch (SocketTimeoutException e) {
			System.err.println("(controller) socket timeout");
		} catch (IOException e) {
			System.err.println("(controller) io exception");
		} catch (Exception e) {
			System.err.println("(controller) " + Debug.exceptionToString(e));
		} finally {
			System.out.println("(controller) " + "thread finish");
			
			try {
				is.close();
				System.err.println("(controller) instream closed");
			} catch (Exception e) {
				System.err.println("(controller) cannot close instream");
			}
			try {
				s.close();
				System.err.println("(controller) socket closed");
			} catch (Exception e) {
				System.err.println("(controller) cannot close socket");
			}
			
			
			final UI ui = ref.get();
			if (ui!=null) {
				ui.stopJob();
			}

		}
	}
}

