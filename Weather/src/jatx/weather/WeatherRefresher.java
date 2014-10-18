package jatx.weather;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WeatherRefresher {
	final static String WEATHER_TABLE = "weather";
	final static String KEY_CITY_ID = "city_id";
	final static String KEY_DT_TXT = "dt_txt";
	final static String KEY_TEMP = "temp";
	final static String KEY_PRESSURE = "pressure";
	final static String KEY_HUMIDITY = "humidity";
	final static String KEY_DESC = "description";
	
	
	final static String urlBase = 
			"http://api.openweathermap.org/data/2.5/forecast?id=";
	
	private WeatherDBHelper mDBHelper;
	private SQLiteDatabase db;
	private Context mContext;
	private String urlContent;
	
	public WeatherRefresher(Context context) {
		mContext = context;
		mDBHelper = new WeatherDBHelper(mContext);
	}
	
	public void refreshByCityIds(List<Long> city_ids) {
		db = mDBHelper.getWritableDatabase();
		for (Long id: city_ids) {
			final String city_id = id.toString();
			Thread t = new Thread() {
				public void run() {
					try {
						Scanner scanner;
						scanner = new Scanner(new URL(urlBase+city_id).openStream(), "UTF-8");
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
					e.printStackTrace();
			}
				
			JSONObject jsonRes = (JSONObject) JSONValue.parse(urlContent);
	        JSONArray list = (JSONArray) jsonRes.get("list");
	            
	        for (int i=0; i<list.size(); i++) {
	          	JSONObject entry = (JSONObject)list.get(i);
	           	String dt_txt = (String)entry.get("dt_txt");
	           	JSONObject main = (JSONObject)entry.get("main");
	           	Double temp = ((Number)main.get("temp")).doubleValue();
	           	temp -= 273.15;
	           	Double pressure = ((Number)main.get("pressure")).doubleValue();
	           	pressure *= 0.75006375541921;
	           	Long humidity = ((Number)main.get("humidity")).longValue();
	           	JSONArray weather = (JSONArray)entry.get("weather");
	           	String description = (String)(((JSONObject)weather.get(0)).get("description"));
	            	
	           	StringBuilder query = new StringBuilder();
	           	query.append("INSERT INTO ");
	           	query.append(WEATHER_TABLE + " (");
	           	query.append(KEY_CITY_ID + ", ");
	           	query.append(KEY_DT_TXT + ", ");
	           	query.append(KEY_TEMP + ", ");
	           	query.append(KEY_PRESSURE + ", ");
	           	query.append(KEY_HUMIDITY + ", ");
	           	query.append(KEY_DESC);
	           	query.append(") VALUES (");
	           	query.append(id.toString()+", '");
	           	query.append(dt_txt+"', ");
	           	query.append(temp.toString()+", ");
	           	query.append(pressure.toString()+", ");
	           	query.append(humidity.toString()+", '");
	           	query.append(description+"')");
	            	
	           	db.execSQL(query.toString());
	           	Log.d("debug","insert ok");
	        }
		}
		db.close();
	}
}
