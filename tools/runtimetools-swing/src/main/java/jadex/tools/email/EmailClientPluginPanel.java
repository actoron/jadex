package jadex.tools.email;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DefaultResultListener;

/**
 * 
 */
public class EmailClientPluginPanel extends JPanel
{
	/**
	 * 
	 */
	public EmailClientPluginPanel(final IControlCenter jcc)
	{
		final JTextArea tain = new JTextArea(20, 20);
		JButton bugen = new JButton("Generate");
		bugen.setToolTipText("Click to generate a signed version of the email.");
		final JTextArea taout = new JTextArea(20, 20);
		taout.setEditable(false);
		
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setOneTouchExpandable(true);
		JScrollPane usp = new JScrollPane(tain);
		usp.setBorder(new TitledBorder(new EtchedBorder(), "Email Command Text"));
		JScrollPane lsp = new JScrollPane(taout);
		lsp.setBorder(new TitledBorder(new EtchedBorder(), "Generated Command Text"));
		sp.add(usp);
		sp.add(lsp);
		
		long min = 10;
		final JTextField tfvd = new JTextField(""+min);
		final JLabel lvd = new JLabel("( = "+min*60*1000+" ms )");
		tfvd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					long mil = Long.parseLong(tfvd.getText())*60*1000;
					lvd.setText("( = "+mil+" ms )");
				}
				catch(Exception ex)
				{
					lvd.setText("( = err ms )");
				}
			}
		});
		tfvd.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				try
				{
					long mil = Long.parseLong(tfvd.getText())*60*1000;
					lvd.setText("( = "+mil+" ms )");
				}
				catch(Exception ex)
				{
					lvd.setText("( = err ms )");
				}
			}
			
			public void focusGained(FocusEvent e)
			{
			}
		});
		
		JPanel so = new JPanel(new GridBagLayout());
		so.add(new JLabel("Validity duration [mins]: "), new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
		so.add(tfvd, new GridBagConstraints(1, 0, 1, 1, 1, 1, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		so.add(lvd, new GridBagConstraints(2, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		so.add(bugen, new GridBagConstraints(3, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
			
		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
		add(so, BorderLayout.SOUTH);
		
		bugen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String intxt = tain.getText();
				final String modintxt = intxt.replaceAll("\\r|\\n", "");
				
				long mil = 600000;
				try
				{
					mil = Long.parseLong(tfvd.getText())*60*1000;
				}
				catch(Exception ex)
				{
				}
				final long vd = mil;
				
				jcc.getPlatformAccess().searchService( new ServiceQuery<>( ISecurityService.class, ServiceScope.PLATFORM))
					.addResultListener(new DefaultResultListener<ISecurityService>()
				{
					public void resultAvailable(ISecurityService sser)
					{
//						final DefaultAuthorizable da = new DefaultAuthorizable();
						
//						da.setDigestContent(modintxt);
//						da.setValidityDuration(vd); 
						
						throw new UnsupportedOperationException("todo: long term authentication");
						
//						sser.preprocessRequest(da, null).addResultListener(new DefaultResultListener<Void>()
//						{
//							public void resultAvailable(Void result)
//							{
//								final StringBuffer buf = new StringBuffer();
//								
//								buf.append(intxt).append(SUtil.LF);
//								buf.append("#").append(da.getTimestamp()).append("#").append(SUtil.LF);
//								buf.append("#").append(da.getValidityDuration()).append("#").append(SUtil.LF);
//
//								List<byte[]> dgs = da.getAuthenticationData();
//								for(byte[] dg: dgs)
//								{
////									System.out.println("authdata: "+SUtil.arrayToString(dg));
//									String txt = new String(Base64.encode(dg));
//									buf.append("#");
//									buf.append(txt).append("#").append(SUtil.LF);
//								}	
//								
//								SwingUtilities.invokeLater(new Runnable()
//								{
//									public void run()
//									{
//										taout.setText(buf.toString());
//									}
//								});
//							}
//						});
					}
				});
			}
		});
	}
	
	/**
	 * 
	 */
	public void dispose()
	{
		// todo
	}
}
