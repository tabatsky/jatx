package jatx.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends ActionBarActivity {
	private Spinner defCitySpinner;
	private TextView label1;
	private Button saveRestartButton;
	
	WeatherDBHelper mDBHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mDBHelper = WeatherDBHelper.getInstance(getApplicationContext());
		
		label1 = (TextView) findViewById(R.id.settings_label1);
		defCitySpinner = (Spinner) findViewById(R.id.def_city_spinner);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				R.layout.my_spinner_item, mDBHelper.getCityNames());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		defCitySpinner.setAdapter(adapter);
		
		defCitySpinner.setSelection(mDBHelper.getCityIds().indexOf(Globals.getDefCity(this)));
		
		saveRestartButton = (Button) findViewById(R.id.save_restart_button);
		saveRestartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int defCityPos = defCitySpinner.getSelectedItemPosition();
				Globals.saveDefCity(getApplicationContext(), 
						mDBHelper.getCityIds().get(defCityPos));
				
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

}
