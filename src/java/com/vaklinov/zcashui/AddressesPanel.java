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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;

import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;


/**
 * Addresses...
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class AddressesPanel
	extends JPanel
{
	private static final String T_ADDRESSES_FILE = "CreatedTransparentAddresses.txt";
	
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
	
		// Build panel of buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
		buttonPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JButton newTAddressButton = new JButton("New T (Transparent) address");
		buttonPanel.add(newTAddressButton);
		JButton newZAddressButton = new JButton("New Z (Private) address");
		buttonPanel.add(newZAddressButton);
		buttonPanel.add(new JLabel("           "));
		JButton refreshButton = new JButton("Refresh");
		buttonPanel.add(refreshButton);
		
		addressesPanel.add(buttonPanel, BorderLayout.SOUTH);

		// Table of transactions
		lastAddressBalanceData = getAddressBalanceDataFromWallet();
		addressesPanel.add(addressBalanceTablePane = new JScrollPane(
				               addressBalanceTable = this.createAddressBalanceTable(lastAddressBalanceData)),
				           BorderLayout.CENTER);
		
		// Button actions
		refreshButton.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					AddressesPanel.this.updateWalletAddressBalanceTable();
				} catch (Exception ex)
				{
					/* TODO: report exceptions to the user */
					ex.printStackTrace();
				}
			}
		});
		
		newTAddressButton.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{
				createNewAddress(false);
			}
		});
		
		newZAddressButton.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{
				createNewAddress(true);
			}
		});
	}

	
	private void createNewAddress(boolean isZAddress)
	{
		try
		{
			String address = this.clientCaller.createNewAddress(isZAddress);
			
			if (!isZAddress)
			{
				this.addCreatedTAddress(address);
			}
			
			JOptionPane.showMessageDialog(
				this.getRootPane().getParent(), 
				"A new " + (isZAddress ? "Z (Private)" : "T (Transparent)") 
				+ " address has been created cuccessfully:\n" + address, 
				"Title", JOptionPane.INFORMATION_MESSAGE);
			
			this.updateWalletAddressBalanceTable();
		} catch (Exception e)
		{
			/* TODO: report exceptions to the user */
			e.printStackTrace();			
		}
	}
	

	private void updateWalletAddressBalanceTable()
		throws WalletCallException, IOException, InterruptedException
	{
		String[][] newAddressBalanceData = this.getAddressBalanceDataFromWallet();

		//if (lastAddressBalanceData.length != newAddressBalanceData.length) -always refreshed
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
		// Z Addresses - they are OK
		String[] zAddresses = clientCaller.getWalletZAddresses();
		
		// T Addresses created by GUI only
		// TODO: What if wallet is changed -stored addresses are invalid?!!
		String[] tAddresses = this.getCreatedAndStoredTAddresses();
		Set<String> tStoredAddressSet = new HashSet<>();
		for (String address : tAddresses)
		{
			tStoredAddressSet.add(address);
		}
		
		// T addresses with unspent outputs (even if not GUI created)...
		String[] tAddressesWithUnspentOuts = this.clientCaller.getWalletPublicAddressesWithUnspentOutputs();
		Set<String> tAddressSetWithUnspentOuts = new HashSet<>();
		for (String address : tAddressesWithUnspentOuts)
		{
			tAddressSetWithUnspentOuts.add(address);
		}
		
		// Combine all known T addresses
		Set<String> tAddressesCombined = new HashSet<>();
		tAddressesCombined.addAll(tStoredAddressSet);
		tAddressesCombined.addAll(tAddressSetWithUnspentOuts);
		
		String[][] addressBalances = new String[zAddresses.length + tAddressesCombined.size()][];
		
		int i = 0;

		for (String address : tAddressesCombined)
		{
			addressBalances[i++] = new String[] 
			{  
				this.clientCaller.getBalanceForAddress(address),
				address
			};
		}
		
		for (String address : zAddresses)
		{
			addressBalances[i++] = new String[] 
			{  
				this.clientCaller.getBalanceForAddress(address),
				address
			};
		}

		return addressBalances;
	}
	
	
	private String[] getCreatedAndStoredTAddresses()
		throws IOException
	{
		File tAddressesFile = new File(OSUtil.getSettingsDirectory() + "/" + T_ADDRESSES_FILE);
		
		if (!tAddressesFile.exists())
		{
			return new String[0];
		}
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(tAddressesFile));
		Set<String> addressSet = new HashSet<String>();
		
		String line;
		while ((line = lnr.readLine()) != null)
		{
			addressSet.add(line.trim());
		}
		
		return addressSet.toArray(new String[0]);
	}
	
	
	private void addCreatedTAddress(String address)
		throws IOException
	{
		File tAddressesFile = new File(OSUtil.getSettingsDirectory() + "/" + T_ADDRESSES_FILE);
		if (!tAddressesFile.exists())
		{
			tAddressesFile.createNewFile();
		}
		
		 long rafLength = tAddressesFile.length();
		 RandomAccessFile raf = new RandomAccessFile(tAddressesFile, "rw");
		 raf.seek(rafLength);
		 raf.write((address + "\n").getBytes());
		 raf.close();
	}
}
