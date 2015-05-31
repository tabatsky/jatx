package jatx.musictransmitter.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TrackLongTapDialog extends DialogFragment {
	private MusicTransmitterActivity mActivity;
	private int mTrackNumber;
	
	public static TrackLongTapDialog newInstance(MusicTransmitterActivity activity, int trackNumber) {
		TrackLongTapDialog dialog = new TrackLongTapDialog();
		
		dialog.mActivity = activity;
		dialog.mTrackNumber = trackNumber;
		
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    // Pick a style based on the num.
	    int style = DialogFragment.STYLE_NORMAL;
	    //int theme = android.R.style.Theme_Material_Light_Dialog;
	    
	    int theme = android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog;
	    setStyle(style, theme);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(mActivity.getString(R.string.dialog_track_title));

        View v = inflater.inflate(R.layout.dialog_track_edit_delete, container, false);
        
        final Button cancelButton = (Button) v.findViewById(R.id.button_cancel);
        final Button openTagEditorButton = (Button) v.findViewById(R.id.button_open_tag_editor);
        final Button removeThisTrackButton = (Button) v.findViewById(R.id.button_remove_this_track);
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        
        removeThisTrackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.removeTrack(mTrackNumber);
				dismiss();
			}
		});
        
        openTagEditorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mActivity, MusicEditorActivity.class);
				intent.setData(Uri.fromFile(mActivity.mFileList.get(mTrackNumber)));
				mActivity.startActivityForResult(intent, MusicTransmitterActivity.REQUEST_EDIT_TRACK);
				dismiss();
			}
		});
        
        return v;
	}
}
