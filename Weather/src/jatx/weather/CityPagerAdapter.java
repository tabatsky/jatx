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
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CityPagerAdapter extends PagerAdapter {	
	private WeatherDBHelper mDBHelper;
	private MainActivity mActivity;
	
	private List<Long> mCityIds;
	
	public CityPagerAdapter(MainActivity activity) {
		mActivity = activity;
		mDBHelper = WeatherDBHelper.getInstance(mActivity.getApplicationContext());
	}
	
	@Override
	public int getCount() {
		return mDBHelper.getCityEntryCount();
	}
	
	@Override
    public Object instantiateItem(View collection, int position){
		LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		View v = inflater.inflate(R.layout.page_city, null);
		
		CityEntry ce = mDBHelper.getCityEntry((long)position);
		
		TextView title = (TextView) v.findViewById(R.id.city_title);
		TextView info = (TextView) v.findViewById(R.id.page_info_text);
		ImageView img = (ImageView) v.findViewById(R.id.page_info_img);
		
		title.setText(ce.name);
		
		WeatherEntry we = mDBHelper.getWeatherEntry(ce.id, 0l);
   	 
    	String dt_txt = we.dt_txt;
 	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 		df.setTimeZone(TimeZone.getTimeZone("UTC"));
 		try {
 			Date date = df.parse(dt_txt);
 			DateFormat dfLocal = new SimpleDateFormat("HH:mm dd.MM.yyyy");
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
 		//sb.append("Время: ").append(dt_txt).append("\n");
 		sb.append("Температура: ").append(temp_str).append("\u00b0").append("C\n");
 		sb.append("Давление: ").append(pressure_str).append(" мм.рт.ст.\n");
 		sb.append("Влажность: ").append(humidity).append("%");
 		
 		info.setText(sb.toString());
 		
 		Log.i("desc",desc);
 		
 		Integer drawableId = Globals.getIconId(we.icon);
		
    	if (drawableId!=null) {
    		img.setImageDrawable(mActivity.getResources().getDrawable(drawableId));
    	}
    	
    	final ListView forecastListView = (ListView) v.findViewById(R.id.forecast_list);
		final ForecastAdapter fa = new ForecastAdapter(mActivity.getApplicationContext(), ce.id);
		forecastListView.setAdapter(fa);
    		
        ((ViewPager) collection).addView(v, 0);
        return v;
    }

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
