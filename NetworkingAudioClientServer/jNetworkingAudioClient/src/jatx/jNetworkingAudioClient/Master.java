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

/**
 * 
 * @author jatx
 *
 * This class listens port 7373
 * Identifies remote user by user_id (md5 of email)
 * Accepts or declines calls
 * Reads data from microphone
 * And sends it to remote user if call accepted
 */
public class Master {
	public static final int UNDEF = 0;
	public static final int DECLINE = 1;
	public static final int ACCEPT = 2;
	
	static Sender sender;
	static MicrophoneReader mr;
	
	static volatile Integer numBytesRead; // number of bytes read from microphone
	static volatile Integer senderNotReady = 0; // Sender ready status
	static volatile byte[] data; // Sender and MicrophoneReader exchange buffer
	static final Object monitor = new Object(); // Object for synchronizing
	
	static volatile boolean quitFlag;
	static volatile boolean busyFlag;
	static volatile int acceptValue;
	
	static volatile String caller_id;
	static volatile String caller_id_forced;
	
	/*
	 * start Master:
	 */
	public static void listenStart() {
		/*
		 * init param:
		 */
		quitFlag = false;
		busyFlag = false;
		caller_id = null;
		caller_id_forced = null;
		acceptValue = UNDEF;
		
		/*
		 * create and start Sender:
		 */
		sender = new Sender();
		sender.start();
		
		/*
		 * create and start Sender:
		 */
		mr = new MicrophoneReader();
		mr.start();
	}
	
	/*
	 * say threads that they can finish:
	 */
	public static void setQuitFlag() {
		quitFlag = true;
	}
	
	/*
	 * reset Sender state:
	 */
	public static void reset(String who) {		
		caller_id_forced = null;
		caller_id = null;
		busyFlag = false;
		senderNotReady = 0;
		
		sender.interrupt();
		
		System.err.println("Reset by "+who);
	}
	
	/* 
	 * say Sender to accept or decline call
	 * or reset it to undefined state:
	 */
	public static void accept(int value) {
		acceptValue = value;
	}
	
	/*
	 * is Sender busy?
	 */
	public static boolean isBusy() {
		return busyFlag;
	}
	
	/*
	 * force Sender to receive calls 
	 * only from specified user 
	 */
	public static void setCallerId(String caller_id_param) {
		caller_id_forced = caller_id_param;
	}
	
	/*
	 * return user_id of remote user
	 * if call is received
	 * or null otherwise
	 */
	public static String getCallerId() {
		return caller_id;
	}
	
	/*
	 * This class listens port 7373
	 * may receive calls from remote user
	 * and sends to remote user data from microphone, 
	 * stored in the buffer
	 */
	static class Sender extends Thread {
		InputStream is;
		OutputStream os;
		
		public Sender() {
			setName("Sender");
			System.err.println("Sender started");
		}
		
		public void run() {
			try {
				/*
				 * starting listen port 7373:
				 */
				ServerSocket ss = new ServerSocket(7373);
				
				/*
				 * main loop:
				 */
				while (!quitFlag) {
					/*
					 * reset param:
					 */
					caller_id_forced = null;
					caller_id = null;
					busyFlag = false;
					acceptValue = UNDEF;
					senderNotReady = 0;
					
					/*
					 * waiting for incoming connection:
					 */
					Socket s = ss.accept();
					
					try {
						is = s.getInputStream();
						
						/*
						 * receiving 32 bytes (md5-hash) from remote user:
						 */
						byte[] md5 = new byte[32];
						int numBytes = 0;
						while (numBytes<32) {
							numBytes += is.read(md5,numBytes,32-numBytes);
						}
						
						/*
						 * automatically accept call
						 * if remote user is specified by main thread
						 * (call initiated by our user):
						 */
						if (caller_id_forced!=null&&(new String(md5, "US-ASCII")).equals(caller_id_forced)) {
							accept(ACCEPT);
							busyFlag = true;
						}
						
						caller_id = new String(md5, "US-ASCII");
						
						/*
						 * automatically decline call
						 * if whe wait for remote user reply
						 * and other user is calling:
						 */
						if (caller_id_forced!=null&&!caller_id.equals(caller_id_forced)) {
							accept(DECLINE);
						}
					
						System.err.println("Incoming: "+new String(md5, "US-ASCII"));
						
						/*
						 * waiting until user accepts or declines call
						 * from main thread:
						 */
						while (acceptValue==UNDEF) {
							/*
							 * trying to read one byte from remote user
							 * if byte 127(0xFF) received
							 * then remote user canceled call
							 * and no need to wait any more
							 */
							byte[] tmp = new byte[1];
							if(is.available()>0&&is.read(tmp,0,1)==1&&tmp[0]==(byte)127) {
								Main.sendCmd(Util.EMPTY_CMD);
								throw new DeclinedException("Call canceled by remote user");
							}
						}
						
						os = s.getOutputStream();
						
						if (acceptValue==DECLINE) {
							/*
							 * user declined call
							 * from main thread
							 * sends 127(0xFF) byte to remote user
							 * and throws exception:
							 */
							reset("Sender");
							byte[] tmp = new byte[]{(byte)127};
							os.write(tmp);
							os.flush();
							throw new DeclinedException("Call declined by you");
						} else {
							/*
							 * call accepted by user from main thread
							 * sends 0(0x00) byte to remote user
							 * and sets 'active' flag for Master
							 */
							byte[] tmp = new byte[]{(byte)0};
							os.write(tmp);
							os.flush();
							busyFlag = true;
							Main.masterActive = true;
						}
									
						/*
						 * sending data from microphone 
						 * (stored in the buffer) 
						 * to remote user
						 * while InterruptedException or other Exception
						 * is not occured:
						 */
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
					} catch (IOException e) {
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
	
	/*
	 * This class reads data from microphone
	 * and stores it to the buffer
	 */
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
				
				/*
				 * init microphone:
				 */
				DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		        microphone = (TargetDataLine) AudioSystem.getLine(info);
		        microphone.open(format);
		        microphone.start();
		        
		        /*
		         * create exchange buffer:
		         */
		        data = new byte[Util.CHUNK_SIZE];
		        		        
		        /*
		         * main loop
		         * while quit flag not set:
		         */
		        while (!quitFlag) {
		        	synchronized (monitor) {
		        		if (senderNotReady==1) {
			        		monitor.notifyAll();
			        		continue;
		        		}
		        		
		        		/*
		        		 * store data from microphone to buffer:
		        		 */
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
