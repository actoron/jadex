package jadex.bdi.examples.cleanerworld;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.gui.plugin.AbstractInteractionPlugin;
import jadex.extension.envsupport.observer.perspective.Perspective2D;

/**
 * 
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
		System.out.println("init");
		this.ml = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				System.out.println(getWorldCoordinates(e.getPoint()));
				AbstractEnvironmentSpace space = center.getSpace();
				Map<String, Object> props = new HashMap<String, Object>();
				props.put(Space2D.PROPERTY_POSITION, getWorldCoordinates(e.getPoint()));
				space.createSpaceObject("waste", props, null);
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
