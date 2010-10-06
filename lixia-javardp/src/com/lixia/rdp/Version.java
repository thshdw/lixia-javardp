/* Version.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.1.1.1 $
 * Author: $Author: suvarov $
 * Date: $Date: 2007/03/08 00:26:26 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Stores version information
 */

package com.lixia.rdp;

import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Records the current version information of properJavaRDP
 */

public class Version {
	public static String version = "2.0";

	/**
	 * Display the current version of properJavaRDP
	 */
	public static void main(String[] argv) {
		try {
			SimpleDateFormat format =
	            new SimpleDateFormat("yyyyMMdd");
			String buildVersion = version + "-" + format.format(Calendar.getInstance().getTime());
			if (argv.length == 0) {
				System.out.println(buildVersion);
			} else {
				String filename = argv[0];
				System.out.println("Writing version information to: "
						+ filename);
				PrintWriter file = new PrintWriter(new FileOutputStream(
						filename), true);

				file.println("product.version=" + buildVersion);
				file.close();
			}
		} catch (Exception e) {
			System.err.println("Problem writing version information: " + e);
			e.printStackTrace(System.err);
		}
	}
}
