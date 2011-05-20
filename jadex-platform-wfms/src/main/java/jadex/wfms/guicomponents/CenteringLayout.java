package jadex.wfms.guicomponents;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class CenteringLayout implements LayoutManager
{

	public void addLayoutComponent(String name, Component comp)
	{
		/*if (component == null)
			this.component = comp;
		else
			throw new RuntimeException("Only one component supported: " + comp);*/
	}

	public void removeLayoutComponent(Component comp)
	{
	}

	public Dimension preferredLayoutSize(Container parent)
	{
		Component component = parent.getComponent(0);
		if (component != null)
			return component.getPreferredSize();
		return new Dimension(0, 0);
	}

	public Dimension minimumLayoutSize(Container parent)
	{
		Component component = parent.getComponent(0);
		if (component != null)
			return component.getMinimumSize();
		return new Dimension(0, 0);
	}

	public void layoutContainer(Container parent)
	{
		Component component = parent.getComponent(0);
		
		Dimension pd = component.getPreferredSize();
		int x;
		int w;
		if (parent.getWidth() >= pd.width)
		{
			double halfdiff = (parent.getWidth() - pd.width) / 2.0;
			x = (int) halfdiff;
			w = pd.width;
		}
		else
		{
			x = 0;
			w = parent.getWidth();
		}
		
		int y;
		int h;
		if (parent.getHeight() >= pd.height)
		{
			double halfdiff = (parent.getHeight() - pd.height) / 2.0;
			y = (int) halfdiff;
			h = pd.height;
		}
		else
		{
			y = 0;
			h = parent.getHeight();
		}

		component.setBounds(x, y, w, h);
	}
	
	public static JPanel createCenteringPanel(JComponent centeredcomponent)
	{
		JPanel centerpanel = new JPanel();
		centerpanel.setLayout(new CenteringLayout());
		centerpanel.add(centeredcomponent);
		return centerpanel;
	}
}
