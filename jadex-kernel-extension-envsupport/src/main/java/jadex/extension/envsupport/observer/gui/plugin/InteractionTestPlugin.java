package jadex.extension.envsupport.observer.gui.plugin;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.observer.gui.ObserverCenter;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class InteractionTestPlugin extends AbstractInteractionPlugin
{
	
	@Override
	protected void handleObjectClick(ISpaceObject object)
	{
		System.out.println(object.getType());
	}
	
	protected void initialize(ObserverCenter center)
	{
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				System.out.println(getWorldCoordinates(e.getPoint()));
			}
		});
	}

	public String getName()
	{
		return "Test Interaction";
	}
	
	public Component getView()
	{
		return new JPanel();
	}

}
