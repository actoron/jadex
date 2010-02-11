package jadex.tools.common.treecombo;

import jadex.commons.SGUI;
import jadex.tools.common.jtreetable.JTreeTable;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class JTreeTableComboTest
{
	public static void test(final JTreeTable tree)
	{
		JTreeTableComboModelRenderer	jttcmr	= new JTreeTableComboModelRenderer(tree);
		final JComboBox	jcb	= new JComboBox(jttcmr);
		jcb.setRenderer(jttcmr);

		jcb.setPreferredSize(tree.getPreferredSize());
		
		JFrame	f	= new JFrame("Tree Combo test");
		f.getContentPane().setLayout(new FlowLayout());
		f.getContentPane().add(jcb, BorderLayout.CENTER);
		f.setSize(400, 300);
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	
	}
}
