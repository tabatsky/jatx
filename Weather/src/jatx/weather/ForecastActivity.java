package jatx.weather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ForecastActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);
		
		Bundle extras = getIntent().getExtras();
		final Long city_id = extras.getLong("city_id");
		
		final ListView forecastListView = (ListView) findViewById(R.id.forecastListView);
		ForecastAdapter fa = new ForecastAdapter(this, city_id);
		forecastListView.setAdapter(fa);
		
		final Button refreshButton = (Button)findViewById(R.id.refreshForecastButton);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<Long> idList = new ArrayList<Long>();
				idList.add(city_id);
				(new WeatherRefresher(getApplicationContext())).refreshByCityIds(idList);
				ForecastAdapter fa = new ForecastAdapter(getApplicationContext(), city_id);
				forecastListView.setAdapter(fa);
			}
		});
	}
}
