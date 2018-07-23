package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import jadex.commons.Tuple2;
import jadex.commons.gui.JBusyRing;
import jadex.commons.gui.JPlaceholderTextField;
import jadex.commons.gui.SGUI;
import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SSecurity;

/**
 *  Panel for adding new certificates.
 */
@SuppressWarnings("rawtypes") // Bad JComboBox, BAD!
public class AddCertPanel extends JPanel
{
	/** Serial */
	private static final long serialVersionUID = 8626220123653845241L;

	/** Shorthand for GL-size. */
	protected static final int PS = GroupLayout.PREFERRED_SIZE;
	
	/** Shorthand for GL-size. */
	protected static final int DS = GroupLayout.DEFAULT_SIZE;
	
	/** Certificate of the issuer, if available. */
	protected PemKeyPair issuercert;
	
	/** Button group of certificate type (CA, self-signed, etc.). */
	protected ButtonGroup certtypes;
	
	/** Signature algorithm chooser. */
	protected JComboBox sigalg;
	
	/** Signature algorithm key strength chooser. */
	protected JComboBox sigalgstr;
	
	/** Signature algorithm configuration chooser. */
	protected JComboBox sigalgconf;
	
	/** Hash algorithm chooser. */
	protected JComboBox hashalg;
	
	/** Validity of the certificate */
	protected JComboBox validity;
	
	/** Path length of CA certificates */
	protected JComboBox pathlen;
	
	/** The subject panel. */
	protected SubjectPanel subjectpanel;
	
	/** Certificate text area. */
	protected JScrollPane certarea;
	
	/** Key text area. */
	protected JScrollPane keyarea;
	
	/** The action listener. */
	protected ActionListener listener;
	
	/**
	 * 
	 */
	public AddCertPanel(PemKeyPair selectedcert, ActionListener listener)
	{
		this.listener = listener;
		this.issuercert = selectedcert;
		
		JPanel certtypepanel = createCertTypePanel();
		add(certtypepanel);
		
		JPanel secpanel = createSecurityPanel();
		add(secpanel);
		
		subjectpanel = new SubjectPanel();
		add(subjectpanel);
		
		JPanel certareapanel = new JPanel();
		certareapanel.setPreferredSize(new Dimension(80, 80));
		certareapanel.setLayout(new BorderLayout());
		certareapanel.setBorder(BorderFactory.createTitledBorder("Certificate"));
		JTextArea txta = new JTextArea();
		txta.setEditable(true);
		certarea = new JScrollPane(txta);
		certarea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		certarea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		certareapanel.add(certarea);
		add(certareapanel);
		
		final JPanel keyareapanel = new JPanel();
		keyareapanel.setPreferredSize(new Dimension(80, 80));
		keyareapanel.setLayout(new BorderLayout());
		keyareapanel.setBorder(BorderFactory.createTitledBorder("Private Key"));
		txta = new JTextArea();
		txta.setEditable(true);
		keyarea = new JScrollPane(txta);
		keyarea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		keyarea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		keyareapanel.add(keyarea);
		add(keyareapanel);
		
		JPanel buttonpanel = createButtonPanel();
		add(buttonpanel);
		
		SGUI.adjustComponentHorizontalSizes(new JComponent[] { certtypepanel, secpanel, subjectpanel });
		
		GroupLayout layout = createGroupLayout(this);
		setLayout(layout);
		
		SequentialGroup noncontrolgroup = layout.createSequentialGroup();
		noncontrolgroup.addGroup(layout.createParallelGroup()
				.addComponent(certtypepanel, PS, DS, PS)
				.addComponent(secpanel, PS, DS, PS)
				.addComponent(subjectpanel, PS, DS, PS));
		noncontrolgroup.addGroup(layout.createParallelGroup()
				.addComponent(certareapanel)
				.addComponent(keyareapanel));
//				.addComponent(buttonpanel));
		SequentialGroup hgroup = layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addGroup(noncontrolgroup)
					.addComponent(buttonpanel));
		layout.setHorizontalGroup(hgroup);
		
		SequentialGroup vgroup = layout.createSequentialGroup();
		vgroup.addGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addComponent(certtypepanel, PS, DS, PS)
				.addComponent(secpanel, PS, DS, PS)
				.addComponent(subjectpanel, DS, DS, PS))
			.addGroup(layout.createSequentialGroup()
				.addComponent(certareapanel)
				.addComponent(keyareapanel))
		);
		vgroup.addGroup(layout.createParallelGroup()
					.addComponent(buttonpanel, PS, DS, PS));
		layout.setVerticalGroup(vgroup);
		
		doLayout();
//		busyring.activate();
	}
	
	/**
	 *  Gets the certificate from the panel.
	 */
	public PemKeyPair getCertificate()
	{
		PemKeyPair ret = null;
		String cert = ((JTextArea) certarea.getViewport().getView()).getText();
		String key = ((JTextArea) keyarea.getViewport().getView()).getText();
		if (cert != null && cert.length() > 0)
		{
			ret = new PemKeyPair();
			ret.setCertificate(cert);
			if (key != null && key.length() > 0)
				ret.setKey(key);
		}
		return ret;
	}
	
	protected JPanel createCertTypePanel()
	{
		if (issuercert != null)
		{
			if (!SSecurity.isCaCertificate(issuercert.getCertificate()))
			{
				issuercert = null;
			}
		}
		
		certtypes = new ButtonGroup();
		JPanel typepanel = new JPanel();
		typepanel.setLayout(new GridLayout(issuercert == null ? 2 : 4, 1));
		typepanel.setBorder(BorderFactory.createTitledBorder("Certificate Type"));
		
		ActionListener certtypelis = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int sel = SGUI.getSelectedButton(certtypes);
				if (sel == 1 || sel == 2)
					pathlen.setEnabled(true);
				else
					pathlen.setEnabled(false);
			}
		};
		
		JRadioButton certtype = new JRadioButton("Self-signed Certificate");
		certtype.setSelected(true);
		certtype.addActionListener(certtypelis);
		certtypes.add(certtype);
		typepanel.add(certtype);
		
		certtype = new JRadioButton("Root CA Certificate");
		certtype.addActionListener(certtypelis);
		certtypes.add(certtype);
		typepanel.add(certtype);
		
		if (issuercert != null)
		{
			certtype = new JRadioButton("Intermediate CA Certificate");
			certtype.addActionListener(certtypelis);
			certtypes.add(certtype);
			typepanel.add(certtype);
			certtype = new JRadioButton("Standard Certificate");
			certtype.addActionListener(certtypelis);
			certtype.setSelected(true);
			certtypes.add(certtype);
			typepanel.add(certtype);
		}
		
		return typepanel;
	}
	
	/**
	 *  Creates the panel for security infos.
	 *  
	 *  @return The panel for security infos.
	 */
	@SuppressWarnings("unchecked")
	protected JPanel createSecurityPanel()
	{
		JPanel cryptsettings = new JPanel();
		cryptsettings.setBorder(BorderFactory.createTitledBorder("Security"));
		
		GroupLayout layout = createGroupLayout(cryptsettings);
		cryptsettings.setLayout(layout);
		
		JLabel sigalgstrlb = new JLabel("Key Strength (min.)");
		sigalgstr = new JComboBox();
		sigalgstr.setEditable(false);
		sigalgstr.setLightWeightPopupEnabled(false);
		
		JLabel sigalgconflb = new JLabel("Curve");
		sigalgconf = new JComboBox();
		sigalgconf.addItem("NIST P");
		sigalgconf.addItem("NIST K");
		sigalgconf.addItem("Brainpool");
		sigalgconf.setLightWeightPopupEnabled(false);
		
		JLabel sigalglb = new JLabel("Algorithm");
		sigalg = new JComboBox();
		sigalg.setLightWeightPopupEnabled(false);
		sigalg.addItem("ECDSA");
		sigalg.addItem("RSA");
		sigalg.addItem("DSA");
		sigalg.setEditable(false);
		ItemListener sigalglis = new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				sigalgstr.removeAllItems();
				if (e == null || "ECDSA".equals(e.getItemSelectable().getSelectedObjects()[0]))
				{
					sigalgstr.addItem("256");
					sigalgstr.addItem("384");
					sigalgstr.addItem("512");
					sigalgstr.setSelectedIndex(1);
					
					sigalgconf.setEnabled(true);
				}
				else
				{
					sigalgstr.addItem("2048");
					sigalgstr.addItem("3072");
					sigalgstr.addItem("4096");
					sigalgstr.addItem("6144");
					sigalgstr.addItem("8192");
					sigalgstr.addItem("16384");
					sigalgstr.setSelectedIndex(2);
					
					sigalgconf.setEnabled(false);
				}
			}
		};
		sigalg.addItemListener(sigalglis);
		sigalglis.itemStateChanged(null);
		
		JLabel hashalglb = new JLabel("Signature Hash");
		hashalg = new JComboBox();
		hashalg.setLightWeightPopupEnabled(false);
		hashalg.setEditable(false);
		hashalg.addItem("SHA256");
		hashalg.addItem("SHA384");
		hashalg.addItem("SHA512");
		hashalg.setSelectedIndex(2);
		
		JLabel validitylb = new JLabel("Validity");
		validity = new JComboBox();
		validity.setLightWeightPopupEnabled(false);
		validity.setEditable(true);
		validity.addItem("30 days");
		validity.addItem("90 days");
		validity.addItem("180 days");
		validity.addItem("1 year");
		validity.addItem("2 years");
		validity.addItem("3 years");
		validity.addItem("5 years");
		validity.addItem("10 years");
		validity.setSelectedIndex(4);
		validity.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (!validity.hasFocus())
				{
					String val = (String) validity.getModel().getSelectedItem();
					
					try
					{
						int ival = Integer.parseInt(val);
						
						if (ival > 0)
						{
							if (ival != 0 && ival % 365 == 0)
								validity.getModel().setSelectedItem("" + (ival / 365) + " years");
							else
								validity.getModel().setSelectedItem("" + ival + " days");
						}
						else
						{
							validity.setSelectedIndex(4);
						}
					}
					catch (Exception e1)
					{
						int ival = parseIntSuffix(val, " days");
						if (ival < 0)
						{
							ival = parseIntSuffix(val, " years");
						}
						
						if (ival > 0)
							validity.getModel().setSelectedItem(val);
						else
							validity.setSelectedIndex(4);
					}
				}
			}
		});
		((JTextComponent) validity.getEditor().getEditorComponent()).addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				((JTextComponent) validity.getEditor().getEditorComponent()).setText("");
			}
		});
		
		JLabel pathlenlb = new JLabel("Path Length");
		pathlen = new JComboBox();
		pathlen.setLightWeightPopupEnabled(false);
		pathlen.addItem("0");
		pathlen.addItem("1");
		pathlen.addItem("2");
		pathlen.addItem("3");
		pathlen.addItem("4");
		pathlen.addItem("5");
		pathlen.addItem("Unlimited");
		pathlen.setSelectedIndex(6);
		pathlen.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (!pathlen.hasFocus())
				{
					int val = 0;
					try
					{
						val = Integer.parseInt((String) pathlen.getSelectedItem());
					}
					catch (Exception e1)
					{
					}
					
					pathlen.getModel().setSelectedItem("" + val);
				}
			}
		});
		pathlen.setEnabled(false);
		
		cryptsettings.add(sigalglb);
		cryptsettings.add(sigalgconflb);
		cryptsettings.add(sigalgstrlb);
		cryptsettings.add(hashalglb);
		cryptsettings.add(validitylb);
		cryptsettings.add(pathlenlb);
		cryptsettings.add(sigalg);
		cryptsettings.add(sigalgconf);
		cryptsettings.add(sigalgstr);
		cryptsettings.add(hashalg);
		cryptsettings.add(validity);
		cryptsettings.add(pathlen);
		
		SequentialGroup hgroup = layout.createSequentialGroup();
		hgroup.addGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addGroup(createLabeledGroupH(layout, sigalglb, sigalg))
				.addGroup(createLabeledGroupH(layout, validitylb, validity))
				.addGroup(createLabeledGroupH(layout, pathlenlb, pathlen)))
			.addGroup(layout.createSequentialGroup()
				.addGroup(createLabeledGroupH(layout, sigalgstrlb, sigalgstr))
				.addGroup(createLabeledGroupH(layout, hashalglb, hashalg))
				.addGroup(createLabeledGroupH(layout, sigalgconflb, sigalgconf))));
		layout.setHorizontalGroup(hgroup);
		
		SequentialGroup vgroup = layout.createSequentialGroup();
		vgroup.addGroup(layout.createParallelGroup()
			.addGroup(createLabeledGroupV(layout, sigalglb, sigalg))
			.addGroup(createLabeledGroupV(layout, validitylb, validity))
			.addGroup(createLabeledGroupV(layout, pathlenlb, pathlen)));
		vgroup.addGroup(layout.createParallelGroup()
			.addGroup(createLabeledGroupV(layout, sigalgstrlb, sigalgstr))
			.addGroup(createLabeledGroupV(layout, hashalglb, hashalg))
			.addGroup(createLabeledGroupV(layout, sigalgconflb, sigalgconf)));
		layout.setVerticalGroup(vgroup);
		
		SGUI.adjustComponentSizes(cryptsettings.getComponents());
		
		return cryptsettings;
	}
	
	/**
	 *  Creates the button panel.
	 *  
	 *  @return The button panel.
	 */
	protected JPanel createButtonPanel()
	{
		JPanel ret = new JPanel();
		ret.setLayout(new BoxLayout(ret, BoxLayout.LINE_AXIS));
//		GroupLayout l = new GroupLayout(ret);
		
		final JBusyRing busyring = new JBusyRing();
//		busyring.activate();
		
		final JButton gen = new JButton(new AbstractAction("Generate")
		{
			public void actionPerformed(final ActionEvent e)
			{
				final String subjectdn = subjectpanel.getDn();
				if (subjectdn == null)
					return;
				
				final String sigalgtxt = sigalg.getSelectedItem().toString();
				final int strength = Integer.parseInt(sigalgstr.getSelectedItem().toString());
				final String sigalgcfg = sigalgconf.getSelectedItem().toString();
				final String hashalgtxt = hashalg.getSelectedItem().toString();
				
				String validstr = (String) validity.getSelectedItem();
				int valid = 0;
				if (validstr.endsWith(" days"))
				{
					valid = Integer.parseInt(validstr.substring(0, validstr.length() - 5));
				}
				else if (validstr.endsWith(" years"))
				{
					valid = Integer.parseInt(validstr.substring(0, validstr.length() - 6));
					valid *= 365;
				}
				else if (validstr.equals("Unlimited"))
				{
					valid = -1;
				}
				final int daysvalid = valid;
				
				((JButton) e.getSource()).setEnabled(false);				
				
				Thread t = new Thread(new Runnable()
				{
					public void run()
					{
						Tuple2<String, String> tmp = null;
						int choice = SGUI.getSelectedButton(certtypes);
						switch (choice)
						{
							case 0:
							default:
								tmp = SSecurity.createSelfSignedCertificate(subjectdn, sigalgtxt, sigalgcfg, hashalgtxt, strength, daysvalid);
								break;
							case 1:
								tmp = SSecurity.createRootCaCertificate(subjectdn, -1, sigalgtxt, sigalgcfg, hashalgtxt, strength, daysvalid);
								break;
							case 2:
								tmp = SSecurity.createIntermediateCaCertificate(issuercert.getCertificate(), issuercert.getKey(), subjectdn, 0, sigalgtxt, sigalgcfg, hashalgtxt, strength, daysvalid);
								break;
							case 3:
								tmp = SSecurity.createCertificate(issuercert.getCertificate(), issuercert.getKey(), subjectdn, sigalgtxt, sigalgcfg, hashalgtxt, strength, daysvalid);
						}
						final Tuple2<String, String> res = tmp;
						
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								((JButton) e.getSource()).setEnabled(true);
								busyring.deactivate();
								
								((JTextArea) certarea.getViewport().getView()).setText(res.getFirstEntity());
								((JTextArea) certarea.getViewport().getView()).setCaretPosition(0);
								((JTextArea) keyarea.getViewport().getView()).setText(res.getSecondEntity());
								((JTextArea) keyarea.getViewport().getView()).setCaretPosition(0);
							}
						});
					}
				});
				t.setDaemon(true);
				busyring.activate();
				t.start();
			}
		});
		
		busyring.setSize(gen.getPreferredSize().height, gen.getPreferredSize().height);
//		busyring.setSize(256, 256);
		
		ret.add(busyring);
		ret.add(Box.createHorizontalGlue());
		ret.add(gen);
		
		if (listener != null)
		{
			JButton cancel = new JButton(new AbstractAction("Cancel")
			{
				public void actionPerformed(ActionEvent e)
				{
					((JTextArea) certarea.getViewport().getView()).setText("");
					((JTextArea) keyarea.getViewport().getView()).setText("");
					
					listener.actionPerformed(new ActionEvent(AddCertPanel.this, -1, ""));
				}
			});
			
			ret.add(cancel);
			
			JButton add = new JButton(new AbstractAction("Add")
			{
				public void actionPerformed(ActionEvent e)
				{
					try
					{
						boolean check = SSecurity.readCertificateChainFromPEM(((JTextArea) certarea.getViewport().getView()).getText()) != null;
						if (((JTextArea) keyarea.getViewport().getView()).getText().length() > 0)
							check &= SSecurity.readPrivateKeyFromPEM(((JTextArea) keyarea.getViewport().getView()).getText()) != null;
						
						if (!check)
							throw new IllegalArgumentException();
						
						try
						{
							listener.actionPerformed(new ActionEvent(AddCertPanel.this, ActionEvent.ACTION_PERFORMED, ""));
						}
						catch (Exception e1)
						{
						}
					}					
					catch (Exception e1)
					{
						JOptionPane.showMessageDialog(AddCertPanel.this, "Invalid certificate.");
					}
				}
			});
			
			ret.add(add);
		}
		
//		SequentialGroup hgroup = l.createSequentialGroup()
//			.addComponent(gen, PS, DS, PS)
//			.addComponent(busyring, PS, DS, PS);
//		l.setHorizontalGroup(hgroup);
//		
//		SequentialGroup vgroup = l.createSequentialGroup()
//			.addGroup(l.createParallelGroup()
//				.addComponent(gen, PS, DS, PS)
//				.addComponent(busyring, PS, DS, PS));
//		l.setVerticalGroup(vgroup);
		
		return ret;
	}
	
	/**
	 *  Panel with subject data.
	 *
	 */
	protected static class SubjectPanel extends JPanel
	{
		/** CN field. */
		protected JPlaceholderTextField cnfield;
		
		/** OU field. */
		protected JPlaceholderTextField oufield;
		
		/** O field. */
		protected JPlaceholderTextField ofield;
		
		/** L field. */
		protected JPlaceholderTextField lfield;
		
		/** S field. */
		protected JPlaceholderTextField sfield;
		
		/** C field. */
		protected JPlaceholderTextField cfield;
		
		/** Light yellow. */
		protected static final Color L_Yellow = new Color(1.0f, 1.0f, 0.82f, 1.0f);
		
		/** Soft red. */
		protected static final Color S_RED = new Color(252, 220, 216);
		
		public SubjectPanel()
		{
			setLayout(new BorderLayout());
			setMinimumSize(new Dimension(200, getMinimumSize().height));
			
			JPanel inner = new JPanel();
			setBorder(BorderFactory.createTitledBorder("Certificate Subject"));
			
			cnfield = new JPlaceholderTextField();
			cnfield.setPlaceholder("Name (CN)");
			cnfield.setInvalidColor(Color.RED);
			cnfield.addFocusListener(new FocusAdapter()
			{
				public void focusGained(FocusEvent e)
				{
//					cnfield.setBackground(Color.WHITE);
				}
			});
			inner.add(cnfield);
			
			oufield = new JPlaceholderTextField();
			oufield.setPlaceholder("Organizational Unit (OU)");
			inner.add(oufield);
			
			ofield = new JPlaceholderTextField();
			ofield.setPlaceholder("Organization (O)");
			inner.add(ofield);
			
			lfield = new JPlaceholderTextField();
			lfield.setPlaceholder("City (L)");
			inner.add(lfield);
			
			sfield = new JPlaceholderTextField();
			sfield.setPlaceholder("State (S)");
			inner.add(sfield);
			
			cfield = new JPlaceholderTextField();
			cfield.setPlaceholder("Country (C)");
			inner.add(cfield);
			
			GroupLayout layout = createGroupLayout(inner);
			inner.setLayout(layout);
			
			SequentialGroup hgroup = layout.createSequentialGroup();
			hgroup.addGroup(layout.createParallelGroup()
					.addComponent(cnfield)
					.addComponent(ofield)
					.addComponent(oufield)
					.addComponent(lfield)
					.addComponent(sfield)
					.addComponent(cfield));
			layout.setHorizontalGroup(hgroup);
			
			int mingap = 5;
			int maxgap = 5;
			
			SequentialGroup vgroup = layout.createSequentialGroup()
				.addComponent(cnfield, PS, DS, PS)
				.addGap(mingap, mingap, maxgap)
				.addComponent(ofield, PS, DS, PS)
				.addGap(mingap, mingap, maxgap)
				.addComponent(oufield, PS, DS, PS)
				.addGap(mingap, mingap, maxgap)
				.addComponent(lfield, PS, DS, PS)
				.addGap(mingap, mingap, maxgap)
				.addComponent(sfield, PS, DS, PS)
				.addGap(mingap, mingap, maxgap)
				.addComponent(cfield, PS, DS, PS);
			layout.setVerticalGroup(vgroup);
			
//			inner.setPreferredSize(inner.getMinimumSize());
//			setMaximumSize(inner.getMaximumSize());
			
//			add(inner);
			JScrollPane scroll = new JScrollPane(inner);
			scroll.setBorder(BorderFactory.createEmptyBorder());
			add(scroll);
		}
		
		/** Returns the DN */
		public String getDn()
		{
			if (cnfield.getText().length() == 0)
			{
//				cnfield.setBackground(S_RED);
//				cnfield.setBackground(Color.RED);
//				cnfieldborder.setInnerColor(Color.RED);
//				cnfieldpanel.repaint();
//				cnfield.repaint();
				cnfield.showInvalid();
				return null;
			}
			
			StringBuilder ret = new StringBuilder();
			ret.append("CN=");
			ret.append(cnfield.getText());
			
			if (oufield.getText().length() > 0)
			{
				ret.append(", OU=");
				ret.append(oufield.getText());
			}
			
			if (ofield.getText().length() > 0)
			{
				ret.append(", O=");
				ret.append(ofield.getText());
			}
			
			if (lfield.getText().length() > 0)
			{
				ret.append(", L=");
				ret.append(lfield.getText());
			}

			if (sfield.getText().length() > 0)
			{
				ret.append(", S=");
				ret.append(sfield.getText());
			}

			if (cfield.getText().length() > 0)
			{
				ret.append(", C=");
				ret.append(cfield.getText());
			}
			
//			cnfieldborder.setInnerColor(Color.WHITE);
//			cnfieldpanel.repaint();
			cnfield.repaint();
//			cnfield.setBackground(L_Yellow);
			
			return ret.toString();
		}
	}
	
	/**
	 *  Creates a configured group layout.
	 *  
	 *  @param host Host component.
	 *  @return Layout.
	 */
	protected static final GroupLayout createGroupLayout(Container host)
	{
		GroupLayout ret = new GroupLayout(host);
		ret.setAutoCreateGaps(true);
		ret.setAutoCreateContainerGaps(true);
		return ret;
	}
	
	/**
	 *  Create horizontal label-button group.
	 */
	protected static final GroupLayout.Group createLabeledGroupH(GroupLayout layout, JLabel label, JComponent widget)
	{
		return layout.createParallelGroup()
			.addComponent(label, PS, DS, PS)
			.addComponent(widget, PS, DS, PS);
	}
	
	/**
	 *  Create vertical label-button group.
	 */
	protected static final GroupLayout.Group createLabeledGroupV(GroupLayout layout, JLabel label, JComponent widget)
	{
//		ComponentPlacement r = ComponentPlacement.RELATED;
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		widget.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		return layout.createSequentialGroup()
				.addComponent(label, PS, DS, PS)
				.addGap(0, 0, 0)
				.addComponent(widget, PS, DS, PS);
	}
	
	/**
	 *  Parses a positive integer with suffix.
	 */
	protected static int parseIntSuffix(String in, String suffix)
	{
		int ret = -1;
		
		if (in.endsWith(suffix))
		{
			try
			{
				ret = Integer.parseInt(in.substring(0, in.length() - suffix.length()));
			}
			catch (Exception e)
			{
			}
		}
		
		return ret;
	}
}
