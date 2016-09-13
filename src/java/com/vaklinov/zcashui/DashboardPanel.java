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


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import com.vaklinov.zcashui.ZCashClientCaller.WalletBalance;
import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;
import com.vaklinov.zcashui.ZCashInstallationObserver.DAEMON_STATUS;
import com.vaklinov.zcashui.ZCashInstallationObserver.DaemonInfo;


/**
 * Dashboard ...
 * 
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class DashboardPanel 
	extends JPanel
{	
	private ZCashInstallationObserver installationObserver;
	private ZCashClientCaller clientCaller;
	
	private JLabel daemonStatusLabel;
	private JLabel walletBalanceLabel;
	
	public DashboardPanel(ZCashInstallationObserver installationObserver,
			              ZCashClientCaller clientCaller)
		throws IOException, InterruptedException, WalletCallException
	{
		this.installationObserver = installationObserver;
		this.clientCaller = clientCaller;
				
		// Build content
		JPanel dashboard = this;
		dashboard.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		dashboard.setLayout(new BorderLayout(0, 0));
		
		// Upper panel with wallet balance
		JPanel balanceStatusPanel = new JPanel();
		balanceStatusPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
		balanceStatusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		JLabel zcLabel = new JLabel("ZCash Wallet  ");
		zcLabel.setFont(new Font("Helvetica", Font.BOLD, 35));
		balanceStatusPanel.add(zcLabel);
		balanceStatusPanel.add(walletBalanceLabel = new JLabel());
		this.updateWalletStatusLabel();
		dashboard.add(balanceStatusPanel, BorderLayout.NORTH);

		// Table of transactions
		String columnNames[] = { "Direction", "Amount", "Address"};
		String rowData[][] = 
		{ 
			{ "=> IN", "123", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
            { "<= OUT", "456", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
			{ "=> IN", "123", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
            { "<= OUT", "456", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
			{ "=> IN", "123", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
            { "<= OUT", "456", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
			{ "=> IN", "123", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},
            { "<= OUT", "456", "tnQKD37NyaeUCkhMer52b1AQkLHQTTTe46utecKHdbvjT29bBfELDMrnynLTuTPAFFbSJd4nNvnUs8EfQZiiz3oaD99HNci"},            
		};
        JTable table = new JTable(rowData, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(900);
		dashboard.add(new JScrollPane(table), BorderLayout.CENTER);

		// Lower panel with installation status
		JPanel installationStatusPanel = new JPanel();
		installationStatusPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
		installationStatusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		installationStatusPanel.add(daemonStatusLabel = new JLabel());		
		this.updateDaemonStatusLabel();
		dashboard.add(installationStatusPanel, BorderLayout.SOUTH);		
		
		// Start timer to refresh the status
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					DashboardPanel.this.updateDaemonStatusLabel();
					DashboardPanel.this.updateWalletStatusLabel();
				} catch (Exception ex) 
				{ 
					/* TODO: -report exceptions to teh user */ 
					ex.printStackTrace();
				}
			}
		};
		new Timer(2000, al).start();

	}		
	
	
	private void updateDaemonStatusLabel()
		throws IOException, InterruptedException
	{
		DaemonInfo daemonInfo = installationObserver.getDaemonInfo();
		String daemonStatus = "<span style=\"color:green;font-weight:bold\">RUNNING</span>";
		if (daemonInfo.status != DAEMON_STATUS.RUNNING)
		{
			daemonStatus = "<span style=\"color:red;font-weight:bold\">NOT RUNNING</span>";
		}
		String runtimeInfo = "";
		if (daemonInfo.status == DAEMON_STATUS.RUNNING)
		{
			runtimeInfo = "Resident: " + daemonInfo.residentSizeMB + "MB, Virtual: " + daemonInfo.virtualSizeMB +
					      " MB, CPU Usage: " + daemonInfo.cpuPercentage + "%";
		}

		String text = 
			"<html>Daemon status: " + daemonStatus + ",   " + runtimeInfo + " <br/>" +
			"Tools directory: " + OSUtil.getProgramDirectory() + " <br/> " +
	        "Blockchain directory: " + OSUtil.getBlockchainDirectory()+ " <br/> " +
		    "System: " + OSUtil.getSystemInfo() +
			"</html>";
		this.daemonStatusLabel.setText(text);
	}
	
	private void updateWalletStatusLabel()
		throws WalletCallException, IOException, InterruptedException
	{
		WalletBalance balance = this.clientCaller.getWalletInfo();
		
		String text = 			
			"<html><span style=\"font-weight:bold\">Balance: " + balance.balance + "</span><br/> " +
			"Immature: <span style=\"font-weight:bold\">" + balance.imatureBalance + "</span><br/> " + 
			"Unconfirmed: <span style=\"color:orange;font-weight:bold\">" + balance.unconfirmedBalance + "</span> <br/>  </html>";
		this.walletBalanceLabel.setText(text);
	}
}
