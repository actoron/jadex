package jadex.wfms.guicomponents;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ButtonPanel extends JPanel
{
	public ButtonPanel()
	{
		this(BoxLayout.LINE_AXIS);
	}
	
	public ButtonPanel(final int orientation)
	{
		super();
		setLayout(new BoxLayout(this, orientation));
		if (orientation == BoxLayout.LINE_AXIS)
			setBorder(new EmptyBorder(5, 0, 5, 0));
		else
			setBorder(new EmptyBorder(0, 5, 0, 5));
		
		addContainerListener(new ContainerAdapter()
		{
			public void componentAdded(ContainerEvent e)
			{
				if (e.getComponent() instanceof JButton)
				{
					JButton b = (JButton) e.getComponent();
					b.setMargin(new Insets(1, 1, 1, 1));
					if (orientation == BoxLayout.PAGE_AXIS)
					{
						Dimension d = new Dimension(b.getWidth(), 20);
						e.getContainer().add(new Box.Filler(d, d, d));
					}
				}
			}
		});
	}
}
