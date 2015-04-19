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
package jatx.musictransmitter.android;

import jatx.musiccommons.transmitter.TrackInfo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TrackListAdapter extends BaseAdapter {
	private List<TrackInfo> mTrackList;
	private MusicTransmitterActivity mActivity;
	private int mCurrentPosition = -1;
	
	TrackListAdapter(MusicTransmitterActivity activity) {
		mActivity = activity;
		mTrackList = new ArrayList<TrackInfo>();
	}
	
	public void setTrackList(List<TrackInfo> trackList) {
		mTrackList = new ArrayList<TrackInfo>(trackList);
		if (mCurrentPosition>=mTrackList.size()) mCurrentPosition = -1;
		notifyDataSetChanged();
	}
	
	public void setCurrentPosition(int position) {
		mCurrentPosition = position;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTrackList.size();
	}

	@Override
	public TrackInfo getItem(int position) {
		return mTrackList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolder;
		
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.entry_song, null);
            
            viewHolder = new ViewHolderItem();
            
            viewHolder.wholeLayout = (LinearLayout) convertView.findViewById(R.id.text_whole_layout);
            viewHolder.titleView = (TextView) convertView.findViewById(R.id.text_title);
            viewHolder.metaView = (TextView) convertView.findViewById(R.id.text_meta);
            
            convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolderItem) convertView.getTag();			
		}
		
		TrackInfo info = mTrackList.get(position);
		
		String meta = info.artist;
		
		if (!meta.equals("")) {
			meta += " | " + info.length;
		} else {
			meta = info.length;
		}
		
		viewHolder.titleView.setText(info.title);
		viewHolder.metaView.setText(meta);
		
		if (position==mCurrentPosition) {
			viewHolder.wholeLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.black));
			viewHolder.titleView.setTextColor(mActivity.getResources().getColor(R.color.white));
			viewHolder.metaView.setTextColor(mActivity.getResources().getColor(R.color.white));
		} else {
			viewHolder.wholeLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
			viewHolder.titleView.setTextColor(mActivity.getResources().getColor(R.color.black));
			viewHolder.metaView.setTextColor(mActivity.getResources().getColor(R.color.black));
		}
		
		return convertView;
	}

	private static class ViewHolderItem {
		LinearLayout wholeLayout;
		TextView titleView;
		TextView metaView;
	}
}
