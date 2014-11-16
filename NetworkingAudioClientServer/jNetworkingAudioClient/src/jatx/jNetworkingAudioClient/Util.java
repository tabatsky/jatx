package jatx.jNetworkingAudioClient;

import javax.sound.sampled.AudioFormat;

/**
 * 
 * @author jatx
 *
 * Accessorial class
 * Keeps some constants
 */
public class Util {
	public static final int CHUNK_SIZE = 2048;
	public static final boolean DEBUG = false;
	public static final String EMPTY_CMD = "foo11223377bar";
	
	static AudioFormat getAudioFormat() {
		float sampleRate = 8000.0f;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
		
		return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
	}
}
