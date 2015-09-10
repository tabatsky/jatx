package jatx.imageloader;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

public class FlickrJsonParser {
	public static List<String> parseURLs(String jsonStr) {
		List<String> result = new ArrayList<String>();
		
		try {
			JSONObject jsonObj = (JSONObject) JSONValue.parse(jsonStr);
			JSONObject photosObj = (JSONObject) jsonObj.get("photos");
			JSONArray photoArr = (JSONArray) photosObj.get("photo");
			
			for (int i=0; i<photoArr.size(); i++) {
				JSONObject photoEntry = (JSONObject) photoArr.get(i);
				
				String id = (String) photoEntry.get("id");
				String secret = (String) photoEntry.get("secret");
				String server = (String) photoEntry.get("server");
				Number farm = (Number) photoEntry.get("farm");
				
				StringBuilder url = new StringBuilder();
				url.append("http://");
				url.append("farm" + farm.toString());
				url.append(".staticflickr.com/");
				url.append(server + "/");
				url.append(id + "_");
				url.append(secret + ".jpg");
				
				Log.i("url", url.toString());
				result.add(url.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return result;
	}
}
