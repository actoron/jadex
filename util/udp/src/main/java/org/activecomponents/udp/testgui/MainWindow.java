/**
 * 
 */
package org.activecomponents.udp.testgui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.activecomponents.udp.Connection;
import org.activecomponents.udp.DaemonThreadExecutor;
import org.activecomponents.udp.IConnectionListener;
import org.activecomponents.udp.SUdpUtil;
import org.activecomponents.udp.UdpConnectionHandler;

/**
 *
 */
public class MainWindow extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected UdpConnectionHandler sockethandler;
	
	protected Connection conn;
	
	/**
	 * 
	 */
	public MainWindow()
	{
		this.setLayout(new GridBagLayout());
		
		int y = 0;
		
		final JTextArea rcvarea = new JTextArea();
		final LabeledTextField hostarea = new LabeledTextField("Remote Address");
		hostarea.setText("127.0.0.1");
		final LabeledTextField portarea = new LabeledTextField("Port");
		final JButton connectbutton = new JButton(new AbstractAction("Connect")
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					conn = sockethandler.connect(hostarea.getText(), Integer.parseInt(portarea.getText()));
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}
			}
		});
		
		final JButton disconnectbutton = new JButton(new AbstractAction("Disconnect")
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					sockethandler.disconnect(conn);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}
			}
		});
		
		final JButton spambutton = new JButton(new AbstractAction("Spam")
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				try
				{
//					Random r = new Random();
					final byte[] msg = new byte[100000000];
//					r.nextBytes(msg);
					
					
					Timer timer = new Timer(800000, new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							System.out.println("Send " + System.currentTimeMillis());
							String txt = rcvarea.getText();
							txt += String.valueOf("Send " + System.currentTimeMillis());
							txt += "\n";
							rcvarea.setText(txt);
							SUdpUtil.longIntoByteArray(msg, 0, System.currentTimeMillis());
							conn.sendMessage(msg);
						}
					});
					timer.setInitialDelay(2000);
					timer.start();
//					while (true)
//					{
//						conn.sendMessage(msg);
////						System.out.println("Send " + System.currentTimeMillis());
//						String txt = rcvarea.getText();
//						txt += String.valueOf("Send " + System.currentTimeMillis());
//						txt += "\n";
//						rcvarea.setText(txt);
//						Thread.sleep(10000);
//					}
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}
			}
		});
		
		final LabeledTextField myportarea = new LabeledTextField("Listen Port");
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.getContentPane().add(myportarea, c);
		
		JButton listenbutton = new JButton(new AbstractAction("Listen")
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				JButton source = (JButton) e.getSource();
				try
				{
					int port = Integer.parseInt(myportarea.getText());
					sockethandler = new UdpConnectionHandler(port, UdpConnectionHandler.DEFAULT_CIPHER, false, null);
					sockethandler.addConnectionListener(new IConnectionListener()
					{
						public void peerConnected(Connection connection)
						{
							conn = connection;
							hostarea.setEnabled(false);
							portarea.setEnabled(false);
							connectbutton.setEnabled(false);
							
							Thread t = new Thread(new Runnable()
							{
								public void run()
								{
									while(conn != null)
									{
										try
										{
											byte[] msgbytes = conn.receive();
//											String txt = new String(msgbytes, "UTF-8");
//											System.out.println("RCV " + System.currentTimeMillis() + " " + txt);
											String txt = rcvarea.getText();
											txt += new String(msgbytes, "UTF-8");
											txt += "\n";
											
//											String txt = rcvarea.getText();
//											long ts = System.currentTimeMillis();
//											long rts = SCodingUtil.longFromByteArray(msgbytes, 0);
//											txt += String.valueOf(msgbytes.length + " " + ts + " " + rts + " " + (ts - rts));
//											txt += "\n";
											
											rcvarea.setText(txt);
										}
										catch(Exception e)
										{
											e.printStackTrace();
										}
									}
								}
							});
							t.setDaemon(true);
							t.start();
						}

						@Override
						public void peerDisconnected(Connection connection)
						{
							System.out.println("Disconnected: " + connection);
							conn = null;
							hostarea.setEnabled(true);
							portarea.setEnabled(true);
							connectbutton.setEnabled(true);
						}
					});
					sockethandler.start(new DaemonThreadExecutor());
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}
				myportarea.setEnabled(false);
				source.setEnabled(false);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.getContentPane().add(listenbutton, c);
		
		c = new GridBagConstraints();
		c.gridy = ++y;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.getContentPane().add(hostarea, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = y;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.getContentPane().add(portarea, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = ++y;
		this.getContentPane().add(connectbutton, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = ++y;
		this.getContentPane().add(disconnectbutton, c);
		
		final JTextArea sendarea = new JTextArea();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridy = ++y;
		JScrollPane sp = new JScrollPane(sendarea);
		this.getContentPane().add(sp, c);
		
		JButton sendbutton = new JButton(new AbstractAction("Send")
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String txt = sendarea.getText();
					System.out.println("Sending: " + txt);
					byte[] msg = txt.getBytes("UTF-8");
					conn.sendMessage(msg);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					return;
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = ++y;
		this.getContentPane().add(sendbutton, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridy = ++y;
		sp = new JScrollPane(rcvarea);
		this.getContentPane().add(sp, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = ++y;
		this.getContentPane().add(spambutton, c);
		
//		JPanel filler = new JPanel();
//		c = new GridBagConstraints();
//		c.gridx = 0;
//		c.gridwidth = 2;
//		c.weightx = 1.0;
//		c.weighty = 1.0;
//		c.fill = GridBagConstraints.BOTH;
//		c.gridy = ++y;
//		this.getContentPane().add(filler, c);
		
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		
		
		this.pack();
		this.setSize(400,300);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public static void main(String[] args) throws Throwable
	{
//		new MainWindow();
		DatagramSocket dgsocket = new DatagramSocket(5000);
		byte[] buf = new byte[65536];
		DatagramPacket dgp = new DatagramPacket(buf, buf.length);
		while (true)
		{
			dgsocket.receive(dgp);
			System.out.println(dgp.getAddress().getHostAddress() + " " + dgp.getPort());
		}
	}
}
