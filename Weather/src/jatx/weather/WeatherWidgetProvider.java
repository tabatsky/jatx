package jatx.weather;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class WeatherWidgetProvider extends AppWidgetProvider {
	@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	 Intent in = new Intent(context.getApplicationContext(), WeatherRefreshService.class);
	 	 context.getApplicationContext().startService(in); 
    	
         RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

         Intent active = new Intent(context, MainActivity.class);
         active.putExtra("city_id", Globals.getDefCity(context));

         PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);

         remoteViews.setOnClickPendingIntent(R.id.widget_info_text, actionPendingIntent);

         appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
    	try {
    	
    	WeatherDBHelper mDBHelper = WeatherDBHelper.getInstance(context.getApplicationContext());
    		
        final String action = intent.getAction();
        if (action.equals(Globals.REFRESH_UI)) {
       	 	Log.i("test","refresh widget");
       	 
       	 	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
        	 
       	 	WeatherEntry we = mDBHelper.getWeatherEntry(Globals.getDefCity(context), 0l);
       	 	
       	 	List<Long> cityIds = mDBHelper.getCityIds();
       	 	List<String> cityNames = mDBHelper.getCityNames();
       	 	
       	 	String cityName = cityNames.get(cityIds.indexOf(Globals.getDefCity(context)));
        	 
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
     		
     		StringBuilder sb = new StringBuilder();
     		sb.append(cityName).append("\n\n");
     		//sb.append("Время: ").append(dt_txt).append("\n");
     		sb.append("Температура: ").append(temp_str).append("\u00b0").append("C\n");
     		sb.append("Давление: ").append(pressure_str).append(" мм.рт.ст.\n");
     		sb.append("Влажность: ").append(humidity).append("%");
        	 
        	remoteViews.setTextViewText(R.id.widget_info_text, sb.toString());
        	
        	Integer drawableId = Globals.getIconId(we.icon);
        	
        	if (drawableId!=null) {
        		remoteViews.setImageViewResource(R.id.widget_info_img, drawableId);
        	}
        		
        	AppWidgetManager manager = AppWidgetManager.getInstance(context);
        	ComponentName widget = new ComponentName(context, WeatherWidgetProvider.class);
            int[] widgetIds = manager.getAppWidgetIds(widget);
        	manager.updateAppWidget(widgetIds, remoteViews);
        } 
        super.onReceive(context, intent);
         
    	} catch (Exception e) {
    		StringWriter sw = new StringWriter();
    		PrintWriter pw = new PrintWriter(sw);
    		e.printStackTrace(pw);
    		Log.e("error",sw.toString());
    	}
    }
}
