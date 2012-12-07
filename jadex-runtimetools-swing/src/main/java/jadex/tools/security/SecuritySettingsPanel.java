package jadex.tools.security;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.idtree.IdTableModel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.KeyStoreEntry;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.commons.ICommand;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.tools.jcc.JCCResultListener;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *  The security settings panel.
 */
public class SecuritySettingsPanel	implements IServiceViewerPanel
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
	
	/** The keystore path. */
	protected JTextField tfstorepath;
	
	/** The keystore password. */
	protected JTextField tfstorepass;
	
	/** The key password. */
	protected JTextField tfkeypass;

	
	protected JSplitPanel sph;
	protected JSplitPanel spv;
	
	//-------- methods --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public IFuture<Void> init(final IControlCenter jcc, IService service)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		this.secservice	= (ISecurityService)service;
//		this.inner	= new JPanel(new BorderLayout());
		this.inner	= new JTabbedPane();

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
//				doRefresh();
			}
		});
		
		tfstorepath = new JTextField(10);
		JButton bustpa = new JButton("...");
		tfstorepass = new JTextField(10);
		tfkeypass = new JTextField(10);
		JButton bustoreset = new JButton("Set");

		bustpa.addActionListener(new ActionListener()
		{
			JFileChooser fc;
			public void actionPerformed(ActionEvent e)
			{
				if(fc==null)
				{
					fc = new JFileChooser(".");
				}
				fc.showOpenDialog(inner);
				File sel = fc.getSelectedFile();
				if(sel!=null && sel.exists())
				{
					try
					{
						tfstorepath.setText(SUtil.convertPathToRelative(sel.getAbsolutePath()));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		
		bustoreset.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String p = tfstorepath.getText();
				String relp = SUtil.convertPathToRelative(p);
				secservice.setKeystoreInfo(relp, tfstorepass.getText(), tfkeypass.getText());
			}
		});
		
		final JPanel pdetails = new JPanel(new BorderLayout());
		
		final JTable ktt = new JTable();
		final IdTableModel<String, KeyStoreEntry> kttm = new IdTableModel<String, KeyStoreEntry>(new String[]{"Alias", "Type", "Date"}, 
			new Class<?>[]{String.class, String.class, String.class}, ktt)
		{
			public Object getValueAt(KeyStoreEntry obj, int column)
			{
				Object ret = obj;
				if(column==0)
				{
					ret = obj.getAlias();
				}
				else if(column==1)
				{
					ret = obj.getType();
				}
				else if(column==2)
				{
					ret = SUtil.SDF.format(new Date(obj.getDate()));
				}
//				else if(column==3)
//				{
//					ret = obj.getDetails();
//				}
				return ret;
			}
		};
		ktt.setModel(kttm);
		ktt.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(e.getValueIsAdjusting())
					return;
				
				pdetails.removeAll();
				int idx = ktt.getSelectedRow();
				if(idx!=-1)
				{
					KeyStoreEntry kse = (KeyStoreEntry)kttm.getValueAt(idx, -1);
					if(kse.getDetails() instanceof Certificate[])
					{
						CertificatePanel ct = new CertificatePanel((Certificate[])kse.getDetails());
						pdetails.add(ct, BorderLayout.CENTER);
					}
					else if(kse.getDetails() instanceof Certificate)
					{
						CertificatePanel ct = new CertificatePanel(new Certificate[]{(Certificate)kse.getDetails()});
						pdetails.add(ct, BorderLayout.CENTER);
					}
//					else
//					{
//						System.out.println("o: "+kse.getDetails());
//					}
				}
				
				pdetails.invalidate();
				inner.invalidate();
				inner.repaint();
			}
		});
		
		final Runnable act = new Runnable()
		{
			public void run()
			{
				secservice.getKeystoreDetails().addResultListener(new IResultListener<Map<String,KeyStoreEntry>>()
				{
					public void resultAvailable(Map<String, KeyStoreEntry> infos)
					{
						kttm.removeAll();
						for(Iterator<String> it = infos.keySet().iterator(); it.hasNext(); )
						{
							KeyStoreEntry kse = infos.get(it.next());
							kttm.addObject(kse.getAlias(), kse);
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}
				});
			}
		};
		
		ktt.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				popup(e);
			}
			public void mouseReleased(MouseEvent e)
			{
				popup(e);
			}
			protected void	popup(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					int	row	= ktt.rowAtPoint(e.getPoint());
					if(row!=-1)
					{
						final KeyStoreEntry kse = (KeyStoreEntry)kttm.getValueAt(row, -1);
						
						JPopupMenu	menu	= new JPopupMenu("Key store menu");
						menu.add(new AbstractAction("Delete")
						{
							public void actionPerformed(ActionEvent e)
							{
								secservice.removeKeyStoreEntry(kse.getAlias());
								act.run();
							}
						});	
						menu.show(ktt, e.getX(), e.getY());
					}
				}
			}
		});
		
		JButton bureload = new JButton("Reload");
		bureload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				act.run();
			}
		});
		act.run();

		// The acquire certificate settings
		final AcquireCertificatePanel acp = new AcquireCertificatePanel(jcc.getJCCAccess(), secservice, jcc.getCMSHandler());
		secservice.getAcquisitionMechanisms().addResultListener(new SwingDefaultResultListener<List<MechanismInfo>>()
		{
			public void customResultAvailable(List<MechanismInfo> result) 
			{
				acp.setMechanisms(result);
				secservice.getSelectedAcquisitionMechanism().addResultListener(new SwingDefaultResultListener<Integer>()
				{
					public void customResultAvailable(Integer sel) 
					{
						acp.setSelectedMechanism(sel);
					}
				});
			}
		});
		
		// The local password settings.
		JPanel	plocal	= new JPanel(new GridBagLayout());
		plocal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Local Password Settings"));
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.insets	= new Insets(0, 3, 0, 3);
		gbc.weightx	= 0;
		gbc.weighty	= 0;
		gbc.anchor	= GridBagConstraints.NORTHWEST;
		gbc.fill	= GridBagConstraints.VERTICAL;
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
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill	= GridBagConstraints.NONE;
		plocal.add(cbtrulan, gbc);
//		plocal.add(new JButton(), gbc);
		
		JPanel slocal	= new JPanel(new GridBagLayout());
		slocal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Local Keystore Settings"));
		int x=0;
		int y=0;
		Insets in = new Insets(2, 2, 2, 2);
		slocal.add(new JLabel("Key store location: "), new GridBagConstraints(x++, y, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.VERTICAL, in, 0, 0));
		slocal.add(tfstorepath, new GridBagConstraints(x++, y, 1, 1, 1, 0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH, in, 0, 0));
		slocal.add(bustpa, new GridBagConstraints(x++, y, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.VERTICAL, in, 0, 0));
//		x=0;
		slocal.add(new JLabel("Key store password:"), new GridBagConstraints(x++, y, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.VERTICAL, in, 0, 0));
		slocal.add(tfstorepass, new GridBagConstraints(x++, y, 1, 1, 1, 0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH, in, 0, 0));
		slocal.add(new JLabel("Key password:"), new GridBagConstraints(x++, y, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.VERTICAL, in, 0, 0));
		slocal.add(tfkeypass, new GridBagConstraints(x++, y, 1, 1, 1, 0, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH, in, 0, 0));
		slocal.add(bustoreset, new GridBagConstraints(x++, y++, 1, 1, 0, 0, 
			GridBagConstraints.WEST, GridBagConstraints.VERTICAL, in, 0, 0));
		slocal.add(new JScrollPane(ktt), new GridBagConstraints(0, y++, 8, 1, 1, 1, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH, in, 0, 0));
		slocal.add(bureload, new GridBagConstraints(0, y++, 8, 1, 1, 0, 
			GridBagConstraints.EAST, GridBagConstraints.NONE, in, 0, 0));
				
		ICommand paddrem = new ICommand()
		{
			public void execute(Object args)
			{
				String[] tmp = (String[])args;
				secservice.setPlatformPassword(new ComponentIdentifier(tmp[0]), tmp[1]).addResultListener(new JCCResultListener<Void>(jcc)
				{
					public void customResultAvailable(Void result)
					{
//						doRefresh();
					}
				});
			}
		};
		ICommand naddrem = new ICommand()
		{
			public void execute(Object args)
			{
				String[] tmp = (String[])args;
				secservice.setNetworkPassword(tmp[0], tmp[1]).addResultListener(new JCCResultListener<Void>(jcc)
				{
					public void customResultAvailable(Void result)
					{
//						doRefresh();
					}
				});
			}
		};
		
		ppp = new PasswordTablePanel("Remote Platform Password Settings", new String[]{"Platform Name", "Password"}, paddrem, paddrem);
		npp = new PasswordTablePanel("Network Password Settings", new String[]{"Network Name", "Password"}, naddrem, naddrem);
		
//		JSplitPanel sp2 = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
//		sp2.setOneTouchExpandable(true);
//		sp2.setDividerLocation(0.5);
//		sp2.add(plocal);
//		sp2.add(slocal);
		
//		JPanel no = new JPanel(new BorderLayout());
//		no.add(plocal, BorderLayout.NORTH);
//		no.add(slocal, BorderLayout.SOUTH);
		
		// Overall layout.
//		inner.add(no, BorderLayout.NORTH);
//		inner.add(sp, BorderLayout.CENTER);
		
		sph = new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT);
		sph.setOneTouchExpandable(true);
		sph.setDividerLocation(0.5);
		sph.add(pdetails);
		sph.add(new JScrollPane(acp));
		
		spv = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
		spv.setOneTouchExpandable(true);
		spv.setDividerLocation(0.5);
		spv.add(slocal);
		spv.add(sph);
		
		((JTabbedPane)inner).addTab("Password", plocal);
		((JTabbedPane)inner).addTab("Keystore", spv);
		((JTabbedPane)inner).addTab("Remote Passwords", ppp);
		((JTabbedPane)inner).addTab("Network Names", npp);
			
		// Gui listeners.
		buapply.setEnabled(false);
		cbusepass.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				final boolean	usepass	= cbusepass.isSelected();
				secservice.setUsePassword(usepass).addResultListener(new JCCResultListener<Void>(jcc)
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
				secservice.setLocalPassword(newpass).addResultListener(new JCCResultListener<Void>(jcc)
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
		
//		doRefresh().addResultListener(new SwingDelegationResultListener<Void>(ret));
		
		secservice.subcribeToEvents().addResultListener(new IIntermediateResultListener<jadex.commons.ChangeEvent<Object>>()
		{
			public void intermediateResultAvailable(jadex.commons.ChangeEvent<Object> event)
			{
				// Skip init
				if(event==null)
				{
					ret.setResult(null);
					return;
				}
				
				System.out.println("event: "+event.getType()+" "+event.getValue());
				
				if(ISecurityService.PROPERTY_USEPASS.equals(event.getType()))
				{
					cbusepass.setSelected(((Boolean)event.getValue()).booleanValue());
				}
				else if(ISecurityService.PROPERTY_TRUSTEDLAN.equals(event.getType()))
				{
					cbtrulan.setSelected(((Boolean)event.getValue()).booleanValue());
				}
				else if(ISecurityService.PROPERTY_LOCALPASS.equals(event.getType()))
				{
					tfpass.setText((String)event.getValue());
				}
				else if(ISecurityService.PROPERTY_PLATFORMPASS.equals(event.getType()))
				{
					ppp.update((Map<String,String>)event.getValue());
				}
				else if(ISecurityService.PROPERTY_NETWORKPASS.equals(event.getType()))
				{
					npp.update((Map<String,String>)event.getValue());
				}
				else if(ISecurityService.PROPERTY_KEYSTORESETTINGS.equals(event.getType()))
				{
					String[] info = (String[])event.getValue();
					tfstorepath.setText(info[0]);
					tfstorepass.setText(info[1]);
					tfkeypass.setText(info[2]);
				}
				else if(ISecurityService.PROPERTY_SELECTEDMECHANISM.equals(event.getType()))
				{
					acp.setSelectedMechanism(((Integer)event.getValue()).intValue());
				}
			}
			
			public void finished()
			{
			}
			
			public void resultAvailable(Collection<jadex.commons.ChangeEvent<Object>> result)
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
		return ret;
	}

	/**
	 *  Informs the plugin that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}

//	/**
//	 *  Refresh the panel.
//	 */
//	protected IFuture<Void> doRefresh()
//	{
//		final Future<Void>	ret	= new Future<Void>();
//		
//		// Initialize values from security service.
//		secservice.isUsePassword().addResultListener(new SwingExceptionDelegationResultListener<Boolean, Void>(ret)
//		{
//			public void customResultAvailable(Boolean usepass)
//			{
//				cbusepass.setSelected(usepass.booleanValue());
//				
//				secservice.isTrustedLanMode().addResultListener(new SwingExceptionDelegationResultListener<Boolean, Void>(ret)
//				{
//					public void customResultAvailable(Boolean trustedlan)
//					{
//						cbtrulan.setSelected(trustedlan.booleanValue());
//						
//						secservice.getLocalPassword().addResultListener(new SwingExceptionDelegationResultListener<String, Void>(ret)
//						{
//							public void customResultAvailable(String password)
//							{
//								if(password!=null)
//								{
//									tfpass.setText(password);
//								}
//								
//								secservice.getPlatformPasswords().addResultListener(new SwingExceptionDelegationResultListener<Map<String, String>, Void>(ret)
//								{
//									public void customResultAvailable(Map<String, String> passwords)
//									{
//		//								System.out.println("plat passes: "+passwords);
//										ppp.update(passwords);
//										
//										secservice.getNetworkPasswords().addResultListener(new SwingExceptionDelegationResultListener<Map<String, String>, Void>(ret)
//										{
//											public void customResultAvailable(Map<String, String> passwords)
//											{
//		//										System.out.println("net passes: "+passwords);
//												npp.update(passwords);
//												
//												secservice.getKeystoreInfo().addResultListener(new SwingExceptionDelegationResultListener<String[], Void>(ret)
//												{
//													public void customResultAvailable(String[] info)
//													{
//														tfstorepath.setText(info[0]);
//														tfstorepass.setText(info[1]);
//														tfkeypass.setText(info[2]);
//														ret.setResult(null);			
//													}
//												});
//											}
//										});
//									}
//								});
//							}
//						});
//					}
//				});
//			}
//		});
//		
//		return ret;
//	}

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
		((JTabbedPane)inner).setSelectedIndex(props.getIntProperty("selected_tab"));
		if(props.getProperty("sph")!=null)
			sph.setDividerLocation(props.getDoubleProperty("sph"));
		if(props.getProperty("spv")!=null)
			spv.setDividerLocation(props.getDoubleProperty("spv"));
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
		props.addProperty(new Property("selected_tab", ""+((JTabbedPane)inner).getSelectedIndex()));
		props.addProperty(new Property("sph", ""+sph.getProportionalDividerLocation()));
		props.addProperty(new Property("spv", ""+spv.getProportionalDividerLocation()));
		return new Future<Properties>(props);
	}
	
	
}
