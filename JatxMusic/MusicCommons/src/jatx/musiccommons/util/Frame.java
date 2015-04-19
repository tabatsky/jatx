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
package jatx.musiccommons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.SampleBuffer;

public class Frame {
	public static final int FRAME_HEADER_SIZE = 64;
	public static final int[] FRAME_RATES = {32000, 44100, 48000};
	
	public int size;
	public int freq;
	public int channels;
	public int position;
	public byte[] data;
	
	public static Frame fromInputStream(InputStream is) throws IOException, InterruptedException {
		int freq1 = 0;
		int freq2 = 0;
		int freq3 = 0;
		int freq4 = 0;
		
		int size1 = 0;
		int size2 = 0;
		int size3 = 0;
		int size4 = 0;
		
		int pos1 = 0;
		int pos2 = 0;
		int pos3 = 0;
		int pos4 = 0;
		
		int channels = 0;
		
		byte[] header = new byte[1];
		
		int bytesRead = 0;
		
		while (bytesRead<FRAME_HEADER_SIZE) {
			if (is.available()>0) {
				int justRead = is.read(header, 0, 1);
				
				if (justRead>0) {
					if (bytesRead==0) {
						size1 = header[0]&0xff;
					} else if (bytesRead==1) {
						size2 = header[0]&0xff;
					} else if (bytesRead==2) {
						size3 = header[0]&0xff;
					} else if (bytesRead==3) {
						size4 = header[0]&0xff;
					} else if (bytesRead==4) {
						freq1 = header[0]&0xff;
					} else if (bytesRead==5) {
						freq2 = header[0]&0xff;
					} else if (bytesRead==6) {
						freq3 = header[0]&0xff;
					} else if (bytesRead==7) {
						freq4 = header[0]&0xff;
					} else if (bytesRead==8) {
						channels = header[0]&0xff;
					} else if (bytesRead==12) {
						pos1 = header[0]&0xff;
					} else if (bytesRead==13) {
						pos2 = header[0]&0xff;
					} else if (bytesRead==14) {
						pos3 = header[0]&0xff;
					} else if (bytesRead==15) {
						pos4 = header[0]&0xff;
					}
					
					bytesRead += justRead;
				}
			} else {
				Thread.sleep(20);
			}
		}
		
		int size = (size1<<24)|(size2<<16)|(size3<<8)|size4;
		int freq = (freq1<<24)|(freq2<<16)|(freq3<<8)|freq4;
		int pos = (pos1<<24)|(pos2<<16)|(pos3<<8)|pos4;
		
		bytesRead = 0;
		
		byte[] data = new byte[size];
		
		while (bytesRead<size) {
			if (is.available()>0) {
				int justRead = is.read(data, bytesRead, size-bytesRead);
				
				if (justRead>0) {					
					bytesRead += justRead;
				}
			} else {
				Thread.sleep(20);
			}
		}
		
		Frame f = new Frame();
		
		f.size = size;
		f.freq = freq;
		f.channels = channels;
		f.position = pos;
		f.data = data;
		
		//System.out.println("frame: " + (new Date()).getTime()%100000);
		
		return f;
	}
	
	public static Frame fromSampleBuffer(SampleBuffer sBuff, int position) 
			throws WrongFrameException {
		
		Frame f = new Frame();
		f.position = position;
		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream(10240);
		
		f.freq = sBuff.getSampleFrequency();
		f.channels = sBuff.getChannelCount();
		
		short[] pcm = sBuff.getBuffer();
		
		boolean wrongRate = true;
		
		for (int rate: FRAME_RATES) {
			if (rate==f.freq) wrongRate = false;
		}
		
		if (wrongRate) {
			throw new WrongFrameException("(player) wrong frame rate: " + Integer.toString(f.freq));
		}
		
		if (f.channels==2) {
			for (int i=0; i<pcm.length/2; i++) {				
				short shrt1 = pcm[2*i];
				short shrt2 = pcm[2*i+1];
				
				outStream.write(shrt1 & 0xff);
				outStream.write((shrt1 >> 8 ) & 0xff);
				outStream.write(shrt2 & 0xff);
				outStream.write((shrt2 >> 8 ) & 0xff);				
			}
		} else if (f.channels==1) {
			throw new WrongFrameException("(player) mono sound");
		} else {
			throw new WrongFrameException("(player) " + Integer.valueOf(f.channels).toString() + " channels");
		}	
		
		f.data = outStream.toByteArray();
		f.size = f.data.length;
		
		return f;
	}
	
	public byte[] toByteArray() {
		byte freq1 = (byte)((freq>>24)&0xff);
		byte freq2 = (byte)((freq>>16)&0xff);
		byte freq3 = (byte)((freq>>8)&0xff);
		byte freq4 = (byte)((freq>>0)&0xff);
		
		byte size1 = (byte)((size>>24)&0xff);
		byte size2 = (byte)((size>>16)&0xff);
		byte size3 = (byte)((size>>8)&0xff);
		byte size4 = (byte)((size>>0)&0xff);
		
		byte pos1 = (byte)((position>>24)&0xff);
		byte pos2 = (byte)((position>>16)&0xff);
		byte pos3 = (byte)((position>>8)&0xff);
		byte pos4 = (byte)((position>>0)&0xff);
		
		byte ch = (byte) (channels&0xff);
		
		byte[] result = new byte[size + FRAME_HEADER_SIZE];
		
		for (int i=0; i<size; i++) {
			result[i+FRAME_HEADER_SIZE] = data[i];
		}
		
		for (int i=0; i<FRAME_HEADER_SIZE; i++) {
			result[i] = (byte)0x00;
		}
		
		result[0] = size1;
		result[1] = size2;
		result[2] = size3;
		result[3] = size4;
		
		result[4] = freq1;
		result[5] = freq2;
		result[6] = freq3;
		result[7] = freq4;
		
		result[8] = ch;
		
		result[12] = pos1;
		result[13] = pos2;
		result[14] = pos3;
		result[15] = pos4;
		
		//System.out.println("frame: " + (new Date()).getTime()%100000);
		
		return result;
	}
	
	public static class WrongFrameException extends Exception {
		private static final long serialVersionUID = 1768474402107432418L;
		
		WrongFrameException(String msg) {
			super(msg);
		}
	}
}
