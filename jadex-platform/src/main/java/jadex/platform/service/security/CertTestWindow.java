package jadex.platform.service.security;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.gui.JBusyRing;
import jadex.commons.security.SSecurity;

public class CertTestWindow extends JFrame
{
	public CertTestWindow()
	{
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		    {
//		        if ("Nimbus".equals(info.getName()))
		    	if ("GTK+".equals(info.getName()))
//		    	if ("CDE/Motif".equals(info.getName()))
		        {
		            UIManager.setLookAndFeel(info.getClassName());
		            
//		            break;
		        }
		        System.out.println(info.getName());
		    }
		}
		catch (Exception e)
		{
		}
		
		final Map<String, Tuple2<String, String>> certmodel = new HashMap<String, Tuple2<String,String>>();
		final Tuple2<String, String> rootcert = SSecurity.createRootCaCertificate("CN=My Root CA", -1, "ECDSA", "brainpool", "SHA256", 256, 365);
		certmodel.put(SSecurity.readCertificateFromPEM(rootcert.getFirstEntity()).getSubject().toString(), rootcert);
		final CertTree certtree = new CertTree(certmodel);
		
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				SUtil.sleep(300);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						Tuple2<String, String> icert = SSecurity.createIntermediateCaCertificate(rootcert.getFirstEntity(), rootcert.getSecondEntity(), "CN=My Intermediate  CA", 2, "ECDSA", "brainpool", "SHA256", 256, 365);
						certmodel.put(SSecurity.readCertificateFromPEM(icert.getFirstEntity()).getSubject().toString(), icert);
						Tuple2<String, String> icert2 = SSecurity.createIntermediateCaCertificate(icert.getFirstEntity(), icert.getSecondEntity(), "CN=My Intermediate  CA2", 1, "ECDSA", "brainpool", "SHA256", 256, 365);
						certmodel.put(SSecurity.readCertificateFromPEM(icert2.getFirstEntity()).getSubject().toString(), icert2);
						Tuple2<String, String> icert3 = SSecurity.createIntermediateCaCertificate(rootcert.getFirstEntity(), rootcert.getSecondEntity(), "CN=My Intermediate  CA3", 2, "ECDSA", "brainpool", "SHA256", 256, 365);
						certmodel.put(SSecurity.readCertificateFromPEM(icert3.getFirstEntity()).getSubject().toString(), icert3);
						Tuple2<String, String> scert = SSecurity.createCertificate(rootcert.getFirstEntity(), rootcert.getSecondEntity(), "CN=My Platform", "ECDSA", "brainpool", "SHA256", 256, 365);
						certmodel.put(SSecurity.readCertificateFromPEM(scert.getFirstEntity()).getSubject().toString(), scert);
						certtree.updateModel();
					}
				});
			}
		});
		t.setDaemon(true);
		t.start();
		
		JScrollPane scrollpane = new JScrollPane(certtree);
		getContentPane().add(scrollpane);
		
//		JBusyRing br = new JBusyRing();
//		getContentPane().add(br);
		
		setVisible(true);
		setSize(1024, 768);
//		br.activate();
	}
	
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new CertTestWindow();
			}
		});
	}
}
