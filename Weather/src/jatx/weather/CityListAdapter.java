package jatx.weather;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityListAdapter extends BaseAdapter {
	final static String CITY_TABLE = "cities";
	final static String KEY_CITY_ID = "city_id";
	final static String KEY_CITY_NAME = "city_name";
	final static String KEY_COUNTRY_NAME = "country_name";
	
	private WeatherDBHelper mDBHelper;
	private SQLiteDatabase db;
	private Context mContext;
	private int mCitiesCount;
	
	private List<Long> mCityIds;
	private List<String> mCityNames;
	private List<String> mCountryNames;
	
	public CityListAdapter(Context context) {
		mContext = context;
		mDBHelper = new WeatherDBHelper(mContext);
		db = mDBHelper.getReadableDatabase();
		
		mCityIds = new ArrayList<Long>();
		mCityNames = new ArrayList<String>();
		mCountryNames = new ArrayList<String>();
		
		Cursor cursor = db.query(CITY_TABLE, new String[] {"city_id","city_name","country_name"}, 
				null, null, null, null, KEY_CITY_NAME);
		mCitiesCount = cursor.getCount();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Long city_id = cursor.getLong(cursor.getColumnIndex(KEY_CITY_ID));
			mCityIds.add(city_id);
			String city_name = cursor.getString(cursor.getColumnIndex(KEY_CITY_NAME));
			mCityNames.add(city_name);
			String country_name = cursor.getString(cursor.getColumnIndex(KEY_COUNTRY_NAME));
			mCountryNames.add(country_name);
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
	}
	
	public List<Long> getCityIds() {
		return mCityIds;
	}
	
	@Override
	public int getCount() {
		return mCitiesCount;
	}

	@Override
	public Object getItem(int position) {
		return mCityNames.get(position)+", "+mCountryNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.city_entry, null);
        }

		db = mDBHelper.getReadableDatabase();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT temp FROM weather WHERE city_id=");
		query.append(mCityIds.get(position).toString());
		query.append(" AND dt_txt>'");
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, -3);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		query.append(df.format(calendar.getTime()));
		query.append("' ORDER BY dt_txt LIMIT 1");
		Cursor cursor = db.rawQuery(query.toString(), null);
		Double temp = 0.0;
		if (cursor.getCount()>0) {
			cursor.moveToFirst();
			temp = cursor.getDouble(0);
			Log.d("debug","temp get ok: "+temp.toString());
		}
		cursor.close();
		db.close();
		

       	NumberFormat formatter = new DecimalFormat("+#0.0;-#0.0"); 
		
		TextView cityEntry = (TextView) convertView.findViewById(R.id.cityEntryInfo);
        cityEntry.setText(mCityNames.get(position)+", "
        					+mCountryNames.get(position)+" : "
        					+formatter.format(temp)+"\u00b0C");
		
		return convertView;
	}

}
