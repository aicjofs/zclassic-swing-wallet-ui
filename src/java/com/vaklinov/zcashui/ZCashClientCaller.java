/***********************************************************************************
 *  _________          _       _   _ ___ 
 * |__  / ___|__ _ ___| |__   | | | |_ _|
 *   / / |   / _` / __| '_ \  | | | || | 
 *  / /| |__| (_| \__ \ | | | | |_| || | 
 * /____\____\__,_|___/_| |_|  \___/|___|
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

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * Calls zcash-cli
 * 
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class ZCashClientCaller 
{	
	public static class WalletBalance
	{
		public double balance;
		public double unconfirmedBalance;
		public double imatureBalance;
	}
	
	public static class WalletCallException
		extends Exception
	{
		public WalletCallException(String message)
		{
			super(message);
		}
	}
	
	private File zcashcli;
	
	public ZCashClientCaller(String installDir)
		throws IOException
	{
		// Detect daemon and client tools installation
		File dir = new File(installDir);
	    zcashcli = new File(dir, "zcash-cli");
		
		if (!zcashcli.exists())
		{
			throw new IOException(
				"The ZCash installation directory " + installDir + " needs to contain " +
				"the command line utilities zcashd and zcash-cli. zcash-cli is missing!");
		}		
	}	
	
	
	public synchronized WalletBalance getWalletInfo()
		throws WalletCallException, IOException, InterruptedException
	{
	    CommandExecutor caller = new CommandExecutor(new String[] {
	    	this.zcashcli.getCanonicalPath(), "getwalletinfo"
	    });
		
		JsonValue response = Json.parse(caller.execute());
		WalletBalance balance = new WalletBalance();
		
		if (response.isObject())
		{
			JsonObject objResponse = response.asObject();
		    if (objResponse.get("error") != null)
		    {
		    	throw new WalletCallException("Error response from wallet: " + response.toString());
		    } else
		    {    	
		    	balance.balance = objResponse.getDouble("balance", -1);
		    	balance.unconfirmedBalance = objResponse.getDouble("unconfirmed_balance", -1);
		    	balance.imatureBalance = objResponse.getDouble("immature_balance", -1);		    	
		    }
		} else
		{
			throw new WalletCallException("Unexpected non-object response from wallet: " + response.toString());
		}
		
		return balance;
	}
	
	
	
}
