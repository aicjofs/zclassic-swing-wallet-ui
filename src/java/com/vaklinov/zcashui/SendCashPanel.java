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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;


/**
 * ... for sending cash
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class SendCashPanel
	extends JPanel
{
	
	private ZCashClientCaller clientCaller;
	
	private JComboBox balanceAddressCombo      = null;
	private String[][] lastAddressBalanceData  = null;
	
	private JTextField destinationAddressField = null;

	public SendCashPanel(ZCashClientCaller clientCaller)
		throws IOException, InterruptedException, WalletCallException
	{
		this.clientCaller = clientCaller;

		// Build content
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.setLayout(new BorderLayout());
		JPanel sendCashPanel = new JPanel();
		this.add(sendCashPanel, BorderLayout.NORTH);
		sendCashPanel.setLayout(new BoxLayout(sendCashPanel, BoxLayout.Y_AXIS));
		sendCashPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Send cash from:"));
		sendCashPanel.add(tempPanel);

		balanceAddressCombo = new JComboBox<>(new String[] { "ONE", "TWO", "THREE" });
		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(balanceAddressCombo);
		sendCashPanel.add(tempPanel);
		
		sendCashPanel.add(new JLabel("  "));

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Destination address:"));
		sendCashPanel.add(tempPanel);
		
		destinationAddressField = new JTextField(70);
		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tempPanel.add(destinationAddressField);
		sendCashPanel.add(tempPanel);
		
		sendCashPanel.add(new JLabel("  "));

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("TODO: Sending cash is not yet implemented"));
		sendCashPanel.add(tempPanel);

	
	}

		

	private void updateWalletAddressPositiveBalanceComboBox()
		throws WalletCallException, IOException, InterruptedException
	{
		String[][] newAddressBalanceData = this.getAddressPositiveBalanceDataFromWallet();

		// TODO: do the update
		
		lastAddressBalanceData = newAddressBalanceData;

		this.validate();
		this.repaint();
	}


	private String[][] getAddressPositiveBalanceDataFromWallet()
		throws WalletCallException, IOException, InterruptedException
	{
		// Z Addresses - they are OK
		String[] zAddresses = clientCaller.getWalletZAddresses();
		
		// T Addresses created by GUI only
		// TODO: What if wallet is changed - stored addresses are invalid?!!
		String[] tAddresses = AddressesPanel.getCreatedAndStoredTAddresses();
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
		
		String[][] tempAddressBalances = new String[zAddresses.length + tAddressesCombined.size()][];
		
		int count = 0;

		for (String address : tAddressesCombined)
		{
			String balance = this.clientCaller.getBalanceForAddress(address);
			if (Double.valueOf(balance) > 0)
			{
				tempAddressBalances[count++] = new String[] 
				{  
					balance, address
				};
			}
		}
		
		for (String address : zAddresses)
		{
			String balance = this.clientCaller.getBalanceForAddress(address);
			if (Double.valueOf(balance) > 0)
			{
				tempAddressBalances[count++] = new String[] 
				{  
					balance, address
				};
			}
		}

		String[][] addressBalances = new String[count][];
		System.arraycopy(tempAddressBalances, 0, addressBalances, 0, count);
		
		return addressBalances;
	}
	
	
}
