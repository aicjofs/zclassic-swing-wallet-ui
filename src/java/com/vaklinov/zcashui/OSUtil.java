/************************************************************************************************
 *  _________          _     ____          _           __        __    _ _      _   _   _ ___
 * |__  / ___|__ _ ___| |__ / ___|_      _(_)_ __   __ \ \      / /_ _| | | ___| |_| | | |_ _|
 *   / / |   / _` / __| '_ \\___ \ \ /\ / / | '_ \ / _` \ \ /\ / / _` | | |/ _ \ __| | | || |
 *  / /| |__| (_| \__ \ | | |___) \ V  V /| | | | | (_| |\ V  V / (_| | | |  __/ |_| |_| || |
 * /____\____\__,_|___/_| |_|____/ \_/\_/ |_|_| |_|\__, | \_/\_/ \__,_|_|_|\___|\__|\___/|___|
 *                                                 |___/
 *
 * Copyright (c) 2016 Ivan Vaklinov <ivan@vaklinov.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 **********************************************************************************/
package com.vaklinov.zcashui;


import java.io.File;
import java.io.IOException;


/**
 * Utilities - may be OS dependent.
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class OSUtil
{

//	public static enum OS_TYPE
//	{
//		LINUX, WINDOWS, MAC_OS, FREE_BSD, OTHER_BSD, SOLARIS, AIX, OTHER_OS
//	};


	public static String getProgramDirectory()
		throws IOException
	{
		// TODO: this way of finding the dir is JAR name dependent - tricky, may not work
		// if program is repackaged as different JAR!
		final String JAR_NAME = "ZCashSwingWalletUI.jar";
		String cp = System.getProperty("java.class.path");
		if ((cp != null) && (cp.indexOf(File.pathSeparator) == -1) &&
			(cp.endsWith(JAR_NAME)))
		{
			File pd = new File(cp.substring(0, cp.length() - JAR_NAME.length()));

			if (pd.exists() && pd.isDirectory())
			{
				return pd.getCanonicalPath();
			}
		}

		String userDir = System.getProperty("user.dir");
		if (userDir != null)
		{
			File ud = new File(userDir);

			if (ud.exists() && ud.isDirectory())
			{
				return ud.getCanonicalPath();
			}
		}

		// TODO: tests and more options

		return new File(".").getCanonicalPath();
	}


	public static String getBlockchainDirectory()
		throws IOException
	{
		return new File(System.getProperty("user.home") + "/.zcash").getCanonicalPath();
	}


	// Directory with program settings to store
	public static String getSettingsDirectory()
		throws IOException
	{
		File dir = new File(System.getProperty("user.home") + "/.ZCashSwingWalletUI");
		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.getCanonicalPath();
	}


	public static String getSystemInfo()
		throws IOException, InterruptedException
	{
		CommandExecutor uname = new CommandExecutor(new String[] { "uname", "-srv" });
		return uname.execute();
	}


	// Can be used to find zcashd/zcash-cli
	// Null if not found
	public static File findZCashCommand(String command)
	{
		final String dirs[] = new String[]
		{
			"/usr/bin/", // Typical Ubuntu
			"/bin/",
			"/usr/local/bin/",
			"/usr/local/zcash/bin/",
			"/usr/lib/zcash/bin/",
			"/opt/local/bin/",
			"/opt/local/zcash/bin/",
			"/opt/zcash/bin/"
		};

		for (String d : dirs)
		{
			File f = new File(d + command);
			if (f.exists())
			{
				return f;
			}
		}

		// TODO: Try to find it with which
		return null;
	}
}
