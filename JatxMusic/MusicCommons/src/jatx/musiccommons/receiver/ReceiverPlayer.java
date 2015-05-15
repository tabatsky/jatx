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

import jatx.musiccommons.receiver.SoundOut;
import jatx.musiccommons.receiver.UI;
import jatx.musiccommons.util.Debug;
import jatx.musiccommons.util.Frame;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ReceiverPlayer extends Thread {
	public static final String LOG_TAG_PLAYER = "receiver player";
	
	public static final int CONNECT_PORT_PLAYER = 7171;
	
	public static final int FRAME_HEADER_SIZE = 32;
	
	public static final int SOCKET_TIMEOUT = 1500;
	
	private volatile WeakReference<UI> ref;
	
	private volatile boolean finishFlag;
	private volatile boolean isPlaying;
	private volatile SoundOut mSoundOut;
	
	private volatile int volume;
	
	private String host;
	
	public ReceiverPlayer(String hostname, UI ui, SoundOut soundOut) {
		host = hostname;
		finishFlag = false;
		isPlaying = true;
		volume = 100;
		ref = new WeakReference<UI>(ui);
		
		mSoundOut = soundOut;
	}
	
	public void setFinishFlag() {
		if (finishFlag) return;
		finishFlag = true;
		interrupt();
	}
	
	public void play() {
		if (!isPlaying) {
			mSoundOut.play();
			isPlaying = true;
		}
	}

	public void pause() {
		if (isPlaying) {
			mSoundOut.pause();
			isPlaying = false;
		}
	}
	
	public void setVolume(int vol) {
		volume = vol;
		
		mSoundOut.setVolume(volume);
	}
	
	public void run() {
		Socket s = null;
		InputStream is = null;
		
		try {
			System.out.println("(player) " + "thread start");
			
			InetAddress ipAddr = InetAddress.getByName(host);
		
			s = new Socket();
			s.connect(new InetSocketAddress(ipAddr, CONNECT_PORT_PLAYER), SOCKET_TIMEOUT);
			is = s.getInputStream();
			
			int frameRate = 44100;
			int channels = 2;
			int position = 0;
			restartPlayer(frameRate, channels);
			
			while (!finishFlag) {
				if (isPlaying) {
					Frame f = Frame.fromInputStream(is);
					
					if (frameRate!=f.freq||channels!=f.channels||position!=f.position) {
						frameRate = f.freq;
						channels = f.channels;
						position = f.position;
						restartPlayer(frameRate, channels);
					}
					
					mSoundOut.write(f.data, 0, f.size);
				}
			}
		} catch (InterruptedException e) {
			System.err.println("(player) thread interrupted");
		} catch (SocketTimeoutException e) {
			System.err.println("(player) socket timeout");
		} catch (IOException e) {
			System.err.println("(player) io exception");
		} catch (Exception e) {
			System.err.println("(player) " + Debug.exceptionToString(e));
		} finally {
			System.out.println("(player) " + "thread finish");
			
			mSoundOut.destroy();
			try {
				is.close();
				System.err.println("(player) socket closed");
			} catch (Exception e) {
				System.err.println("(player) cannot close instream");
			}
			try {
				s.close();
				System.err.println("(player) socket closed");
			} catch (Exception e) {
				System.err.println("(player) cannot close socket");
			}
			
			final UI ui = ref.get();
			if (ui!=null) {
				ui.stopJob();
			}

		}
	}
	
	private void restartPlayer(int frameRate, int channels) {
		mSoundOut.renew(frameRate, channels);
				
		mSoundOut.setVolume(volume);
		
		System.out.println("(player) " + "player restarted");
		System.out.println("(player) " + "frame rate: " + Integer.valueOf(frameRate).toString());
		System.out.println("(player) " + "channels: " + Integer.valueOf(channels).toString());
	}
 }
