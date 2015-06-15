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
package jatx.musicreceiver.android;

import java.util.ArrayList;
import java.util.List;

import jatx.musiccommons.receiver.AutoConnectThread;
import jatx.musiccommons.receiver.ReceiverController;
import jatx.musiccommons.receiver.ReceiverPlayer;
import jatx.musiccommons.receiver.UI;
import jatx.musiccommons.util.Debug;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MusicReceiverActivity extends ActionBarActivity implements UI {
	final static String PREFS_NAME = "MusicReceiverPrefsFile";

	final static String DELIM = "#13731#";
	
	public static final String LOG_TAG_ACTIVITY = "receiver main activity";
	
	private MusicReceiverActivity self;
	
	private ReceiverPlayer rp;
	private ReceiverController rc;
	private AutoConnectThread act;
	
	private boolean isRunning;
	
	public boolean autoConnect;
	public String host;
	public List<String> allHosts;
	int hostIndex;
	
	private EditText hostField;
	private Button mToogleButton;
	private CheckBox mAutoCheckBox;
	
	private volatile WifiManager mWifiManager;
	private volatile WifiLock mLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_receiver);
		
		self = this;
		
		Debug.setCustomExceptionHandler(getExternalFilesDir(null));
				
		isRunning = false;
		
		hostField = (EditText) findViewById(R.id.hostname);
		mToogleButton = (Button)findViewById(R.id.toogle);
		mAutoCheckBox = (CheckBox)findViewById(R.id.auto_connect);
		
		mToogleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isRunning) {
					startJob();
				} else {
					stopJob();
				}
			}
		});
		
		mAutoCheckBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				autoConnect = mAutoCheckBox.isChecked();
				saveSettings();
			}
		});
		
		//prepareAndStart();
		
		SelectHostDialog dialog = SelectHostDialog.newInstance(this);
		dialog.show(getSupportFragmentManager(), "select-host-dialog");
		
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mLock = mWifiManager.createWifiLock("music-receiver-wifi-lock");
		mLock.setReferenceCounted(false);
		mLock.acquire();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(LOG_TAG_ACTIVITY, "on destroy");
		
		if (act!=null) {
			act.interrupt();
		}
		
		mLock.release();
		
		stopJob();
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		 Log.i(LOG_TAG_ACTIVITY, "back pressed");
		 finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_music_receiver, menu);
	    return super.onCreateOptionsMenu(menu);
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		
		switch (item.getItemId()) {
	        case R.id.item_review_app:
	        	try {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("market://details?id=jatx.musicreceiver.android")));
				} catch (android.content.ActivityNotFoundException anfe) {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("https://play.google.com/store/apps/details?id=jatx.musicreceiver.android")));
				}
	        	return true;
	        
	        case R.id.item_transmitter_android:
	        	try {
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
			    		Uri.parse("market://details?id=jatx.musictransmitter.android")));
				} catch (android.content.ActivityNotFoundException anfe) {
			    	startActivity(new Intent(Intent.ACTION_VIEW,
			    			Uri.parse("https://play.google.com/store/apps/details?id=jatx.musictransmitter.android")));
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
	public void startJob() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if (isRunning) return;
				isRunning = true;
				Log.i(LOG_TAG_ACTIVITY, "start job");		
				mToogleButton.setText(getString(R.string.string_stop));
				host = hostField.getText().toString();
				saveSettings();
				rp = new ReceiverPlayer(host, self, new AndroidSoundOut());
				rc = new ReceiverController(host, self);
				rp.start();
				rc.start();
			}
		});
	}
	
	@Override
	public void stopJob() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if (!isRunning) return;
				isRunning = false;
				rp.setFinishFlag();
				rc.setFinishFlag();
				mToogleButton.setText(getString(R.string.string_start));
				Log.i(LOG_TAG_ACTIVITY, "stop job");
				Toast.makeText(self, getString(R.string.toast_disconnect), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void play() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				rp.play();
			}
		});
	}

	@Override
	public void pause() {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				rp.pause();
			}
		});
	}

	@Override
	public void setVolume(final int vol) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				rp.setVolume(vol);
			}
		});		
	}
	
	public void prepareAndStart() {
		//loadSettings();
		
		hostField.setText(host);
		mAutoCheckBox.setChecked(autoConnect);
		
		act = new AutoConnectThread(this);
		act.start();
	}
	
	public void loadSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	
		String allHostsStr = settings.getString("all_IP", "New Transmitter" + DELIM + "127.0.0.1");
		String[] allHostsArr = allHostsStr.split(DELIM);
		
		allHosts = new ArrayList<String>();
		for (int i=0; i<allHostsArr.length; i++) {
			allHosts.add(allHostsArr[i]);
		}
		
		host = settings.getString("IP", "127.0.0.1");
		
		hostIndex = allHosts.indexOf(host);
		
		Log.i("all hosts", allHostsStr);
		Log.i("host index", Integer.toString(hostIndex));
		
		autoConnect = settings.getBoolean("autoConnect", false);
	}
	
	public void saveSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putBoolean("autoConnect", autoConnect);
		editor.putString("IP", host);
		
		StringBuilder allHostsBuilder = new StringBuilder();
		allHostsBuilder.append(allHosts.get(0));
		for (int i=1; i<allHosts.size(); i++) {
			allHostsBuilder.append(DELIM);
			allHostsBuilder.append(allHosts.get(i));
		}
		editor.putString("all_IP", allHostsBuilder.toString());
		
		editor.commit();
	}

	@Override
	public boolean isAutoConnect() {
		return autoConnect;
	}
}
