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
			s = new Socket(ipAddr, 7373);
			
			os = s.getOutputStream();
			byte[] md5 = my_id.getBytes("US-ASCII");
			os.write(md5);
			os.flush();
			//s.shutdownOutput();
			
			is = s.getInputStream();
			
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, Util.getAudioFormat());
	        speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	        speakers.open(Util.getAudioFormat());
	        speakers.start();
			
			
	        byte[] data = new byte[Util.CHUNK_SIZE*25];
				        
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
	        	byte[] tmp = new byte[]{(byte)127};
				os.write(tmp);
				os.flush();
				throw new DeclinedException("Call canceled by you");
	        } else if (first==(byte)0) {
	        	//System.out.println("Call accepted by remote user");
	        	//System.out.println("Type 'breakup'|'quit'");
	        	Main.slaveActive = true;
	        } else if (first==(byte)127) {
	        	Main.sendCmd(Util.EMPTY_CMD);
	        	throw new DeclinedException("Call declined by remote user");
	        }
	       
	        int numBytesRead = 0;
	        
	        boolean startedFlag = false;
	        
	        while(!cancelFlag) {
				numBytesRead = is.read(data);
				if (numBytesRead>0) speakers.write(data, 0, numBytesRead);
				
				if (!startedFlag&&numBytesRead>0) {
					startedFlag = true;
					//s.setSoTimeout(1500);
				}
				
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
