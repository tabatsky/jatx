package jatx.weather;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CitySearchActivity extends Activity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_search);
		
		mContext = this;
		
		final EditText searchText = (EditText) findViewById(R.id.searchText);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		final ListView citySearchListView = (ListView) findViewById(R.id.citySearchListView);
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String query = searchText.getText().toString();
				CitySearchListAdapter csla = 
						new CitySearchListAdapter(mContext, query);
				citySearchListView.setAdapter(csla);
			}
		});
	}
}
