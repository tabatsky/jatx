package jatx.weather;

import android.content.Context;
import android.content.SharedPreferences;

public class Globals {
	public static final String PREFS_NAME = "JatxWeatherPreferences";
	
	public static final String REFRESH_UI = "jatx.weather.refreshUI";
	
	public static volatile boolean refreshRunning = false;
	
	public static long getDefCity(Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
		return sp.getLong("defCity", 2021851l);
	}
	
	public static void saveDefCity(Context context, long defCity) {
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong("defCity", defCity);
		editor.commit();
	}
	
	public static Integer getIconId(String icon) {
		if (icon.equals("01d")) {
			return R.drawable.ic01d;
		} else if (icon.equals("01n")) {
			return R.drawable.ic01n;
		} else if (icon.equals("02d")) {
			return R.drawable.ic02d;
		} else if (icon.equals("02n")) {
			return R.drawable.ic02n;
		} else if (icon.equals("03d")) {
			return R.drawable.ic03d;
		} else if (icon.equals("03n")) {
			return R.drawable.ic03n;
		} else if (icon.equals("04d")) {
			return R.drawable.ic04d;
		} else if (icon.equals("04n")) {
			return R.drawable.ic04n;
		} else if (icon.equals("09d")) {
			return R.drawable.ic09d;
		} else if (icon.equals("09n")) {
			return R.drawable.ic09n;
		} else if (icon.equals("10d")) {
			return R.drawable.ic10d;
		} else if (icon.equals("01n")) {
			return R.drawable.ic10n;
		} else if (icon.equals("11d")) {
			return R.drawable.ic11d;
		} else if (icon.equals("11n")) {
			return R.drawable.ic11n;
		} else if (icon.equals("13d")) {
			return R.drawable.ic13d;
		} else if (icon.equals("13n")) {
			return R.drawable.ic13n;
		} else if (icon.equals("50d")) {
			return R.drawable.ic50d;
		} else if (icon.equals("50n")) {
			return R.drawable.ic50n;
		} else { 
			return null;
		}
	}
}
