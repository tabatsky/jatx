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
package jatx.musiccommons.transmitter;

import jatx.musiccommons.util.Frame;
import jatx.musiccommons.util.Frame.WrongFrameException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class JLayerMp3Decoder extends Mp3Decoder {
	private Decoder mDecoder = null;
	private Bitstream mBitStream = null;
	private int mPosition = 0;
	
	private float msFrame = 0f;
	
	@Override
	public void setPosition(int position) {
		mPosition = position;
	}
	
	@Override
	public void setPath(String path) throws Mp3DecoderException {
		File f = new File(path);
		setFile(f);
	}
	
	@Override
	public synchronized void setFile(File f) throws Mp3DecoderException {
		if (f==null||!f.exists()) {
			throw new Mp3DecoderException("File Read Error");
		}
		
		mDecoder = new Decoder();
		
		if (mBitStream!=null) {
			try {
				mBitStream.close();
			} catch (BitstreamException e) {
				throw new Mp3DecoderException(e);
			}
			mBitStream = null;
		}
	
		try {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(f), 32 * 1024);
			mBitStream = new Bitstream(inputStream);
		} catch (FileNotFoundException e) {
			throw new Mp3DecoderException(e);
		}
		
		currentMs = 0;
		
		resetTimeFlag = true;
	}
	
	@Override
	public synchronized Frame readFrame() throws Mp3DecoderException, WrongFrameException, TrackFinishException {
		Frame f = null;
		
		try {			
			Header frameHeader = null;
			if (mBitStream!=null) {
				frameHeader = mBitStream.readFrame();
			} else {
				throw new Mp3DecoderException("bitstream: null");
			}
				
			if (frameHeader==null) {
				throw new TrackFinishException();
			}
			
			msFrame = frameHeader.ms_per_frame();
			msRead += msFrame;
			msTotal += msFrame;
				
			SampleBuffer output = (SampleBuffer) mDecoder.decodeFrame(frameHeader, mBitStream);
			
			f = Frame.fromSampleBuffer(output, mPosition);
				
			mBitStream.closeFrame();
		} catch (WrongFrameException e) {
			throw e;
		} catch (BitstreamException e) {
			throw new Mp3DecoderException(e);
		} catch (DecoderException e) {
			throw new Mp3DecoderException(e);
		} finally {}
		
		return f;
	}
}

