package jatx.weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class WeatherRefreshService extends IntentService {
	List<Long> mCityIds;
	WeatherDBHelper mDBHelper;
	
	final static String urlBase = 
			"http://api.openweathermap.org/data/2.5/forecast?lang=ru&id=";
	
	public WeatherRefreshService() {
		super("WeatherRefreshService");
	}	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if (Globals.refreshRunning) return;
		
		Globals.refreshRunning = true;
		
		mDBHelper = WeatherDBHelper.getInstance(getApplicationContext());
		mCityIds = mDBHelper.getCityIds();

		for (Long id: mCityIds) {
			String urlContent;
			
			try {
				Scanner scanner;
				scanner = new Scanner(new URL(urlBase+id).openStream(), "UTF-8");
				urlContent = scanner.useDelimiter("\\A").next();
				scanner.close();
				Log.d("degug","get url ok");
			} catch (MalformedURLException e) {
				Log.e("error","malformed url");
				return;
			} catch (IOException e) {
				Log.e("error","io");
				return;
			}
					
			JSONObject jsonRes = (JSONObject) JSONValue.parse(urlContent);
			JSONArray list = (JSONArray) jsonRes.get("list");
					
			for (int i=0; i<list.size(); i++) {
			    WeatherEntry we = new WeatherEntry();
			        	
			    JSONObject entry = (JSONObject)list.get(i);
			    we.dt_txt = (String)entry.get("dt_txt");
			    JSONObject main = (JSONObject)entry.get("main");
			    we.temp = ((Number)main.get("temp")).doubleValue();
			    we.temp -= 273.15;
			    we.pressure = ((Number)main.get("pressure")).doubleValue();
			    we.pressure *= 0.75006375541921;
			    we.humidity = ((Number)main.get("humidity")).longValue();
			    JSONArray weather = (JSONArray)entry.get("weather");
			    we.description = (String)(((JSONObject)weather.get(0)).get("description"));
			    we.icon = (String)(((JSONObject)weather.get(0)).get("icon"));
			           	
			    mDBHelper.putWeatherEntry(id, we);
			           	
			    Log.i("debug","insert ok");
			}
		}	
		Intent in = new Intent(Globals.REFRESH_UI);
		getApplicationContext().sendBroadcast(in);
		
		Globals.refreshRunning = false;
	}

}
