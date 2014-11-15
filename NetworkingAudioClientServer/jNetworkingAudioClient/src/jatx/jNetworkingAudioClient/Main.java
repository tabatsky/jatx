package jatx.jNetworkingAudioClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

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
		
		public void sendCommand(String cmd) {
			input.offer(cmd);
		}
 	}
	
	static void sendCmd(String cmd) {
		ir.sendCommand(cmd);
	}
	
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
		
		while (!loginSuccess) {
			System.out.println("Type 'register'|'login'|'quit'");
			input = sc.nextLine();
			if (input.equals("register")) {
				System.out.println("Type your email:");
				email = sc.nextLine();
				System.out.println("Type your password:");
				password = sc.nextLine();
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
				String url = serverUrl+"updateip?email="+email+"&password="+password;
				try {
					Scanner urlGetter = new Scanner(new URL(url).openStream(), "UTF-8");
					while (urlGetter.hasNextLine()) {
						input = urlGetter.nextLine();
						if (input.startsWith("user_id:")) {
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
				return;
			}
		}
		
		IPUpdater ipupd = new IPUpdater(serverUrl,email,password);
		ipupd.start();
		Master.listenStart();
		ir = new InputReader(sc);
		ir.start();
		
		boolean waitInput = false;
		input = null;
		
loop:	while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			if (slave==null) {
				if (!waitInput) {
					System.out.println("Type 'call'|'quit'");
				}
			}
			
			if (slave!=null) {
				if (slave.isCanceled()) {
					slave = null;
					continue;
				}
				

				if (masterActive&&slaveActive) {
					masterActive = false;
					slaveActive = false;
					System.out.println("Call started");
					waitInput = false;
				}
				
				if (!waitInput) {
					System.out.println("Type 'cancel'|'quit'");
				}
				
				input = ir.getInput();
				if (input==null) {
					waitInput = true;
				} else if (!(input.equals("cancel")||input.equals("quit"))) {
					waitInput = false;
					continue;
				} 
				if (input!=null&&input.equals("cancel")) {
					waitInput = false;
					slave.setCancelFlag();
					slave.interrupt();
					Master.reset("Main");
					continue;
				}
				
				if (input!=null&&input.equals("quit")) {
					System.exit(0);
				}
			}
			
			input = ir.getInput();
			
			if (input==null) { 
				waitInput = true;
			} else if (!(input.equals("call")||input.equals("quit"))) {
				waitInput = false;
				continue;
			}
			
			if (input!=null&&input.equals("quit")) {
				System.exit(0);
			}
			
			if (input!=null&&input.equals("call")) {
				waitInput = false;
				
				//Master.setCallerId("waiting");
				System.out.println("Type target email:");
				do {
					input = ir.getInput();
				} while (input==null);
				String target_email = input;
				
				String[] userInfo = getUserInfo(target_email,null);
				
				if (userInfo==null) {
					System.out.println("Cannot get user info");
					continue;
				}
				
				if (userInfo[2].equals("offline")) {
					System.out.println("User "+target_email+" is offline");
				} else {
					InetAddress ipAddr;
					try {
						ipAddr = InetAddress.getByName(userInfo[2]);
						slave = new Slave(ipAddr, user_id);
						slave.start();
						Master.setCallerId(userInfo[1]);
					} catch (UnknownHostException e) {
						System.out.println("Error: unknown IP");
						e.printStackTrace(System.err);
					}
				}
			}

			if (Master.getCallerId()!=null&&!Master.isBusy()) {
				String[] userInfo = getUserInfo(null,Master.getCallerId());
				
				if (userInfo==null) {
					System.out.println("Cannot get user info");
					continue;
				}
				
				System.out.println(userInfo[0]+" is calling. Accept? 'Y'|'N'");
wait_master:	while (true) {
					if (Master.getCallerId()==null) {
						continue loop;
					}
					input = ir.getInput();
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
				
				if (userInfo[2].equals("offline")) {
					System.out.println("User "+userInfo[0]+" is offline");
				} else {
					InetAddress ipAddr;
					try {
						ipAddr = InetAddress.getByName(userInfo[2]);
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
	
	static String[] getUserInfo(String target_email, String target_user_id) {
		try {
			String url = null;
			if (target_email!=null) {
				url = serverUrl+"getuserinfo?email="+target_email;
			} else if (target_user_id!=null) {
				url = serverUrl+"getuserinfo?user_id="+target_user_id;
			} else {
				return null;
			}
			
			String[] result = new String[3];
			
			String input = null;
			
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
