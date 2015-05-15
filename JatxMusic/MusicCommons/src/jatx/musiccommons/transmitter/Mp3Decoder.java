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

import java.io.File;

public abstract class Mp3Decoder {
	public boolean resetTimeFlag = false;
	public boolean disconnectResetTimeFlag = false;
	
	public float msRead = 0f;
	public float msTotal = 0f;
	
	public float currentMs = 0f;
	public int trackLengthSec = 0;
	
	public abstract void setPosition(int position);
	
	public abstract void setPath(String path) throws Mp3DecoderException;
	
	public abstract void setFile(File f) throws Mp3DecoderException;
	
	public abstract Frame readFrame() throws Mp3DecoderException, WrongFrameException, TrackFinishException;
	
	public static class Mp3DecoderException extends Exception {
		private static final long serialVersionUID = -5257283039222447187L;

		Mp3DecoderException(Exception cause) {
			super(cause);
		}
		
		Mp3DecoderException(String msg) {
			super(msg);
		}
	}
	
	public static class TrackFinishException extends Exception {
		private static final long serialVersionUID = 6563356718258555416L;
	}
}
