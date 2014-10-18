package jatx.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDBHelper extends SQLiteOpenHelper {
	final static String DB_NAME = "weather.db";
	final static int DB_VERSION = 8;
    Context mContext;
    
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
    		+ "description TEXT, "
    		+ "UNIQUE (city_id, dt_txt) ON CONFLICT REPLACE)";
    final static String DROP_WEATHER_TABLE =
    		"DROP TABLE IF EXISTS weather";
    
    
	public WeatherDBHelper(Context context){
	    super(context, DB_NAME, null, DB_VERSION);
	    mContext = context;
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

}
