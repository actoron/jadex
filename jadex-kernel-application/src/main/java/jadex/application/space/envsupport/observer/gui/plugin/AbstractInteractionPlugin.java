package jadex.application.space.envsupport.observer.gui.plugin;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.observer.graphics.IViewport;
import jadex.application.space.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.application.space.envsupport.observer.gui.ObserverCenter;
import jadex.application.space.envsupport.observer.perspective.Perspective2D;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractInteractionPlugin implements IObserverCenterPlugin
{
	private DrawableCombiner marker;
	
	private ObserverCenter obsCenter;
	
	private List listeners;
	
	private MouseListener clickListener;
	private ChangeListener objectListener;
	
	private boolean initialized = false;
	
	/**
	 * Adds a MouseListener.
	 * @param listener The MouseListener.
	 */
	public void addMouseListener(MouseListener listener)
	{
		if (listeners == null)
			listeners = new ArrayList();
		listeners.add(listener);
	}
	
	/**
	 * Removes a MouseListener.
	 * @param listener The MouseListener.
	 */
	public void removeMouseListener(MouseListener listener)
	{
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 *  Initializes the plugin. This method is invoked once when the ObserverCenter becomes available.
	 * 	@param center The OberverCenter.
	 */
	protected abstract void initialize(ObserverCenter center);
	
	/**
	 *  This method is executed when the plugins starts.
	 * 	@param center The OberverCenter.
	 */
	protected void startUp(ObserverCenter center)
	{
	}
	
	/**
	 *  This method is executed when the plugins shuts down.
	 * 	@param center The OberverCenter.
	 */
	protected void cleanUp(ObserverCenter center)
	{
	}
	
	/**
	 *  This method receives space objects on left-clicks.
	 * 	@param object space object that was clicked.
	 */
	protected void handleObjectClick(ISpaceObject object)
	{
	}
	
	public void refresh()
	{
	}
	
	public String getIconPath()
	{
		return "jadex/application/space/envsupport/observer/images/introspector_icon.png";
	}

	@Override
	public final void shutdown()
	{
		// TODO Auto-generated method stub
		((Perspective2D) obsCenter.getSelectedPerspective()).setMarkerDrawCombiner(marker);
		((Canvas) ((Perspective2D) obsCenter.getSelectedPerspective()).getView()).removeMouseListener(clickListener);
		obsCenter.removeSelectedObjectListener(objectListener);
		
		cleanUp(obsCenter);
	}

	@Override
	public final void start(ObserverCenter main)
	{
		if (!initialized)
		{
			initialize(main);
			obsCenter = main;
			clickListener = new MouseListener()
			{
				
				@Override
				public void mouseReleased(MouseEvent e)
				{
					e.setSource(AbstractInteractionPlugin.this);
					if (listeners != null)
						for (Iterator it = listeners.iterator(); it.hasNext(); )
							((MouseListener) it.next()).mouseReleased(e);
				}
				
				@Override
				public void mousePressed(MouseEvent e)
				{
					e.setSource(AbstractInteractionPlugin.this);
					if (listeners != null)
						for (Iterator it = listeners.iterator(); it.hasNext(); )
							((MouseListener) it.next()).mousePressed(e);
				}
				
				@Override
				public void mouseExited(MouseEvent e)
				{
					e.setSource(AbstractInteractionPlugin.this);
					if (listeners != null)
						for (Iterator it = listeners.iterator(); it.hasNext(); )
							((MouseListener) it.next()).mouseExited(e);
				}
				
				@Override
				public void mouseEntered(MouseEvent e)
				{
					e.setSource(AbstractInteractionPlugin.this);
					if (listeners != null)
						for (Iterator it = listeners.iterator(); it.hasNext(); )
							((MouseListener) it.next()).mouseEntered(e);
				}
				
				public void mouseClicked(MouseEvent e)
				{
					e.setSource(AbstractInteractionPlugin.this);
					if (listeners != null)
						for (Iterator it = listeners.iterator(); it.hasNext(); )
							((MouseListener) it.next()).mouseClicked(e);
				}
			};
			
			objectListener = new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					if (obsCenter.getSelectedPerspective().getSelectedObject() != null)
						handleObjectClick((ISpaceObject) obsCenter.getSelectedPerspective().getSelectedObject());
				}
			};
			
			initialized = true;
		}
		
		main.getSelectedPerspective().setSelectedObject(null);
		
		marker = ((Perspective2D) main.getSelectedPerspective()).getMarkerDrawCombiner();
		((Perspective2D) main.getSelectedPerspective()).setMarkerDrawCombiner(new DrawableCombiner());
		
		((Canvas) ((Perspective2D) main.getSelectedPerspective()).getView()).addMouseListener(clickListener);
		main.addSelectedObjectListener(objectListener);
		
		startUp(main);
	}
	
	public IVector2 getWorldCoordinates(Point p)
	{
		IViewport viewport = ((Perspective2D) obsCenter.getSelectedPerspective()).getViewport();
		return viewport.getWorldCoordinates((int) p.getX(), (int) p.getY());
	}
}
