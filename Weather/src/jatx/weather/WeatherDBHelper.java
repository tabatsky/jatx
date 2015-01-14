package jatx.weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherDBHelper extends SQLiteOpenHelper {
	final static String DB_NAME = "weather.db";
	final static int DB_VERSION = 9;
    Context mContext;
    private static volatile WeatherDBHelper mInstance;
    
	final static String WEATHER_TABLE = "weather";
	final static String KEY_CITY_ID = "city_id";
	final static String KEY_DT_TXT = "dt_txt";
	final static String KEY_TEMP = "temp";
	final static String KEY_PRESSURE = "pressure";
	final static String KEY_HUMIDITY = "humidity";
	final static String KEY_DESC = "description";
	final static String KEY_ICON = "icon";
	
	final static String CITY_TABLE = "cities";
	final static String KEY_CITY_NAME = "city_name";
	final static String KEY_COUNTRY_NAME = "country_name";
    
    final static String CREATE_CITIES_TABLE = 
    		"CREATE TABLE cities " +
    		"(city_id INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
    		"city_name TEXT , " +
    		"country_name TEXT)";
    final static String DROP_CITIES_TABLE =
    		"DROP TABLE IF EXISTS cities";
    final static String ADD_MOSCOW_CITY = 
    		"INSERT INTO cities " +
    	    "(city_id, city_name, country_name) VALUES " +
    	    "(524901, 'Moscow', 'RU')";
    final static String ADD_SPB_CITY =
    		"INSERT INTO cities " +
    	    "(city_id, city_name, country_name) VALUES " +
    		"(498817, 'Saint Petersburg', 'RU')";
    final static String ADD_KMS_CITY =
    		"INSERT INTO cities " +
    	    "(city_id, city_name, country_name) VALUES " +
    		"(2021851, 'Komsomolsk-na-Amure', 'RU')";
    
    final static String CREATE_WEATHER_TABLE =
    		"CREATE TABLE weather " +
    		"(city_id INTEGER, "
    		+ "dt_txt TEXT, "
    		+ "temp REAL, "
    		+ "pressure REAL, "
    		+ "humidity INTEGER, "
    		+ "icon TEXT, "
    		+ "description TEXT, "
    		+ "UNIQUE (city_id, dt_txt) ON CONFLICT REPLACE)";
    final static String DROP_WEATHER_TABLE =
    		"DROP TABLE IF EXISTS weather";
    
    
	private WeatherDBHelper(Context context){
	    super(context, DB_NAME, null, DB_VERSION);
	    mContext = context;
	}
	
	public static synchronized WeatherDBHelper getInstance(Context context) {
		if (mInstance==null) {
			mInstance = new WeatherDBHelper(context);
		}
		
		return mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITIES_TABLE);
		db.execSQL(ADD_MOSCOW_CITY);
		db.execSQL(ADD_SPB_CITY);
		db.execSQL(ADD_KMS_CITY);
		
		db.execSQL(CREATE_WEATHER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_CITIES_TABLE);

		db.execSQL(CREATE_CITIES_TABLE);
		db.execSQL(ADD_MOSCOW_CITY);
		db.execSQL(ADD_SPB_CITY);
		db.execSQL(ADD_KMS_CITY);
		
		db.execSQL(DROP_WEATHER_TABLE);
		
		db.execSQL(CREATE_WEATHER_TABLE);
	}

	public synchronized void putWeatherEntry(Long id, WeatherEntry we) {
		SQLiteDatabase db = getWritableDatabase();
		
		StringBuilder query = new StringBuilder();
       	query.append("INSERT INTO ");
       	query.append(WEATHER_TABLE + " (");
       	query.append(KEY_CITY_ID + ", ");
       	query.append(KEY_DT_TXT + ", ");
       	query.append(KEY_TEMP + ", ");
       	query.append(KEY_PRESSURE + ", ");
       	query.append(KEY_HUMIDITY + ", ");
       	query.append(KEY_ICON + ", ");
       	query.append(KEY_DESC);
       	query.append(") VALUES (");
       	query.append(id.toString()+", '");
       	query.append(we.dt_txt+"', ");
       	query.append(we.temp.toString()+", ");
       	query.append(we.pressure.toString()+", ");
       	query.append(we.humidity.toString()+", '");
       	query.append(we.icon+"', '");
       	query.append(we.description+"')");
        	
       	db.execSQL(query.toString());
       	
       	db.close();
	}
	
	public synchronized void putCityEntry(CityEntry ce) {
		SQLiteDatabase db = getWritableDatabase();
		
		StringBuilder query = new StringBuilder();
       	query.append("INSERT INTO ");
       	query.append(CITY_TABLE + " (");
       	query.append(KEY_CITY_ID + ", ");
       	query.append(KEY_CITY_NAME + ", ");
       	query.append(KEY_COUNTRY_NAME);
       	query.append(") VALUES (");
       	query.append(ce.id.toString()+", '");
       	query.append(ce.name+"', '");
       	query.append(ce.country+"')");
        	
       	db.execSQL(query.toString());
       	
       	db.close();
	}
	
	public synchronized WeatherEntry getWeatherEntry(Long city_id, Long position) {
		SQLiteDatabase db = getReadableDatabase();
		WeatherEntry we = new WeatherEntry();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM weather WHERE city_id=");
		query.append(city_id.toString());
		query.append(" AND dt_txt>'");
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, -3);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		query.append(df.format(calendar.getTime()));
		query.append("' ORDER BY dt_txt ");
		query.append(" LIMIT 1 OFFSET " + position);
		Cursor cursor = db.rawQuery(query.toString(), null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			we.dt_txt = cursor.getString(cursor.getColumnIndex(KEY_DT_TXT));
			we.temp = cursor.getDouble(cursor.getColumnIndex(KEY_TEMP));
			we.pressure = cursor.getDouble(cursor.getColumnIndex(KEY_PRESSURE));
			we.humidity = cursor.getLong(cursor.getColumnIndex(KEY_HUMIDITY));
			we.description = cursor.getString(cursor.getColumnIndex(KEY_DESC));
			we.icon = cursor.getString(cursor.getColumnIndex(KEY_ICON));
		}
		cursor.close();
		db.close();
		
		return we;
	}
	
	public synchronized List<WeatherEntry> getWeatherList(Long city_id) {
		SQLiteDatabase db = getReadableDatabase();
		
		List<WeatherEntry> result = new ArrayList<WeatherEntry>();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM weather WHERE city_id=");
		query.append(city_id.toString());
		query.append(" AND dt_txt>'");
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, -3);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		query.append(df.format(calendar.getTime()));
		query.append("' ORDER BY dt_txt ");
		Cursor cursor = db.rawQuery(query.toString(), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			//Log.i("entry","get");
			WeatherEntry we = new WeatherEntry();
			we.dt_txt = cursor.getString(cursor.getColumnIndex(KEY_DT_TXT));
			we.temp = cursor.getDouble(cursor.getColumnIndex(KEY_TEMP));
			we.pressure = cursor.getDouble(cursor.getColumnIndex(KEY_PRESSURE));
			we.humidity = cursor.getLong(cursor.getColumnIndex(KEY_HUMIDITY));
			we.description = cursor.getString(cursor.getColumnIndex(KEY_DESC));
			we.icon = cursor.getString(cursor.getColumnIndex(KEY_ICON));
			result.add(we);
			
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		
		return result;
	}
	
	public synchronized int getWeatherEntryCount(Long city_id) {
		SQLiteDatabase db = getReadableDatabase();
		int count = 0;
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(*) AS count FROM weather WHERE city_id=");
		query.append(city_id.toString());
		query.append(" AND dt_txt>'");
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, -3);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		query.append(df.format(calendar.getTime()));
		query.append("'");
		Cursor cursor = db.rawQuery(query.toString(), null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			count = (int)cursor.getLong(cursor.getColumnIndex("count"));
		}
		cursor.close();
		db.close();
		
		return count;
	}
	
	public synchronized CityEntry getCityEntry(Long position) {
		SQLiteDatabase db = getReadableDatabase();
		CityEntry ce = new CityEntry();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM cities ");
		query.append(" ORDER BY city_name ");
		query.append(" LIMIT 1 OFFSET " + position);
		
		Cursor cursor = db.rawQuery(query.toString(), null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			ce.id = cursor.getLong(cursor.getColumnIndex(KEY_CITY_ID));
			ce.name = cursor.getString(cursor.getColumnIndex(KEY_CITY_NAME));
			ce.country = cursor.getString(cursor.getColumnIndex(KEY_COUNTRY_NAME));
		}
		cursor.close();
		db.close();
		
		return ce;
	}
	
	public synchronized int getCityEntryCount() {
		SQLiteDatabase db = getReadableDatabase();
		int count = 0;
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT COUNT(*) AS count FROM cities");
		Cursor cursor = db.rawQuery(query.toString(), null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			count = (int)cursor.getLong(cursor.getColumnIndex("count"));
		}
		cursor.close();
		db.close();
		
		return count;
	}
	
	public synchronized List<Long> getCityIds() {
		List<Long> list = new ArrayList<Long>();
		
		int size = getCityEntryCount();
		
		for (int i=0; i<size; i++) {
			list.add(getCityEntry((long)i).id);
		}
		
		return list;
	}
	
	public synchronized List<String> getCityNames() {
		List<String> list = new ArrayList<String>();
		
		int size = getCityEntryCount();
		
		for (int i=0; i<size; i++) {
			list.add(getCityEntry((long)i).name);
		}
		
		return list;
	}
}
