package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jadex.commons.Base64;
import jadex.commons.SUtil;
import jadex.commons.gui.JPlaceholderTextField;
import jadex.commons.gui.JWizard;
import jadex.commons.gui.SGUI;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;
import jadex.platform.service.security.auth.KeySecret;
import jadex.platform.service.security.auth.PasswordSecret;
import jadex.platform.service.security.auth.SCryptParallel;

/**
 *  Wizard for selecting authentication secrets.
 *
 */
public class SecretWizard extends JWizard
{
//	protected ButtonGroup secrettypes;
	
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
		keyfield.setMinimumSize(new Dimension(400, keyfield.getMinimumSize().height));
		keyfield.setPreferredSize(keyfield.getMinimumSize());
		
		final int minsize = 12;
		int recsize = 24;
		pwfield.setPlaceholder("Password (Min. " + minsize + " characters, " + recsize + " recommended)...");
		
		JPanel fieldpanel = new JPanel();
		SGUI.createVerticalGroupLayout(fieldpanel, new JComponent[] { keyfield, pwfield }, true);
		
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
//				String pw = JOptionPane.showInputDialog("Enter Password (Min. " + minsize + " characters)");
				final String pw = pwfield.getText();
				if (pw != null)
				{
					if (pw.length() < minsize)
					{
//						JOptionPane.showMessageDialog(SecretWizard.this, "This password is too short to generate a key. Please enter a longer password.");
						pwfield.showInvalid();
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
								final byte[] keydata = SCryptParallel.generate(pw.getBytes(SUtil.UTF8), salt, 524288, 16, 4, 32);
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
		JPanel bpanel = new JPanel();
		SGUI.createVerticalGroupLayout(bpanel, new JComponent[] { randbutton, pwbutton }, true);
		
		JPanel inner = new JPanel();
		SGUI.createHorizontalGroupLayout(inner, new JComponent[] { fieldpanel, bpanel }, true);
		
		final WizardNode node = new WizardNode()
		{
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
		
		WizardNode node = new WizardNode()
		{
			public void onShow()
			{
				if (secret != null)
				{
					SGUI.setText(secretarea, secret.toString());
					secretarea.setEditable(false);
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
		SGUI.createVerticalGroupLayout(inner, new JComponent[] { secretarea }, false);
		return node;
	}
}
