package jadex.backup.swing;

import jadex.bridge.IExternalAccess;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * 
 */
public class ObjectivesPanel extends JPanel
{
	/**
	 * 
	 */
	public ObjectivesPanel(IExternalAccess ea)
	{
		setLayout(new BorderLayout());
		
		JTabbedPane tp = new JTabbedPane();
		tp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Quick Objective Settings "));
		
		tp.addTab("Sync", new SyncPanel(ea));
//		tp.addTab("Backup", new BackupPanel());
		tp.addTab("Jobs", new JobsPanel());
		
		add(tp, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	public static JFrame createFrame(IExternalAccess ea)
	{
		JFrame f = new JFrame("JadexSync");
		f.add(new ObjectivesPanel(ea), BorderLayout.CENTER);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		return f;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		createFrame(null);
	}
	
}
