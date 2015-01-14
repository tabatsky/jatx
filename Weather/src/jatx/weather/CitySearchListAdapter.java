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
import android.os.AsyncTask;
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
	
	private CitySearchActivity mActivity;
	
	private List<CityEntry> mCities;
	
	private WeatherDBHelper mDBHelper;
	
	public CitySearchListAdapter(CitySearchActivity activity, final String query) {
		mActivity = activity;
		
		mDBHelper = WeatherDBHelper.getInstance(mActivity.getApplicationContext());
		
		mCities = new ArrayList<CityEntry>();
		
		AsyncTask<Void,Void,Void> searchTask = new AsyncTask<Void,Void,Void>() {
			private String urlContent = null;
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Scanner scanner;
					scanner = new Scanner(new URL(urlBase+query).openStream(), "UTF-8");
					urlContent = scanner.useDelimiter("\\A").next();
					scanner.close();
					Log.d("degug","get url ok");
				} catch (MalformedURLException e) {
					Log.e("error","malformed url");
				} catch (IOException e) {
					Log.e("error","io");
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				if (urlContent==null) return;
				
				JSONObject jsonRes = (JSONObject) JSONValue.parse(urlContent);
				String cod = (String)jsonRes.get("cod");
				
				if (!cod.equals("200")) {
					Toast.makeText(mActivity, "server error", Toast.LENGTH_LONG).show();
					return;
				}
				
				Long count = (Long)jsonRes.get("count");
				if (count==0) {
					Toast.makeText(mActivity, "nothing found", Toast.LENGTH_LONG).show();
					return;
				}
				
				JSONArray list = (JSONArray) jsonRes.get("list");
		        for (int i=0; i<list.size(); i++) {
		        	CityEntry ce = new CityEntry();
		        	
		        	JSONObject entry = (JSONObject)list.get(i);
		        	ce.id = (Long)entry.get("id");
		        	ce.name = (String)entry.get("name");
		        	JSONObject sys = (JSONObject)entry.get("sys");
		        	ce.country = (String)sys.get("country");
		        	
		        	mCities.add(ce);
		        }
		        
		        notifyDataSetChanged();
		        mActivity.citySearchListView.setSelection(0);
			}
		};
		
		searchTask.execute(null, null, null);
        
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mCities.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mCities.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.city_search_entry, null);
        }
		
		final CityEntry ce = mCities.get(position);
		
		TextView cityEntry = (TextView) convertView.findViewById(R.id.citySearchEntryInfo);
		cityEntry.setText(ce.name+", "+ce.country);
		
		Button addCityButton = (Button) convertView.findViewById(R.id.addCityButton);
		addCityButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDBHelper.putCityEntry(ce);
	    	    
	    	    Intent intent = new Intent();
	    	    mActivity.setResult(Activity.RESULT_OK, intent);
	    	    mActivity.finish();
			}
		});
		
		return convertView;
	}

}
