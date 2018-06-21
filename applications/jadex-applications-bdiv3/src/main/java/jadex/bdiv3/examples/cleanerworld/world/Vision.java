package jadex.bdiv3.examples.cleanerworld.world;

import java.util.ArrayList;
import java.util.List;


/**
 *  Editable Java class for concept Vision of cleaner-generated ontology.
 */
public class Vision extends LocationObject
{
	//-------- attributes ----------

	/** Attribute for slot cleaners. */
	protected java.util.List cleaners;

	/** Attribute for slot wastebins. */
	protected java.util.List wastebins;

	/** Attribute for slot wastes. */
	protected java.util.List wastes;

	/** Attribute for slot stations. */
	protected java.util.List stations;

	/** Attribute for slot daytime. */
	protected boolean daytime;

	//-------- constructors --------

	/**
	 *  Create a new Vision.
	 */
	public Vision()
	{
		// Empty constructor required for JavaBeans (do not remove).
		this.cleaners = new java.util.ArrayList();
		this.wastebins = new java.util.ArrayList();
		this.wastes = new java.util.ArrayList();
		this.stations = new java.util.ArrayList();
	}

	/**
	 *  Create a new vision.
	 */
	public Vision(List wastes, List wastebins, List stations, List cleaners, boolean daytime)
	{
		this.wastes = wastes;
		this.wastebins = wastebins;
		this.stations = stations;
		this.cleaners = cleaners;
		this.daytime = daytime;
		setId(""); // Hack???
	}

	/**
	 *  Get the cleaners of this Vision.
	 * @return cleaners
	 */
	public Cleaner[] getCleaners()
	{
		return (Cleaner[])cleaners.toArray(new Cleaner[cleaners.size()]);
	}

	/**
	 *  Set the cleaners of this Vision.
	 * @param cleaners the value to be set
	 */
	public void setCleaners(Cleaner[] cleaners)
	{
		this.cleaners.clear();
		for(int i = 0; i < cleaners.length; i++)
			this.cleaners.add(cleaners[i]);
	}

	/**
	 *  Get an cleaners of this Vision.
	 *  @param idx The index.
	 *  @return cleaners
	 */
	public Cleaner getCleaner(int idx)
	{
		return (Cleaner)this.cleaners.get(idx);
	}

	/**
	 *  Set a cleaner to this Vision.
	 *  @param idx The index.
	 *  @param cleaner a value to be added
	 */
	public void setCleaner(int idx, Cleaner cleaner)
	{
		this.cleaners.set(idx, cleaner);
	}

	/**
	 *  Add a cleaner to this Vision.
	 *  @param cleaner a value to be removed
	 */
	public void addCleaner(Cleaner cleaner)
	{
		this.cleaners.add(cleaner);
	}

	/**
	 *  Remove a cleaner from this Vision.
	 *  @param cleaner a value to be removed
	 *  @return  True when the cleaners have changed.
	 */
	public boolean removeCleaner(Cleaner cleaner)
	{
		return this.cleaners.remove(cleaner);
	}


	/**
	 *  Get the wastebins of this Vision.
	 * @return wastebins
	 */
	public Wastebin[] getWastebins()
	{
		return (Wastebin[])wastebins.toArray(new Wastebin[wastebins.size()]);
	}

	/**
	 *  Set the wastebins of this Vision.
	 * @param wastebins the value to be set
	 */
	public void setWastebins(Wastebin[] wastebins)
	{
		this.wastebins.clear();
		for(int i = 0; i < wastebins.length; i++)
			this.wastebins.add(wastebins[i]);
	}

	/**
	 *  Get an wastebins of this Vision.
	 *  @param idx The index.
	 *  @return wastebins
	 */
	public Wastebin getWastebin(int idx)
	{
		return (Wastebin)this.wastebins.get(idx);
	}

	/**
	 *  Set a wastebin to this Vision.
	 *  @param idx The index.
	 *  @param wastebin a value to be added
	 */
	public void setWastebin(int idx, Wastebin wastebin)
	{
		this.wastebins.set(idx, wastebin);
	}

	/**
	 *  Add a wastebin to this Vision.
	 *  @param wastebin a value to be removed
	 */
	public void addWastebin(Wastebin wastebin)
	{
		this.wastebins.add(wastebin);
	}

	/**
	 *  Remove a wastebin from this Vision.
	 *  @param wastebin a value to be removed
	 *  @return  True when the wastebins have changed.
	 */
	public boolean removeWastebin(Wastebin wastebin)
	{
		return this.wastebins.remove(wastebin);
	}


	/**
	 *  Get the wastes of this Vision.
	 * @return wastes
	 */
	public Waste[] getWastes()
	{
		return (Waste[])wastes.toArray(new Waste[wastes.size()]);
	}

	/**
	 *  Set the wastes of this Vision.
	 * @param wastes the value to be set
	 */
	public void setWastes(Waste[] wastes)
	{
		this.wastes.clear();
		for(int i = 0; i < wastes.length; i++)
			this.wastes.add(wastes[i]);
	}

	/**
	 *  Get an wastes of this Vision.
	 *  @param idx The index.
	 *  @return wastes
	 */
	public Waste getWaste(int idx)
	{
		return (Waste)this.wastes.get(idx);
	}

	/**
	 *  Set a waste to this Vision.
	 *  @param idx The index.
	 *  @param waste a value to be added
	 */
	public void setWaste(int idx, Waste waste)
	{
		this.wastes.set(idx, waste);
	}

	/**
	 *  Add a waste to this Vision.
	 *  @param waste a value to be removed
	 */
	public void addWaste(Waste waste)
	{
		this.wastes.add(waste);
	}

	/**
	 *  Remove a waste from this Vision.
	 *  @param waste a value to be removed
	 *  @return  True when the wastes have changed.
	 */
	public boolean removeWaste(Waste waste)
	{
		return this.wastes.remove(waste);
	}


	/**
	 *  Get the stations of this Vision.
	 * @return stations
	 */
	public Chargingstation[] getStations()
	{
		return (Chargingstation[])stations.toArray(new Chargingstation[stations.size()]);
	}

	/**
	 *  Set the stations of this Vision.
	 * @param stations the value to be set
	 */
	public void setStations(Chargingstation[] stations)
	{
		this.stations.clear();
		for(int i = 0; i < stations.length; i++)
			this.stations.add(stations[i]);
	}

	/**
	 *  Get an stations of this Vision.
	 *  @param idx The index.
	 *  @return stations
	 */
	public Chargingstation getStation(int idx)
	{
		return (Chargingstation)this.stations.get(idx);
	}

	/**
	 *  Set a station to this Vision.
	 *  @param idx The index.
	 *  @param station a value to be added
	 */
	public void setStation(int idx, Chargingstation station)
	{
		this.stations.set(idx, station);
	}

	/**
	 *  Add a station to this Vision.
	 *  @param station a value to be removed
	 */
	public void addStation(Chargingstation station)
	{
		this.stations.add(station);
		pcs.firePropertyChange("stations", null, stations);
	}

	/**
	 *  Remove a station from this Vision.
	 *  @param station a value to be removed
	 *  @return  True when the stations have changed.
	 */
	public boolean removeStation(Chargingstation station)
	{
		boolean ret = this.stations.remove(station);
		pcs.firePropertyChange("stations", null, stations);
		return ret;
	}


	/**
	 *  Get the daytime of this Vision.
	 * @return daytime
	 */
	public boolean isDaytime()
	{
		return this.daytime;
	}

	/**
	 *  Set the daytime of this Vision.
	 * @param daytime the value to be set
	 */
	public void setDaytime(boolean daytime)
	{
//		boolean oldd = this.daytime;
		this.daytime = daytime;
//		pcs.firePropertyChange("daytime", oldd, daytime);
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Vision.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Vision(" + "id=" + getId() + ", location=" + getLocation() + ")";
	}

	/**
	 *  Clone the object.
	 */
	public Object clone()
	{
		Vision clone = (Vision)super.clone();
		clone.cleaners = (ArrayList)((ArrayList)this.cleaners).clone();
		clone.wastebins = (ArrayList)((ArrayList)this.wastebins).clone();
		clone.wastes = (ArrayList)((ArrayList)this.wastes).clone();
		clone.stations = (ArrayList)((ArrayList)this.stations).clone();
		return clone;
	}
}
