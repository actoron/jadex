package jadex.bdi.examples.cleanerworld;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.gui.plugin.AbstractInteractionPlugin;

/**
 *  Plugin that allows for adding waste via mouse clicks.
 */
public class AddWastePlugin extends AbstractInteractionPlugin
{
	protected MouseListener ml;
	
	/**
	 *  Initializes the plugin. This method is invoked once when the ObserverCenter becomes available.
	 * 	@param center The OberverCenter.
	 */
	protected void initialize(final ObserverCenter center)
	{
		this.ml = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() == 2)
				{
					AbstractEnvironmentSpace space = center.getSpace();
					Map<String, Object> props = new HashMap<String, Object>();
					props.put(Space2D.PROPERTY_POSITION, getWorldCoordinates(e.getPoint()));
					space.createSpaceObject("waste", props, null);
				}
			}
		};
		addMouseListener(ml);
	}
	
	public void shutdown()
	{
		System.out.println("shutdown");
		removeMouseListener(ml);
		super.shutdown();
	}
	
	/**
	 *  Get the plugin name.
	 */
	public String getName()
	{
		return null;
	}
	
	public String getIconPath()
	{
		return null;
	}
	
	/**
	 *  Get the plugin view.
	 */
	public Component getView()
	{
		return null;
	}
	
	/**
	 *  Should plugin be visible.
	 */
	public boolean isVisible()
	{
		return false;
	}
	
	/**
	 *  Should plugin be started on load.
	 */
	public boolean isStartOnLoad()
	{
		return true;
	}
}
