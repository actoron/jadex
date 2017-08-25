package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Properties;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JPlaceholderTextField;
import jadex.commons.gui.JWizard;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.StringArrayTableModel;

/**
 *  Settings for security service.
 *
 */
public class SecuritySettingsPanel implements IServiceViewerPanel
{
	protected static final String DEFAULT_CERT_STORE = "jadex_certstore.zip";
	
	/** Access to jcc component. */
	protected IExternalAccess jccaccess;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	protected JTabbedPane main;
	
	/** Networks table. */
	protected JTable nwtable;
	
	/** Role table. */
	protected JTable roletable;
	
	/** Use secret check box. */
	protected JCheckBox usesecret;
	
	/** Print secret check box. */
	protected JCheckBox printsecret;
	
	/** Text area to display the platform secret. */
	protected JTextArea pfsecret;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture<Void> init(IControlCenter jcc, IService service)
	{
		final Future<Void> ret = new Future<Void>();
		//this.secservice	= (ISecurityService)service;
		
		jccaccess = jcc.getJCCAccess();
		
		final IServiceIdentifier sid = service.getServiceIdentifier();
		
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				secservice = (ISecurityService) SServiceProvider.getService(ia, sid, true).get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						main = new JTabbedPane();
						
						main.add("General", createGeneralPanel());
						
						main.add("Networks", createNetworkPanel());
						
						main.add("Roles", createRolePanel());
						
						main.add("Certificate Store", new CertTree(DEFAULT_CERT_STORE));
						
						jccaccess.scheduleStep(new IComponentStep<Void>()
						{
							public IFuture<Void> execute(IInternalAccess ia)
							{
								ret.setResult(null);
								return IFuture.DONE;
							}
						});
					}
				});
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "SecuritySettingsPanel";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return main;
	}

	/**
	 * Override
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		return IFuture.DONE;
	}

	/**
	 * Override
	 */
	public IFuture<Properties> getProperties()
	{
		return new Future<Properties>(new Properties());
	}
	
	/**
	 * 
	 * @return
	 */
	protected JPanel createGeneralPanel()
	{
		usesecret = new JCheckBox(new AbstractAction("Use Secret")
		{
			public void actionPerformed(ActionEvent e)
			{
				final boolean val = usesecret.isSelected();
				jccaccess.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						secservice.setUsePlatformSecret(val);
						
						return IFuture.DONE;
					}
				});
				
			}
		});
		
		printsecret = new JCheckBox(new AbstractAction("Print Secret")
		{
			public void actionPerformed(ActionEvent e)
			{
				final boolean val = printsecret.isSelected();
				jccaccess.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						secservice.setPrintPlatformSecret(val);
						
						return IFuture.DONE;
					}
				});
			}
		});
		
		JButton setsecret = new JButton(new AbstractAction("Set...")
		{
			public void actionPerformed(ActionEvent e)
			{
				SecretWizard wizard = new SecretWizard();
				
				wizard.addTerminationListener(new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (JWizard.FINISH_ID == e.getID())
						{
							final String secret = ((SecretWizard) e.getSource()).getResult().toString();
							jccaccess.scheduleStep(new IComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									secservice.setPlatformSecret(null, secret);
									refreshPlatformSecretState();
									
									return IFuture.DONE;
								};
							});
						}
					}
				});
				
				JWizard.createFrame("Set Platform Secret", wizard).setVisible(true);
				
				
			}
		});
		
		pfsecret = new JTextArea();
		pfsecret.setEditable(false);
		SGUI.addCopyPasteMenu(pfsecret);
		JScrollPane pfscroll = new JScrollPane(pfsecret);
		SGUI.setMinimumSize(pfscroll, 400, 200);
		
		JPanel cbpanel = new JPanel();
		SGUI.createEdgelessHorizontalGroupLayout(cbpanel, new JComponent[] { usesecret, printsecret }, true);
		
		JPanel pfspanel = new JPanel();
		pfspanel.setBorder(BorderFactory.createTitledBorder("Platform Secret"));
		JPanel buttonpanel = new JPanel();
		SGUI.createEdgelessHorizontalGroupLayout(buttonpanel, new JComponent[] { setsecret }, true, true);
		SGUI.createVerticalGroupLayout(pfspanel, new JComponent[] { cbpanel, pfscroll, buttonpanel }, true);
		
		JPanel general = new JPanel();
		SGUI.createVerticalGroupLayout(general, new JComponent[] { pfspanel }, false);
		
		refreshPlatformSecretState();
		
		return general;
	}
	
	/**
	 *  Create the network panel.
	 *  
	 *  @return The network panel.
	 */
	protected JPanel createNetworkPanel()
	{
		nwtable = new JTable();
		JScrollPane scroll = new JScrollPane(nwtable);
		
		final JPlaceholderTextField nwname = new JPlaceholderTextField();
		nwname.setPlaceholder("Network Name");
//		nwname.setMaximumSize(new Dimension(Short.MAX_VALUE, nwname.getMaximumSize().height));
		
		JButton add = new JButton(new AbstractAction("Add...")
		{
			public void actionPerformed(ActionEvent e)
			{
				if (nwname.getText().length() == 0)
				{
					nwname.showInvalid();
					return;
				}
				
				String nwn = nwname.getText();
				nwname.setText("");
				setNetwork(nwn);
			}
		});
		
		JButton change = new JButton(new AbstractAction("Edit...")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = nwtable.getSelectedRow();
				if (row >= 0)
				{
					String nwn = (String) nwtable.getModel().getValueAt(row, 0);
					setNetwork(nwn);
				}
			}
		});
		
		JButton remove = new JButton(new AbstractAction("Remove")
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = nwtable.getSelectedRow();
				if (row >= 0)
				{
					final String nwname = (String) nwtable.getModel().getValueAt(row, 0);
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							secservice.setNetwork(nwname, null).get();
							refreshNetworks();
							return IFuture.DONE;
						};
					});
				}
			}
		});
		
		JButton refresh = new JButton(new AbstractAction("Refresh")
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshNetworks();
			}
		});
		
		// start button panel layout
		JPanel buttonpanel = new JPanel();
		GroupLayout l = new GroupLayout(buttonpanel);
		buttonpanel.setLayout(l);
//		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		SequentialGroup hgroup = l.createSequentialGroup();
		
		ParallelGroup vgroup = l.createParallelGroup();
		
		for (JComponent comp : new JComponent[] { nwname, add, change, remove, refresh })
		{
			vgroup.addComponent(comp);
			hgroup.addComponent(comp);
		}
		l.linkSize(SwingConstants.VERTICAL, nwname, add, change, remove, refresh);
		
		l.setVerticalGroup(vgroup);
		l.setHorizontalGroup(hgroup);
		// end button panel layout
		
		JPanel nwpanel = new JPanel();
		l = new GroupLayout(nwpanel);
		nwpanel.setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		ParallelGroup ohgroup = l.createParallelGroup();
		SequentialGroup ovgroup = l.createSequentialGroup();
		
		for (JComponent comp : new JComponent[] { scroll, buttonpanel })
		{
			ovgroup.addComponent(comp);
			ohgroup.addComponent(comp);
		}
		
		l.setHorizontalGroup(ohgroup);
		l.setVerticalGroup(ovgroup);
		
		refreshNetworks();
		
		return nwpanel;
	}
	
	/**
	 *  Creates the role panel.
	 *  
	 *  @return The role panel.
	 */
	protected JPanel createRolePanel()
	{
		roletable = new JTable();
		
		JScrollPane scroll = new JScrollPane(roletable);
		
		final JPlaceholderTextField entityname = new JPlaceholderTextField();
		entityname.setPlaceholder("Entity Name");
		
		final JPlaceholderTextField rolename = new JPlaceholderTextField();
		rolename.setPlaceholder("Role Name");
		
		JButton add = new JButton(new AbstractAction("Add")
		{
			public void actionPerformed(ActionEvent e)
			{
				final String entity = entityname.getText();
				if (entity.length() == 0)
				{
					entityname.showInvalid();
					return;
				}
				
				final String role = rolename.getText();
				if (role.length() == 0)
				{
					rolename.showInvalid();
					return;
				}
				
				entityname.setNonPlaceholderText("");
				rolename.setNonPlaceholderText("");
				
				jccaccess.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						secservice.addRole(entity, role).get();
						refreshRoles();
						
						return IFuture.DONE;
					}
				});
			}
		});
		
		JButton remove = new JButton(new AbstractAction("Remove...")
		{
			public void actionPerformed(ActionEvent e)
			{
				int ind = roletable.getSelectedRow();
				if (ind >= 0)
				{
					final String entity = (String) roletable.getModel().getValueAt(ind, 0);
					
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							final Set<String> roles = secservice.getRoleMap().get().get(entity);
							
							SwingUtilities.invokeLater(new Runnable()
							{
								@SuppressWarnings("unchecked")
								public void run()
								{
									JPanel removepanel = new JPanel();
									@SuppressWarnings("rawtypes")
									final JComboBox roleschoice = new JComboBox();
									roleschoice.setEditable(false);
									roleschoice.setLightWeightPopupEnabled(false);
									for (String role : roles)
										roleschoice.addItem(role);
									
									final JCheckBox all = new JCheckBox("All Roles");
									
									final JFrame frame = new JFrame("Remove Role");
									
									JPanel buttonpanel = new JPanel();
									JButton okbutton = new JButton(new AbstractAction("OK")
									{
										public void actionPerformed(ActionEvent e)
										{
											final List<String> removal = new ArrayList<String>();
											if (!all.isSelected())
											{
												String sel = (String) roleschoice.getSelectedItem();
												if (sel == null)
												{
													frame.dispose();
													return;
												}
												removal.add(sel);
											}
											else
											{
												removal.addAll(roles);
											}
											
											jccaccess.scheduleStep(new IComponentStep<Void>()
											{
												public IFuture<Void> execute(IInternalAccess ia)
												{
													for (String role : removal)
														secservice.removeRole(entity, role).get();
													refreshRoles();
													return IFuture.DONE;
												}
											});
											
											frame.dispose();
										}
									});
									
									JButton cancelbutton = new JButton(new AbstractAction("Cancel")
									{
										public void actionPerformed(ActionEvent e)
										{
											frame.dispose();
										}
									});
									
									SGUI.createHorizontalGroupLayout(buttonpanel, new JComponent[] { okbutton, cancelbutton }, true);
									
									SGUI.createVerticalGroupLayout(removepanel, new JComponent[] { roleschoice, all, buttonpanel }, true);
									
									frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
									frame.getRootPane().setLayout(new BorderLayout());
									frame.getRootPane().add(removepanel, BorderLayout.CENTER);
									frame.setSize(400, 300);
									frame.setMinimumSize(frame.getRootPane().getPreferredSize());
									frame.setLocation(SGUI.calculateMiddlePosition(frame));
									frame.setVisible(true);
								}
							});
							
							return IFuture.DONE;
						}
					});
				}
			}
		});
		
		JButton refresh = new JButton(new AbstractAction("Refresh")
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshRoles();
			}
		});
		
//		SGUI.adjustComponentSizes(new JComponent[] { entityname, rolename, add, remove, refresh } );
		
		JPanel buttonpanel = new JPanel();
		GroupLayout l = new GroupLayout(buttonpanel);
		buttonpanel.setLayout(l);
//		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		SequentialGroup hgroup = l.createSequentialGroup();
		ParallelGroup vgroup = l.createParallelGroup();
		
		for (JComponent comp : new JComponent[] { entityname, rolename, add, remove, refresh })
		{
			vgroup.addComponent(comp);
			hgroup.addComponent(comp);
		}
		l.linkSize(SwingConstants.VERTICAL, entityname, rolename, add, remove, refresh);
		
		l.setVerticalGroup(vgroup);
		l.setHorizontalGroup(hgroup);
		
		JPanel rolepanel = new JPanel();
		l = new GroupLayout(rolepanel);
		rolepanel.setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		ParallelGroup ohgroup = l.createParallelGroup();
		SequentialGroup ovgroup = l.createSequentialGroup();
		
		for (JComponent comp : new JComponent[] { scroll, buttonpanel })
		{
			ovgroup.addComponent(comp);
			ohgroup.addComponent(comp);
		}
		
		l.setHorizontalGroup(ohgroup);
		l.setVerticalGroup(ovgroup);
		
		refreshRoles();
		
		return rolepanel;
	}
	
	/**
	 *  Sets secret for network.
	 *  
	 *  @param nwn The network name.
	 */
	protected void setNetwork(final String nwn)
	{
		SecretWizard wizard = new SecretWizard();
		wizard.setEntity(nwn);
		
		wizard.addTerminationListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e.getID() == JWizard.FINISH_ID)
				{
					final String secret = ((SecretWizard) e.getSource()).getResult().toString();
					
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							secservice.setNetwork(nwn, secret).get();
							refreshNetworks();
							
							return IFuture.DONE;
						}
					});
				}
			}
		});
		
		JWizard.createFrame("Network Authentication Secret", wizard).setVisible(true);;
	}
	
	/**
	 *  Refreshes the networks.
	 */
	protected void refreshPlatformSecretState()
	{
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final boolean use = secservice.isUsePlatformSecret().get();
				final boolean print = secservice.isPrintPlatformSecret().get();
				final String secret = secservice.getPlatformSecret(null).get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						usesecret.setSelected(use);
						printsecret.setSelected(print);
						pfsecret.setText(secret);
						pfsecret.setCaretPosition(0);
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Refreshes the networks.
	 */
	protected void refreshNetworks()
	{
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Map<String, String> nws = secservice.getNetworks().get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String[][] table = null;
						if (nws != null && nws.size() > 0)
						{
							table = new String[nws.size()][2];
							
							int i = 0;
							for (Map.Entry<String, String> entry : nws.entrySet())
							{
								table[i][0] = entry.getKey();
								table[i][1] = entry.getValue();
								++i;
							}
						}
						else
						{
							table = new String[0][0];
						}
						
						StringArrayTableModel model = new StringArrayTableModel(table);
						model.setColumnNames(new String[] { "Network Name", "Secret" });
						nwtable.setModel(model);
						
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Refreshes the networks.
	 */
	protected void refreshRoles()
	{
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Map<String, Set<String>> roles = secservice.getRoleMap().get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String[][] table = null;
						if (roles != null && roles.size() > 0)
						{
							table = new String[roles.size()][2];
							
							int i = 0;
							for (Map.Entry<String, Set<String>> entry : roles.entrySet())
							{
								table[i][0] = entry.getKey();
								StringBuilder rolesstr = new StringBuilder();
								for (String role : entry.getValue())
								{
									rolesstr.append(role);
									rolesstr.append(',');
								}
								rolesstr.delete(rolesstr.length() - 1, rolesstr.length());
								table[i][1] = rolesstr.toString();
								++i;
							}
						}
						else
						{
							table = new String[0][0];
						}
						
						StringArrayTableModel model = new StringArrayTableModel(table);
						model.setColumnNames(new String[] { "Entity", "Roles" });
						roletable.setModel(model);
						
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
}
