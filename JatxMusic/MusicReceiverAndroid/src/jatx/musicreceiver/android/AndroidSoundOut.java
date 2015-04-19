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

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import jatx.musiccommons.receiver.SoundOut;

public class AndroidSoundOut implements SoundOut {
	private AudioTrack aTrack = null;
	
	@Override
	public synchronized void renew(int frameRate, int channels) {
		if (aTrack!=null) {
			aTrack.stop();
			aTrack.release();
		}
		
		int chFormat = 0;
		
		if (channels==1) {
			chFormat = AudioFormat.CHANNEL_OUT_MONO;
			//chFormat = AudioFormat.CHANNEL_OUT_STEREO;
		} else if (channels==2) {
			chFormat = AudioFormat.CHANNEL_OUT_STEREO;
		}
		
		int bufferSize = AudioTrack.getMinBufferSize(frameRate, 
				chFormat, AudioFormat.ENCODING_PCM_16BIT);
		aTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 
				frameRate, chFormat, AudioFormat.ENCODING_PCM_16BIT,
				bufferSize, AudioTrack.MODE_STREAM);
		aTrack.play();
	}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void setVolume(int volume) {		
		if (aTrack!=null) {
			aTrack.setStereoVolume(volume*0.01f, volume*0.01f);
		}
	}

	@Override
	public synchronized void write(byte[] data, int offset, int size) {
		if (aTrack!=null) {
			aTrack.write(data, offset, size);
		}
	}

	@Override
	public synchronized void destroy() {
		if (aTrack!=null) {
			aTrack.stop();
			aTrack.release();
		}
	}

	@Override
	public synchronized void play() {
		if (aTrack!=null) {
			aTrack.play();
		}
	}

	@Override
	public synchronized void pause() {		
		if (aTrack!=null) {
			aTrack.pause();
		}
	}
	
}
