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

public class Globals {
	public static volatile TransmitterController tc;
	public static volatile TransmitterPlayer tp;
	public static volatile TimeUpdater tu;

	public static volatile Integer volume = 100;
	
	public static final int SO_TIMEOUT = 1000;
}
