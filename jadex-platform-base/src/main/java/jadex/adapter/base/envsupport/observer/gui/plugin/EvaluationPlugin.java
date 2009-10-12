package jadex.adapter.base.envsupport.observer.gui.plugin;

import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.commons.SimplePropertyObject;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JSplitPane;

/**
 *  Th evaluation plugin.
 */
public class EvaluationPlugin extends SimplePropertyObject implements IObserverCenterPlugin
{
	//-------- attributes --------
	
	/** Plugin name. */
	private static final String NAME = "Evaluation";
	
	/** The main panel. */
	private JSplitPane mainpane;
	
	/** The observer center. */
	private ObserverCenter obscenter;
	
	//-------- attributes --------

	/**
	 *  Create the plugin.
	 */
	public EvaluationPlugin()
	{
		mainpane = new JSplitPane();
		mainpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainpane.setOneTouchExpandable(true);
		mainpane.setDividerLocation(160);
		mainpane.setResizeWeight(0.5);
		mainpane.setMinimumSize(new Dimension(200, 200));
		
//		JSplitPane persViewPane = new JSplitPane();
//		persViewPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
//		persViewPane.setOneTouchExpandable(true);
//		persViewPane.setDividerLocation(80);
//		persViewPane.setResizeWeight(0.5);
//		mainpane.setTopComponent(persViewPane);
	}
	
	/** 
	 *  Starts the plugin.
	 *  @param the observer center
	 */
	public void start(ObserverCenter main)
	{
		obscenter = main;
		
		refresh();
	}
	
	/** 
	 * Shutdowns the plugin.
	 */
	public void shutdown()
	{
		
	}
	
	/** 
	 *  Returns the name of the plugin.
	 *  @return name of the plugin.
	 */
	public String getName()
	{
		return NAME;
	}
	
	/** 
	 *  Returns the path to the icon for the plugin in the toolbar.
	 *  @return path to the icon.
	 */
	public String getIconPath()
	{
		return getClass().getPackage().getName().replaceAll("gui.plugin","").concat("images.").replaceAll("\\.", "/").concat("evaluation_icon.png");
	}
	
	/** 
	 *  Returns the viewable component of the plugin.
	 *  @return viewable component of the plugin.
	 */
	public Component getView()
	{
		Component c = (Component)getProperty("panel_0");
		System.out.println("comp: "+c);
		return c;
		//		return mainpane;
	}
	
	/** Refreshes the display
	 */
	public void refresh()
	{
	}
}
