package jatx.jNetworkingAudioClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * 
 * @author jatx
 *
 * This class initiates call
 * Sends user_id (md5 of email) to remote user
 * Receives data from remote user 
 * and writes it to speakers if call is accepted
 */
public class Slave extends Thread {
	String my_id;
	InetAddress ipAddr;
	volatile boolean cancelFlag;
	SourceDataLine speakers;
	Socket s;
	InputStream is;
	OutputStream os;
	
	public Slave(InetAddress ipAddr, String my_id) {
		setName("Slave");
		
		this.ipAddr = ipAddr;
		this.my_id = my_id;
		
		cancelFlag = false;
		System.err.println("Slave started");
	}
	
	public void setCancelFlag() {
		cancelFlag = true;
	}
	
	public boolean isCanceled() {
		return cancelFlag;
	}
	
	public void run() {
		try {
			/*
			 * init connection to remote user:
			 */
			s = new Socket(ipAddr, 7373);
			
			/*
			 * send md5-hash to remote user:
			 */
			os = s.getOutputStream();
			byte[] md5 = my_id.getBytes("US-ASCII");
			os.write(md5);
			os.flush();
			
			is = s.getInputStream();
			
			/*
			 * init speakers:
			 */
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, Util.getAudioFormat());
	        speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	        speakers.open(Util.getAudioFormat());
	        speakers.start();
			
			/*
			 * create buffer:
			 */
	        byte[] data = new byte[Util.CHUNK_SIZE*25];
				        
	        /*
	         * loop running until user not canceled call
	         * or one byte from remote user received
	         * trying to read one byte 
	         * and sending 0(0x00) byte to remote user:
	         */
	        while(!cancelFlag) {
	        	if (is.available()>0&&is.read(data, 0, 1)==1) {
	    	        break;
	        	}
	        	byte[] tmp = new byte[]{(byte)0};
				os.write(tmp);
				os.flush();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
	        }
	        
	        byte first = data[0];
	        
	        if (cancelFlag) {
	        	/*
	        	 * call canceled by user from main thread
	        	 * sends 127(0xFF) byte to remote user
	        	 * and throws exception
	        	 */
	        	byte[] tmp = new byte[]{(byte)127};
				os.write(tmp);
				os.flush();
				throw new DeclinedException("Call canceled by you");
	        } else if (first==(byte)0) {
	        	/*
	        	 * call accepted by remote user
	        	 * sets 'active' flag for Slave
	        	 */
	        	Main.slaveActive = true;
	        } else if (first==(byte)127) {
	        	/*
	        	 * call declined by remote user
	        	 * sends command to InputReader
	        	 * and throws Exception:
	        	 */
	        	Main.sendCmd(Util.EMPTY_CMD);
	        	throw new DeclinedException("Call declined by remote user");
	        }
	       
	        int numBytesRead = 0;
	        
	        boolean startedFlag = false;
	        
	        /*
	         * main loop
	         * running while Slave not canceled or interrupted:
	         */
	        while(!cancelFlag) {
				numBytesRead = is.read(data);
				
				/*
				 * if some data received from remote user
				 * write it to speakers
				 */
				if (numBytesRead>0) speakers.write(data, 0, numBytesRead);
				
				/*
				 * if call started:
				 */
				if (!startedFlag&&numBytesRead>0) {
					startedFlag = true;
				}
				
				/*
				 * send command to InputReader
				 * and throw Exception
				 * if InputStream from socket finished:
				 */
				if (startedFlag&&numBytesRead==-1) {
					Main.sendCmd(Util.EMPTY_CMD);
					throw new DeclinedException("Call canceled by remote user");
				}
				
				if (Util.DEBUG) {
					System.err.print("Receiver: ");
					System.err.print(numBytesRead);
					System.err.println(" bytes received");
				} else {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace(System.err);
						throw new DeclinedException("Call canceled by you");
					}
				}
			}
			
	        s.close();
	        
	        System.out.println("Connection closed");
		} catch (LineUnavailableException e) {
			System.out.println("Problems with sound output");
		} catch (DeclinedException | IOException e) {
			if (e instanceof DeclinedException) {
				System.out.println(((DeclinedException)e).getMsg());
			} else {
				System.out.println("Disconnect occured");
				Main.sendCmd(Util.EMPTY_CMD);
			}
			e.printStackTrace(System.err);
			//Main.sendCmd(Util.EMPTY_CMD);
		} 	finally {
			speakers.close();
			cancelFlag = true;
			Master.reset("Slave");
		}
	}
}
