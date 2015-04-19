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
package jatx.musictransmitter.fx;

import jatx.musiccommons.transmitter.TrackInfo;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableListBase;

public class MyList extends ObservableListBase<String> {
	private List<TrackInfo> mTrackList;
	
	MyList(List<TrackInfo> trackList) {
		mTrackList = new ArrayList<TrackInfo>(trackList);
	}
	
	public void setTrackList(List<TrackInfo> trackList) {
		mTrackList = new ArrayList<TrackInfo>(trackList);
	}

	@Override
	public String get(int pos) {
		final TrackInfo info = mTrackList.get(pos);
		final String result = Integer.toString(pos+1) + ". " + info.toString(); 
		return result;
	}

	@Override
	public int size() {
		return mTrackList.size();
	}

}
