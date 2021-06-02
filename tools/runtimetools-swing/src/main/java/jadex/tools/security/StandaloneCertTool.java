package jadex.tools.security;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

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

	protected File lastpath = Paths.get("").toFile();

	public StandaloneCertTool()
	{
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
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
//						X509PemStringsSecret secret = new X509PemStringsSecret(chain[chain.length - 1], null);
						//System.out.println("Root Trust Cert: \"" + secret.toString() + "\"");
					}
					if (kp.getKey() != null)
					{
//						String catchain = "";
//						System.out.println(chain.length);
//						for (String cert : SUtil.notNull(chain))
//						{
//							//System.out.println(cert);
//							catchain += cert;
//						}
//						X509PemStringsSecret secret = new X509PemStringsSecret(catchain, kp.getKey());
//						System.out.println("SECWITHKEY: \"" + kp.getKey().replace("\n", "\\n").replace("\r", "\\r") + "\"");
						//System.out.println("Full Secret: \"" + secret.toString() + "\"");
					}
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane(certtree);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		JButton copyassecret = new JButton(new AbstractAction("Copy Secret") {
			private static final long serialVersionUID = 5753744525100891468L;

			public void actionPerformed(ActionEvent e)
			{
				PemKeyPair kp = certtree.getSelectedCert();
				if (kp != null)
				{
					String[] chain = certtree.getSelectedCertChain();
					if (kp.getKey() != null)
					{
						String catchain = "";
						System.out.println(chain.length);
						if (chain != null)
						{
							for (String cert : chain)
								catchain += cert;
						}
						X509PemStringsSecret secret = new X509PemStringsSecret(catchain, kp.getKey());
						StringSelection contents = new StringSelection(secret.toString());
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
					}
				}
			}
		});
		buttons.add(copyassecret);
		JButton copyroot = new JButton(new AbstractAction("Copy Root Certificate") {
			private static final long serialVersionUID = 5753744525100891468L;

			public void actionPerformed(ActionEvent e)
			{
				PemKeyPair kp = certtree.getSelectedCert();
				if (kp != null)
				{
					String[] chain = certtree.getSelectedCertChain();
					X509PemStringsSecret secret = new X509PemStringsSecret(chain[chain.length - 1], null);
					StringSelection contents = new StringSelection(secret.toString());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
				}
			}
		});
		buttons.add(copyroot);
		
		getContentPane().add(buttons, BorderLayout.SOUTH);
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu filemenu = new JMenu("File");
		menubar.add(filemenu);
		
		JMenuItem openmenu = new JMenuItem(new AbstractAction("Open...")
		{
			private static final long serialVersionUID = -3179710057306649806L;

			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fc = new JFileChooser(lastpath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Certificate Stores", "zip");
				fc.setFileFilter(filter);
				int res = fc.showOpenDialog(StandaloneCertTool.this);
				if (res == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					if (file.getParentFile().isDirectory())
						lastpath = file.getParentFile();
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
				JFileChooser fc = new JFileChooser(lastpath);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Certificate Stores", "zip");
				fc.setFileFilter(filter);
				int res = fc.showSaveDialog(StandaloneCertTool.this);
				if (res == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
					if (file.getParentFile().isDirectory())
						lastpath = file.getParentFile();
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
		filemenu.add(exitmenu);
		
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
		
		setVisible(true);
		setSize(1024, 768);
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
