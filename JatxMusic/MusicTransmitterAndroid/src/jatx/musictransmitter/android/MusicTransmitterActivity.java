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
package jatx.musictransmitter.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jatx.musiccommons.transmitter.Globals;
import jatx.musiccommons.transmitter.JLayerMp3Decoder;
import jatx.musiccommons.transmitter.Mp3Decoder;
import jatx.musiccommons.transmitter.TimeUpdater;
import jatx.musiccommons.transmitter.TrackInfo;
import jatx.musiccommons.transmitter.TransmitterController;
import jatx.musiccommons.transmitter.TransmitterPlayer;
import jatx.musiccommons.transmitter.UI;
import jatx.musiccommons.util.Debug;
import jatx.musiccommons.util.FolderUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import ar.com.daidalos.afiledialog.FileChooserActivity;

public class MusicTransmitterActivity extends ActionBarActivity implements UI {
	private static final String PREFS_NAME = "MusicTransmitterPreferences";
	private static final String LOG_TAG_ACTIVITY = "transmitter main activity";
	
	private static final int REQUEST_OPEN_FILE = 501;
	private static final int REQUEST_OPEN_DIR = 502;
	private static final int REQUEST_EXPORT_LIST = 503;
	private static final int REQUEST_IMPORT_LIST = 504;
	public static final int REQUEST_EDIT_TRACK = 505;
	
	List<File> mFileList;
	private TrackListAdapter mAdapter;
	
	private File mCurrentMusicDir;
	private File mCurrentListDir;
	
	private ListView mListView;
	private ImageButton mPlayButton;
	private ImageButton mPauseButton;
	private ImageButton mRevButton;
	private ImageButton mFwdButton;
	private ImageButton mVolDownButton;
	private ImageButton mVolUpButton;
	private TextView mVolLabel;
	private ImageView mWifiOkIcon;
	private ImageView mWifiNoIcon;
	private ProgressBar mProgressBar;
	
	private volatile WifiManager mWifiManager;
	private volatile WifiLock mLock;
	
	private int mCurrentPosition = -1;
	
	private MusicTransmitterActivity self;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_transmitter);
		
		Debug.setCustomExceptionHandler(getExternalFilesDir(null));
		
		self = this;
		
		Log.i(LOG_TAG_ACTIVITY, "on create");
		
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		TrackInfo.setDBCache(new MusicInfoDBHelper(this));
		TrackInfo.setUI(this);
		
		loadFileList(null);
		loadSettings();
		
		mAdapter = new TrackListAdapter(this);
		
	    mListView = (ListView)findViewById(R.id.list);
	    mPlayButton = (ImageButton)findViewById(R.id.play);
	    mPauseButton = (ImageButton)findViewById(R.id.pause);
	    mRevButton = (ImageButton)findViewById(R.id.rev);
	    mFwdButton = (ImageButton)findViewById(R.id.fwd);
	    mVolDownButton = (ImageButton) findViewById(R.id.vol_down);
	    mVolUpButton = (ImageButton) findViewById(R.id.vol_up);
	    mVolLabel = (TextView) findViewById(R.id.vol_label);
	    mWifiOkIcon = (ImageView) findViewById(R.id.wifi_ok);
	    mWifiNoIcon = (ImageView) findViewById(R.id.wifi_no);
	    mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
	    
	    mListView.setAdapter(mAdapter);
	    
	    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPlayButton.performClick();
				mCurrentPosition = position;
				Globals.tp.setPosition(position);
			}
		});
	    
	    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//removeTrack(position);
				
				TrackLongTapDialog dialog =
						TrackLongTapDialog.newInstance(self, position);
				dialog.show(getSupportFragmentManager(), "dialog");
				
				return true;
			}
		});
	    
	    mPlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFileList.size()==0) {
					return;
				} else if (mCurrentPosition==-1) {
					Globals.tp.setPosition(0);
	        		setPosition(0);
				}
				
				mPlayButton.setVisibility(View.GONE);
				mPauseButton.setVisibility(View.VISIBLE);
				Globals.tp.play();
				Globals.tc.play();
			}
		});
	    
	    mPauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPauseButton.setVisibility(View.GONE);
				mPlayButton.setVisibility(View.VISIBLE);
				Globals.tp.pause();
				Globals.tc.pause();
			}
		});
	    
	    mVolDownButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Globals.volume = Globals.volume>0?Globals.volume-5:0;
				mVolLabel.setText(Globals.volume.toString()+"%");
				Globals.tc.setVolume(Globals.volume);
				saveSettings();
			}
		});
	    
	    mFwdButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFileList.size()==0) return;
				
				final int newPosition = (mCurrentPosition+1)%mFileList.size();
				
        		mPlayButton.performClick();
				
        		Globals.tp.setPosition(newPosition);
			}
		});
	    
	    mRevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFileList.size()==0) return;
				
				final int newPosition;
				
				if (mCurrentPosition>0) {
					newPosition = mCurrentPosition - 1;
				} else {
					newPosition = mFileList.size() - 1;
				}
				
				mPlayButton.performClick();
				
        		Globals.tp.play();
				Globals.tc.play();
        		
        		Globals.tp.setPosition(newPosition);
			}
		});
	    
	    mVolUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Globals.volume = Globals.volume<100?Globals.volume+5:100;
				mVolLabel.setText(Globals.volume.toString()+"%");
				Globals.tc.setVolume(Globals.volume);
				saveSettings();
			}
		});
	    
	    mProgressBar.setMax(1000);
	    mProgressBar.setProgress(0);

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mLock = mWifiManager.createWifiLock("music-transmitter-wifi-lock");
		mLock.setReferenceCounted(false);
		mLock.acquire();
		
	    prepareAndStart();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.i(LOG_TAG_ACTIVITY, "on start");
	}
	
	@Override
	protected void onStop() {
		Log.i(LOG_TAG_ACTIVITY, "on stop");
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(LOG_TAG_ACTIVITY, "on destroy");
		
		Globals.tu.interrupt();
		Globals.tp.interrupt();
		Globals.tc.setFinishFlag();
		TrackInfo.destroy();
		
		mLock.release();
		
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_music_transmitter, menu);
	    return super.onCreateOptionsMenu(menu);
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		
		switch (item.getItemId()) {
	        case R.id.item_menu_add_track:
	        	openMP3File(null);
	        	return true;
	        
	        case R.id.item_menu_add_folder:
		        openDir(null);
	        	return true;
	        	
	        case R.id.item_menu_remove_track:
		        Toast.makeText(this, getString(R.string.toast_long_tap), Toast.LENGTH_LONG).show();
	        	return true;
	        
	        case R.id.item_menu_remove_all:
		        removeAllTracks();
	        	return true;
	        	
	        case R.id.item_menu_export_m3u8:
	        	exportM3U8(null);
	        	return true;
	        	
	        case R.id.item_menu_import_m3u8:	
	        	importM3U8(null);
	        	return true;
	        	
	        case R.id.item_review_app:
	        	try {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("market://details?id=jatx.musictransmitter.android")));
				} catch (android.content.ActivityNotFoundException anfe) {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://play.google.com/store/apps/details?id=jatx.musictransmitter.android")));
				}
	        	return true;
	        
	        case R.id.item_receiver_android:
	        	try {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("market://details?id=jatx.musicreceiver.android")));
				} catch (android.content.ActivityNotFoundException anfe) {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://play.google.com/store/apps/details?id=jatx.musicreceiver.android")));
				}
	        	return true;
	        	
	        /*
	        case R.id.item_javafx_version:
	        	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://yadi.sk/d/T2QKUqOGgxKR8")));
	        	return true;
	        */
	        	
	        case R.id.item_receiver_javafx:
	        	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://yadi.sk/d/mUHvCxcchFZ7s")));
	        	return true;
	        
	        case R.id.item_transmitter_javafx:
	        	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://yadi.sk/d/9vBoZFZVhFZ7D")));
	        	return true;
	        
	        case R.id.item_source_code:
	        	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://github.com/tabatsky/jatx/tree/master/JatxMusic")));
	        	return true;
	        	
	        case R.id.item_dev_site:
	        	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("http://tabatsky.ru")));
	        	return true;
	        
	        default:
	        	return false;
	    }
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode!=RESULT_OK) return;
		
		if (requestCode==REQUEST_EDIT_TRACK&&resultCode==RESULT_OK) {
			System.out.println("result edit track");
			
			refreshList();
			return;
		}
		
		String filePath = "";
        
        Bundle bundle = data.getExtras();
        if(bundle != null) {
            if(bundle.containsKey(FileChooserActivity.OUTPUT_NEW_FILE_NAME)) {
            	File folder = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                String name = bundle.getString(FileChooserActivity.OUTPUT_NEW_FILE_NAME);
                filePath = folder.getAbsolutePath() + "/" + name;
            } else {
                File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                filePath = file.getAbsolutePath();
            }
        }
        
        File f = new File(filePath);
		
		if (requestCode==REQUEST_OPEN_FILE&&resultCode==RESULT_OK) {
			openMP3File(f);
		} else if (requestCode==REQUEST_OPEN_DIR&&resultCode==RESULT_OK) {
			openDir(f);
		} else if (requestCode==REQUEST_EXPORT_LIST&&resultCode==RESULT_OK) {			
			exportM3U8(f);
		} else if (requestCode==REQUEST_IMPORT_LIST&&resultCode==RESULT_OK) {
			importM3U8(f);
		} 
		
	}
	
	@Override
	public void onBackPressed() {
		 Log.i(LOG_TAG_ACTIVITY, "back pressed");
		 finish();
	}
	
	@Override
	public void setWifiStatus(final boolean status) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if (status) {
					mWifiOkIcon.setVisibility(View.VISIBLE);
					mWifiNoIcon.setVisibility(View.GONE);
				} else {
					mWifiNoIcon.setVisibility(View.VISIBLE);
					mWifiOkIcon.setVisibility(View.GONE);
				}
			}
		});
	}
	
	@Override
	public void setPosition(final int position) {
		mCurrentPosition = position;
		
		if (position<0||position>=mFileList.size()) {
			return;
		}
		
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mAdapter.setCurrentPosition(position);
				mListView.setSelection(position);
			}
		});
	}
	
	@Override
	public void updateTrackList(final List<TrackInfo> trackList, final List<File> fileList) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mAdapter.setTrackList(trackList);
				setPosition(mCurrentPosition);
				Globals.tp.setFileList(fileList);
			}
		});
	}
	
	@Override
	public void setCurrentTime(final float currentMs, final float trackLengthMs) {
		if (trackLengthMs<=0) return;
		
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				int progress = (int)((currentMs*1000)/trackLengthMs);
				mProgressBar.setProgress(progress);
			}
		});
	}
	
	@Override
	public void forcePause() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				mPauseButton.setVisibility(View.GONE);
				mPlayButton.setVisibility(View.VISIBLE);
			}
		});
	}

	private void prepareAndStart() {	
		Mp3Decoder decoder = new JLayerMp3Decoder();
		
		Globals.tu = new TimeUpdater(this, decoder);
		Globals.tu.start();
		
		Globals.tp = new TransmitterPlayer(mFileList, this, decoder);
	    Globals.tp.start();
	    
	    Globals.tc = new TransmitterController(this);
	    Globals.tc.start();
	    
	    mVolLabel.setText(Globals.volume.toString()+"%");
		Globals.tc.setVolume(Globals.volume);
		
		refreshList();
	}
	
	private void refreshList() {
		saveFileList(null);
		
		TrackInfo.setFileList(mFileList);
	}
	
	private void loadSettings() {
		mCurrentListDir = Environment.getExternalStorageDirectory();
		mCurrentMusicDir = Environment.getExternalStorageDirectory();
		
		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
		final String listDirPath = sp.getString("listDirPath", mCurrentListDir.getAbsolutePath());
		final String musicDirPath = sp.getString("musicDirPath", mCurrentMusicDir.getAbsolutePath());
		Globals.volume = sp.getInt("volume", 100);
		
		File tmp;
		
		tmp = new File(listDirPath);
		if (tmp.exists()) mCurrentListDir = tmp;
		tmp = new File(musicDirPath);
		if (tmp.exists()) mCurrentMusicDir = tmp;
	}
	
	private void saveSettings() {
		SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("listDirPath", mCurrentListDir.getAbsolutePath());
		editor.putString("musicDirPath", mCurrentMusicDir.getAbsolutePath());
		editor.putInt("volume", Globals.volume);
		editor.commit();
	}
	
	private void openMP3File(File selectedFile) {
		if (selectedFile==null) {
			Intent intent = new Intent(this, FileChooserActivity.class);
		    intent.putExtra(FileChooserActivity.INPUT_START_FOLDER, mCurrentMusicDir.getAbsolutePath());
		    intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, false);
		    intent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER, ".*\\.mp3");
		    startActivityForResult(intent, REQUEST_OPEN_FILE);
		} else {
			Log.i(LOG_TAG_ACTIVITY, "file: " + selectedFile.getAbsolutePath());
			
			mFileList.add(selectedFile);
			refreshList();
			
			mCurrentMusicDir = selectedFile.getParentFile();
			saveSettings();
		}
	}
	
	private void openDir(File selectedDir) {
		if (selectedDir==null) {
			Intent intent = new Intent(this, FileChooserActivity.class);
		    intent.putExtra(FileChooserActivity.INPUT_START_FOLDER, mCurrentMusicDir.getAbsolutePath());
		    intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
		    startActivityForResult(intent, REQUEST_OPEN_DIR);
		} else {
			Log.i(LOG_TAG_ACTIVITY, "dir: " + selectedDir.getAbsolutePath());
			
			List<File> fileList = FolderUtil.findFiles(selectedDir.getAbsolutePath(), ".*\\.mp3$");
			for (int i=0; i<fileList.size(); i++) {
				mFileList.add(fileList.get(i));
			}
			refreshList();
			
			mCurrentMusicDir = selectedDir;
			saveSettings();
		}
	}
	
	void removeTrack(final int position) {
		/*
		 * final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.string_remove));
        dialog.setMessage(getString(R.string.question_remove_track));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), 
        		new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            	dialog.dismiss();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), 
        		new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            	dialog.dismiss();
            }
        });
        dialog.show();
        */
		
		mFileList.remove(position);
		if (position<mCurrentPosition) {
			mCurrentPosition -=1 ;
		} else if (position==mCurrentPosition) {
			mCurrentPosition = -1;
		}
		refreshList();
	}
	
	private void removeAllTracks() {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.string_remove_all));
        dialog.setMessage(getString(R.string.question_remove_all));
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), 
        		new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            	dialog.dismiss();
            	
        		mFileList.clear();
        		mCurrentPosition = -1;
        		refreshList();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), 
        		new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            	dialog.dismiss();
            }
        });
        dialog.show();
	}
	
	private void exportM3U8(File selectedFile) {
		if (selectedFile==null) {
			Intent intent = new Intent(this, FileChooserActivity.class);
		    intent.putExtra(FileChooserActivity.INPUT_START_FOLDER, mCurrentListDir.getAbsolutePath());
		    intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, false);
		    intent.putExtra(FileChooserActivity.INPUT_CAN_CREATE_FILES, true);
		    startActivityForResult(intent, REQUEST_EXPORT_LIST);
		} else {
			String fileName = selectedFile.getName();
			if (!fileName.endsWith(".m3u8")) {
				Toast.makeText(this, getString(R.string.toast_m3u8_ext), Toast.LENGTH_LONG).show();
				return;
			}
			
			mCurrentListDir = selectedFile.getParentFile();
			saveSettings();
			
			saveFileList(selectedFile.getAbsolutePath());
		}
	}
	
	private void importM3U8(File selectedFile) {
		if (selectedFile==null) {
			Intent intent = new Intent(this, FileChooserActivity.class);
		    intent.putExtra(FileChooserActivity.INPUT_START_FOLDER, mCurrentListDir.getAbsolutePath());
		    intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, false);
		    intent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER, ".*\\.m3u8");
		    startActivityForResult(intent, REQUEST_IMPORT_LIST);
		} else {
			mCurrentListDir = selectedFile.getParentFile();
			saveSettings();
			
			loadFileList(selectedFile.getAbsolutePath());
			refreshList();
		}
	}
	
	private void loadFileList(String path) {
		mFileList = new ArrayList<File>();
		
		File f;
		if (path==null) {
			File appDir = getExternalFilesDir(null);
			if (appDir==null) {
				appDir = getFilesDir();
			}
			if (appDir==null) {
				appDir = Environment.getExternalStorageDirectory();
			}
			if (appDir==null) {
				return;
			}
			
			path = appDir.getAbsolutePath() + File.separator + "current.m3u8";
			f = new File(path);
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
			File appDir = getExternalFilesDir(null);
			if (appDir==null) {
				appDir = getFilesDir();
			}
			if (appDir==null) {
				appDir = Environment.getExternalStorageDirectory();
			}
			if (appDir==null) {
				return;
			}
			
			path = appDir.getAbsolutePath() + File.separator + "current.m3u8";
			f = new File(path);
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
}
