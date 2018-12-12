package jadex.bdiv3.examples.cleanerworld.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;

/**
 *  The environment object for non distributed applications.
 */
public class Environment implements IEnvironment
{
	//-------- class attributes --------

	/** The singleton. */
	protected static Environment instance;

	//-------- attributes --------

	/** The daytime. */
	protected boolean daytime;

	/** The cleaners. */
	protected List cleaners;

	/** The wastes. */
	protected List wastes;

	/** The waste bins. */
	protected List wastebins;

	/** The charging stations. */
	protected List stations;

	/** The cleaner ages. */
	protected Map ages;

	/** The helper object for bean events. */
	public SimplePropertyChangeSupport pcs;

	//-------- constructors --------

	/**
	 *  Create a new environment.
	 */
	public Environment()
	{
		this.daytime = true;
		this.cleaners = new ArrayList();
		this.wastes = new ArrayList();
		this.wastebins = new ArrayList();
		this.stations = new ArrayList();
		this.ages = new HashMap();
		this.pcs = new SimplePropertyChangeSupport(this);

		// Add some things to our world.
		addWaste(new Waste(new Location(0.1, 0.5)));
		addWaste(new Waste(new Location(0.2, 0.5)));
		addWaste(new Waste(new Location(0.3, 0.5)));
		addWaste(new Waste(new Location(0.9, 0.9)));
		addWastebin(new Wastebin(new Location(0.2, 0.2), 20));
		addWastebin(new Wastebin(new Location(0.8, 0.1), 20));
		addChargingStation(new Chargingstation(new Location(0.8, 0.8)));
		addChargingStation(new Chargingstation(new Location(0.2, 0.4)));
	}

	/**
	 *  Get the singleton.
	 *  @return The environment.
	 */
	public static synchronized Environment getInstance()
	{
		if(instance==null)
		{
			instance = new Environment();
		}
		return instance;
	}
	
	/**
	 *  Clear the singleton instance.
	 */
	public static void clearInstance()
	{
		instance = null;
	}

	
	//-------- cleaner actions from IEnvironment --------

	/**
	 *  Get the current vision.
	 *  @param cleaner The cleaner.
	 *  @return The current vision, null if failure.
	 */
	public synchronized Vision getVision(Cleaner cleaner)
	{
		// Update cleaner
		if(cleaners.contains(cleaner))
			cleaners.remove(cleaner);
		cleaners.add(cleaner);
		// reset age for cleaner
		ages.put(cleaner, Integer.valueOf(0));

		//System.out.println("getVision: "+this.hashCode()+" "+cleaners.size());

		Location cloc = cleaner.getLocation();
		double range = cleaner.getVisionRange();

		ArrayList nearwastes = new ArrayList();
		for(int i=0; i<wastes.size(); i++)
		{
			LocationObject	obj	= (LocationObject)wastes.get(i);
			if(obj.getLocation().isNear(cloc, range))
				nearwastes.add(obj.clone());
		}

		ArrayList nearwastebins = new ArrayList();
		for(int i=0; i<wastebins.size(); i++)
		{
			LocationObject	obj	= (LocationObject)wastebins.get(i);
			if(obj.getLocation().isNear(cloc, range))
				nearwastebins.add(obj.clone());
		}

		ArrayList nearstations = new ArrayList();
		for(int i=0; i<stations.size(); i++)
		{
			LocationObject	obj	= (LocationObject)stations.get(i);
			if(obj.getLocation().isNear(cloc, range))
				nearstations.add(obj.clone());
		}

		ArrayList nearcleaners = new ArrayList();
		for(int i=0; i<cleaners.size(); i++)
		{
			LocationObject	obj	= (LocationObject)cleaners.get(i);
			if(obj.getLocation().isNear(cloc, range))
				nearcleaners.add(obj.clone());
		}

		// Construct the ontology vision object.
		Vision v = new Vision(nearwastes, nearwastebins, nearstations, nearcleaners, getDaytime());

		return v;
	}

	/**
	 *  Try to pick up some piece of waste.
	 *  @param waste The waste.
	 *  @return True if the waste could be picked up.
	 */
	public synchronized boolean pickUpWaste(Waste waste)
	{
		// Todo: Implement random failure?
		boolean ret	= wastes.remove(waste);

//		if(!ret)
//		{
//			System.out.println("Failed to pick up: "+waste);
//			System.out.println("Wastes: "+wastes);
//		}

		return ret;
	}

	/**
	 *  Drop a piece of waste.
	 */
	public synchronized boolean dropWasteInWastebin(Waste waste, Wastebin wastebin)
	{
		assert waste!=null;
		
//		if(waste==null)
//			System.out.println("here");
		
		boolean ret = false;

		wastebin	= getWastebin(wastebin);
		if(!wastebin.contains(waste) && !wastebin.isFull())
		{
			wastebin.addWaste((Waste)waste.clone());
			ret = true;
			pcs.firePropertyChange("env", null, "env"); // Hack? allows for rechecking achievecleanup target cond
		}
		return ret;
	}

	/**
	 *  Get a wastebin for a template.
	 *  @return The wastebin.
	 */
	protected synchronized Wastebin	getWastebin(Wastebin wb)
	{
		Wastebin ret = null;
		for(int i=0; i<wastebins.size() && ret==null; i++)
		{
			if(wb.equals(wastebins.get(i)))
				ret = (Wastebin)wastebins.get(i);
		}
		return ret;
	}

	//-------- methods --------

	/**
	 *  Get the complete vision.
	 *  @return The current vision, null if failure.
	 */
	public synchronized Vision getCompleteVision()
	{
		// Construct the ontology vision object.
		return new Vision(wastes, wastebins, stations, cleaners, getDaytime());
	}

	/**
	 *  Get the daytime.
	 *  @return The current vision.
	 */
	public synchronized boolean getDaytime()
	{
		return daytime;
	}

	/**
	 *  Set the daytime.
	 *  @param daytime The daytime.
	 */
	public synchronized void setDaytime(boolean daytime)
	{
		this.daytime = daytime;
		this.pcs.firePropertyChange("daytime", null, Boolean.valueOf(daytime));
	}

	/**
	 *  Add a cleaner.
	 *  @param cleaner The cleaner.
	 */
	public synchronized void addCleaner(Cleaner cleaner)
	{
		cleaners.add(cleaner);
		this.pcs.firePropertyChange("cleaners", null, cleaner);
	}

	/**
	 *  Remove a cleaner.
	 *  @param cleaner The cleaner.
	 */
	public synchronized void removeCleaner(Cleaner cleaner)
	{
		cleaners.remove(cleaner);
		this.pcs.firePropertyChange("cleaners", null, cleaner);
	}

	/**
	 *  Add a piece of waste.
	 *  @param waste The new piece of waste.
	 */
	public synchronized void addWaste(Waste waste)
	{
		wastes.add(waste);
		this.pcs.firePropertyChange("wastes", null, waste);
	}

	/**
	 *  Remove a piece of waste.
	 *  @param waste The piece of waste.
	 */
	public synchronized void removeWaste(Waste waste)
	{
		wastes.remove(waste);
		this.pcs.firePropertyChange("wastes", waste, null);
	}

	/**
	 *  Add a wastebin.
	 *  @param wastebin The new waste bin.
	 */
	public synchronized void addWastebin(Wastebin wastebin)
	{
		wastebins.add(wastebin);
		this.pcs.firePropertyChange("wastebins", null, wastebin);
	}

	/**
	 *  Add a charging station.
	 *  @param station The new charging station.
	 */
	public synchronized void addChargingStation(Chargingstation station)
	{
		stations.add(station);
		this.pcs.firePropertyChange("chargingstations", null, station);
	}

	/**
	 *  Get all wastes.
	 *  @return All wastes.
	 */
	public synchronized Waste[] getWastes()
	{
		return (Waste[])wastes.toArray(new Waste[wastes.size()]);
	}

	/**
	 *  Get all wastebins.
	 *  @return All wastebins.
	 */
	public synchronized Wastebin[] getWastebins()
	{
		return (Wastebin[])wastebins.toArray(new Wastebin[wastebins.size()]);
	}

	/**
	 *  Get all charging stations.
	 *  @return All stations.
	 */
	public synchronized Chargingstation[] getChargingstations()
	{
		return (Chargingstation[])stations.toArray(new Chargingstation[stations.size()]);
	}

	/**
	 *  Get all cleaners.
	 *  @return All cleaners.
	 */
	public synchronized Cleaner[] getCleaners()
	{
		Cleaner[] cls = (Cleaner[])cleaners.toArray(new Cleaner[cleaners.size()]);
		// Increase ages of cleaners
		for(int i=0; i<cls.length; i++)
		{
			if(!ages.containsKey(cls[i]))
			{
				ages.put(cls[i], Integer.valueOf(0));
			}
			else
			{
				int age = ((Integer)ages.get(cls[i])).intValue();
				if(age>100)
				{
					removeCleaner(cls[i]);
					ages.remove(cls[i]);
				}
				else
				{
					ages.put(cls[i], Integer.valueOf(age+1));
				}
			}
		}
		return (Cleaner[])cleaners.toArray(new Cleaner[cleaners.size()]);
	}

	/**
	 *  Get a wastebin for a name.
	 *  @return The wastebin.
	 */
	public synchronized Wastebin getWastebin(String name)
	{
		Wastebin ret = null;
		for(int i=0; i<wastebins.size() && ret==null; i++)
		{
			if(((Wastebin)wastebins.get(i)).getName().equals(name))
				ret = (Wastebin)wastebins.get(i);
		}
		return ret;
	}

	/**
	 *  Clear the environment.
	 */
	public synchronized void clear()
	{
		cleaners.clear();
		wastes.clear();
		wastebins.clear();
		stations.clear();
		daytime = true;
	}

	/**
	 *  Get the age of a cleaner.
	 *  @return The age.
	 */
	public synchronized int getAge(Cleaner cleaner)
	{
		int ret = 0;
		Integer age = (Integer)ages.get(cleaner);
		if(age!=null)
			ret = age.intValue();
		return ret;
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }

}
