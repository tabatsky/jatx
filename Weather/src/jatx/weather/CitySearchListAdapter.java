package jatx.weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CitySearchListAdapter extends BaseAdapter {
	final static String urlBase = 
			"http://api.openweathermap.org/data/2.5/find?type=like&q=";
	
	private Context mContext;
	
	private List<Long> mCityIds;
	private List<String> mCityNames;
	private List<String> mCountryNames;
	
	private String urlContent;
	
	private WeatherDBHelper mDBHelper;
	
	public CitySearchListAdapter(Context context, final String query) {
		mContext = context;
		
		mDBHelper = new WeatherDBHelper(mContext);
		
		mCityIds = new ArrayList<Long>();
		mCityNames = new ArrayList<String>();
		mCountryNames = new ArrayList<String>();
		
		Thread t = new Thread() {
			public void run() {
				try {
					Scanner scanner;
					scanner = new Scanner(new URL(urlBase+query).openStream(), "UTF-8");
					urlContent = scanner.useDelimiter("\\A").next();
					scanner.close();
					Log.d("degug","get url ok");
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		try {
			t.start();
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonRes = (JSONObject) JSONValue.parse(urlContent);
		String cod = (String)jsonRes.get("cod");
		
		if (!cod.equals("200")) {
			Toast.makeText(mContext, "server error", Toast.LENGTH_LONG).show();
			return;
		}
		
		Long count = (Long)jsonRes.get("count");
		if (count==0) {
			Toast.makeText(mContext, "nothing found", Toast.LENGTH_LONG).show();
			return;
		}
		
        JSONArray list = (JSONArray) jsonRes.get("list");
        for (int i=0; i<list.size(); i++) {
        	JSONObject entry = (JSONObject)list.get(i);
        	Long city_id = (Long)entry.get("id");
        	String city_name = (String)entry.get("name");
        	JSONObject sys = (JSONObject)entry.get("sys");
        	String country_name = (String)sys.get("country");
        	
        	mCityIds.add(city_id);
        	mCityNames.add(city_name);
        	mCountryNames.add(country_name);
        }
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCityIds.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCityNames.get(position)+", "+mCountryNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.city_search_entry, null);
        }
		
		TextView cityEntry = (TextView) convertView.findViewById(R.id.citySearchEntryInfo);
		cityEntry.setText(mCityNames.get(position)+", "+mCountryNames.get(position));
		
		Button addCityButton = (Button) convertView.findViewById(R.id.addCityButton);
		addCityButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = mDBHelper.getWritableDatabase();
				Long city_id = mCityIds.get(position);
				String city_name = mCityNames.get(position);
				String country_name = mCountryNames.get(position);
				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO cities ");
	    	    query.append("(city_id, city_name, country_name) VALUES (");
	    	    query.append(city_id.toString()+", '");
	    	    query.append(city_name+"', '");
	    	    query.append(country_name+"')");
	    	    db.execSQL(query.toString());
	    	    db.close();
	    	    
	    	    List<Long> ids = new ArrayList<Long>();
	    	    ids.add(city_id);
	    	    (new WeatherRefresher(mContext)).refreshByCityIds(ids);
	    	    
	    	    Intent intent = new Intent();
	    	    ((Activity)mContext).setResult(Activity.RESULT_OK, intent);
	    	    ((Activity)mContext).finish();
			}
		});
		
		return convertView;
	}

}
