package jatx.musicreceiver.android;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SelectHostDialog extends DialogFragment {
	private MusicReceiverActivity mActivity;
	
	private Spinner mHostSpinner;
	private EditText mHostEdit;
	private Button mOkButton;
	private Button mExitButton;
	
	public static SelectHostDialog newInstance(MusicReceiverActivity activity) {
		SelectHostDialog dialog = new SelectHostDialog();
		
		dialog.mActivity = activity;
		
		dialog.setCancelable(false);
		
	    return dialog;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog;
        
        setStyle(style, theme);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().setTitle("");

        View v = inflater.inflate(R.layout.dialog_select_host, container, false);
        
        mHostSpinner = (Spinner) v.findViewById(R.id.select_dialog_host_spinner);
        mHostEdit = (EditText) v.findViewById(R.id.select_dialog_host_edit);
        mOkButton = (Button) v.findViewById(R.id.select_dialog_ok_button);
        mExitButton = (Button) v.findViewById(R.id.select_dialog_exit_button);
        
        updateActualHost();
        
        mHostSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (position>0) {
					mHostEdit.setText(mActivity.allHosts.get(position));
				} else {
					mHostEdit.setText("");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        mOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.host = mHostEdit.getText().toString().trim();
				
				if (mActivity.host.equals("")) {
					Toast.makeText(mActivity, mActivity.getString(R.string.toast_empty_host), Toast.LENGTH_LONG).show();
					return;
				}
				
				if (!mActivity.allHosts.contains(mActivity.host)) {
					mActivity.allHosts.add(mActivity.host);
				}
				
				mActivity.saveSettings();
				mActivity.prepareAndStart();
				
				dismiss();
			}
		});
        
        mExitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				mActivity.finish();
			}
		});
        
        return v;
	}
	
	private void updateActualHost() {
		mActivity.loadSettings();
		
		int index = 0;
		String host = "";
		
		if (mActivity.hostIndex>0) {
			index = mActivity.hostIndex;
			host = mActivity.allHosts.get(index);
		} /* else if (mActivity.hostIndex==0) {
			index = 0;
			host = "";
		} */ else {
			host = mActivity.host;
			mActivity.allHosts.add(host);
			index = mActivity.allHosts.indexOf(host);
		}
		
		final String[] hosts = mActivity.allHosts.toArray(new String[mActivity.allHosts.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, 
				android.R.layout.simple_spinner_item, hosts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mHostSpinner.setAdapter(adapter);
		
		mHostSpinner.setSelection(index);
		mHostEdit.setText(host);
	}
}
