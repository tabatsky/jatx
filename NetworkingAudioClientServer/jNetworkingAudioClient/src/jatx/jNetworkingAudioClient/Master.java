package jatx.jNetworkingAudioClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Master {
	public static final int UNDEF = 0;
	public static final int DECLINE = 1;
	public static final int ACCEPT = 2;
	
	static Sender sender;
	static MicrophoneReader mr;
	
	static volatile Integer numBytesRead;
	static volatile Integer senderNotReady = 0;
	static volatile byte[] data;
	static final Object monitor = new Object();
	
	static volatile boolean quitFlag;
	static volatile boolean busyFlag;
	static volatile int acceptValue;
	
	static volatile String caller_id;
	static volatile String caller_id_forced;
	
	public static void listenStart() {
		quitFlag = false;
		busyFlag = false;
		caller_id = null;
		caller_id_forced = null;
		//acceptValue = UNDEF;
		
		sender = new Sender();
		sender.start();
		
		mr = new MicrophoneReader();
		mr.start();
	}
	
	public static void setQuitFlag() {
		quitFlag = true;
	}
	
	public static void reset(String who) {		
		caller_id_forced = null;
		caller_id = null;
		busyFlag = false;
		senderNotReady = 0;
		
		sender.interrupt();
		
		System.err.println("Reset by "+who);
	}
	
	public static void accept(int value) {
		acceptValue = value;
	}
	
	public static boolean isBusy() {
		return busyFlag;
	}
	
	public static void setCallerId(String caller_id_param) {
		caller_id_forced = caller_id_param;
	}
	
	public static String getCallerId() {
		return caller_id;
	}
	
	static class Sender extends Thread {
		InputStream is;
		OutputStream os;
		
		public Sender() {
			setName("Sender");
			System.err.println("Sender started");
		}
		
		public void run() {
			try {
				ServerSocket ss = new ServerSocket(7373);
				
				while (!quitFlag) {
					caller_id_forced = null;
					caller_id = null;
					busyFlag = false;
					acceptValue = UNDEF;
					senderNotReady = 0;
					
					Socket s = ss.accept();
					
					try {
						is = s.getInputStream();
						
						byte[] md5 = new byte[32];
						int numBytes = 0;
						while (numBytes<32) {
							numBytes += is.read(md5,numBytes,32-numBytes);
						}
						//s.shutdownInput();
						
						if (caller_id_forced!=null&&(new String(md5, "US-ASCII")).equals(caller_id_forced)) {
							accept(ACCEPT);
							busyFlag = true;
						}
						caller_id = new String(md5, "US-ASCII");
						if (caller_id_forced!=null&&!caller_id.equals(caller_id_forced)) {
							accept(DECLINE);
						}
					
						System.err.println("Incoming: "+new String(md5, "US-ASCII"));
						
						while (acceptValue==UNDEF) {
							byte[] tmp = new byte[1];
							if(is.available()>0&&is.read(tmp,0,1)==1&&tmp[0]==(byte)127) {
								Main.sendCmd(Util.EMPTY_CMD);
								throw new DeclinedException("Call canceled by remote user");
							}
						}
						
						os = s.getOutputStream();
						
						if (acceptValue==DECLINE) {
							reset("Sender");
							byte[] tmp = new byte[]{(byte)127};
							os.write(tmp);
							os.flush();
							throw new DeclinedException("Call declined by you");
						} else {
							byte[] tmp = new byte[]{(byte)0};
							os.write(tmp);
							os.flush();
							busyFlag = true;
							//System.out.println("Call started");
							//System.out.println("Type 'breakup'|'quit'");
							Main.masterActive = true;
						}
									
						while (busyFlag) {
							synchronized (monitor) {
								senderNotReady++;
							
								try {
									monitor.wait();
								} catch (InterruptedException e) {
									e.printStackTrace(System.err);
								}
								
								os.write(data, 0, numBytesRead);
								os.flush();
						
								senderNotReady--;
							}
							
							if (Util.DEBUG) {
								System.err.print("Sender: ");
								System.err.print(numBytesRead);
								System.err.println(" bytes sent");
							} else {
								Thread.sleep(25);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace(System.err);
					} catch (DeclinedException e) {
						reset("Declined Exception");
						System.out.println(e.getMsg());
						e.printStackTrace(System.err);
						//Main.sendCmd(Util.EMPTY_CMD);
					} catch (IOException e) {
						//System.out.println("Disconnect occured");
						//Main.sendCmd(Util.EMPTY_CMD);
						e.printStackTrace(System.err);
					} finally {
						s.shutdownOutput();
						s.close();
					}
				}
			} catch (IOException e) {
				System.out.println("Disconnect occured");
				e.printStackTrace(System.err);
			}
		}
	}
	
	static class MicrophoneReader extends Thread {
		AudioFormat format;
		TargetDataLine microphone;
		
		public MicrophoneReader() {
			setName("MicrophoneReader");
			format = Util.getAudioFormat();
			System.err.println("Microphone reader started");
		}
		
		public void run() {
			try {
				microphone = AudioSystem.getTargetDataLine(format);
				
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		        microphone = (TargetDataLine) AudioSystem.getLine(info);
		        microphone.open(format);
				
		        
		        data = new byte[Util.CHUNK_SIZE];
		        microphone.start();
		        		        
		        while (!quitFlag) {
		        	synchronized (monitor) {
		        		if (senderNotReady==1) {
			        		monitor.notifyAll();
			        		continue;
		        		}
		        		
		        		numBytesRead = microphone.read(data, 0, Util.CHUNK_SIZE);	
		        	}

		        	if (Util.DEBUG) {
	        			System.err.print("Microphone reader: ");
	        			System.err.print(numBytesRead);
	        			System.err.println(" bytes read");
		        	} else {
		        		try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		        	}
		        }
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
}
