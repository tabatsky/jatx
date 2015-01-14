package jatx.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CitySearchActivity extends ActionBarActivity {
	private CitySearchActivity self;
	ListView citySearchListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_search);
		
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		
		self = this;
		
		final EditText searchText = (EditText) findViewById(R.id.searchText);
		final Button searchButton = (Button) findViewById(R.id.searchButton);
		citySearchListView = (ListView) findViewById(R.id.citySearchListView);
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String query = searchText.getText().toString();
				CitySearchListAdapter csla = 
						new CitySearchListAdapter(self, query);
				citySearchListView.setAdapter(csla);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
		case android.R.id.home:
        	Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
}
