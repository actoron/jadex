package com.daimler.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class SwingUtils
{
	public static JPanel getPanelWithComponent(JComponent c)
	{
		JPanel p = new JPanel();
		return getPanelWithComponent(c, p.getBackground(), Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT);
	}
	
	public static JPanel getPanelWithComponent(JComponent c, float xAlignment, float yAlignment)
	{
		JPanel p = new JPanel();
		return getPanelWithComponent(c, p.getBackground(), xAlignment, yAlignment);
	}
	
	public static JPanel getPanelWithComponent(JComponent c, Color color, float xAlignment , float yAlignment)
	{
		return getPanelWithComponent(c, color, xAlignment, yAlignment, null);
	}
	
	public static JPanel getPanelWithComponent(JComponent c, Color color, float xAlignment , float yAlignment, Border border)
	{
		JPanel pTemp;
		pTemp = new JPanel();
		pTemp.setLayout(new BoxLayout(pTemp, BoxLayout.X_AXIS));
		pTemp.setBackground(color);
		pTemp.setAlignmentY(yAlignment);
		pTemp.setAlignmentX(xAlignment);
		pTemp.add(c);
		if (border != null) pTemp.setBorder(border);
		c.setAlignmentY(yAlignment);
		c.setAlignmentX(xAlignment);
		//c.setPreferredSize(new Dimension(135, c.getHeight()));
		
		return pTemp;
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Color color, Dimension dimension)
	{
		return getPanelWithComponentAndSize(c, color, Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT, dimension, false);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Color color, Dimension dimension, Border border)
	{
		return getPanelWithComponentAndSize(c, color, Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT, dimension, false, border);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Dimension dimension)
	{
		JPanel p = new JPanel();
		return getPanelWithComponentAndSize(c, p.getBackground(), Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT, dimension, false);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Color color, Dimension dimension, boolean fixSize)
	{
		return getPanelWithComponentAndSize(c, color, Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT, dimension, fixSize);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Color color, Dimension dimension, boolean fixSize, Border border)
	{
		return getPanelWithComponentAndSize(c, color, Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT, dimension, fixSize, border);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Dimension dimension, boolean fixSize)
	{
		JPanel p = new JPanel();
		return getPanelWithComponentAndSize(c, p.getBackground(), Component.LEFT_ALIGNMENT, Component.TOP_ALIGNMENT, dimension, fixSize);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, float xAlignment, float yAlignment, Dimension dimension, boolean fixSize)
	{
		JPanel p = new JPanel();
		return getPanelWithComponentAndSize(c, p.getBackground(), xAlignment, yAlignment, dimension, fixSize);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Color color, float xAlignment , float yAlignment, Dimension dimension, boolean fixSize)
	{
		return getPanelWithComponentAndSize(c, color, xAlignment, yAlignment, dimension, fixSize, null);
	}
	
	public static JPanel getPanelWithComponentAndSize(JComponent c, Color color, float xAlignment , float yAlignment, Dimension dimension, boolean fixSize, Border border)
	{
		JPanel pTemp;
		pTemp = new JPanel();
		pTemp.setLayout(new BoxLayout(pTemp, BoxLayout.X_AXIS));
		pTemp.setBackground(color);
		pTemp.setAlignmentY(yAlignment);
		pTemp.setAlignmentX(xAlignment);
		pTemp.add(c);
		if (border != null) pTemp.setBorder(border);
		c.setAlignmentY(yAlignment);
		c.setAlignmentX(xAlignment);
		c.setPreferredSize(dimension);
		if (fixSize)
		{
			c.setMaximumSize(dimension);
			c.setMinimumSize(dimension);
		}
		//c.setPreferredSize(new Dimension(135, c.getHeight()));
		
		return pTemp;
	}
	
	public static JScrollPane getScrollPanelWithComponent(JComponent c, Color color)
	{
		JScrollPane pTemp;
		pTemp = new JScrollPane(c);
		pTemp.setBackground(color);
		pTemp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//pTemp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pTemp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		//pTemp.setViewportView(c);
		return pTemp;
	}
	
	public static JPanel getPanelWithComponentWithoutAlignment(JComponent c)
	{
		JPanel p = new JPanel();
		return getPanelWithComponentWithoutAlignment(c, p.getBackground());
	}
	
	public static JPanel getPanelWithComponentWithoutAlignment(JComponent c, Color color)
	{
		JPanel pTemp;
		pTemp = new JPanel();
		pTemp.setBackground(color);
		pTemp.add(c);
		
		return pTemp;
	}
	
	public static void expandTreeNode(JTree tree, TreeNode node)
	{
		Enumeration e = node.children();
		if (e != null)
		{
			while (e.hasMoreElements())
			{
				ArrayList nodePath = new ArrayList();
				TreeNode tn = (TreeNode) e.nextElement();
				//System.out.println("Expanding " + tn.toString());
				if (tn.getChildCount() > 0)
				{
					expandTreeNode(tree, tn);
				}
				nodePath.add(0, tn);
				while (tn.getParent() != null)
				{
					tn = tn.getParent();
					nodePath.add(0, tn);
					
				}
				tree.expandPath(new TreePath(nodePath.toArray()));
			}	
		}
		
	}
	
	public static void expandWholeTree(JTree tree)
	{
		TreeNode tn = (TreeNode) tree.getModel().getRoot();
		expandTreeNode(tree, tn);
	}
	
	public static void setContainerEnabled(Component c, boolean b)
	{
		if (c instanceof Container)
		{
			Component[] comps = ((Container) c).getComponents();
			for (int i = 0; i < comps.length; i++)
			{
				setContainerEnabled(comps[i], b);
			}
		}
		c.setEnabled(b);
	}
	
}
