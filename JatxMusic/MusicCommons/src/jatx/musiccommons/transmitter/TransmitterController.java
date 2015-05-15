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
package jatx.musiccommons.transmitter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TransmitterController extends Thread {
	public static final int CONNECT_PORT_CONTROLLER = 7172;
	
	public static final byte COMMAND_EMPTY = (byte)255;
	public static final byte COMMAND_STOP = (byte)127;
	public static final byte COMMAND_PAUSE = (byte)126;
	public static final byte COMMAND_PLAY = (byte)125;
	
	volatile WeakReference<UI> ref;
	
	volatile boolean finishFlag;
	volatile BlockingQueue<Byte> fifo;
	
	volatile boolean mForceDisconnectFlag;
	
	public TransmitterController(UI ui) {
		finishFlag = false;
		mForceDisconnectFlag = false;
		
		ref = new WeakReference<UI>(ui);
		
		fifo = new ArrayBlockingQueue<Byte>(2048);
	}
	
	public void setFinishFlag() {
		finishFlag = true;
	}
	
	public void forceDisconnect() {
		mForceDisconnectFlag = true;
	}
	
	public void play() {
		System.out.println("(controller) play");
		fifo.offer(COMMAND_PLAY);
	}
	
	public void pause() {
		System.out.println("(controller) pause");
		fifo.offer(COMMAND_PAUSE);
	}
	
	public void sendStop() {
		System.out.println("(controller) stop");
		fifo.offer(COMMAND_STOP);
	}
	
	public void setVolume(int vol) {
		System.out.println("(controller) set volume: " + Integer.valueOf(vol).toString());
		
		if (vol>=0&&vol<=100) {
			fifo.offer((byte)vol);
		}
	}
	
	@Override
	public void run() {
		ServerSocket ss = null;
		OutputStream os = null;
		
		try {
			while(!finishFlag) {
				Thread.sleep(100);
				
				ss = new ServerSocket(CONNECT_PORT_CONTROLLER);
				System.out.println("(controller) new server socket");
				
				try {
					ss.setSoTimeout(Globals.SO_TIMEOUT);
					Socket s = ss.accept();
					os = s.getOutputStream();
					
					System.out.println("(controller) socket connect");
					setVolume(Globals.volume);
					
					while (!finishFlag) {
						byte cmd;
						
						Byte objCmd = fifo.poll();
						
						if (objCmd!=null) {
							cmd = objCmd.byteValue();
						} else {
							cmd = COMMAND_EMPTY;
						}
						
						byte[] data = new byte[]{cmd};
						
						os.write(data);
						os.flush();
						Thread.sleep(10);
						
						if (mForceDisconnectFlag) {
							System.out.println("(player) disconnect flag: throwing DisconnectException");
							throw new DisconnectException();
						}		
					}
					
					byte[] data = new byte[]{(byte)COMMAND_STOP};
					
					os.write(data);
					os.flush();
				} catch (SocketTimeoutException e) {
					System.err.println("(controller) socket timeout");

					mForceDisconnectFlag = false;
				} catch (DisconnectException e){
					System.err.println("(controller) socket force disconnect");
					System.err.println("(controller) " + (new Date()).getTime()%10000);
					
					mForceDisconnectFlag = false;
				} catch (IOException e) {
					System.err.println("(controller) socket disconnect");
					System.err.println("(controller) " + (new Date()).getTime()%10000);
					
					Globals.tp.forceDisconnect();
					
					Thread.sleep(250);
					
					mForceDisconnectFlag = false;
				} finally {
					try {
						os.close();
						System.out.println("(controller) outstream closed");
					} catch (Exception ex) {
						System.err.println("(controller) cannot close outstream");
					}
					try {
						ss.close();
						System.out.println("(controller) server socket closed");
					} catch (Exception ex) {
						System.err.println("(controller) cannot close server socket");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.err.println("(controller) thread interrupted");
			try {
				os.close();
				System.out.println("(controller) outstream closed");
			} catch (Exception ex) {
				System.err.println("(controller) cannot close outstream");
			}
			try {
				ss.close();
				System.out.println("(controller) server socket closed");
			} catch (Exception ex) {
				System.err.println("(controller) cannot close server socket");
			}
		} finally {
			System.out.println("(controller) thread finished");
		}
	}
}

