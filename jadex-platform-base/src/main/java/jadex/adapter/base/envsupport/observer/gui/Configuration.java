package jadex.adapter.base.envsupport.observer.gui;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.drawable.IDrawable;
import jadex.adapter.base.envsupport.observer.graphics.drawable.TexturedRectangle;
import jadex.adapter.base.envsupport.observer.gui.plugin.IObserverCenterPlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for the observer GUI.
 */
public class Configuration
{
	private String windowtitle;
	private boolean opengl;
	private List customplugins;
	private IVector1 selectorDistance;
	private boolean invertxaxis;
	private boolean invertyaxis;
	private Map themes;
	private Comparator displayorder;
	private IVector2 objectShift;
	
	public Configuration()
	{
		this.windowtitle = "Default Title";
		this.opengl = true;
		this.customplugins = new ArrayList();
		this.themes = new HashMap();
		this.objectShift = new Vector2Double();
	}
	
	/**
	 * Sets the title of the observer window
	 * @param title the title of the observer window
	 */
	public synchronized void setWindowTitle(String title)
	{
		windowtitle = title;
	}
	
	/**
	 * Returns the title of the observer window
	 * @return the title of the observer window
	 */
	public synchronized String getWindowTitle()
	{
		return windowtitle;
	}
	
	/**
	 * Sets whether to try to use OpenGL.
	 * @param opengl true, if attempt should be made to use OpenGL
	 */
	public synchronized void setOpenGl(boolean opengl)
	{
		this.opengl = opengl;
	}
	
	/**
	 * Tells whether to try to use OpenGL.
	 * @return true, if an attempt should be made to use OpenGL
	 */
	public synchronized boolean useOpenGl()
	{
		return opengl;
	}
	
	/**
	 * Adds a custom observer plugin.
	 * @param plugin the plugin
	 */
	public synchronized void addPlugin(IObserverCenterPlugin plugin)
	{
		customplugins.add(plugin);
	}
	
	/**
	 * Returns an array of the custom plugins
	 */
	public synchronized IObserverCenterPlugin[] getPlugins()
	{
		return (IObserverCenterPlugin[]) customplugins.toArray(new IObserverCenterPlugin[0]);
	}
	
	/** 
	 * Sets the maximum distance for selecting objects.
	 * 
	 * @param maxDist selections distance
	 */
	public synchronized void setSelectorDistance(IVector1 maxDist)
	{
		selectorDistance = maxDist;
	}
	
	/** 
	 * Gets the maximum distance for selecting objects.
	 * 
	 * @returns selections distance
	 */
	public synchronized IVector1 getSelectorDistance()
	{
		return selectorDistance;
	}
	
	/**
	 * Gets x-axis inversion.
	 * @return true, if the x-axis should be inverted.
	 */
	public synchronized boolean getInvertXAxis()
	{
		return invertxaxis;
	}
	
	/**
	 * Sets x-axis inversion.
	 * @param invert true, if the x-axis should be inverted.
	 */
	public synchronized void setInvertXAxis(boolean invert)
	{
		invertxaxis = invert;
	}
	
	/**
	 * Gets y-axis inversion.
	 * @return true, if the y-axis should be inverted.
	 */
	public synchronized boolean getInvertYAxis()
	{
		return invertyaxis;
	}
	
	/**
	 * Sets y-axis inversion.
	 * @param invert true, if the y-axis should be inverted.
	 */
	public synchronized void setInvertYAxis(boolean invert)
	{
		invertyaxis = invert;
	}
	
	/**
	 * Gets the names of all available themes.
	 * @return names of all themes
	 */
	public synchronized String[] getThemeNames()
	{
		return (String[]) themes.keySet().toArray(new String[0]);
	}
	
	/**
	 * Returns a specific theme.
	 * @return the theme
	 */
	public synchronized Map getTheme(String name)
	{
		return (Map) themes.get(name);
	}
	
	/**
	 * Adds a theme.
	 * @param name name of the theme
	 * @param theme the theme
	 */
	public synchronized void setTheme(String name, Map theme)
	{
		if (!theme.containsKey("marker"))
		{
			DrawableCombiner objectMarker = new DrawableCombiner();
			IDrawable markerDrawable = new TexturedRectangle(getClass().getPackage().getName().replaceAll("gui", "").concat("images.").replaceAll("\\.", "/").concat("selection_marker.png"));
			objectMarker.addDrawable(markerDrawable, Integer.MAX_VALUE);
			theme.put("marker", objectMarker);
		}
		themes.put(name, theme);
	}
	
	/**
	 * Gets the display order.
	 * @return the display order
	 */
	public synchronized Comparator getDisplayOrder()
	{
		return displayorder;
	}
	
	/**
	 * Sets the display order.
	 * @param order the display order
	 */
	public synchronized void setDisplayOrder(Comparator order)
	{
		displayorder = order;
	}
	
	/**
	 * Gets the object shift.
	 * @return the object shift
	 */
	public synchronized IVector2 getObjectShift()
	{
		return objectShift;
	}
	
	/**
	 * Sets the object shift.
	 * @param shift the object shift
	 */
	public synchronized void setObjectShift(IVector2 shift)
	{
		objectShift = shift.copy();
	}
}
