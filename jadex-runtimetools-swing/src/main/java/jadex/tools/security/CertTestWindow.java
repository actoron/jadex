package jadex.tools.security;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jadex.commons.gui.JWizard;

public class CertTestWindow extends JFrame
{
	public CertTestWindow()
	{
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
		    {
		        if ("Nimbus".equals(info.getName()))
//		    	if ("GTK+".equals(info.getName()))
//		    	if ("CDE/Motif".equals(info.getName()))
		        {
		            UIManager.setLookAndFeel(info.getClassName());
		            
		            break;
		        }
//		        System.out.println(info.getName());
		    }
		}
		catch (Exception e)
		{
		}
		
		final CertTree certtree = new CertTree(SecuritySettingsPanel.DEFAULT_CERT_STORE);
		
		JScrollPane scrollpane = new JScrollPane(certtree);
		getContentPane().add(scrollpane);
		
		setVisible(true);
		setSize(1024, 768);
		
		final SecretWizard wizard = new SecretWizard();
		JFrame dia = JWizard.createFrame("TestWizard", wizard);
		dia.setVisible(true);
		wizard.addTerminationListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Result was: " + wizard.getResult());
			}
		});
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
