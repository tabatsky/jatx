package jatx.jNetworkingAudioClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * 
 * @author jatx
 *
 * User interface program logic
 */
public class Main {
	static InputReader ir;
	
	static String serverUrl;
	static String email;
	static String password;
	static String user_id;
	static boolean loginSuccess;
	
	static Slave slave;
	
	public static volatile boolean masterActive = false;
	public static volatile boolean slaveActive = false;
	
	/* 
	 * Asynchronously reads console input
	 * and saves commands to queue
	 */
	static class InputReader extends Thread {
		volatile Queue<String> input;
		volatile boolean finishFlag;
		volatile Scanner sc;
		
		public InputReader(Scanner sc) {
			setName("InputReader");
			
			input = new LinkedList<String>();
			finishFlag = false;
			this.sc = sc;
		}
		
		public void setFinishFlag() {
			finishFlag = true;
		}
		
		public void run() {
			while (!finishFlag) {
				input.offer(sc.nextLine());
			}
		}
		
		public String getInput() {
			return input.poll();
		}
		
		/*
		 * Emulate console input
		 */
		public void sendCommand(String cmd) {
			input.offer(cmd);
		}
 	}
	
	/*
	 * Send command to InputReader
	 */
	static void sendCmd(String cmd) {
		ir.sendCommand(cmd);
	}
	
	/*
	 * main thread of application
	 * interface logic
	 */
	public static void main(String[] args) {
		loginSuccess = false;
		if (args.length>0) {
			serverUrl = args[0];
		} else {
			return;
		}
		
		Scanner sc = new Scanner(System.in);
		String input = "";
		
		System.out.println("Using server: "+serverUrl);
		System.out.println("ATTENTION: Redirect port 7373 if you using router");
		
		/*
		 * trying to login or register
		 * until no successful login
		 */
		while (!loginSuccess) {
			System.out.println("Type 'register'|'login'|'quit'");
			input = sc.nextLine();
			if (input.equals("register")) {
				System.out.println("Type your email:");
				email = sc.nextLine();
				System.out.println("Type your password:");
				password = sc.nextLine();
				/*
				 * get request
				 * to register servlet:
				 */
				String url = serverUrl+"register?email="+email+"&password="+password;
				try {
					Scanner urlGetter = new Scanner(new URL(url).openStream(), "UTF-8");
					while (urlGetter.hasNextLine()) {
						System.out.println(urlGetter.nextLine());
					}
					urlGetter.close();
				} catch (MalformedURLException e) {
					e.printStackTrace(System.err);
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			} else if (input.equals("login")) {
				System.out.println("Type your email:");
				email = sc.nextLine();
				System.out.println("Type your password:");
				password = sc.nextLine();
				/* 
				 * get request
				 * to IP update servlet:
				 */
				String url = serverUrl+"updateip?email="+email+"&password="+password;
				try {
					Scanner urlGetter = new Scanner(new URL(url).openStream(), "UTF-8");
					while (urlGetter.hasNextLine()) {
						input = urlGetter.nextLine();
						if (input.startsWith("user_id:")) {
							/*
							 * if servlet returns user_id
							 * then login successful
							 * and loop finished
							 */
							user_id = input.replace("user_id:", "");
							loginSuccess = true;
							System.out.println("Logged in successfully");
						} else {
							System.out.println(input);
						}
					}
					urlGetter.close();
				} catch (MalformedURLException e) {
					e.printStackTrace(System.err);
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			} else if (input.equals("quit")) {
				/*
				 * close application on 'quit' command
				 */
				return;
			}
		}
		
		/*
		 * starting IPUpdater:
		 */
		IPUpdater ipupd = new IPUpdater(serverUrl,email,password);
		ipupd.start();
		/*
		 * starting Master:
		 */
		Master.listenStart();
		/*
		 * starting InputReader:
		 */
		ir = new InputReader(sc);
		ir.start();
		
		boolean waitInput = false;
		input = null;
		
		/*
		 * main loop:
		 */
loop:	while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			/*
			 * you may only make call or quit app
			 * if no active Slave exists:
			 */
			if (slave==null) {
				if (!waitInput) {
					System.out.println("Type 'call'|'quit'");
				}
			}
			
			/* 
			 * if Slave exists:
			 */
			if (slave!=null) {
				/*
				 * delete Slave if it canceled:
				 */
				if (slave.isCanceled()) {
					slave = null;
					continue;
				}
				
				/*
				 * if both Master and Slave became active
				 * then call successfully started
				 * and we may reset 'active' flags:
				 */
				if (masterActive&&slaveActive) {
					masterActive = false;
					slaveActive = false;
					System.out.println("Call started");
					waitInput = false;
				}
				
				/*
				 * Print prompt
				 * if app is not waiting for input:
				 */
				if (!waitInput) {
					System.out.println("Type 'cancel'|'quit'");
				}
				
				/*
				 * read input from console:
				 */
				input = ir.getInput();
				if (input==null) {
					/*
					 * wait if no input:
					 */
					waitInput = true;
				} else if (!(input.equals("cancel")||input.equals("quit"))) {
					/*
					 * continue if command is correct:
					 */
					waitInput = false;
					continue;
				} 
				if (input!=null&&input.equals("cancel")) {
					/*
					 * cancel call:
					 */
					waitInput = false;
					slave.setCancelFlag();
					slave.interrupt();
					Master.reset("Main");
					continue;
				}
				
				if (input!=null&&input.equals("quit")) {
					/*
					 * exit app:
					 */
					System.exit(0);
				}
			}
			
			/*
			 * read input from console:
			 */
			input = ir.getInput();
			
			if (input==null) {
				/*
				 * wait if no input:
				 */
				waitInput = true;
			} else if (!(input.equals("call")||input.equals("quit"))) {
				/*
				 * continue if command is correct:
				 */
				waitInput = false;
				continue;
			}
			
			if (input!=null&&input.equals("quit")) {
				/*
				 * exit app:
				 */
				System.exit(0);
			}
			
			if (input!=null&&input.equals("call")) {
				/*
				 * init call:
				 */
				waitInput = false;
				
				System.out.println("Type target email:");
				/*
				 * wait target e-mail from input:
				 */
				do {
					input = ir.getInput();
				} while (input==null);
				String target_email = input;
				
				/*
				 * get target user info by email -
				 * user_id and current IP:
				 */
				String[] userInfo = getUserInfo(target_email,null);
				
				/*
				 * continue main loop
				 * if cannot get user info from server:
				 */
				if (userInfo==null) {
					System.out.println("Cannot get user info");
					continue;
				}
				
				if (userInfo[2].equals("offline")) {
					System.out.println("User "+target_email+" is offline");
				} else {
					/*
					 * if user online:
					 */
					InetAddress ipAddr;
					try {
						/*
						 * resolve IP address:
						 */
						ipAddr = InetAddress.getByName(userInfo[2]);
						/*
						 * create and start new Slave:
						 */
						slave = new Slave(ipAddr, user_id);
						slave.start();
						/*
						 * force Master
						 * to receive calls
						 * only from target user:
						 */
						Master.setCallerId(userInfo[1]);
					} catch (UnknownHostException e) {
						System.out.println("Error: unknown IP");
						e.printStackTrace(System.err);
					}
				}
			}

			/*
			 * Master has incoming call request,
			 * waiting accept or decline from user: 
			 */
			if (Master.getCallerId()!=null&&!Master.isBusy()) {
				/*
				 * resolve remote user e-mail and IP by received user_id:
				 */
				String[] userInfo = getUserInfo(null,Master.getCallerId());
				
				if (userInfo==null) {
					System.out.println("Cannot get user info");
					continue;
				}
				
				System.out.println(userInfo[0]+" is calling. Accept? 'Y'|'N'");
wait_master:	while (true) {
					/*
					 * remote user canceled call,
					 * no need to wait any more,
					 * continue main loop:
					 */
					if (Master.getCallerId()==null) {
						continue loop;
					}
					input = ir.getInput();
					/*
					 * waiting until user inputs 'Y' or 'N'
					 * to accept or decline incoming call:
					 */
					if (input==null) continue wait_master;
					if (input.equals("Y")) {
						Master.accept(Master.ACCEPT);
						waitInput = false;
						break wait_master;
					} else if (input.equals("N")) {
						Master.accept(Master.DECLINE);
						waitInput = false;
						continue loop;
					} else if (input.equals(Util.EMPTY_CMD)) {
						waitInput = false;
						continue loop;
					} else {
						continue wait_master;
					}
				}
				
				/* 
				 * check if remote user still online:
				 */
				if (userInfo[2].equals("offline")) {
					System.out.println("User "+userInfo[0]+" is offline");
				} else {
					InetAddress ipAddr;
					try {
						/*
						 * resolve IP address:
						 */
						ipAddr = InetAddress.getByName(userInfo[2]);
						/*
						 * create and start new Slave:
						 */
						slave = new Slave(ipAddr, user_id);
						slave.start();
					} catch (UnknownHostException e) {
						System.out.println("Error: unknown IP");
						e.printStackTrace(System.err);
					}
				}
			}
		}
	}
	
	/* 
	 * resolves user info by e-mail or user id
	 */
	static String[] getUserInfo(String target_email, String target_user_id) {
		try {
			String url = null;
			if (target_email!=null) {
				/*
				 * resolve by e-mail:
				 */
				url = serverUrl+"getuserinfo?email="+target_email;
			} else if (target_user_id!=null) {
				/*
				 * resolve by user-id:
				 */
				url = serverUrl+"getuserinfo?user_id="+target_user_id;
			} else {
				return null;
			}
			
			String[] result = new String[3];
			
			String input = null;
			
			/* 
			 * make get request to servlet:
			 */
			Scanner urlGetter = new Scanner(new URL(url).openStream(), "UTF-8");
			
			while (urlGetter.hasNextLine()) {
				input = urlGetter.nextLine();
				if (input.startsWith("Error:")) {
					System.out.println(input);
					return null;
				} else if (input.startsWith("email:")) {
					result[0] = input.replace("email:", "");
				} else if (input.startsWith("user_id:")) {
					result[1] =input.replace("user_id:", "");
				} else if (input.startsWith("ip:")) {
					result[2] = input.replace("ip:", "");
				}
			}
			urlGetter.close();
		
			return result;
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}
}
