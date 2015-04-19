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

import jatx.musiccommons.receiver.AutoConnectThread;
import jatx.musiccommons.receiver.ReceiverController;
import jatx.musiccommons.receiver.ReceiverPlayer;
import jatx.musiccommons.receiver.UI;
import jatx.musiccommons.util.Debug;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MusicReceiverActivity extends Activity implements UI {
	final static String PREFS_NAME = "MusicReceiverPrefsFile";
	
	public static final String LOG_TAG_ACTIVITY = "receiver main activity";
	
	private MusicReceiverActivity self;
	
	private ReceiverPlayer rp;
	private ReceiverController rc;
	private AutoConnectThread act;
	
	private boolean isRunning;
	
	private boolean mAutoConnect;
	private String mHost;
	
	private EditText mHostField;
	private Button mToogleButton;
	private CheckBox mAutoCheckBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_receiver);
		
		self = this;
		
		Debug.setCustomExceptionHandler(getExternalFilesDir(null));
				
		isRunning = false;
		
		mHostField = (EditText) findViewById(R.id.hostname);
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
				mAutoConnect = mAutoCheckBox.isChecked();
				saveSettings();
			}
		});
		
		prepareAndStart();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(LOG_TAG_ACTIVITY, "on destroy");
		
		if (act!=null) {
			act.interrupt();
		}
		
		stopJob();
		
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		 Log.i(LOG_TAG_ACTIVITY, "back pressed");
		 finish();
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
				mHost = mHostField.getText().toString();
				saveSettings();
				rp = new ReceiverPlayer(mHost, self, new AndroidSoundOut());
				rc = new ReceiverController(mHost, self);
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
	
	private void prepareAndStart() {
		loadSettings();
		
		mHostField.setText(mHost);
		mAutoCheckBox.setChecked(mAutoConnect);
		
		act = new AutoConnectThread(this);
		act.start();
	}
	
	private void loadSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mHost = settings.getString("IP", "127.0.0.1");
		mAutoConnect = settings.getBoolean("autoConnect", false);
	}
	
	private void saveSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putBoolean("autoConnect", mAutoConnect);
		editor.putString("IP", mHost);
		
		editor.commit();
	}

	@Override
	public boolean isAutoConnect() {
		return mAutoConnect;
	}
}
