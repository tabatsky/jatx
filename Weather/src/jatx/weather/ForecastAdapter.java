package jatx.weather;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
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
	private Context mContext;
	
	private List<WeatherEntry> weatherList;
	
	private Long mCityId;
	
	public ForecastAdapter(Context context, Long city_id) {
		mCityId = city_id;
		mContext = context;
		mDBHelper = WeatherDBHelper.getInstance(mContext);
		
		weatherList = mDBHelper.getWeatherList(city_id);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//return mDBHelper.getWeatherEntryCount(mCityId);
		return weatherList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		//return mDBHelper.getWeatherEntry(mCityId, (long)position);
		return weatherList.get(position);
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
		
		//WeatherEntry we = mDBHelper.getWeatherEntry(mCityId, (long)position);
		WeatherEntry we = weatherList.get(position);
		
		TextView forecastEntryInfo = (TextView)convertView.findViewById(R.id.forecastEntryInfo);
		
		String dt_txt = we.dt_txt;
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
		
		Double temp = we.temp;
		NumberFormat formatter = new DecimalFormat("+#0.0;-#0.0"); 
		String temp_str = formatter.format(temp);
		Double pressure = we.pressure;
		NumberFormat formatter2 = new DecimalFormat("##0.0");
		String pressure_str = formatter2.format(pressure);
		Long humidity = we.humidity;
		String desc = we.description;
		
		StringBuilder sb = new StringBuilder();
		sb.append(dt_txt).append(" | ");
		sb.append(temp_str).append("\u00b0").append("C | ");
		sb.append(pressure_str).append(" мм.рт.ст. | ");
		sb.append(humidity).append("% | ");
		sb.append(desc);
		
		forecastEntryInfo.setText(sb.toString());
		
		return convertView;
	}

}
