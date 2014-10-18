package jatx.weather;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class ForecastAdapter extends BaseAdapter {
	final static String WEATHER_TABLE = "weather";
	final static String KEY_CITY_ID = "city_id";
	final static String KEY_DT_TXT = "dt_txt";
	final static String KEY_TEMP = "temp";
	final static String KEY_PRESSURE = "pressure";
	final static String KEY_HUMIDITY = "humidity";
	final static String KEY_DESC = "description";
	
	private WeatherDBHelper mDBHelper;
	private SQLiteDatabase db;
	private Context mContext;
	
	private List<String> mDateList;
	private List<Double> mTempList;
	private List<Double> mPressureList;
	private List<Long> mHumidityList;
	private List<String> mDescList;
	
	private Long mCityId;
	
	public ForecastAdapter(Context context, Long city_id) {
		mCityId = city_id;
		mContext = context;
		mDBHelper = new WeatherDBHelper(mContext);
		db = mDBHelper.getReadableDatabase();
		
		mDateList = new ArrayList<String>();
		mTempList = new ArrayList<Double>();
		mPressureList = new ArrayList<Double>();
		mHumidityList = new ArrayList<Long>();
		mDescList = new ArrayList<String>();
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT * FROM weather WHERE city_id=");
		query.append(mCityId.toString());
		query.append(" AND dt_txt>'");
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, -3);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		query.append(df.format(calendar.getTime()));
		query.append("' ORDER BY dt_txt");
		Cursor cursor = db.rawQuery(query.toString(), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String dt_txt = cursor.getString(cursor.getColumnIndex(KEY_DT_TXT));
			mDateList.add(dt_txt);
			Double temp = cursor.getDouble(cursor.getColumnIndex(KEY_TEMP));
			mTempList.add(temp);
			Double pressure = cursor.getDouble(cursor.getColumnIndex(KEY_PRESSURE));
			mPressureList.add(pressure);
			Long humidity = cursor.getLong(cursor.getColumnIndex(KEY_HUMIDITY));
			mHumidityList.add(humidity);
			String desc = cursor.getString(cursor.getColumnIndex(KEY_DESC));
			mDescList.add(desc);
			
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDateList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.forecast_entry, null);
        }
		
		TextView forecastEntryInfo = (TextView)convertView.findViewById(R.id.forecastEntryInfo);
		
		String dt_txt = mDateList.get(position);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date date = df.parse(dt_txt);
			DateFormat dfLocal = new SimpleDateFormat("dd.MM HH:mm");
			dt_txt = dfLocal.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("error", "date parse error");
		}
		
		Double temp = mTempList.get(position);
		NumberFormat formatter = new DecimalFormat("+#0.0;-#0.0"); 
		String temp_str = formatter.format(temp);
		Double pressure = mPressureList.get(position);
		NumberFormat formatter2 = new DecimalFormat("##0.0");
		String pressure_str = formatter2.format(pressure);
		Long humidity = mHumidityList.get(position);
		String desc = mDescList.get(position);
		
		StringBuilder sb = new StringBuilder();
		sb.append(dt_txt).append(" | ");
		sb.append(temp_str).append("\u00b0").append("C | ");
		sb.append(pressure_str).append("mmHg | ");
		sb.append(humidity).append("% | ");
		sb.append(desc);
		
		forecastEntryInfo.setText(sb.toString());
		
		return convertView;
	}

}
