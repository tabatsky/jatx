package com.example.androidaudioreceiver;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	ReceiverPlayer rp;
	boolean isRunning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		isRunning = false;
		
		final EditText hostname = (EditText) findViewById(R.id.hostname);
		final Button toogle = (Button)findViewById(R.id.toogle);
		
		toogle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isRunning) {
					isRunning = true;
					toogle.setText("Stop");
					rp = new ReceiverPlayer(hostname.getText().toString());
					rp.start();
				} else {
					toogle.setText("Start");
					isRunning = false;
					rp.setFinishFlag();
				}
			}
		});
	}
	
	class ReceiverPlayer extends Thread {
		volatile boolean finishFlag;
		String host;
		
		public ReceiverPlayer(String hostname) {
			host = hostname;
			finishFlag = false;
		}
		
		public void setFinishFlag() {
			finishFlag = true;
		}
		
		public void run() {
			try {
				InetAddress ipAddr = InetAddress.getByName(host);
			
				Socket s = new Socket(ipAddr, 7373);
				InputStream is = s.getInputStream();
				
				int bufferSize = AudioTrack.getMinBufferSize(16000, 
						AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
				
				int numBytesRead;
				byte[] data = new byte[bufferSize];
				
				AudioTrack aTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
						16000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
						bufferSize, AudioTrack.MODE_STREAM);
				aTrack.play();
				
				while (!finishFlag) {
					numBytesRead = is.read(data, 0, bufferSize);
					aTrack.write(data, 0, numBytesRead);
				}
				
				aTrack.stop();
				s.close();
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Log.e("Error",sw.toString());
			}
		}
	}
}
