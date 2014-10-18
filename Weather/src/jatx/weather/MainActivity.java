package jatx.weather;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
	private List<Long> mCityIds;
	private WeatherRefresher wr;
	
	private ListView mCityListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		wr = new WeatherRefresher(getApplicationContext());
		
		mCityListView = (ListView) findViewById(R.id.cityListView);
		CityListAdapter cla = new CityListAdapter(getApplicationContext());
		mCityListView.setAdapter(cla);
		
		mCityIds = cla.getCityIds();
		
		mCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Long city_id = mCityIds.get(position);
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), ForecastActivity.class);
				intent.putExtra("city_id", city_id);
				startActivity(intent);
			}
		});
		
		final Button refreshButton = (Button) findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wr.refreshByCityIds(mCityIds);
				mCityListView.setAdapter(new CityListAdapter(getApplicationContext()));
			}
		});
		
		final Button addCityButton = (Button) findViewById(R.id.addCityButton);
		addCityButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CitySearchActivity.class);
				startActivityForResult(intent, 1);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode==1 && resultCode==RESULT_OK) {
	    	CityListAdapter cla = new CityListAdapter(getApplicationContext());
			mCityListView.setAdapter(cla);
			mCityIds = cla.getCityIds();
	    }
	}
}
