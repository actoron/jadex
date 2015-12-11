package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import jadex.commons.SUtil;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.platform.service.security.SSecurity;

/**
 *  Panel for displaying a certificate.
 */
public class CertificatePanel extends JPanel
{
	//-------- attributes --------
	
	/** The certificates. */
	protected Certificate[] certs;
	
	//-------- constructors --------
	
	/**
	 *  Create a new panel.
	 */
	public CertificatePanel(Certificate[] certs)
	{
		this.certs = certs;
		
		JTabbedPane tp = new JTabbedPane();
		for(Certificate cert: certs)
		{
			PropertiesPanel pp = new PropertiesPanel();
			
			String key = cert.getPublicKey().getAlgorithm();
			int len = SSecurity.getKeyLength(cert.getPublicKey());
			if(len!=-1)
				key += " ("+len+" bits)";
			
			int i=0;
			if(cert instanceof X509Certificate)
			{
				i++;
				try
				{
					final X509Certificate xcert = (X509Certificate)cert;
					pp.createTextField("Version", ""+xcert.getVersion());
					pp.createTextField("Subject", ""+xcert.getSubjectDN());
					pp.createTextField("Issuer", ""+xcert.getIssuerDN());
					pp.createTextField("Serial number", ""+SUtil.hex(xcert.getSerialNumber().toByteArray(), " ", 2));
					pp.createTextField("Validity duration", SUtil.SDF.get().format(xcert.getNotBefore())+" - "+SUtil.SDF.get().format(xcert.getNotAfter()));
					pp.createTextField("Public key", key);
					pp.createTextField("Signature algoritm", xcert.getSigAlgName());
					pp.createTextField("Fingerprint SHA-1", SSecurity.getHexMessageDigest(xcert.getEncoded(), "SHA1"));
					pp.createTextField("Fingerprint MD5", SSecurity.getHexMessageDigest(xcert.getEncoded(), "MD5"));
					
					JButton but = pp.createButton("PEM Encoding", "Show");
					but.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
//							StringWriter sw = new StringWriter();
//							PEMWriter pw = new PEMWriter(sw);
//							String pem = null;
//							try
//							{
//								pw.writeObject(xcert);
//								pw.flush();
//								pem = sw.toString();
//							}
//							catch(IOException ex)
//							{
//							}
//							try
//							{
//								pw.close();
//							}
//							catch (IOException ex)
//							{
//							}
							
							JTextArea ta = new JTextArea();
							ta.setEditable(false);
							ta.setText(SSecurity.getCertificateText(xcert));
							
							final JDialog dia = new JDialog((JFrame)null, "PEM Encoding", false);
							
							JButton bok = new JButton("OK");
							JPanel ps = new JPanel(new GridBagLayout());
							ps.add(bok, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));

							dia.getContentPane().add(ta, BorderLayout.CENTER);
							dia.getContentPane().add(ps, BorderLayout.SOUTH);
							final boolean[] ok = new boolean[1];
							bok.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									ok[0] = true;
									dia.dispose();
								}
							});
							dia.pack();
							dia.setLocation(SGUI.calculateMiddlePosition(SGUI.getWindowParent(CertificatePanel.this), dia));
							dia.setVisible(true);
						}
					});
					
					tp.addTab("Certificate "+i, pp);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
			else
			{
				pp.createTextField("Type", ""+cert.getType());
				pp.createTextField("Public key", key);
			}
		}
		
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tp), BorderLayout.CENTER);
	}
}
