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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import jatx.musiccommons.receiver.SoundOut;

public class DesktopSoundOut implements SoundOut {
	private SourceDataLine mSpeakers = null;

	@Override
	public synchronized void renew(int frameRate, int channels) {
		if (mSpeakers!=null) {
			mSpeakers.stop();
			mSpeakers.close();
			mSpeakers = null;
		}
		
		AudioFormat af = new AudioFormat(frameRate, 16, 2, true, false);
		DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, af);
		try {
			mSpeakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	        mSpeakers.open(af);
	        mSpeakers.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void setVolume(int volume) {
		if (mSpeakers!=null && mSpeakers.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl volumeControl = (FloatControl)mSpeakers.getControl(FloatControl.Type.MASTER_GAIN);
			
			float volf = volume/10.11f + 0.101f;
			float vollog = (float)Math.log10(volf);
			
			float min = volumeControl.getMinimum();
			float max = volumeControl.getMaximum();
			
			float mean = (max + min)/2;
			float disp = (max - min)/2;
			
			float value = mean + vollog*disp;
			
			volumeControl.setValue(value);
			
			System.out.println("(sound out) set volume: " + value);
		} else {
			System.out.println("(sound control) not supported");			
		}
	}

	@Override
	public synchronized void write(byte[] data, int offset, int size) {
		if (mSpeakers!=null) {
			mSpeakers.write(data, offset, size);
		}
		
	}

	@Override
	public synchronized void destroy() {
		if (mSpeakers!=null) {
			mSpeakers.stop();
			mSpeakers.close();
			mSpeakers = null;
		}
	}

	@Override
	public synchronized void play() {
		if (mSpeakers!=null) {
			mSpeakers.start();
		}
	}

	@Override
	public synchronized void pause() {
		if (mSpeakers!=null) {
			mSpeakers.stop();
		}
	}

}
