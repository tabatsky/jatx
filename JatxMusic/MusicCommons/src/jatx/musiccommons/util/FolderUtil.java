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
import java.util.ArrayList;
import java.util.List;

public class FolderUtil {
	public static List<File> findFiles(String startPath, String pattern) {
		List<File> dirs  = new ArrayList<File>();
		List<File> files = new ArrayList<File>();
		
		File startDir = new File(startPath);
		if (!startDir.exists()||!startDir.isDirectory()) {
			System.out.println("Wrong start path");
			System.exit(0);
		}

		File dir = startDir;
		
		int next = 0;
		
		while (true) {			
		File[] list = dir.listFiles();
				
			for (int i=0; list!=null&&i<list.length; i++) {
				File tmp = list[i];
				if (tmp.isDirectory()) {
					dirs.add(tmp);
				} else if (tmp.isFile()&&tmp.getName().matches(pattern)) {
					files.add(tmp);
				}
			}
 			
			if (next>=dirs.size()) {
				break;
			}
			
			dir = dirs.get(next);
			next++;
		}
		
		return files;
	}
}
