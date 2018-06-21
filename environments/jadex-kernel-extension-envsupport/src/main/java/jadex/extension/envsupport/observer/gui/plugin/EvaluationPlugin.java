package jadex.extension.envsupport.observer.gui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.observer.gui.ObserverCenter;

/**
 *  Th evaluation plugin.
 */
public class EvaluationPlugin extends SimplePropertyObject implements IObserverCenterPlugin
{
	//-------- attributes --------
	
	/** Plugin name. */
	private static final String NAME = "Evaluation";
	
	/** The main panel. */
	private Component mainpane;
	
//	/** The observer center. */
//	private ObserverCenter obscenter;
	
	/** The evaluation components. */
	protected List components;
	
	//-------- attributes --------

	/**
	 *  Create the plugin.
	 */
	public EvaluationPlugin()
	{
	}
	
	/** 
	 *  Starts the plugin.
	 *  @param the observer center
	 */
	public void start(ObserverCenter main)
	{
//		obscenter = main;
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
		if (mainpane != null)
			return mainpane;
		
		components = new ArrayList();
		
		for(int i=0; ;i++)
		{
			if(i==0 && getPropertyNames().contains("component"))
			{
				components.add(getProperty("component"));
			}
			else
			{
				Component c = (Component)getProperty("component_"+i);
				if(c!=null)
					components.add(c);
				else
					break;
			}
		}
		
		mainpane = new JPanel(new BorderLayout());
		mainpane.setMinimumSize(new Dimension(50, 200));

		Component parent = mainpane;
		for(int i=0; i<components.size(); i++)
		{
			// Creation of child.
			Component child;
			if(components.size()-i>1)
			{
				JSplitPane p = new JSplitPane();
				p.setOrientation(JSplitPane.VERTICAL_SPLIT);
				p.setOneTouchExpandable(true);
				p.setDividerLocation(250);
				p.setResizeWeight(0.5);
				p.setTopComponent((Component)components.get(i));
				child = p;
			}
			else
			{
				child = (Component)components.get(i);
			}
			
			// Addition of child.
			if(parent instanceof JPanel)
			{
				((JPanel)parent).add(BorderLayout.CENTER, child);
			}
			else // if(c instanceof JSpiltPane)
			{
				((JSplitPane)parent).setBottomComponent(child);
			}
			
			parent = child;
		}
		
		return mainpane;
	}
	
	/** 
	 * Refreshes the display
	 */
	public void refresh()
	{
//		System.out.println("refresh called");

		if(components!=null)
		{
			for(int i=0; i<components.size(); i++)
			{
				Component c = (Component)components.get(i);
				c.repaint();
			}
		}
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
	
	public static void main(String[] args)
	{
		double test = Math.sqrt(2);
		System.out.println(test);
		double t2 = ((int)(test*100))/100.0;
		System.out.println(t2);
	}
}
