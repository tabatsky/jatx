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
package jatx.musicreceiver.fx;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import jatx.musiccommons.receiver.AutoConnectThread;
import jatx.musiccommons.receiver.ReceiverController;
import jatx.musiccommons.receiver.ReceiverPlayer;
import jatx.musiccommons.receiver.UI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class Main extends Application implements UI {
	public static final String SETTINGS_DIR_PATH;
	
	static {
		String path = System.getProperty("user.home") + File.separator + ".jatxmusic_receiver";
		File tmp = new File(path);
		tmp.mkdirs();
		
		if (tmp.exists()) {
			SETTINGS_DIR_PATH = path;
		} else {
			SETTINGS_DIR_PATH = ".";
		}
	}
	
	private Stage mStage;
	
	private Main self;
	
	private Button mToogleButton;
	private boolean isRunning;
	
	private CheckBox mAutoCheckBox;
	private boolean mAutoConnect;
	
	private TextField mHostField;
	private String mHost;
	
	private ReceiverPlayer rp;
	private ReceiverController rc;
	private AutoConnectThread act;
	
	@Override
	public void start(Stage primaryStage) {
		mStage = primaryStage;
		mStage.setTitle("JatxMusic Receiver");
		
		self = this;
		
		isRunning = false;
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
			Scene scene = new Scene(root);
			
			mHostField = (TextField) scene.lookup("#host_field");
			
			mToogleButton = (Button) scene.lookup("#toogle_button");
			mToogleButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					if (!isRunning) {
						startJob();
					} else {
						stopJob();
					}
				}
			});
			
			mAutoCheckBox = (CheckBox) scene.lookup("#auto_connect");
			mAutoCheckBox.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					mAutoConnect = mAutoCheckBox.isSelected();
					
					saveSettings();
					
					System.out.println("Auto Connect: " + mAutoConnect);
				}
			});
			
			Platform.setImplicitExit(false);			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent event) {
			    	if (act!=null) {
						act.interrupt();
					}
			    	
			    	stopJob();
			        
			        System.out.println("close app");
			        
			        Platform.exit();
			    }
			});
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			prepareAndStart();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void startJob() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (isRunning) return;
				isRunning = true;
				System.out.println("start job");		
				mToogleButton.setText("Stop");
				mHost = mHostField.getText();
				saveSettings();
				rp = new ReceiverPlayer(mHost, self, new DesktopSoundOut());
				rc = new ReceiverController(mHost, self);
				rp.start();
				rc.start();
			}
		});
	}

	@Override
	public void stopJob() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!isRunning) return;
				isRunning = false;
				rp.setFinishFlag();
				rc.setFinishFlag();
				mToogleButton.setText("Start");
				System.out.println("stop job");
			}
		});
	}

	@Override
	public void play() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rp.play();
			}
		});
	}

	@Override
	public void pause() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rp.pause();
			}
		});
	}

	@Override
	public void setVolume(final int vol) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rp.setVolume(vol);
			}
		});
	}
	
	private void prepareAndStart() {
		loadSettings();
		
		mHostField.setText(mHost);
		mAutoCheckBox.setSelected(mAutoConnect);
		
		act = new AutoConnectThread(this);
		act.start();
	}
	
	private void loadSettings() {
		mHost = "127.0.0.1";
		mAutoConnect = false;
		
		File f = new File(SETTINGS_DIR_PATH + File.separator + "settings.txt");
		if (!f.exists()) return;
		
		try {
			Scanner sc = new Scanner(f);
			
			while(sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				
				if (line.startsWith("IP=")) {
					mHost = line.replace("IP=", "");
				}
				
				if (line.startsWith("AUTO_CONNECT=")) {
					String autoStr = line.replace("AUTO_CONNECT=", "");
					mAutoConnect = Boolean.parseBoolean(autoStr);
				}
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void saveSettings() {
		File f = new File(SETTINGS_DIR_PATH + File.separator + "settings.txt");
		
		try {
			PrintWriter pw = new PrintWriter(f);
			
			pw.println("IP=" + mHost);
			pw.println("AUTO_CONNECT=" + Boolean.toString(mAutoConnect));
			
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAutoConnect() {
		return mAutoConnect;
	}
}
