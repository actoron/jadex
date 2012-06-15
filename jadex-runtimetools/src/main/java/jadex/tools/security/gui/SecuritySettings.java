package jadex.tools.security.gui;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.ICommand;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *  The security settings panel.
 */
public class SecuritySettings	implements IServiceViewerPanel
{
	//-------- attributes --------
	
	/** The security service. */
	protected ISecurityService secservice;
	
	/** Enable password protected for local platform. */
	protected JCheckBox	cbusepass;
	
	/** The local platform password. */
	protected JPasswordField	tfpass;
	
	/** Show / hide password characters in gui. */
	protected JCheckBox	cbshowchars;
	
	/** The trusted lan option. */
	protected JCheckBox cbtrulan;

	/** The platform passwords panel. */
	protected PasswordTablePanel ppp;
	
	/** The network passwords panel. */
	protected PasswordTablePanel npp;
	
	/** The inner panel. */
	protected JComponent	inner;
	
	//-------- methods --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public IFuture<Void> init(final IControlCenter jcc, IService service)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		this.secservice	= (ISecurityService)service;
		
		cbusepass	= new JCheckBox("Use password");
		final JLabel	lbpass	= new JLabel("Password");
		tfpass	= new JPasswordField(10);
		final char	echo	= tfpass.getEchoChar();
		final JButton	buapply	= new JButton("Apply");
		cbshowchars	= new JCheckBox("Show characters");
		
		cbusepass.setToolTipText("Enable / disable password protection of the platform.");
		lbpass.setToolTipText("The platform password");
		tfpass.setToolTipText("The platform password (<enter> to set new password)");
		buapply.setToolTipText("Set new password");
		cbshowchars.setToolTipText("Show / hide password characters in gui");
		buapply.setMargin(new Insets(0, 0, 0, 0));
		
		cbtrulan = new JCheckBox("Trust platforms from the same network (caution)");
		cbtrulan.setToolTipText("The trusted networks are not password protected per default. Enter password to disable spoofing.");
		cbtrulan.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				secservice.setTrustedLanMode(cbtrulan.isSelected());
				doRefresh();
			}
		});
		
		// The local password settings.
		JPanel	plocal	= new JPanel(new GridBagLayout());
		plocal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Local Password Settings"));
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.insets	= new Insets(0, 3, 0, 3);
		gbc.weightx	= 0;
		gbc.weighty	= 0;
		gbc.anchor	= GridBagConstraints.WEST;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.gridy	= 0;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		plocal.add(cbusepass, gbc);
		gbc.gridwidth	= 1;
		gbc.gridy++;
		plocal.add(lbpass, gbc);
		plocal.add(tfpass, gbc);
		plocal.add(buapply, gbc);
		gbc.weightx	= 1;
		plocal.add(cbshowchars, gbc);
		gbc.gridy++;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		plocal.add(cbtrulan, gbc);
		
		ICommand paddrem = new ICommand()
		{
			public void execute(Object args)
			{
				String[] tmp = (String[])args;
				secservice.setPlatformPassword(new ComponentIdentifier(tmp[0]), tmp[1]).addResultListener(new SwingDefaultResultListener<Void>()
				{
					public void customResultAvailable(Void result)
					{
						doRefresh();
					}
				});
			}
		};
		ICommand naddrem = new ICommand()
		{
			public void execute(Object args)
			{
				String[] tmp = (String[])args;
				secservice.setNetworkPassword(tmp[0], tmp[1]).addResultListener(new SwingDefaultResultListener<Void>()
				{
					public void customResultAvailable(Void result)
					{
						doRefresh();
					}
				});
			}
		};
		
		ppp = new PasswordTablePanel("Remote Platform Password Settings", new String[]{"Platform Name", "Password"}, paddrem, paddrem);
		npp = new PasswordTablePanel("Network Password Settings", new String[]{"Network Name", "Password"}, naddrem, naddrem);
		
		JSplitPanel sp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.setDividerLocation(0.5);
		sp.add(ppp);
		sp.add(npp);
		
		// Overall layout.
		this.inner	= new JPanel(new BorderLayout());
		inner.add(plocal, BorderLayout.NORTH);
		inner.add(sp, BorderLayout.CENTER);
		
		// Gui listeners.
		buapply.setEnabled(false);
		cbusepass.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				final boolean	usepass	= cbusepass.isSelected();
				secservice.setUsePassword(usepass).addResultListener(new SwingDefaultResultListener<Void>(inner)
				{
					public void customResultAvailable(Void result)
					{
						lbpass.setEnabled(usepass);
						tfpass.setEnabled(usepass);
						cbshowchars.setEnabled(usepass);
					}
				});
			}
		});
		ActionListener	al	= new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String	newpass	= new String(tfpass.getPassword());
				secservice.setLocalPassword(newpass).addResultListener(new SwingDefaultResultListener<Void>(inner)
				{
					public void customResultAvailable(Void result)
					{
						buapply.setEnabled(false);
					}
				});
			}
		};
		buapply.addActionListener(al);
		tfpass.addActionListener(al);
		tfpass.addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent e)
			{
				buapply.setEnabled(true);
			}
		});
		cbshowchars.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tfpass.setEchoChar(cbshowchars.isSelected() ? 0 : echo);
			}
		});
		
		doRefresh().addResultListener(new SwingDelegationResultListener<Void>(ret));
		
		return ret;
	}

	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}

	/**
	 *  Refresh the panel.
	 */
	protected IFuture<Void> doRefresh()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Initialize values from security service.
		secservice.isUsePassword().addResultListener(new SwingDefaultResultListener<Boolean>()
		{
			public void customResultAvailable(Boolean usepass)
			{
				cbusepass.setSelected(usepass.booleanValue());
				
				secservice.isTrustedLanMode().addResultListener(new SwingDefaultResultListener<Boolean>()
				{
					public void customResultAvailable(Boolean trustedlan)
					{
						cbtrulan.setSelected(trustedlan.booleanValue());
						
						secservice.getLocalPassword().addResultListener(new SwingDefaultResultListener<String>()
						{
							public void customResultAvailable(String password)
							{
								if(password!=null)
								{
									tfpass.setText(password);
								}
								
								secservice.getPlatformPasswords().addResultListener(new SwingDefaultResultListener<Map<String, String>>()
								{
									public void customResultAvailable(Map<String, String> passwords)
									{
		//								System.out.println("plat passes: "+passwords);
										ppp.update(passwords);
										
										secservice.getNetworkPasswords().addResultListener(new SwingDefaultResultListener<Map<String, String>>()
										{
											public void customResultAvailable(Map<String, String> passwords)
											{
		//										System.out.println("net passes: "+passwords);
												npp.update(passwords);
												ret.setResult(null);								
											}
										});
									}
								});
								
								
							}
							
							// Todo: SwingExceptionDelegationResultListener
							public void customExceptionOccurred(Exception exception)
							{
								ret.setException(exception);
							}
						});
					}
					
					// Todo: SwingExceptionDelegationResultListener
					public void customExceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		});
		
		return ret;
	}

	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		return inner;
//		return new AutoRefreshPanel()
//		{
//			public IFuture<Void> refresh()
//			{
//				return SecuritySettings.this.doRefresh();
//			}
//			
//			public IFuture<JComponent> createInnerPanel()
//			{
//				return new Future<JComponent>(inner);
//			}
//		};
	}
		
	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "securitysettings";
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		cbshowchars.setSelected(props.getBooleanProperty("showchars"));
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		props.addProperty(new Property("showchars", Boolean.toString(cbshowchars.isSelected())));
		return new Future<Properties>(props);
	}
}
