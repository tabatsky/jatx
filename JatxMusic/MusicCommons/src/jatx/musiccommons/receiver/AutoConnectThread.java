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

import jatx.musiccommons.receiver.UI;

import java.lang.ref.WeakReference;

public class AutoConnectThread extends Thread {	
	WeakReference<UI> ref;
	
	public AutoConnectThread(UI ui) {
		ref = new WeakReference<UI>(ui);
	}
	
	public void run() {
		try {
			while(true) {
				final UI ui = ref.get();
				
				if (ui!=null && ui.isAutoConnect()) {
					ui.startJob();
				}
				
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			System.out.println("(auto connect thread) interrupted");
		}
	}
}
