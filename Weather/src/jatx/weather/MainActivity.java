package jatx.weather;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	final static int REQUEST_SETTINGS = 555;
	final static int REQUEST_ADD = 888;
	
	private List<Long> mCityIds;
	private WeatherDBHelper mDBHelper;
	
	MainActivity self;
	
	private ViewPager pager;
	private CityPagerAdapter cla;
	
	private IntentFilter refreshFilter;
    private BroadcastReceiver refreshReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("test","1");	
			
		self = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		
		mDBHelper = WeatherDBHelper.getInstance(getApplicationContext());
		
		mCityIds = mDBHelper.getCityIds();
		
		Log.i("test","2");
	
		Bundle extras = getIntent().getExtras();
		Long city_id = Globals.getDefCity(this);
		if (extras!=null&&extras.containsKey("city_id")) {
			city_id = extras.getLong("city_id");
		}
		
		Log.i("test","3");
		
		int index = mCityIds.indexOf(city_id);
		
		Log.i("test","4");
		
		if (index<0) Log.e("error","minus");
		
		pager = (ViewPager) findViewById(R.id.pager);
		cla = new CityPagerAdapter(this);
		pager.setAdapter(cla);
		
		pager.setCurrentItem(index);
		
		Log.i("test","5");
		
		refreshFilter = new IntentFilter(Globals.REFRESH_UI);
		refreshReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) 
	        {
				Long city_id = mCityIds.get(pager.getCurrentItem());
				
				cla = new CityPagerAdapter(self);
				pager.setAdapter(cla);
				
				mCityIds = mDBHelper.getCityIds();
				int index = mCityIds.indexOf(city_id);
				pager.setCurrentItem(index);
	        }
		};
		registerReceiver(refreshReceiver, refreshFilter);
		
		Log.i("test","9");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode==REQUEST_ADD && resultCode==RESULT_OK) {
	    	Long city_id = mCityIds.get(pager.getCurrentItem());
			
			cla = new CityPagerAdapter(self);
			pager.setAdapter(cla);
			
			mCityIds = mDBHelper.getCityIds();
			int index = mCityIds.indexOf(city_id);
			pager.setCurrentItem(index);
	    }
	    
	    if (requestCode==REQUEST_SETTINGS && resultCode==RESULT_OK) {
	    	Long city_id = Globals.getDefCity(this);
			
			cla = new CityPagerAdapter(self);
			pager.setAdapter(cla);
			
			mCityIds = mDBHelper.getCityIds();
			int index = mCityIds.indexOf(city_id);
			pager.setCurrentItem(index);
			
			Intent in = new Intent(this, WeatherRefreshService.class);
			startService(in);
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu); 
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        
        int id = item.getItemId();
		
		switch (id) {
		case R.id.open_settings:
			Intent in1 = new Intent();
			in1.setClass(getApplicationContext(), SettingsActivity.class);
			startActivityForResult(in1, REQUEST_SETTINGS);
			
			return true;
		case R.id.refresh:
			Intent in = new Intent(this, WeatherRefreshService.class);
			startService(in);
			
			return true;
		case R.id.add_city:
			Intent in2 = new Intent();
			in2.setClass(this, CitySearchActivity.class);
			startActivityForResult(in2, REQUEST_ADD);
			
			return true;
			
		default:
            return super.onOptionsItemSelected(item);
		}
    }
}
