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
package jatx.musiccommons.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Debug {
	public static boolean isCustomHandler = false;
	
    private static Thread.UncaughtExceptionHandler defaultUEH;
    private static Thread.UncaughtExceptionHandler customUEH;
    
    private static File sLogDir;
    
    public static void setCustomExceptionHandler(File logDir) {
    	if (isCustomHandler) {
    		System.out.println("(debug) custom handler already set");
    		return;
    	}
    	System.out.println("(debug) setting custom handler");

    	sLogDir = logDir;
    	
    	defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		customUEH = new Thread.UncaughtExceptionHandler() {
			 public void uncaughtException(final Thread thread, final Throwable ex) {
			     System.err.println("(uncaught error) " + Debug.exceptionToString(ex));
			     Debug.exceptionToFile(ex, "fatal_error_");
			     //Debug.exceptionToDeveloper(ex, "Fatal Error");

			     defaultUEH.uncaughtException(thread, ex);
			 }
		};
		Thread.setDefaultUncaughtExceptionHandler(customUEH);
		
		isCustomHandler = true;
    }
	
	public static String exceptionToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public static void exceptionToFile(Throwable e, String prefix) {
		if (sLogDir==null) return;
	    sLogDir.mkdirs();
	    if (!sLogDir.exists()) return;
	     
	    SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_HH_mm_ss");
	     
	    File errorDump = new File(
	    		 sLogDir.getAbsolutePath()
	    		 + File.separator
	    		 + prefix
	    		 + sdf.format(new Date())
	    		 + ".txt");
	     
	    try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(errorDump));
			pw.println(exceptionToString(e));
			pw.close();
	    } catch (FileNotFoundException ex) {}
	}
	
	/*
	public static void exceptionToDeveloper(Throwable e, String title) {
		MailHelper.sendEmailToDev(title, exceptionToString(e));
	} 
	*/
	
	public static void logToFile(String data) {
		if (sLogDir==null) return;
	    sLogDir.mkdirs();
	    if (!sLogDir.exists()) return;
		
		//File dataDir = appContext.getExternalFilesDir(null);
	    //dataDir.mkdirs();
	     
	    SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_HH_mm_ss");
	     
	    File errorDump = new File(
	    		 sLogDir.getAbsolutePath()
	    		 + File.separator
	    		 + "data_log_"
	    		 + sdf.format(new Date())
	    		 + ".txt");
	     
	    try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(errorDump));
			pw.println(data);
			pw.close();
	    } catch (FileNotFoundException ex) {}
	}
}
