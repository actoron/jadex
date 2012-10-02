package jadex.backup.swing;

import jadex.base.gui.filetree.FileTreePanel;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.ObjectCardLayout;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
	public ObjectivesPanel()
	{
		setLayout(new BorderLayout());
		
		JPanel quickp = new JPanel(new GridBagLayout());
		quickp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Quick Objective Settings "));
		
		JLabel methodl = new JLabel("What should be done: ");
		JLabel datal = new JLabel("What data should be considered:");
		JLabel wherel = new JLabel("Where should the data be placed: ");
		JLabel whenl = new JLabel("When should the data be saved:");
		JLabel olderl = new JLabel("Should older versions be kept: ");

		JComboBox methodcb = new JComboBox(new String[]{"synchronize", "backup"});
		JComboBox datacb = new JComboBox(new String[]{"private user data"});
		JComboBox wherecb = new JComboBox(new String[]{"let the system decide"});
		JComboBox whencb = new JComboBox(new String[]{"let the system decide"});
		JComboBox oldercb = new JComboBox(new String[]{"no", "yes, <=3", "yes, all"});
		
//		JButton methodb = new JButton("...");
		JButton datab = new JButton("...");
		JButton whereb = new JButton("...");
		JButton whenb = new JButton("...");
		JButton olderb = new JButton("...");
		
		int x = 0;
		int y = 0;
		quickp.add(methodl, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(methodcb, new GridBagConstraints(x++,y++,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
//		quickp.add(methodb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
//			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		quickp.add(datal, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(datacb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		quickp.add(datab, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		quickp.add(wherel, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(wherecb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		quickp.add(whereb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		quickp.add(whenl, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(whencb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		quickp.add(whenb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		quickp.add(olderl, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(oldercb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		quickp.add(olderb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
		final ObjectCardLayout ocl = new ObjectCardLayout();
		final JPanel detailp = new JPanel(ocl);
		detailp.setMinimumSize(new Dimension(1,1));
		detailp.setPreferredSize(new Dimension(100,100));
		
		JSplitPanel splitp = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
//		splitp.setDividerLocation(0.5);
		splitp.setOneTouchExpandable(true);
		splitp.add(quickp);
		splitp.add(new JScrollPane(detailp));
		
		datab.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if(ocl.getComponent("data")==null)
				{
					detailp.add(new SourceSelectionPanel(null), "data");
				}
				ocl.show("data");
			}
		});
		
		add(splitp, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.add(new ObjectivesPanel(), BorderLayout.CENTER);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
}
