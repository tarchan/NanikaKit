/*
 * NanikaTest.java
 * NanikaKit
 *
 * Created by tarchan on 2008/03/26.
 * Copyright (c) 2008 tarchan. All rights reserved.
 */
package test.com.mac.tarchan.nanika;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mac.tarchan.nanika.nar.NanikaArchive;
import com.mac.tarchan.nanika.nar.NanikaEntry;

/**
 * @version 1.0
 * @author tarchan
 */
public class NanikaTest
{
	/** ロガー */
	private static final Log log = LogFactory.getLog(NanikaTest.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			System.out.println("user.dir=" + System.getProperty("user.dir"));
			NanikaArchive nar = new NanikaArchive(args[0]);
			log.debug("nar=" + nar);
			Properties props = nar.getProperties();

			File install = new File(props.getProperty("nanika.install"));
			NanikaEntry installEntry = nar.getEntry(install);
//			readText(installEntry);
			installEntry.readDescript();

			File shellHome = new File(props.getProperty("nanika.shell"), props.getProperty("nanika.shell.home"));
			File shellDescript = new File(shellHome, props.getProperty("nanika.shell.descript"));
			log.debug("shellDescript=" + shellDescript);
			NanikaEntry shellDescriptEntry = nar.getEntry(shellDescript);
			shellDescriptEntry.readDescript();
//			readText(shellDescriptEntry);

			File shellSurfaces = new File(shellHome, props.getProperty("nanika.shell.surfaces"));
			log.debug("shellSurfaces=" + shellSurfaces);
			NanikaEntry shellSurfacesEntry = nar.getEntry(shellSurfaces);
			shellSurfacesEntry.readDescript();
//			readText(shellSurfacesEntry);

			nar.getSurface("0");
		}
		catch (IOException e)
		{
			log.error("何かエラー", e);
		}
	}

//	private static void readText(NanikaEntry entry) throws IOException
//	{
//		Properties descript = new Properties();
//		log.debug("entry=" + entry.getName());
//		InputStream in = entry.getInputStream();
//		String enc = "Shift_JIS";
//		BufferedReader reader = new BufferedReader(new InputStreamReader(in, enc));
//		int index = 0;
//		while (true)
//		{
//			String line = reader.readLine();
//			if (line == null) break;
//
//			String[] token = line.split(" *, *", 2);
//			log.debug(String.format("%2d: %s", ++index, Arrays.toString(token)));
//			if (token.length == 2)
//			{
//				descript.setProperty(token[0], token[1]);
//			}
//		}
//	}
}
