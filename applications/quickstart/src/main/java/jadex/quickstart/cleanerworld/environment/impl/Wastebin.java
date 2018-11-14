package jadex.quickstart.cleanerworld.environment.impl;

import java.util.ArrayList;
import java.util.List;

import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.IWastebin;


/**
 *  (Knowledge about) a waste bin.
 */
public class Wastebin extends LocationObject	implements IWastebin
{
	/** The instance counter. */
	private static int instancecnt = 0;

	/**
	 *  Get an instance number.
	 */
	private static synchronized int	getNumber()
	{
		return ++instancecnt;
	}

	//-------- attributes ----------

	/** The contained wastes. */
	private List<IWaste> wastes;

	/** The maximum number of waste objects to fit in this waste bin. */
	private int capacity;

	//-------- constructors --------

	/**
	 *  Create a new Wastebin.
	 */
	public Wastebin()
	{
		// Empty constructor required for JavaBeans (do not remove).
		this.wastes = new ArrayList<IWaste>();
	}

	/**
	 *  Create a new wastebin.
	 */
	public Wastebin(Location location, int capacity)
	{
		super("Wastebin #" + getNumber(), location);
		this.wastes = new ArrayList<IWaste>();
		setCapacity(capacity);
	}

	//-------- accessor methods --------

	/**
	 *  Get the wastes of this Wastebin.
	 * @return wastes
	 */
	public Waste[] getWastes()
	{
		return (Waste[])wastes.toArray(new Waste[wastes.size()]);
	}

	/**
	 *  Set the wastes of this Wastebin.
	 * @param wastes the value to be set
	 */
	public void setWastes(Waste[] wastes)
	{
		this.wastes.clear();
		for(int i = 0; i < wastes.length; i++)
			this.wastes.add(wastes[i]);
		getPropertyChangeHandler().firePropertyChange("wastes", null, wastes);
	}

	/**
	 *  Get an wastes of this Wastebin.
	 *  @param idx The index.
	 *  @return wastes
	 */
	public Waste getWaste(int idx)
	{
		return (Waste)this.wastes.get(idx);
	}

	/**
	 *  Set a waste to this Wastebin.
	 *  @param idx The index.
	 *  @param waste a value to be added
	 */
	public void setWaste(int idx, Waste waste)
	{
		this.wastes.set(idx, waste);
		getPropertyChangeHandler().firePropertyChange("wastes", null, wastes);
	}

	/**
	 *  Add a waste to this Wastebin.
	 *  @param waste a value to be removed
	 */
	public void addWaste(IWaste waste)
	{
		this.wastes.add(waste);
		getPropertyChangeHandler().firePropertyChange("wastes", null, wastes);
	}

	/**
	 *  Remove a waste from this Wastebin.
	 *  @param waste a value to be removed
	 *  @return  True when the wastes have changed.
	 */
	public boolean removeWaste(Waste waste)
	{
		boolean ret = this.wastes.remove(waste);
		if(ret)
			getPropertyChangeHandler().firePropertyChange("wastes", null, wastes);
		return ret;
	}

	/**
	 *  Get the capacity of this Wastebin.
	 * @return The maximum number of waste objects to fit in this waste bin.
	 */
	public int getCapacity()
	{
		return this.capacity;
	}

	/**
	 *  Set the capacity of this Wastebin.
	 * @param capacity the value to be set
	 */
	public void setCapacity(int capacity)
	{
		int oldc = this.capacity;
		this.capacity = capacity;
		getPropertyChangeHandler().firePropertyChange("capacity", oldc, capacity);
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Wastebin.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Wastebin(" + "id=" + getId() + ", location=" + getLocation() + ")";
	}

	//-------- custom code --------

	/**
	 *  Test is the wastebin is full.
	 *  @return True, when wastebin is full.
	 */
	public boolean isFull()
	{
		return wastes.size() >= capacity;
	}

	/**
	 *  Empty the waste bin.
	 */
	public void empty()
	{
		wastes.clear();
	}

	/**
	 *  Fill the waste bin.
	 */
	public void fill()
	{
		// Fill with imaginary waste ;-)
		while(!isFull())
			wastes.add(new Waste(new Location(-1, -1)));
	}


	/**
	 *  Test is the waste is in the waste bin.
	 *  @return True, when wastebin contains the waste.
	 */
	public boolean contains(IWaste waste)
	{
		return wastes.contains(waste);
	}

	/**
	 *  Clone the object.
	 */
	public Wastebin clone()
	{
		Wastebin clone = (Wastebin)super.clone();
		clone.wastes = new ArrayList<IWaste>();
		for(int i = 0; i < wastes.size(); i++)
			clone.wastes.add((IWaste)((Waste)wastes.get(i)).clone());
		return clone;
	}
}
