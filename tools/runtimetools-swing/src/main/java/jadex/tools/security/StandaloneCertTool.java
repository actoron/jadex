package jadex.tools.security;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jadex.commons.SUtil;
import jadex.commons.security.PemKeyPair;
import jadex.platform.service.security.auth.X509PemStringsSecret;

/** 
 *  Standalone Tool for certificate store management.
 *
 */
public class StandaloneCertTool extends JFrame
{
	/** ID */
	private static final long serialVersionUID = -7352829740448705005L;


	public StandaloneCertTool()
	{
//		try {
//		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
//		    {
//		        if ("Nimbus".equals(info.getName()))
//		    	if ("GTK+".equals(info.getName()))
//		    	if ("CDE/Motif".equals(info.getName()))
//		        {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            
//		            break;
//		        }
//		        System.out.println(info.getName());
//		    }
//		}
//		catch (Exception e)
//		{
//		}
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
//		
		final CertTree certtree = new CertTree();
		
		certtree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				PemKeyPair kp = certtree.getSelectedCert();
				
				if (kp != null)
				{
					String[] chain = certtree.getSelectedCertChain();
					if (kp.getCertificate() != null)
					{
//						System.out.println("CERTCHAIN: \"" + chain.replace("\n", "\\n").replace("\r", "\\r") + "\"");
						X509PemStringsSecret secret = new X509PemStringsSecret(chain[chain.length - 1], null);
						System.out.println("Root Trust Cert: \"" + secret.toString() + "\"");
					}
					if (kp.getKey() != null)
					{
						String catchain = null;
						for (String cert : SUtil.notNull(chain))
							catchain = catchain == null ? cert : catchain + cert;
						X509PemStringsSecret secret = new X509PemStringsSecret(catchain, kp.getKey());
//						System.out.println("SECWITHKEY: \"" + kp.getKey().replace("\n", "\\n").replace("\r", "\\r") + "\"");
						System.out.println("Full Secret: \"" + secret.toString() + "\"");
					}
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane(certtree);
		getContentPane().add(scrollpane);
		
		setVisible(true);
		setSize(1024, 768);
		
		
		
		JMenu filemenu = new JMenu("File");
		menubar.add(filemenu);
		
		JMenuItem openmenu = new JMenuItem(new AbstractAction("Open...")
		{
			private static final long serialVersionUID = -3179710057306649806L;

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser();
				int res = fc.showOpenDialog(StandaloneCertTool.this);
				if (res == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					try
					{
						byte[] certstorecontent = SUtil.readFile(file);
						if (certstorecontent != null)
							certtree.load(certstorecontent);
					}
					catch (Exception e1)
					{
					}
				}
			}
		});
		filemenu.add(openmenu);
		
		JMenuItem saveasmenu = new JMenuItem(new AbstractAction("Save as...")
		{
			private static final long serialVersionUID = 1915341778859754227L;

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser("certstore.zip");
				int res = fc.showSaveDialog(StandaloneCertTool.this);
				if (res == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					byte[] certstorecontent = certtree.save();
					OutputStream os = null;
					try
					{
						File tmpfile = File.createTempFile("certstore", ".zip");
						os = new FileOutputStream(tmpfile);
						os.write(certstorecontent);
						SUtil.close(os);
						SUtil.moveFile(tmpfile, file);
					}
					catch (Exception e1)
					{
					}
					finally
					{
						SUtil.close(os);
					}
				}
			}
		});
		filemenu.add(saveasmenu);
		filemenu.addSeparator();
		
		JMenuItem exitmenu = new JMenuItem(new AbstractAction("Exit")
		{
			private static final long serialVersionUID = -8273600254313534032L;

			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		
//		final SecretWizard wizard = new SecretWizard(null);
//		JFrame dia = JWizard.createFrame("TestWizard", wizard);
//		dia.setVisible(true);
//		wizard.addTerminationListener(new AbstractAction()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				System.out.println("Result was: " + wizard.getResult());
//			}
//		});
	}
	
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new StandaloneCertTool();
			}
		});
	}
}
