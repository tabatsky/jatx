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
package jatx.musiccommons.receiver;

public interface SoundOut {
	public void renew(int frameRate, int channels);
	public void setVolume(int volume);
	public void write(byte[] data, int offset, int size);
	public void destroy();
	public void play();
	public void pause();
}

