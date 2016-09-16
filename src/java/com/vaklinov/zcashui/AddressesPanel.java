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


import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;


/**
 * Addresses...
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class AddressesPanel
	extends JPanel
{
	private ZCashClientCaller clientCaller;

	private JTable addressBalanceTable   = null;
	private JScrollPane addressBalanceTablePane  = null;
	
	String[][] lastAddressBalanceData = null;

	public AddressesPanel(ZCashClientCaller clientCaller)
		throws IOException, InterruptedException, WalletCallException
	{
		this.clientCaller = clientCaller;

		// Build content
		JPanel addressesPanel = this;
		addressesPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		addressesPanel.setLayout(new BorderLayout(0, 0));
		
		addressesPanel.add(new JButton("TODO"), BorderLayout.SOUTH);

		// Table of transactions
		lastAddressBalanceData = getAddressBalanceDataFromWallet();
		addressesPanel.add(addressBalanceTablePane = new JScrollPane(
				               addressBalanceTable = this.createAddressBalanceTable(lastAddressBalanceData)),
				           BorderLayout.CENTER);



	}


	private void updateWalletAddressBalanceTable()
		throws WalletCallException, IOException, InterruptedException
	{
		String[][] newAddressBalanceData = this.getAddressBalanceDataFromWallet();

		if (lastAddressBalanceData.length != newAddressBalanceData.length)
		{
			this.remove(addressBalanceTablePane);
			this.add(addressBalanceTablePane = new JScrollPane(
			             addressBalanceTable = this.createAddressBalanceTable(newAddressBalanceData)),
			         BorderLayout.CENTER);
		}

		lastAddressBalanceData = newAddressBalanceData;

		this.validate();
		this.repaint();
	}


	private JTable createAddressBalanceTable(String rowData[][])
		throws WalletCallException, IOException, InterruptedException
	{
		String columnNames[] = { "Balance", "Address" };
        JTable table = new JTable(rowData, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(1000);

        return table;
	}


	private String[][] getAddressBalanceDataFromWallet()
		throws WalletCallException, IOException, InterruptedException
	{
		return new String[][]
		{
			{ "0.1", "A1AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
			{ "0.2", "A2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
			{ "0.3", "A3AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
			{ "0.4", "A4AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" },
			{ "0.5", "A5AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" }
		};
	}
}
