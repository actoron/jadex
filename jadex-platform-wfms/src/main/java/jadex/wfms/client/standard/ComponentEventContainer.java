package jadex.wfms.client.standard;

import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class ComponentEventContainer
{
	protected IndexMap eventmap;
	
	public ComponentEventContainer()
	{
		this.eventmap = new IndexMap();
	}
	
	/**
	 *  Adds an event to the tree.
	 *  
	 */
	public void addEvent(IComponentChangeEvent event)
	{
		if (event.getComponentCreationTime() == 0)
			System.out.println("Broken Event: " + event.toString());
		Tuple key = new Tuple(event.getComponent(), event.getComponentCreationTime())
		{
			public String toString()
			{
				return ((IComponentIdentifier) get(0)).getLocalName() + " (" + DateFormat.getDateTimeInstance().format(new Date((Long) get(1))) + ")";
			}
		};
		
		Map<String, Map<String, TreeSet<IComponentChangeEvent>>> compmap = (Map<String, Map<String, TreeSet<IComponentChangeEvent>>>) eventmap.get(key);
		if (compmap == null)
		{
			compmap = new HashMap<String, Map<String, TreeSet<IComponentChangeEvent>>>();
			eventmap.put(key, compmap);
		}
		
		Map<String, TreeSet<IComponentChangeEvent>> catmap = compmap.get(event.getSourceCategory());
		if (catmap == null)
		{
			catmap = new HashMap<String, TreeSet<IComponentChangeEvent>>();
			compmap.put(event.getSourceCategory(), catmap);
		}
		
		TreeSet<IComponentChangeEvent> eset = catmap.get(event.getSourceName());
		if (eset == null)
		{
			eset = new TreeSet<IComponentChangeEvent>(new Comparator<IComponentChangeEvent>()
			{
				public int compare(IComponentChangeEvent o1, IComponentChangeEvent o2)
				{
					long diff = o1.getTime() - o2.getTime();
					
					// Return value is int, so we need to reduce the range
					// in case the difference is greater than ~month
					return (int) Math.signum(diff);
				}
			});
			catmap.put(event.getSourceName(), eset);
		}
		
		eset.add(event);
	}
	
	/**
	 *  Gets the component count.
	 *  @return The component count.
	 */
	public int getComponentCount()
	{
		return eventmap.size();
	}
	
	/**
	 *  Gets the components.
	 *  @return The components key list.
	 */
	public List<Tuple> getComponents()
	{
		return new ArrayList<Tuple>(eventmap.getAsList());
	}
	
	/**
	 *  Gets a component by index.
	 *  @return The component key.
	 */
	public Tuple getComponent(int index)
	{
		return (Tuple) eventmap.getKeys()[index];
	}
	
	/**
	 *  Gets the events of a category of sources of a component.
	 *  @param component The component key.
	 *  @param category The category of event sources.
	 *  @return The events.
	 */
	public Map<String, TreeSet<IComponentChangeEvent>> getComponentEvents(Tuple component, String category)
	{
		Map <String, Map<String, TreeSet<IComponentChangeEvent>>> compmap = (Map <String, Map<String, TreeSet<IComponentChangeEvent>>>) eventmap.get(component);
		return compmap.get(category);
	}
	
	/**
	 *  Gets the events of a component by index.
	 *  @param Component The component.
	 *  @return The events.
	 */
	public Map<String, TreeSet<IComponentChangeEvent>> getComponentEvents(int index, String category)
	{
		Map <String, Map<String, TreeSet<IComponentChangeEvent>>> compmap = (Map <String, Map<String, TreeSet<IComponentChangeEvent>>>) eventmap.get(index);
		return compmap.get(category);
	}
}
