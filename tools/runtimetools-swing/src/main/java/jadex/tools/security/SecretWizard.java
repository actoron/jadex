package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.gui.JPlaceholderTextField;
import jadex.commons.gui.JWizard;
import jadex.commons.gui.SGUI;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.KeySecret;
import jadex.platform.service.security.auth.PasswordSecret;
import jadex.platform.service.security.auth.SCryptParallel;
import jadex.platform.service.security.auth.X509PemSecret;

/**
 *  Wizard for selecting authentication secrets.
 *
 */
public class SecretWizard extends JWizard
{
//	protected ButtonGroup secrettypes;
	
	/** ID */
	private static final long serialVersionUID = 5314846699201632703L;

	/** SCrypt work factor / hardness for password strengthening. */
	protected static final int SCRYPT_N = 131072;
	
	/** SCrypt block size. */
	protected static final int SCRYPT_R = 8;
	
	/** SCrypt parallelization. */
	protected static final int SCRYPT_P = 4;
	
	/** Entity type which is the secret owner. */
	protected String entitytype;
	
	/** Entity which is the secret owner. */
	protected String entity;
	
	/** Current secret. */
	protected AbstractAuthenticationSecret secret;
	
	/** Final result. */
	protected AbstractAuthenticationSecret result;
	
	public SecretWizard()
	{
		String[] stypes = new String[] { "Key", "Password", "X509 Certificates", "Enter Encoded Secret" };
		start = new ChoiceNode("Select Secret Type", stypes)
		{
			public void onShow()
			{
				result = null;
				secret = null;
			}
		};
		
		WizardNode finish = createSummaryNode();
		
		WizardNode node = createKeyNode();
		node.addChild(finish);
		start.addChild(node);
		
		node = createPasswordNode();
		node.addChild(finish);
		start.addChild(node);
		
		node = createPasswordX509Node();
		node.addChild(finish);
		start.addChild(node);
		
		start.addChild(finish);
		
		next();
	}
	
	/**
	 *  Returns the result of the wizard.
	 *  
	 *  @return The result.
	 */
	public AbstractAuthenticationSecret getResult()
	{
		return result;
	}
	
	public String getEntity()
	{
		return entity;
	}
	
	/**
	 *  Sets the entity type of the secret.
	 *  @param entitytype The entity type.
	 */
	public void setEntityType(String entitytype)
	{
		this.entitytype = entitytype;
		if (entitytype != null && entitytype.length() != 0)
		{
			WizardNode choice = start;
			start = createEntityNode();
			start.addChild(choice);
			reset();
			next();
		}
	}
	
	/**
	 *  Creates the entity node.
	 *  
	 *  @return The entity node.
	 */
	protected WizardNode createEntityNode()
	{
		final JPlaceholderTextField entityfield = new JPlaceholderTextField();
		entityfield.setMinimumSize(new Dimension(400, entityfield.getMinimumSize().height));
		entityfield.setPreferredSize(entityfield.getMinimumSize());
		
		final JPanel inner = new JPanel();
		inner.setBorder(BorderFactory.createTitledBorder("Please enter " + entitytype + ":"));
		SGUI.createVerticalGroupLayout(inner, new JComponent[] { entityfield }, true);
		
		final WizardNode node = new WizardNode()
		{
			protected void onNext()
			{
				entity = entityfield.getText();
				
				if (entity == null || entity.length() == 0)
				{
					entityfield.showInvalid();
					throw new IllegalArgumentException();
				}
			}
			
			public void onShow()
			{
				entityfield.requestFocus();
			}
		};
		node.setLayout(new BorderLayout());
		node.add(inner);
		
		return node;
	}
	
	/**
	 *  Creates the key node.
	 *  
	 *  @return The key node.
	 */
	protected WizardNode createKeyNode()
	{
		final JPlaceholderTextField keyfield = new JPlaceholderTextField();
		final JPlaceholderTextField pwfield = new JPlaceholderTextField();
		
		keyfield.setPlaceholder("Key...");
//		keyfield.setMinimumSize(new Dimension(400, keyfield.getMinimumSize().height));
		keyfield.setPreferredSize(keyfield.getMinimumSize());
		
		final int minsize = 16;
		int recsize = 24;
		pwfield.setPlaceholder("Password (Min. " + minsize + " characters, " + recsize + " recommended)...");
		
//		JPanel fieldpanel = new JPanel();
//		SGUI.createVerticalGroupLayout(fieldpanel, new JComponent[] { keyfield, pwfield }, true);
		
		final JButton randbutton = new JButton(new AbstractAction("Generate Random")
		{
			public void actionPerformed(ActionEvent e)
			{
				String key = SUtil.toUTF8(Base64.encodeNoPadding(KeySecret.createRandom().getKey()));
				keyfield.setNonPlaceholderText(key);
			}
		});
		JButton pwbutton = new JButton(new AbstractAction("Generate from Password")
		{
			public void actionPerformed(ActionEvent e)
			{
				final JButton pwbutton = (JButton) e.getSource();
				final String pw = pwfield.getText();
				if (pw != null)
				{
					if (pw.length() < minsize)
					{
//						JOptionPane.showMessageDialog(SecretWizard.this, "This password is too short to generate a key. Please enter a longer password.");
						pwfield.showInvalid("Password must be at least " + minsize + " characters.");
					}
					else
					{
						byte[] tsalt = null;
						if (entity != null)
							tsalt = entity.getBytes(SUtil.UTF8);
						else
							tsalt = pw.getBytes(SUtil.UTF8);
						final byte[] salt = tsalt;
						
						
						setAllButtonsEnabled(false);
						randbutton.setEnabled(false);
						pwbutton.setEnabled(false);
						Thread t = new Thread(new Runnable()
						{
							public void run()
							{
								final byte[] keydata = SCryptParallel.generate(pw.getBytes(SUtil.UTF8), salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, 32);
								SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										keyfield.setNonPlaceholderText(new String(Base64.encodeNoPadding(keydata), SUtil.ASCII));
										setAllButtonsEnabled(true);
										randbutton.setEnabled(true);
										pwbutton.setEnabled(true);
										busyring.deactivate();
									}
								});
							}
						});
						t.setPriority(Thread.MIN_PRIORITY);
						t.setDaemon(true);
						busyring.activate();
						t.start();
						
					}
				}
			}
		});
		
		SGUI.adjustComponentSizes(new JComponent[] { randbutton, pwbutton });
		JPanel inner = new JPanel();
		GroupLayout l = new GroupLayout(inner);
		inner.setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l.setAutoCreateGaps(true);
		
		SequentialGroup khgroup = l.createSequentialGroup();
		ParallelGroup kvgroup = l.createParallelGroup();
		for (JComponent comp : new JComponent[] { keyfield, randbutton })
		{
			kvgroup.addComponent(comp);
			khgroup.addComponent(comp);
		}
		
		SequentialGroup phgroup = l.createSequentialGroup();
		ParallelGroup pvgroup = l.createParallelGroup();
		for (JComponent comp : new JComponent[] { pwfield, pwbutton })
		{
			pvgroup.addComponent(comp);
			phgroup.addComponent(comp);
		}
		
		ParallelGroup hgroup = l.createParallelGroup();
		hgroup.addGroup(khgroup);
		hgroup.addGroup(phgroup);
		SequentialGroup vgroup = l.createSequentialGroup();
		vgroup.addGroup(kvgroup);
		vgroup.addGroup(pvgroup);
		
		l.linkSize(SwingConstants.VERTICAL, keyfield, randbutton, pwfield, pwbutton);
		
		l.setVerticalGroup(vgroup);
		l.setHorizontalGroup(hgroup);
		
//		SGUI.adjustComponentVerticalSizes(new JComponent[] { randbutton, pwbutton, keyfield, pwfield });
		
		final WizardNode node = new WizardNode()
		{
			/** Flag if first shown. */
			protected boolean firstshow = true;
			
			protected void onNext()
			{
				KeySecret keysecret = null;
				
				try
				{
					keysecret = new KeySecret(Base64.decodeNoPadding(keyfield.getText().getBytes(SUtil.UTF8)), false);
				}
				catch (Exception e)
				{
				}
				
				if (keysecret == null)
				{
					keyfield.showInvalid();
					throw new IllegalArgumentException();
				}
				
				secret = keysecret;
			}
			
			public void onShow()
			{
				if (firstshow)
				{
					String key = SUtil.toUTF8(Base64.encodeNoPadding(KeySecret.createRandom().getKey()));
					keyfield.setNonPlaceholderText(key);
					firstshow = false;
				}
				randbutton.requestFocus();
			}
		};
		node.setLayout(new BorderLayout());
		node.add(inner);
		
		return node;
	}
	
	/**
	 *  Creates the key node.
	 *  
	 *  @return The key node.
	 */
	protected WizardNode createPasswordNode()
	{
		final JPlaceholderTextField pwfield = new JPlaceholderTextField();
		pwfield.setMinimumSize(new Dimension(400, pwfield.getMinimumSize().height));
		pwfield.setPreferredSize(pwfield.getMinimumSize());
		
		final int minsize = 10;
		int recsize = 16;
		String restr = "(Min. " + minsize + " characters, " + recsize + " recommended)";
		pwfield.setPlaceholder("Password " + restr +"...");
		
		final JPanel inner = new JPanel();
		inner.setBorder(BorderFactory.createTitledBorder("Enter Password " + restr));
		SGUI.createVerticalGroupLayout(inner, new JComponent[] { pwfield }, true);
		
		WizardNode node = new WizardNode()
		{
			protected void onNext()
			{
				String pw = pwfield.getText();
				
				if (pw.length() < minsize)
				{
					pwfield.showInvalid();
					throw new IllegalArgumentException();
				}
				
				secret = new PasswordSecret(pw, false);
			}
		};
		node.setLayout(new BorderLayout());
		node.add(inner);
		
		return node;
	}
	
	/**
	 *  Creates the X509 node.
	 *  
	 *  @return The X509 node.
	 */
	protected WizardNode createPasswordX509Node()
	{
		final CertTree trusttree = new CertTree(SecuritySettingsPanel.DEFAULT_CERT_STORE);
		final CertTree certtree = new CertTree(SecuritySettingsPanel.DEFAULT_CERT_STORE);
		
		JPanel trustpanel = new JPanel();
		trustpanel.setBorder(BorderFactory.createTitledBorder("Trust Anchor"));
		trustpanel.setLayout(new BorderLayout());
		trustpanel.add(trusttree);
		
		JPanel certpanel = new JPanel();
		certpanel.setBorder(BorderFactory.createTitledBorder("Local Certificate"));
		certpanel.setLayout(new BorderLayout());
		certpanel.add(certtree);
		
		final JCheckBox validonly = new JCheckBox("Validation only");
		validonly.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				certtree.setEnabled(!validonly.isSelected());
//				if (certtree.isEnabled())
//					certtree.updateExternalModel();
			}
		});
		certpanel.add(validonly, BorderLayout.SOUTH);
		
		final AbstractAction validationaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean valid = trusttree.getSelectedCert() != null;
				valid &= validonly.isSelected() | (certtree.getSelectedCert() != null && certtree.getSelectedCert().getSecondEntity() != null);
				setEnableNext(valid);
			}
		};
		TreeSelectionListener sellis = new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				validationaction.actionPerformed(null);
			}
		};
		
		validonly.addActionListener(validationaction);
		trusttree.addTreeSelectionListener(sellis);
		certtree.addTreeSelectionListener(sellis);
		
		JPanel inner = new JPanel();
		SGUI.createVerticalGroupLayout(inner, new JComponent[] { trustpanel, certpanel }, false);
		
		WizardNode node = new WizardNode()
		{
			public void onShow()
			{
				validationaction.actionPerformed(null);
			}
			
			protected void onNext()
			{
				Tuple2<String, String> trust = trusttree.getSelectedCert();
				Tuple2<String, String> cert = certtree.getSelectedCert();
				
				X509PemSecret s = null;
				if (validonly.isSelected())
					s = new X509PemSecret(trust.getFirstEntity(), null, null);
				else
					s = new X509PemSecret(trust.getFirstEntity(), cert.getFirstEntity(), cert.getSecondEntity());
				
				secret = s;
			}
		};
		node.setLayout(new BorderLayout());
		node.add(inner);
		
		return node;
	}
	
	/**
	 *  Creates the key node.
	 *  
	 *  @return The key node.
	 */
	protected WizardNode createSummaryNode()
	{
		final JTextArea secretarea = new JTextArea();
		SGUI.addCopyPasteMenu(secretarea);
		secretarea.getDocument().addDocumentListener(new DocumentListener()
		{
			public void removeUpdate(DocumentEvent e)
			{
				changedUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				changedUpdate(e);
			}
			
			public void changedUpdate(DocumentEvent e)
			{
				AbstractAuthenticationSecret secret = null;
				try
				{
					secret = AbstractAuthenticationSecret.fromString(SGUI.getText(secretarea), true);
				}
				catch (Exception e1)
				{
				}
				
				if (secret != null)
					setEnableNext(true);
				else
					setEnableNext(false);
			}
		});
		JScrollPane scroll = new JScrollPane(secretarea);
		
		WizardNode node = new WizardNode()
		{
			public void onShow()
			{
				if (secret != null)
				{
					SGUI.setText(secretarea, secret.toString());
					secretarea.setEditable(false);
					secretarea.setCaretPosition(0);
				}
				else
				{
					setEnableNext(false);
				}
			}
			
			protected void onFinish()
			{
				result = AbstractAuthenticationSecret.fromString(SGUI.getText(secretarea), true);
			}
		};
		node.setLayout(new BorderLayout());
		JPanel inner = new JPanel();
		inner.setBorder(BorderFactory.createTitledBorder("Encoded Secret"));
		node.add(inner);
		SGUI.createVerticalGroupLayout(inner, new JComponent[] { scroll }, false);
		return node;
	}
}