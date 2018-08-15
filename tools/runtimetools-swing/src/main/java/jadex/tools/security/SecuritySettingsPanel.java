package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.ICommand;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JPlaceholderTextField;
import jadex.commons.gui.JWizard;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.StringArrayTableModel;
import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SSecurity;
import jadex.platform.service.registryv2.SuperpeerClientAgent;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.AbstractX509PemSecret;
import jadex.platform.service.security.auth.X509PemStringsSecret;

/**
 *  Settings for security service.
 *
 */
public class SecuritySettingsPanel implements IServiceViewerPanel
{
	protected static final String DEFAULT_CERT_STORE = "certstore.zip";
	
	/** Access to jcc component. */
	protected IExternalAccess jccaccess;
	
	/** The settings service. */
	protected ISettingsService settingsservice;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	/** Main pane. */
	protected JTabbedPane main;
	
	/** Networks table. */
	protected JTable nwtable;
	
	/** Name authorities panel. */
	protected JTable natable;
	
	/** Name authority certificates (dn to cert). */
	protected Map<String, String> nacerts = new HashMap<>();
	
	/** Trusted names panel. */
	protected JTable trustedtable;
	
	/** Role table. */
	protected JTable roletable;
	
	/** Use secret check box. */
	protected JCheckBox usesecret;
	
	/** Print secret check box. */
	protected JCheckBox printsecret;
	
	/** Text area to display the platform secret. */
	protected JTextArea pfsecret;
	
	/** The main certificate tree. */
	protected CertTree certtree;
	
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
		
		final IServiceIdentifier sid = service.getId();
		
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IComponentIdentifier targetpf = sid.getProviderId().getRoot();
				secservice = ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ISecurityService.class).setScope(RequiredServiceInfo.SCOPE_PLATFORM).setSearchStart(targetpf)).get();
				settingsservice = ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(ISettingsService.class).setScope(RequiredServiceInfo.SCOPE_PLATFORM).setSearchStart(targetpf)).get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						main = new JTabbedPane();
						
						main.add("General", createGeneralPanel());
						
						main.add("Networks", createNetworkPanel());
						
						main.add("Roles", createRolePanel());
						
						main.add("Name Authorities", createNameAuthoritiesPanel());
						
						main.add("Trusted Platform Names", createTrustedNamesPanel());
						
						certtree = new CertTree();
						certtree.load(settingsservice.loadFile(DEFAULT_CERT_STORE).get());
						certtree.setSaveCommand(new ICommand<byte[]>()
						{
							public void execute(byte[] store)
							{
								settingsservice.saveFile(DEFAULT_CERT_STORE, store).get();
							}
						});
						main.add("Certificate Store", certtree);
						
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
			private static final long serialVersionUID = 3199039268331252401L;

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
			private static final long serialVersionUID = -7360330361452239905L;

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
			private static final long serialVersionUID = -5656178836803829151L;

			public void actionPerformed(ActionEvent e)
			{
				byte[] oldstore = settingsservice.loadFile(DEFAULT_CERT_STORE).get();
				SecretWizard wizard = new SecretWizard(oldstore);
				
				wizard.addTerminationListener(new AbstractAction()
				{
					private static final long serialVersionUID = -7931248714652926912L;

					public void actionPerformed(ActionEvent e)
					{
						if (JWizard.FINISH_ID == e.getID())
						{
							SecretWizard wizard = ((SecretWizard) e.getSource());
							if (wizard.getCertstore() != null && !Arrays.equals(oldstore, wizard.getCertstore()))
								writeCertStore(wizard.getCertstore());
							final String secret = wizard.getResult().toString();
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
		
		JPanel general = new JPanel();
		GroupLayout l = new GroupLayout(general);
		general.setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		
		SequentialGroup shgroup = l.createSequentialGroup();
		ParallelGroup svgroup = l.createParallelGroup();
		for (JComponent comp : new JComponent[] { usesecret, printsecret })
		{
			shgroup.addComponent(comp);
			svgroup.addComponent(comp);
		}
		
		SequentialGroup bhgroup = l.createSequentialGroup();
		bhgroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
		ParallelGroup bvgroup = l.createParallelGroup();
		for (JComponent comp : new JComponent[] { setsecret })
		{
			bhgroup.addComponent(comp);
			bvgroup.addComponent(comp);
		}
		
		ParallelGroup hgroup = l.createParallelGroup();
		hgroup.addGroup(shgroup);
		hgroup.addComponent(pfscroll);
		hgroup.addGroup(bhgroup);
		SequentialGroup vgroup = l.createSequentialGroup();
		vgroup.addGroup(svgroup);
		vgroup.addComponent(pfscroll);
		vgroup.addGroup(bvgroup);
		
		l.linkSize(SwingConstants.VERTICAL, usesecret, printsecret);
		
		l.setVerticalGroup(vgroup);
		l.setHorizontalGroup(hgroup);
		
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
		
//		final JPlaceholderTextField nwname = new JPlaceholderTextField();
//		nwname.setPlaceholder("Network Name");
//		nwname.setMaximumSize(new Dimension(Short.MAX_VALUE, nwname.getMaximumSize().height));
		
		JButton add = new JButton(new AbstractAction("Add...")
		{
			private static final long serialVersionUID = 8248266251755976630L;

			public void actionPerformed(ActionEvent e)
			{
//				if (nwname.getText().length() == 0)
//				{
//					nwname.showInvalid();
//					return;
//				}
				
//				String nwn = nwname.getText();
//				nwname.setText("");
				setNetwork();
			}
		});
		
//		JButton change = new JButton(new AbstractAction("Edit...")
//		{
//			private static final long serialVersionUID = 65319789712239257L;
//
//			public void actionPerformed(ActionEvent e)
//			{
//				int row = nwtable.getSelectedRow();
//				if (row >= 0)
//				{
//					String nwn = (String) nwtable.getModel().getValueAt(row, 0);
//					setNetwork(nwn);
//				}
//			}
//		});
		
		JButton remove = new JButton(new AbstractAction("Remove")
		{
			private static final long serialVersionUID = 1894456300828658272L;

			public void actionPerformed(ActionEvent e)
			{
				int[] rows = nwtable.getSelectedRows();
				if (rows != null && rows.length > 0)
				{
					final String[] nwnames = new String[rows.length];
					final String[] nwsecrets = new String[rows.length];
					for (int i = 0; i < rows.length; ++i)
					{
						nwnames[i] = (String) nwtable.getModel().getValueAt(rows[i], 0);
						nwsecrets[i] = (String) nwtable.getModel().getValueAt(rows[i], 1);
					}
					
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							for (int i = 0; i < nwnames.length; ++i)
								secservice.removeNetwork(nwnames[i], nwsecrets[i]).get();
							refreshNetworks();
							return IFuture.DONE;
						};
					});
				}
			}
		});
		
		JButton refresh = new JButton(new AbstractAction("Refresh")
		{
			private static final long serialVersionUID = -5577499766624680290L;

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
		
		for (JComponent comp : new JComponent[] { add, remove, refresh })
		{
			vgroup.addComponent(comp);
			hgroup.addComponent(comp);
		}
		l.linkSize(SwingConstants.VERTICAL, add, remove, refresh);
//		for (JComponent comp : new JComponent[] { nwname, add, change, remove, refresh })
//		{
//			vgroup.addComponent(comp);
//			hgroup.addComponent(comp);
//		}
//		l.linkSize(SwingConstants.VERTICAL, nwname, add, change, remove, refresh);
		
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
			private static final long serialVersionUID = -3636160157428186911L;

			public void actionPerformed(ActionEvent e)
			{
				final String entity = entityname.getText();
				if (entity.length() == 0)
				{
					entityname.showInvalid();
					return;
				}
				
				final String role = rolename.getText();
				if (role.length() == 0 || role.contains(","))
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
			private static final long serialVersionUID = 7003483731709427886L;

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
										private static final long serialVersionUID = 9140455080961018315L;

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
										private static final long serialVersionUID = 1997807029644448744L;

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
			private static final long serialVersionUID = -1813011770525012609L;

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
	 *  Creates panel for name authorities.
	 * @return Panel for name authorities.
	 */
	protected JPanel createNameAuthoritiesPanel()
	{
		natable = new JTable();
		JScrollPane scroll = new JScrollPane(natable);
		
		JButton add = new JButton(new AbstractAction("Add...")
		{
			private static final long serialVersionUID = -181301177525012609L;

			public void actionPerformed(ActionEvent e)
			{
				JFrame adddialog = new JFrame("Add Name Authority");
				adddialog.getContentPane().setLayout(new BorderLayout());
				
				CertTree nacerttree = new CertTree();
				nacerttree.load(settingsservice.loadFile(DEFAULT_CERT_STORE).get());
				
				adddialog.getContentPane().add(nacerttree, BorderLayout.CENTER);
				
				JButton okbutton = new JButton(new AbstractAction("Ok")
				{
					private static final long serialVersionUID = -134253457623452345L;

					public void actionPerformed(ActionEvent e)
					{
						PemKeyPair keypair = nacerttree.getSelectedCert();
						settingsservice.saveFile(DEFAULT_CERT_STORE, nacerttree.save()).get();
						certtree.load(settingsservice.loadFile(DEFAULT_CERT_STORE).get());
						adddialog.dispose();
						if (keypair != null)
						{
							secservice.addNameAuthority(keypair.getCertificate()).get();
						}
						refreshNameAuthorities();
					}
				});
				
				JButton cancelbutton = new JButton(new AbstractAction("Cancel")
				{
					private static final long serialVersionUID = 94523452345234L;

					public void actionPerformed(ActionEvent e)
					{
						adddialog.dispose();
					}
				});
				
				JPanel buttonpanel = new JPanel();
				buttonpanel.add(okbutton);
				buttonpanel.add(cancelbutton);
				
				adddialog.getContentPane().add(buttonpanel, BorderLayout.SOUTH);
				
				adddialog.setSize(800, 600);
				adddialog.setVisible(true);
			}
		});
		
		JButton remove = new JButton(new AbstractAction("Remove")
		{
			private static final long serialVersionUID = 7247608829907080898L;

			public void actionPerformed(ActionEvent e)
			{
				int[] rows = nwtable.getSelectedRows();
				if (rows != null && rows.length > 0)
				{
					final String[] nacertstrs = new String[rows.length];
					for (int i = 0; i < rows.length; ++i)
						nacertstrs[i] = nacerts.get((String) nwtable.getModel().getValueAt(rows[i], 1));
					
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							for (int i = 0; i < nacertstrs.length; ++i)
								secservice.removeNameAuthority(nacertstrs[i]).get();
							return IFuture.DONE;
						};
					}).get();
					
					refreshNameAuthorities();
				}
			}
		});
		
		JButton refresh = new JButton(new AbstractAction("Refresh")
		{

			private static final long serialVersionUID = 1342352352317L;

			public void actionPerformed(ActionEvent e)
			{
				refreshNameAuthorities();
			}
		});
		
		JPanel buttonpanel = new JPanel();
		GroupLayout l = new GroupLayout(buttonpanel);
		buttonpanel.setLayout(l);
		l.setAutoCreateGaps(true);
		SequentialGroup hgroup = l.createSequentialGroup();
		
		ParallelGroup vgroup = l.createParallelGroup();
		
		for (JComponent comp : new JComponent[] { add, remove, refresh })
		{
			vgroup.addComponent(comp);
			hgroup.addComponent(comp);
		}
		l.linkSize(SwingConstants.VERTICAL, add, remove, refresh);
		
		l.setVerticalGroup(vgroup);
		l.setHorizontalGroup(hgroup);
		
		JPanel napanel = new JPanel();
		
		l = new GroupLayout(napanel);
		napanel.setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		
		l = new GroupLayout(napanel);
		napanel.setLayout(l);
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
		
		refreshNameAuthorities();
		
		return napanel;
	}
	
	/**
	 *  Creates panel for name authorities.
	 * @return Panel for name authorities.
	 */
	protected JPanel createTrustedNamesPanel()
	{
		trustedtable = new JTable();
		JScrollPane scroll = new JScrollPane(trustedtable);
		
		JButton add = new JButton(new AbstractAction("Add...")
		{
			private static final long serialVersionUID = -18130117751292609L;

			public void actionPerformed(ActionEvent e)
			{
				final String res = JOptionPane.showInputDialog(trustedtable, "Add trusted platform name", "Add Trusted Platform Name", JOptionPane.PLAIN_MESSAGE);
				if (res != null)
				{
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							return secservice.addTrustedPlatform(res);
						}
					}).get();
					refreshTrustedPlatforms();
				}
			}
		});
		
		JButton remove = new JButton(new AbstractAction("Remove")
		{
			private static final long serialVersionUID = 724762329907080898L;

			public void actionPerformed(ActionEvent e)
			{
				int[] rows = trustedtable.getSelectedRows();
				if (rows != null && rows.length > 0)
				{
					final String[] names = new String[rows.length];
					for (int i = 0; i < rows.length; ++i)
						names[i] = (String) trustedtable.getModel().getValueAt(rows[i], 0);
					
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							for (int i = 0; i < names.length; ++i)
								secservice.removeTrustedPlatform(names[i]).get();
							
							return IFuture.DONE;
						};
					}).get();
					refreshTrustedPlatforms();
				}
			}
		});
		
		JButton refresh = new JButton(new AbstractAction("Refresh")
		{

			private static final long serialVersionUID = 1342352354352317L;

			public void actionPerformed(ActionEvent e)
			{
				refreshTrustedPlatforms();
			}
		});
		
		JPanel buttonpanel = new JPanel();
		GroupLayout l = new GroupLayout(buttonpanel);
		buttonpanel.setLayout(l);
		l.setAutoCreateGaps(true);
		SequentialGroup hgroup = l.createSequentialGroup();
		
		ParallelGroup vgroup = l.createParallelGroup();
		
		for (JComponent comp : new JComponent[] { add, remove, refresh })
		{
			vgroup.addComponent(comp);
			hgroup.addComponent(comp);
		}
		l.linkSize(SwingConstants.VERTICAL, add, remove, refresh);
		
		l.setVerticalGroup(vgroup);
		l.setHorizontalGroup(hgroup);
		
		JPanel trustedpanel = new JPanel();
		
		l = new GroupLayout(trustedpanel);
		trustedpanel.setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		
		l = new GroupLayout(trustedpanel);
		trustedpanel.setLayout(l);
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
		
		refreshTrustedPlatforms();
		
		return trustedpanel;
	}
	
	/**
	 *  Sets secret for network.
	 *  
	 *  @param nwn The network name.
	 */
	protected void setNetwork()
	{
		byte[] oldstore = settingsservice.loadFile(DEFAULT_CERT_STORE).get();
		SecretWizard wizard = new SecretWizard(oldstore);
		//wizard.setEntity(nwn);
		wizard.setEntityType("the network name");
		
		wizard.addTerminationListener(new AbstractAction()
		{
			private static final long serialVersionUID = -3403001893533826804L;

			public void actionPerformed(ActionEvent e)
			{
				if (e.getID() == JWizard.FINISH_ID)
				{
					SecretWizard wizard = (SecretWizard) e.getSource();
					if (wizard.getCertstore() != null && !Arrays.equals(oldstore, wizard.getCertstore()))
						writeCertStore(wizard.getCertstore());
					final String nw = wizard.getEntity();
					final String secret = wizard.getResult().toString();
					
					jccaccess.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							secservice.setNetwork(nw, secret).get();
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
	 *  Writes the cert store.
	 *  @param newstore The content.
	 */
	protected void writeCertStore(byte[] newstore)
	{
		settingsservice.saveFile(DEFAULT_CERT_STORE, newstore).get();
		certtree.load(newstore);
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
				final MultiCollection<String, String> nws = secservice.getAllKnownNetworks().get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String[][] table = null;
						if (nws != null && nws.size() > 0)
						{
							List<String[]> dtable = new ArrayList<>();
							
							for (Map.Entry<String, Collection<String>> entry : nws.entrySet())
							{
								if (entry.getValue() != null && entry.getValue().size() > 0 && !SuperpeerClientAgent.GLOBAL_NETWORK_NAME.equals(entry.getKey()))
								{
									for (String secret : entry.getValue())
									{
										String[] tentry = new String[2];
										tentry[0] = entry.getKey();
										tentry[1] = secret;
										dtable.add(tentry);
									}
								}
							}
							table = dtable.toArray(new String[dtable.size()][]);
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
	
	/**
	 *  Refreshes the networks.
	 */
	protected void refreshNameAuthorities()
	{
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Set<String> nas = secservice.getNameAuthorities().get();
				final Set<String> custom = secservice.getCustomNameAuthorities().get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String[][] table = null;
						if (nas != null && nas.size() > 0)
						{
							table = new String[nas.size()][3];
							nacerts.clear();
							
							int i = 0;
							for (String cert : nas)
							{
								String subjectid = null;
								String dn = null;
								InputStream is = null;
								try
								{
									subjectid = SSecurity.getCommonName(SSecurity.readCertificateFromPEM(cert).getSubject());
									dn = SSecurity.readCertificateFromPEM(cert).getSubject().toString();
								}
								catch (Exception e)
								{
								}
								finally
								{
									SUtil.close(is);
								}
								
								nacerts.put(dn, cert);
								
								table[i][0] = subjectid != null ? subjectid : "";
								table[i][1] = dn != null ? dn : "";
								table[i][2] = custom.contains(cert) ? "Custom CA" : "Java CA";
								++i;
							}
						}
						else
						{
							table = new String[0][0];
						}
						
						StringArrayTableModel model = new StringArrayTableModel(table);
						model.setColumnNames(new String[] { "Subject Common Name", "Subject Distinguished Name", "Type" });
						natable.setModel(model);
						
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Refreshes the networks.
	 */
	protected void refreshTrustedPlatforms()
	{
		jccaccess.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Set<String> tps = secservice.getTrustedPlatforms().get();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String[][] table = null;
						if (tps != null && tps.size() > 0)
						{
							List<String[]> dtable = new ArrayList<>();
							
							for (String tname : tps)
							{
								if (tname != null)
								{
									String[] tentry = new String[] { tname };
									dtable.add(tentry);
								}
							}
							
							table = dtable.toArray(new String[dtable.size()][]);
						}
						else
						{
							table = new String[0][0];
						}
						
						StringArrayTableModel model = new StringArrayTableModel(table);
						model.setColumnNames(new String[] { "Trusted Platform Name" });
						trustedtable.setModel(model);
						
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
}
