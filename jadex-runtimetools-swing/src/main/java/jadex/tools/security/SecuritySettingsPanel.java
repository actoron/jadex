package jadex.tools.security;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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
	protected static final String DEFAULT_CERT_STORE = "certstore.zip";
	
	/** Access to jcc component. */
	protected IExternalAccess jccaccess;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	protected JTabbedPane main;
	
	/** Networks table. */
	protected JTable nwtable;
	
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
		JPanel general = new JPanel();
		
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
		SGUI.createHorizontalGroupLayout(cbpanel, new JComponent[] { usesecret, printsecret }, true);
		
		JPanel pfspanel = new JPanel();
		pfspanel.setBorder(BorderFactory.createTitledBorder("Platform Secret"));
		SGUI.createVerticalGroupLayout(pfspanel, new JComponent[] { cbpanel, pfscroll, setsecret }, true);
		
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
		JPanel nwpanel = new JPanel();
		
		nwtable = new JTable();
		JScrollPane scroll = new JScrollPane(nwtable);
		
		JPanel buttonpanel = new JPanel();
		
		final JPlaceholderTextField nwname = new JPlaceholderTextField();
		nwname.setPlaceholder("Network Name");
		
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
				if (row > 0)
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
				if (row > 0)
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
		
		SGUI.adjustComponentSizes(new JComponent[] { nwname, add, change, remove, refresh } );
		
		SGUI.createHorizontalGroupLayout(buttonpanel, new JComponent[] { nwname, add, change, remove, refresh }, true);
		
		SGUI.setMinimumSize(nwname, 200, -1);
		
		SGUI.createVerticalGroupLayout(nwpanel, new JComponent[] { scroll, buttonpanel }, false);
		
		refreshNetworks();
		
		return nwpanel;
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
}
