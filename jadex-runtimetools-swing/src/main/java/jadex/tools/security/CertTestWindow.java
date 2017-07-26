package jadex.tools.security;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
		            
//		            break;
		        }
		        System.out.println(info.getName());
		    }
		}
		catch (Exception e)
		{
		}
		
		final CertTree certtree = new CertTree("certstore.zip");
		
		JScrollPane scrollpane = new JScrollPane(certtree);
		getContentPane().add(scrollpane);
		
		setVisible(true);
		setSize(1024, 768);
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
