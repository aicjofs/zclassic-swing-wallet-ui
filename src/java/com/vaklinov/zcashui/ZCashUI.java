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


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;
import com.vaklinov.zcashui.ZCashInstallationObserver.InstallationDetectionException;


/**
 * Main ZCash Window.
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class ZCashUI
    extends JFrame
{
    private ZCashInstallationObserver installationObserver;
    private ZCashClientCaller clientCaller;
    private StatusUpdateErrorReporter errorReporter;

    private WalletOperations walletOps;

    private JMenuItem menuItemExit;
    private JMenuItem menuItemAbout;
    private JMenuItem menuItemEncrypt;
    private JMenuItem menuItemBackup;

    private DashboardPanel dashboard;
    private AddressesPanel addresses;
    private SendCashPanel  sendPanel;

    public ZCashUI()
        throws IOException, InterruptedException, WalletCallException
    {
        super("ZCash Swing Wallet UI 0.26 (beta)");
        ClassLoader cl = this.getClass().getClassLoader();

        this.setIconImage(new ImageIcon(cl.getResource("images/Z-yellow.orange-logo.png")).getImage());

        Container contentPane = this.getContentPane();

        errorReporter = new StatusUpdateErrorReporter(this);
        installationObserver = new ZCashInstallationObserver(OSUtil.getProgramDirectory());
        clientCaller = new ZCashClientCaller(OSUtil.getProgramDirectory());

        // Build content
        JTabbedPane tabs = new JTabbedPane();
        Font oldTabFont = tabs.getFont();
        Font newTabFont  = new Font(oldTabFont.getName(), Font.BOLD | Font.ITALIC, oldTabFont.getSize() * 57 / 50);
        tabs.setFont(newTabFont);
        tabs.addTab("Overview ",
        		    new ImageIcon(cl.getResource("images/overview.png")),
        		    dashboard = new DashboardPanel(installationObserver, clientCaller, errorReporter));
        tabs.addTab("Own addresses ",
        		    new ImageIcon(cl.getResource("images/address-book.png")),
        		    addresses = new AddressesPanel(clientCaller, errorReporter));
        tabs.addTab("Send cash ",
        		    new ImageIcon(cl.getResource("images/send.png")),
        		    sendPanel = new SendCashPanel(clientCaller, errorReporter));
        contentPane.add(tabs);

        this.walletOps = new WalletOperations(
            	this, this.dashboard, this.sendPanel, installationObserver, clientCaller, errorReporter);

        this.setSize(new Dimension(870, 427));

        // Build menu
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu("Main");
        file.setMnemonic(KeyEvent.VK_M);
        file.add(menuItemAbout = new JMenuItem("About...", KeyEvent.VK_A));
        file.addSeparator();
        file.add(menuItemExit = new JMenuItem("Quit", KeyEvent.VK_Q));
        mb.add(file);

        JMenu wallet = new JMenu("Wallet");
        wallet.setMnemonic(KeyEvent.VK_W);
        wallet.add(menuItemBackup = new JMenuItem("Backup...", KeyEvent.VK_B));
        wallet.add(menuItemEncrypt = new JMenuItem("Encrypt...", KeyEvent.VK_E));
        mb.add(wallet);

        // TODO: Temporarily disable encryption until further notice - Oct 24 2016
        menuItemEncrypt.setEnabled(false);
        
        // TODO: Temporary warning regarding spending mined coins
        // https://github.com/zcash/zcash/issues/1616
        tabs.addChangeListener(
        	new ChangeListener() 
        	{	
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					JTabbedPane tabs = (JTabbedPane)e.getSource();
					if (tabs.getSelectedIndex() == 2)
					{
		                try
		                {
		                    String userDir = OSUtil.getSettingsDirectory();
		                    File warningFlagFile = new File(userDir + "/warningOnIssue1616Shown.flag");
		                    if (warningFlagFile.exists())
		                    {
		                        return;
		                    } else
		                    {
		                        warningFlagFile.createNewFile();
		                    }

		                } catch (IOException ioe)
		                {
		                    /* TODO: report exceptions to the user */
		                    ioe.printStackTrace();
		                }
		                
		                JOptionPane.showMessageDialog(
		                    ZCashUI.this.getRootPane().getParent(),
		                    "The ZCash 1.0 release has a known issue when spending freshly mined cash. \n" + 
		                    "When you spend freshly mined cash from a T address to a Z address, you must \n" +
		                    "spend the entire available mined T address balance. If you attempt to spend\n" +
		                    "only a part of it, the entire balance will be spent and sent to the specified\n" +
		                    "destination address anyway! \n\n" +
		                    "For full details see issue: https://github.com/zcash/zcash/issues/1616\n" +
		                    "\n" +
		                    "(This message will be shown only once)",
		                    "Warning on spending newly mined cash...", JOptionPane.WARNING_MESSAGE);

					}
				}
			}
        );
        // END  warning regarding spending mined coins
        
        this.setJMenuBar(mb);

        // Add listeners etc.
        menuItemExit.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.exitProgram();
                }
            }
        );

        menuItemAbout.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    AboutDialog ad = new AboutDialog(ZCashUI.this);
                    ad.setVisible(true);
                }
            }
        );

        menuItemBackup.addActionListener(   
        	new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.backupWallet();
                }
            }
        );
        
        menuItemEncrypt.addActionListener(
            new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    ZCashUI.this.walletOps.encryptWallet();
                }
            }
        );


        // Close operation
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                ZCashUI.this.exitProgram();
            }
        });

        // Show initial message
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    String userDir = OSUtil.getSettingsDirectory();
                    File warningFlagFile = new File(userDir + "/initialInfoShown.flag");
                    if (warningFlagFile.exists())
                    {
                        return;
                    } else
                    {
                        warningFlagFile.createNewFile();
                    }

                } catch (IOException ioe)
                {
                    /* TODO: report exceptions to the user */
                    ioe.printStackTrace();
                }

                JOptionPane.showMessageDialog(
                    ZCashUI.this.getRootPane().getParent(),
                    "The ZCash GUI Wallet is currently considered experimental. Use of this software\n" +
                    "comes at your own risk! Be sure to read the list of known issues and limitations\n" +
                    "at this page: \n" +
                    "https://github.com/vaklinov/zcash-swing-wallet-ui#known-issues-and-limitations\n\n" +
                    "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                    "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                    "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                    "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                    "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                    "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN\n" +
                    "THE SOFTWARE.\n\n" +
                    "(This message will be shown only once)",
                    "Disclaimer", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void exitProgram()
    {
        System.out.println("Exiting ...");

        this.dashboard.stopThreadsAndTimers();

        ZCashUI.this.setVisible(false);
        ZCashUI.this.dispose();

        System.exit(0);
    }

    public static void main(String argv[])
        throws IOException
    {
        try
        {
            System.out.println("Starting ZCash Swing Wallet ...");
            System.out.println("Current directory: " + new File(".").getCanonicalPath());
            System.out.println("Class path: " + System.getProperty("java.class.path"));
            System.out.println("Environment PATH: " + System.getenv("PATH"));

            ////////////////////////////////////////////////////////////
            for (LookAndFeelInfo ui : UIManager.getInstalledLookAndFeels())
            {
                System.out.println("Available look and feel: " + ui.getName() + " " + ui.getClassName());
                if (ui.getName().equals("Nimbus"))
                {
                    UIManager.setLookAndFeel(ui.getClassName());
                    break;
                }
            }

            /////////////////////////////////////////////////////
            ZCashUI ui = new ZCashUI();
            ui.setVisible(true);

        } catch (InstallationDetectionException ide)
        {
            ide.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "This program was started in directory: " + OSUtil.getProgramDirectory() + "\n" +
                ide.getMessage() + "\n" +
                "See the console output for more detailed error information!",
                "Installation error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (WalletCallException wce)
        {
            wce.printStackTrace();

            if ((wce.getMessage().indexOf("{\"code\":-28,\"message\":\"Verifying blocks")      != -1)  ||
            	(wce.getMessage().indexOf("{\"code\":-28,\"message\":\"Rescanning")            != -1)  ||
            	(wce.getMessage().indexOf("{\"code\":-28,\"message\":\"Loading wallet")        != -1)  ||
            	(wce.getMessage().indexOf("{\"code\":-28,\"message\":\"Activating best chain") != -1))
            {
                JOptionPane.showMessageDialog(
                        null,
                        "It appears that zcashd has been started but is not ready to accept wallet\n" +
                        "connections. It is still loading the wallet and blockchain. Please try to \n" +
                        "start the GUI wallet later...",
                        "Wallet communication error",
                        JOptionPane.ERROR_MESSAGE);
            } else
            {
                JOptionPane.showMessageDialog(
                    null,
                    "There was a problem communicating with the ZCash daemon/wallet. \n" +
                    "Please ensure that the ZCash server zcashd is started (e.g. via \n" + 
                    "command  \"zcashd --daemon\"). Error message is: \n" +
                     wce.getMessage() +
                    "See the console output for more detailed error information!",
                    "Wallet communication error",
                    JOptionPane.ERROR_MESSAGE);
            }

            System.exit(2);
        } catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "A general unexpected critical error has occurred: \n" + e.getMessage() + "\n" +
                "See the console output for more detailed error information!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(3);
        }
    }
}
