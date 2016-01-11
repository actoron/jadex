package jadex.tools.security;

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import jadex.base.gui.PlatformSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.idtree.IdTableModel;
import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.security.KeyStoreEntry;
import jadex.bridge.service.types.security.MechanismInfo;
import jadex.commons.ICommand;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.security.SSecurity;
import jadex.tools.jcc.JCCResultListener;

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

	/** The split panels. */
	protected JSplitPanel sph;
	protected JSplitPanel spv;
	
	/** The keystore update action. */
	protected Runnable updateact;
	
	/** The validity duration textfield. */
	protected JTextField tfvaldur;
	
	//-------- methods --------
	
	/**
	 *  Create main panel.
	 *  @return The main panel.
	 */
	public IFuture<Void> init(final IControlCenter jcc, IService service)
	{
		final Future<Void> ret = new Future<Void>();
		
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
//		buapply.setMargin(new Insets(0, 0, 0, 0));
		
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
		
		tfvaldur = new JTextField(10);
		tfvaldur.setToolTipText("Default validity duration of messages");
		JButton buvaldur = new JButton("Apply");
		buvaldur.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					long val = Long.parseLong(tfvaldur.getText());
					secservice.setValidityDuration(val*60000);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
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
		final IdTableModel<String, KeyStoreEntry> kttm = new IdTableModel<String, KeyStoreEntry>(new String[]
			{"Alias", "Type", "Protected", "Expired", "Algorithm", "Validity", "Creation"}, 
			new Class<?>[]{String.class, String.class, Boolean.class, Boolean.class, String.class, String.class, String.class}, ktt)
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
					ret = obj.isProtected();
				}
				else if(column==3)
				{
					ret = obj.isExpired();
				}
				else if(column==4)
				{
					ret = obj.getAlgorithm();
				}
				else if(column==5)
				{
					ret = SUtil.SDF2.get().format(new Date(obj.getFrom())) +" - "+ SUtil.SDF2.get().format(new Date(obj.getTo()));
				}
				else if(column==6)
				{
					ret = SUtil.SDF.get().format(new Date(obj.getDate()));
				}
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
					CertificatePanel ct = new CertificatePanel(kse.getCertificates());
					pdetails.add(ct, BorderLayout.CENTER);
				}
				
				pdetails.invalidate();
				inner.invalidate();
				inner.repaint();
			}
		});
		
		updateact = new Runnable()
		{
			public void run()
			{
				secservice.getKeystoreDetails().addResultListener(new SwingResultListener<Map<String, KeyStoreEntry>>(new IResultListener<Map<String,KeyStoreEntry>>()
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
						jcc.setStatusText("Exception during refresh: "+exception.getMessage());
					}
				}));
			}
		};
		
		JScrollPane sktt = new JScrollPane(ktt);
		
		MouseAdapter ma = new MouseAdapter()
		{
			// Create file chooser lazily to allow jenkins build to succeed
			// (new JFileChooser() throws exception in headless windows)
			JFileChooser chls;
			
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
				if(chls==null)
				{
					chls	= new JFileChooser(".");
					chls.setFileFilter(new FileFilter()
					{
						public String getDescription()
						{
							return ".cer";
						}
						
						public boolean accept(File f)
						{
							return !f.isDirectory() && f.getName().endsWith(".cer");
						}
					});
				}

				if(e.isPopupTrigger())
				{
					int	row	= ktt.rowAtPoint(e.getPoint());
					
					JPopupMenu	menu	= new JPopupMenu("Key store menu");
					
					if(row!=-1)
					{
						ktt.getSelectionModel().setSelectionInterval(row, row);
						final KeyStoreEntry kse = (KeyStoreEntry)kttm.getValueAt(row, -1);
						
						
						menu.add(new AbstractAction("Delete "+kse.getType())
						{
							public void actionPerformed(ActionEvent e)
							{
								secservice.removeKeyStoreEntry(kse.getAlias());
								updateact.run();
							}
						});	
					
						menu.add(new AbstractAction("Export certificate ...")
						{
							public void actionPerformed(ActionEvent e)
							{
								String text = SSecurity.getCertificateText(((Certificate[])kse.getCertificates())[0]);
								if(JFileChooser.APPROVE_OPTION==chls.showSaveDialog(inner))
								{
									BufferedWriter out = null;
									try 
									{
										File f = chls.getSelectedFile();
										if(f.getName().indexOf(".")==-1)
											f = new File(f.getParent(), f.getName()+".cer");
										out = new BufferedWriter(new FileWriter(f));
										out.write(text);
									}
									catch(IOException ex)
									{ 
									}
									finally
									{
										try
										{
											out.close();
										}
										catch(Exception exc)
										{
										}
									}
								}
							}
						});	
					}
					
					menu.add(new AbstractAction("Import certificate from file ...")
					{
						public void actionPerformed(ActionEvent e)
						{
							PropertiesPanel pp = new PropertiesPanel();
							
							JPanel ppl = new JPanel(new BorderLayout());
							final JTextField tfentry = new JTextField();
							JButton busep = new JButton("...");
							ppl.add(tfentry, BorderLayout.CENTER);
							ppl.add(busep, BorderLayout.EAST);
							pp.addComponent("Entry name", ppl);
							
							busep.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									PlatformSelectorDialog psd = new PlatformSelectorDialog(inner, jcc.getPlatformAccess(), 
										jcc.getJCCAccess(), jcc.getCMSHandler(), jcc.getPropertyHandler(), new ComponentIconCache(jcc.getJCCAccess()));
									IComponentIdentifier cid = psd.selectAgent(null);
									if(cid!=null)
										tfentry.setText(cid.getPlatformPrefix());
								}
							});
							
							JPanel pfi = new JPanel(new BorderLayout());
							final JTextField tffile = new JTextField();
							JButton busel = new JButton("...");
							pfi.add(tffile, BorderLayout.CENTER);
							pfi.add(busel, BorderLayout.EAST);
							pp.addComponent("File", pfi);
							
							busel.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									if(JFileChooser.APPROVE_OPTION==chls.showOpenDialog(inner))
									{
										try
										{
											tffile.setText(chls.getSelectedFile().getCanonicalPath());
										}
										catch(Exception ex)
										{
										}
									}
								}
							});
							
							if(SGUI.createDialog("Import Certificate", pp, inner))
							{
								String alias = tfentry.getText();
								String fname = tffile.getText();
								if(alias.length()>0 && fname.length()>0)
								{
									try
									{
										final String name = tfentry.getText();
										FileInputStream fis = new FileInputStream(fname);
										secservice.addPlatformCertificate(new BasicComponentIdentifier(name), SSecurity.createCertificate(fis))
											.addResultListener(new IResultListener<Void>()
										{
											public void resultAvailable(Void result)
											{
												jcc.setStatusText("Successfully imported certificate for: "+name);
											}
											
											public void exceptionOccurred(Exception exception)
											{
												jcc.setStatusText("Problem while importing certificate for: "+name+" "+exception.getMessage());
											}
										});
									}
									catch(Exception ex)
									{
										ex.printStackTrace();
									}
								}
							}
						}
					});
					
					menu.add(new AbstractAction("Import certificate from platform ...")
					{
						public void actionPerformed(ActionEvent e)
						{
							PropertiesPanel pp = new PropertiesPanel();
							
							JPanel ppl = new JPanel(new BorderLayout());
							final JTextField tfentry = new JTextField();
							tfentry.setEditable(false);
							JButton busep = new JButton("...");
							ppl.add(tfentry, BorderLayout.CENTER);
							ppl.add(busep, BorderLayout.EAST);
							pp.addComponent("Entry name", ppl);
							
							final IComponentIdentifier[] cid = new IComponentIdentifier[1];
							busep.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									final PlatformSelectorDialog psd = new PlatformSelectorDialog(inner, jcc.getPlatformAccess(),
										jcc.getJCCAccess(), jcc.getCMSHandler(), jcc.getPropertyHandler(), new ComponentIconCache(jcc.getJCCAccess()));
									cid[0] = psd.selectAgent(null);
									if(cid[0]!=null)
									{
										tfentry.setText(cid[0].getPlatformPrefix());
										tfentry.setToolTipText(ComponentIdentifierRenderer.getTooltipText(cid[0]));
									}
								}
							});
							
							if(SGUI.createDialog("Import Certificate", pp, inner))
							{
								if(cid[0]!=null)
								{
									try
									{
										SServiceProvider.getService(jcc.getJCCAccess(), cid[0].getRoot(), ISecurityService.class)
											.addResultListener(new IResultListener<ISecurityService>()
										{
											public void resultAvailable(ISecurityService ss)
											{
												ss.getPlatformCertificate(null).addResultListener(new IResultListener<Certificate>()
												{
													public void resultAvailable(Certificate cert)
													{
														secservice.addPlatformCertificate(cid[0], cert)
															.addResultListener(new IResultListener<Void>()
														{
															public void resultAvailable(Void result)
															{
																jcc.setStatusText("Successfully imported certificate for: "+cid[0].getPlatformPrefix());
															}
															
															public void exceptionOccurred(Exception exception)
															{
																jcc.setStatusText("Problem while importing certificate for: "+cid[0].getPlatformPrefix()+" "+exception.getMessage());
															}
														});
													}
													
													public void exceptionOccurred(Exception exception)
													{
														jcc.setStatusText("Problem while importing certificate for: "+cid[0].getPlatformPrefix()+" "+exception.getMessage());
													}
												});
											}
											
											public void exceptionOccurred(Exception exception)
											{
												jcc.setStatusText("Problem while importing certificate for: "+cid[0].getPlatformPrefix()+" "+exception.getMessage());
											}
										});
									}
									catch(Exception ex)
									{
										jcc.setStatusText("Problem while importing certificate for: "+cid[0].getPlatformPrefix()+" "+ex.getMessage());
									}
								}
							}
						}
					});
					
					menu.add(new AbstractAction("Generate certificate ...")
					{
						public void actionPerformed(ActionEvent e)
						{
							PropertiesPanel pp = new PropertiesPanel();
							
							JPanel ppl = new JPanel(new BorderLayout());
							final JTextField tfentry = new JTextField();
							JButton busep = new JButton("...");
							ppl.add(tfentry, BorderLayout.CENTER);
							ppl.add(busep, BorderLayout.EAST);
							pp.addComponent("Entry name", ppl);
							
							JTextField tfdn = pp.createTextField("Distinguished name ", "CN=CKS Self Signed Cert", true);
							JTextField tfdur = pp.createTextField("Validity duration (days)", "365", true);
							JTextField tfalg = pp.createTextField("Algorithm", "MD5withRSA", true);
							
							busep.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									PlatformSelectorDialog psd = new PlatformSelectorDialog(inner, jcc.getPlatformAccess(),
										jcc.getJCCAccess(), jcc.getCMSHandler(), jcc.getPropertyHandler(), new ComponentIconCache(jcc.getJCCAccess()));
									IComponentIdentifier cid = psd.selectAgent(null);
									if(cid!=null)
//										tfentry.setText(cid.getName());
										tfentry.setText(cid.getPlatformPrefix());
								}
							});
							
							try
							{
								if(SGUI.createDialog("Generate Certificate", pp, inner))
								{
									final String name = tfentry.getText();
									final String dn = tfdn.getText();
									final int dur = Integer.parseInt(tfdur.getText());
									final String alg = tfalg.getText();
									
									KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");  
							 	    gen.initialize(1024);  
							 	    KeyPair keys = gen.generateKeyPair();
							 	    
							 	    Certificate cert = SSecurity.generateCertificate(dn, keys, dur, alg);

									secservice.addPlatformCertificate(new BasicComponentIdentifier(name), cert)
										.addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											jcc.setStatusText("Successfully generated certificate for: "+name);
										}
										
										public void exceptionOccurred(Exception exception)
										{
											jcc.setStatusText("Problem while generating certificate for: "+name+" "+exception.getMessage());
										}
									});
								}
							}
							catch(Exception ex)
							{
								jcc.setStatusText("Error during certificate generation: "+ex.getMessage());
							}
						}
					});	
					
					menu.add(new AbstractAction("Generate key pair ...")
					{
						public void actionPerformed(ActionEvent e)
						{
							PropertiesPanel pp = new PropertiesPanel();
							
							JPanel ppl = new JPanel(new BorderLayout());
							final JTextField tfentry = new JTextField();
							JButton busep = new JButton("...");
							ppl.add(tfentry, BorderLayout.CENTER);
							ppl.add(busep, BorderLayout.EAST);
							pp.addComponent("Entry name", ppl);
							
							busep.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									PlatformSelectorDialog psd = new PlatformSelectorDialog(inner, jcc.getPlatformAccess(),
										jcc.getJCCAccess(), jcc.getCMSHandler(), jcc.getPropertyHandler(), new ComponentIconCache(jcc.getJCCAccess()));
									IComponentIdentifier cid = psd.selectAgent(null);
									if(cid!=null)
//										tfentry.setText(cid.getName());
										tfentry.setText(cid.getPlatformPrefix());
								}
							});
							
							JTextField tfalg = pp.createTextField("Algorithm ", "RSA", true);
							JTextField tfsize = pp.createTextField("Keysize", "2048", true);
							JTextField tfval = pp.createTextField("Validity (days)", "365", true);
							JTextField tfpass = pp.createTextField("Password", "", true);
							
							try
							{
								if(SGUI.createDialog("Generate Key Pair", pp, inner))
								{
									final String name = tfentry.getText();
									final String alg = tfalg.getText();
									final int size = Integer.parseInt(tfsize.getText());
									final String pass = tfalg.getText();
									final int val = Integer.parseInt(tfval.getText());
									
									secservice.createKeyPair(new BasicComponentIdentifier(name), alg, size, pass.length()>0? pass: null, val)
										.addResultListener(new IResultListener<Void>()
									{
										public void resultAvailable(Void result)
										{
											jcc.setStatusText("Successfully generated key pair for: "+name);
										}
										
										public void exceptionOccurred(Exception exception)
										{
											jcc.setStatusText("Problem while generating key pair for: "+name+" "+exception.getMessage());
										}
									});
								}
							}
							catch(Exception ex)
							{
								jcc.setStatusText("Error during certificate generation: "+ex.getMessage());
							}
						}
					});	
					
					menu.show(ktt, e.getX(), e.getY());
				}
			}
		};
		
		ktt.addMouseListener(ma);
		ktt.getTableHeader().addMouseListener(ma);
		sktt.addMouseListener(ma);
		
		JButton bureload = new JButton("Reload");
		bureload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateact.run();
			}
		});
		updateact.run();

		// The acquire certificate settings
		final AcquireCertificatePanel acp = new AcquireCertificatePanel(jcc.getPlatformAccess(), jcc.getJCCAccess(), secservice, jcc.getCMSHandler());
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
		gbc.insets	= new Insets(2, 2, 2, 2);
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
//		gbc.gridy++;
//		gbc.gridwidth = GridBagConstraints.REMAINDER;
//		gbc.weightx = 1;
//		gbc.weighty = 0;
//		gbc.fill	= GridBagConstraints.NONE;
//		plocal.add(cbtrulan, gbc);
		gbc.gridy++;
		JLabel l = new JLabel("Validity duration [mins]");
		l.setToolTipText("Validity duration of messages, i.e. older messages are not accepted.");
		plocal.add(l, new GridBagConstraints(0, gbc.gridy, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, gbc.insets, 0, 0));
		plocal.add(tfvaldur, new GridBagConstraints(1, gbc.gridy, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, gbc.insets, 0, 0));
		plocal.add(buvaldur, new GridBagConstraints(2, gbc.gridy, 1, 1, 0, 0,  GridBagConstraints.WEST, GridBagConstraints.VERTICAL, gbc.insets, 0, 0));
		gbc.gridy++;
		plocal.add(new JPanel(), new GridBagConstraints(0, gbc.gridy, 1, 1, GridBagConstraints.REMAINDER, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, gbc.insets, 0, 0));
		
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
		slocal.add(sktt, new GridBagConstraints(0, y++, 8, 1, 1, 1, 
			GridBagConstraints.WEST, GridBagConstraints.BOTH, in, 0, 0));
		slocal.add(bureload, new GridBagConstraints(0, y++, 8, 1, 1, 0, 
			GridBagConstraints.EAST, GridBagConstraints.NONE, in, 0, 0));
				
		ICommand paddrem = new ICommand()
		{
			public void execute(Object args)
			{
				String[] tmp = (String[])args;
				secservice.setPlatformPassword(new BasicComponentIdentifier(tmp[0]), tmp[1]).addResultListener(new JCCResultListener<Void>(jcc)
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
		npp.add(cbtrulan, BorderLayout.NORTH);
		
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
		((JTabbedPane)inner).addTab("Key Store", spv);
		((JTabbedPane)inner).addTab("Remote Passwords", ppp);
		((JTabbedPane)inner).addTab("Network Names", npp);
		((JTabbedPane)inner).addTab("Virtual Platform Names", new DualVirtualNamesPanel(jcc.getPlatformAccess(), jcc.getJCCAccess(), secservice, jcc.getCMSHandler()));
			
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
		
//		secservice.getPlatformPasswords().addResultListener(new SwingExceptionDelegationResultListener<Map<String, String>, Void>(ret)
//		{
//			public void customResultAvailable(Map<String, String> passwords)
//			{
//				ppp.update(passwords);
//			}
//		});
//		
//		secservice.getNetworkPasswords().addResultListener(new SwingExceptionDelegationResultListener<Map<String, String>, Void>(ret)
//		{
//			public void customResultAvailable(Map<String, String> passwords)
//			{
//				npp.update(passwords);
//			}
//		});
		
		IIntermediateFuture<jadex.commons.ChangeEvent<Object>> fut = secservice.subscribeToEvents();
		fut.addResultListener(new IIntermediateResultListener<jadex.commons.ChangeEvent<Object>>()
		{
			public void intermediateResultAvailable(final jadex.commons.ChangeEvent<Object> event)
			{
				// Skip init
				if(event==null)
				{
					ret.setResult(null);
					return;
				}
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.out.println("event: "+event.getType()+" "+event.getValue());
						
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
						else if(ISecurityService.PROPERTY_VALIDITYDURATION.equals(event.getType()))
						{
							tfvaldur.setText(""+((Long)event.getValue()).longValue()/60000);
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
						else if(ISecurityService.PROPERTY_KEYSTORESETTINGS.equals(event.getType()))
						{
							String[] info = (String[])event.getValue();
							tfstorepath.setText(info[0]);
							tfstorepass.setText(info[1]);
							tfkeypass.setText(info[2]);
						}
						else if(ISecurityService.PROPERTY_KEYSTOREENTRIES.equals(event.getType()))
						{
//							Map<String, KeyStoreEntry> entries = (Map<String, KeyStoreEntry>)event.getValue();
							updateact.run();
						}
						else if(ISecurityService.PROPERTY_SELECTEDMECHANISM.equals(event.getType()))
						{
							acp.setSelectedMechanism(((Integer)event.getValue()).intValue());
						}
						else if(ISecurityService.PROPERTY_MECHANISMPARAMETER.equals(event.getType()))
						{
							Object[] data = (Object[])event.getValue();
							acp.setParameterValue(((Class<?>)event.getSource()).getName(), (String)data[0], data[1]);
						}
					}
				});
				
			}
			
			public void finished()
			{
				System.out.println("fin");
			}
			
			public void resultAvailable(Collection<jadex.commons.ChangeEvent<Object>> result)
			{
				System.out.println("ra");
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
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
