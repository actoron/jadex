package jadex.tools.email;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.DefaultAuthorizable;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.platform.service.security.SecurityService;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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
		JButton bugen = new JButton("Gen");
		final JTextArea taout = new JTextArea(20, 20);
		taout.setEditable(false);
		
		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.add(tain);
		sp.add(taout);
		
		JPanel so = new JPanel(new BorderLayout());
		so.add(bugen, BorderLayout.EAST);
		
		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
		add(so, BorderLayout.SOUTH);
		
		bugen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String intxt = tain.getText();
				
				SServiceProvider.getService(jcc.getPlatformAccess().getServiceProvider(), ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener<ISecurityService>()
				{
					public void resultAvailable(ISecurityService sser)
					{
						final DefaultAuthorizable da = new DefaultAuthorizable();
						da.setDigestContent(intxt);
						sser.preprocessRequest(da, null).addResultListener(new DefaultResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								final StringBuffer buf = new StringBuffer();
								
								buf.append(intxt).append(SUtil.LF).append("#");
								buf.append(da.getTimestamp()).append("#").append(SUtil.LF);

								List<byte[]> dgs = da.getAuthenticationData();
								for(byte[] dg: dgs)
								{
									String txt = new String(Base64.encode(dg));
									buf.append("#");
									buf.append(txt).append("#").append(SUtil.LF);
								}	
								
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										taout.setText(buf.toString());
									}
								});
							}
						});
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
