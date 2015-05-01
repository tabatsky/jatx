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
package jatx.musictransmitter.fx;

import jatx.musiccommons.transmitter.TrackInfo;
import jatx.musiccommons.transmitter.TrackInfo.DBCache;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDBCache implements DBCache {
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
	
	private Connection mConnect = null;
	
	public SQLiteDBCache() {
		 try {
			 Class.forName("org.sqlite.JDBC");
			 mConnect = DriverManager.getConnection("jdbc:sqlite:" 
					 	+ Main.SETTINGS_DIR_PATH + File.separator + "musicinfo.db");
			 System.out.println("DB opened successfully");
			 
			 Statement st = mConnect.createStatement();
			 st.execute(CREATE_TRACK_INFO_TABLE);
			 st.close();
			 
			 System.out.println("Table created successfully");
		 } catch ( Exception e ) {
			 e.printStackTrace();
		 }
	}
	
	protected void finalize() {
		if (mConnect!=null) {
			try {
				mConnect.close();
				System.out.println("DB closed successfully");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public TrackInfo get(String path, long lastModified) {
		if (mConnect==null) return null;
				
		try {
			String query = "SELECT * FROM track_info WHERE path=?";
			PreparedStatement ps = mConnect.prepareStatement(query);
			ps.setString(1, path);
			
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			if (lastModified > rs.getLong("last_modified")) {
				return null;
			}
			
			TrackInfo info = new TrackInfo();
			
			info.path = rs.getString("path");
			info.artist = rs.getString("artist");
			info.album = rs.getString("album");
			info.title = rs.getString("title");
			info.year = rs.getString("year");
			info.length = rs.getString("length");
			info.number = rs.getString("number");
			
			System.out.println("from db: " + info.toString());
			
			return info;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void put(TrackInfo info, long lastModified) {
		if (mConnect==null) return;
		
		try {
			StringBuilder query = new StringBuilder(); 
			query.append("INSERT INTO track_info ");
		    query.append("(path, artist, album, title, ");
		    query.append("year, length, number, last_modified) VALUES (");
		    query.append("?, ?, ?, ?, ?, ?, ?, ?)");
			
			PreparedStatement ps = mConnect.prepareStatement(query.toString());
			
			ps.setString(1, info.path);
			ps.setString(2, info.artist);
			ps.setString(3, info.album);
			ps.setString(4, info.title);
			ps.setString(5, info.year);
			ps.setString(6, info.length);
			ps.setString(7, info.number);
			ps.setString(8, Long.toString(lastModified));
			
			ps.executeUpdate();
			
			ps.close();
			
			System.out.println("to db: " + info.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
