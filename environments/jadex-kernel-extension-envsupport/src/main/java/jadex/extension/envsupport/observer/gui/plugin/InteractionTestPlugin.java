package jadex.extension.envsupport.observer.gui.plugin;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.observer.gui.ObserverCenter;

/**
 * 
 */
public class InteractionTestPlugin extends AbstractInteractionPlugin
{
	/**
	 * 
	 */
	protected void handleObjectClick(ISpaceObject object)
	{
		System.out.println(object.getType());
	}
	
	/**
	 * 
	 */
	protected void initialize(ObserverCenter center)
	{
		addMouseListener(new MouseAdapter()
		{
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
	
	/**
	 *  Should plugin be visible.
	 */
	public boolean isVisible()
	{
		return true;
	}
	
	/**
	 *  Should plugin be started on load.
	 */
	public boolean isStartOnLoad()
	{
		return false;
	}
}
