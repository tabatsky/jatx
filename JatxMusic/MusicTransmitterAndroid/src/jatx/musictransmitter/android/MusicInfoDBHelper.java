/*******************************************************************************
 * Copyright (c) 2015 Evgeny Tabatsky.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Evgeny Tabatsky - initial API and implementation
 ******************************************************************************/
package jatx.musictransmitter.android;

import jatx.musiccommons.transmitter.TrackInfo;
import jatx.musiccommons.transmitter.TrackInfo.DBCache;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MusicInfoDBHelper extends SQLiteOpenHelper implements DBCache {
	private static final String LOG_TAG_HELPER = "music info db helper";
	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "musictransmitter.db";
	
	private static final String DROP_TRACK_INFO_TABLE =
			"DROP TABLE track_info";
	private static final String CREATE_TRACK_INFO_TABLE = 
    		"CREATE TABLE IF NOT EXISTS track_info " +
    		"(path TEXT PRIMARY KEY ON CONFLICT REPLACE, " +
    		"artist TEXT , " +
    		"album TEXT , " +
    		"title TEXT , " +
    		"year TEXT , " +
    		"length TEXT , " +
    		"number TEXT , " +
    		"last_modified INT)";
	
	public MusicInfoDBHelper(Context context){
	    super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TRACK_INFO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TRACK_INFO_TABLE);
		db.execSQL(CREATE_TRACK_INFO_TABLE);
	}

	@Override
	public synchronized TrackInfo get(String path, long lastModified) {
		String query = "SELECT * FROM track_info "
				+ "WHERE path=" + DatabaseUtils.sqlEscapeString(path);
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			return null;
		}
		
		if (lastModified > cursor.getLong(cursor.getColumnIndex("last_modified"))) {
			return null;
		}
		
		TrackInfo info = new TrackInfo();
		
		info.path = path;
		info.artist = cursor.getString(cursor.getColumnIndex("artist"));
		info.album = cursor.getString(cursor.getColumnIndex("album"));
		info.title = cursor.getString(cursor.getColumnIndex("title"));
		info.year = cursor.getString(cursor.getColumnIndex("year"));
		info.length = cursor.getString(cursor.getColumnIndex("length"));
		info.number = cursor.getString(cursor.getColumnIndex("number"));
		
		cursor.close();
		db.close();
		
		Log.i(LOG_TAG_HELPER, "from db: " + info.toString());
		
		return info;
	}

	@Override
	public synchronized void put(TrackInfo info, long lastModified) {
		SQLiteDatabase db = getWritableDatabase();		
		
		StringBuilder query = new StringBuilder(); 
		query.append("INSERT INTO track_info ");
	    query.append("(path, artist, album, title, ");
	    query.append("year, length, number, last_modified) VALUES (");
	    query.append(DatabaseUtils.sqlEscapeString(info.path)+", ");
	    query.append(DatabaseUtils.sqlEscapeString(info.artist)+", ");
	    query.append(DatabaseUtils.sqlEscapeString(info.album)+", ");
	    query.append(DatabaseUtils.sqlEscapeString(info.title)+", ");
	    query.append(DatabaseUtils.sqlEscapeString(info.year)+", ");
	    query.append(DatabaseUtils.sqlEscapeString(info.length)+", ");
	    query.append(DatabaseUtils.sqlEscapeString(info.number)+", ");
	    query.append(Long.toString(lastModified)+")");
	    db.execSQL(query.toString());
		
		db.close();
		
		Log.i(LOG_TAG_HELPER, "to db: " + info.toString());
	}
}
