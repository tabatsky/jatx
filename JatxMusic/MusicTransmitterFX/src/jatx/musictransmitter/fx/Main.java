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
package jatx.musictransmitter.fx;

import jatx.musiccommons.transmitter.Globals;
import jatx.musiccommons.transmitter.TrackInfo;
import jatx.musiccommons.transmitter.TransmitterController;
import jatx.musiccommons.transmitter.TransmitterPlayer;
import jatx.musiccommons.transmitter.UI;
import jatx.musiccommons.util.FolderUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Main extends Application implements UI {
	private boolean isPlaying = false;
	private volatile boolean isWifiOk = false;
	
	private ListView<String> mListView;
	private Button mToogleButton;
	private Button mDownButton;
	private Button mUpButton;
	private Button mWifiButton;
	private Label mVolLabel;
	
	private Stage mStage;
	
	private List<File> mFileList;
	
	private File mCurrentMusicDir;
	private File mCurrentListDir;
	
	private MyList mItems;
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) {
		mStage = primaryStage;
		
		final Image playImg = new Image("/icons/ic_play.png");
		final Image pauseImg = new Image("/icons/ic_pause.png");
		
		TrackInfo.setDBCache(new SQLiteDBCache());
		TrackInfo.setUI(this);
		
		loadFileList(null);
		loadSettings();
		
		try {			
			Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));
			Scene scene = new Scene(root);
			
			mListView = (ListView<String>) scene.lookup("#my_list");
			
			mListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent click) {
		        	int pos = mListView.getSelectionModel().getSelectedIndex();
		        	
		        	if (click.getClickCount()==2) {
		        		System.out.println("double click on " + pos);
		        		
		        		isPlaying = true;
		        		
		        		mToogleButton.setGraphic(new ImageView(pauseImg));
		        		
		        		Globals.tp.play();
						Globals.tc.play();
		        		
		        		Globals.tp.setPosition(pos);
		        	} else if (click.getClickCount()==1) {
		        		System.out.println("single clink on " + pos);
		        	}
		        }
		    });
			
			mToogleButton = (Button) scene.lookup("#button_toogle");
			mToogleButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					isPlaying = !isPlaying;
					System.out.println("toogle: " + isPlaying);
					
					if (isPlaying) {
						mToogleButton.setGraphic(new ImageView(pauseImg));
						
						Globals.tp.play();
						Globals.tc.play();
					} else {
						mToogleButton.setGraphic(new ImageView(playImg));
						
						Globals.tp.pause();
						Globals.tc.pause();
					}
				}
			});
			
			mVolLabel = (Label) scene.lookup("#vol_label");
					
			mUpButton = (Button) scene.lookup("#button_up");
			mUpButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					Globals.volume = Globals.volume<100?Globals.volume+5:100;
					mVolLabel.setText(Globals.volume.toString()+"%");
					Globals.tc.setVolume(Globals.volume);
					saveSettings();
				}
			});
			
			mDownButton = (Button) scene.lookup("#button_down");
			mDownButton.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					Globals.volume = Globals.volume>0?Globals.volume-5:0;
					mVolLabel.setText(Globals.volume.toString()+"%");
					Globals.tc.setVolume(Globals.volume);
					saveSettings();
				}
			});
			
			mWifiButton = (Button) scene.lookup("#button_wifi");
			
			final MenuBar menuBar = (MenuBar) scene.lookup("#menu_pane_top");
			for (final Menu menu: menuBar.getMenus()) {
				for (final MenuItem item: menu.getItems()) {
					final String itemId = item.getId();
					
					item.setOnAction(new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent event) {
							menuAction(itemId);
						}
					});
				}
			}
			
			Platform.setImplicitExit(false);			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(WindowEvent event) {
			        Globals.tp.interrupt();
			        Globals.tc.setFinishFlag();
			        TrackInfo.destroy();
			        
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
	
	@Override
	public void setWifiStatus(boolean status) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				isWifiOk = status;
				System.out.println("wifi: " + isWifiOk);
				
				final Image wifiNoImg = new Image("/icons/ic_wifi_no.png");
				final Image wifiOkImg = new Image("/icons/ic_wifi_ok.png");
				
				if (isWifiOk) {
					mWifiButton.setGraphic(new ImageView(wifiOkImg));
				} else {
					mWifiButton.setGraphic(new ImageView(wifiNoImg));
				}
			}
		});
	}
	
	@Override
	public void setPosition(int position) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mListView.getSelectionModel().select(position);
				mListView.scrollTo(position);
			}
		});
	}
	
	@Override
	public void updateTrackList(List<TrackInfo> trackList, List<File> fileList) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				mItems = new MyList(trackList);
				mListView.setItems(mItems);
				Globals.tp.setFileList(fileList);
			}
		});
	}
	
	private void prepareAndStart() {
		Globals.tp = new TransmitterPlayer(mFileList, this);
	    Globals.tp.start();
	    
	    Globals.tc = new TransmitterController(this);
	    Globals.tc.start();
	    
	    mVolLabel.setText(Globals.volume.toString()+"%");
		Globals.tc.setVolume(Globals.volume);
		
		refreshList();
	}
	
	private void menuAction(String itemId) {
		if (itemId.equals("open_file_item")) {
			System.out.println("Open File");
			
			openMP3File();
		} else if (itemId.equals("open_folder_item")) {
			System.out.println("Open Folder");
			
			openDir();
		} else if (itemId.equals("remove_this_item")) {
			System.out.println("Remove Selected Tracks");
			
			removeSelectedTracks();
		} else if (itemId.equals("remove_all_item")) {
			System.out.println("Remove All Tracks");
			
			removeAllTracks();
		} else if (itemId.equals("m3u_import_item")) {
			System.out.println("Import M3U");
			
			importM3U();
		} else if (itemId.equals("m3u_export_item")) {
			System.out.println("Export M3U");
			
			exportM3U();
		}
		
	}
	
	private void importM3U() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import M3U");
		fileChooser.setInitialDirectory(mCurrentListDir);
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("M3U", "*.m3u"));
		final File selectedFile = fileChooser.showOpenDialog(mStage);
		
		mCurrentListDir = selectedFile.getParentFile();
		saveSettings();
		
		loadFileList(selectedFile.getAbsolutePath());
		refreshList();
		saveFileList(null);
	}
	
	private void exportM3U() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export M3U");
		fileChooser.setInitialDirectory(mCurrentListDir);
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("M3U", "*.m3u"));
		final File selectedFile = fileChooser.showSaveDialog(mStage);
	
		mCurrentListDir = selectedFile.getParentFile();
		saveSettings();
		
		saveFileList(selectedFile.getAbsolutePath());
	}
	
	private void openMP3File() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open MP3 File");
		fileChooser.setInitialDirectory(mCurrentMusicDir);
		fileChooser.getExtensionFilters().add(
				new ExtensionFilter("MP3 Files", "*.mp3"));
		final File selectedFile = fileChooser.showOpenDialog(mStage);
		
		if (selectedFile!=null) {
			System.out.println("file: " + selectedFile.getAbsolutePath());
			
			mFileList.add(selectedFile);
			refreshList();
			
			mCurrentMusicDir = selectedFile.getParentFile();
			saveSettings();
		} else {
			System.out.println("file: null");
		}
	}
	
	private void openDir() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Open Dir With Music");
		dirChooser.setInitialDirectory(mCurrentMusicDir);
		File selectedDir = dirChooser.showDialog(mStage);
		
		if (selectedDir!=null) {
			System.out.println("dir: " + selectedDir.getAbsolutePath());
			
			List<File> fileList = FolderUtil.findFiles(selectedDir.getAbsolutePath(), ".*mp3$");
			for (int i=0; i<fileList.size(); i++) {
				mFileList.add(fileList.get(i));
			}
			refreshList();
			
			mCurrentMusicDir = selectedDir;
			saveSettings();
		} else {
			System.out.println("dir: null");
		}
	}
	
	private void removeSelectedTracks() {
		int pos = mListView.getSelectionModel().getSelectedIndex();
		
		if (pos>=0 && pos<mFileList.size()) {
			mFileList.remove(pos);
			refreshList();
		}
		
		if (pos<mFileList.size()) {
			setPosition(pos);
		} else if (mFileList.size()>0) {
			setPosition(0);
		}
 	}
	
	private void removeAllTracks() {
		mFileList.clear();
		
		refreshList();
	}
	
	private void refreshList() {		
		saveFileList(null);
		
		TrackInfo.setFileList(mFileList);
	}
	
	private void loadFileList(String path) {
		mFileList = new ArrayList<File>();
		
		File f;
		if (path==null) {
			f = new File("current.m3u");
		} else {
			f = new File(path);
		}
		if (!f.exists()) return;
		
		try {
			Scanner sc = new Scanner(new FileInputStream(f));
			
			while(sc.hasNextLine()) {
				String trackPath = sc.nextLine();
				
				File track = new File(trackPath);
				if (track.exists()) {
					mFileList.add(track);
				}
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void saveFileList(String path) {
		File f;
		if (path==null) {
			f = new File("current.m3u");
		} else {
			f = new File(path);
		}
		
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(f));
			
			for (int i=0; i<mFileList.size(); i++) {
				pw.println(mFileList.get(i).getAbsolutePath());
			}
			
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void loadSettings() {
		mCurrentMusicDir = new File(System.getProperty("user.home"));
		mCurrentListDir = new File(System.getProperty("user.home"));
		Globals.volume = 100;
		
		File f = new File("settings.txt");
		if (!f.exists()) return;
		
		try {
			Scanner sc = new Scanner(f);
			
			while(sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				
				if (line.startsWith("CURRENT_MUSIC_DIR=")) {
					String path = line.replace("CURRENT_MUSIC_DIR=", "");
					File fmd = new File(path);
					if (fmd.exists()) mCurrentMusicDir = fmd;
				}
				
				if (line.startsWith("CURRENT_LIST_DIR=")) {
					String path = line.replace("CURRENT_LIST_DIR=", "");
					File fld = new File(path);
					if (fld.exists()) mCurrentListDir = fld;
				}
				
				if (line.startsWith("VOLUME=")) {
					String volStr = line.replace("VOLUME=", "");
					try {
						Globals.volume = Integer.parseInt(volStr);
					} catch (NumberFormatException e) {
						Globals.volume = 100;
					}
				}
			}
			
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void saveSettings() {
		File f = new File("settings.txt");
		
		try {
			PrintWriter pw = new PrintWriter(f);
			
			pw.println("CURRENT_MUSIC_DIR=" + mCurrentMusicDir.getAbsolutePath());
			pw.println("CURRENT_LIST_DIR=" + mCurrentListDir.getAbsolutePath());
			pw.println("VOLUME=" + Globals.volume);
			
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}
}
