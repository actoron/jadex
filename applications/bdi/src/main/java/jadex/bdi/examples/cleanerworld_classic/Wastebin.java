package jadex.bdi.examples.cleanerworld_classic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 *  Editable Java class for concept Wastebin of cleaner-generated ontology.
 */
public class Wastebin extends LocationObject
{
	/** The instance counter. */
	protected static int instancecnt = 0;

	//-------- attributes ----------

	/** Attribute for slot wastes. */
	protected java.util.List wastes;

	/** Attribute for slot capacity. */
	protected int capacity;

	/** Attribute for slot name. */
	protected String name;
	
	//-------- constructors --------

	/**
	 *  Create a new Wastebin.
	 */
	public Wastebin()
	{
		// Empty constructor required for JavaBeans (do not remove).
		this(null, 0);
	}

	/**
	 *  Create a new wastebin.
	 */
	public Wastebin(Location location, int capacity)
	{
		this("Wastebin #" + instancecnt++, location, capacity);
	}

	/**
	 *  Create a new Wastebin.
	 */
	public Wastebin(String name, Location location, int capacity)
	{
		this.wastes = new ArrayList();
		setId(name);
		setName(name);
		setLocation(location);
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
		pcs.firePropertyChange("wastes", null, wastes);
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
		pcs.firePropertyChange("wastes", null, wastes);
	}

	/**
	 *  Add a waste to this Wastebin.
	 *  @param waste a value to be removed
	 */
	public void addWaste(Waste waste)
	{
		this.wastes.add(waste);
		pcs.firePropertyChange("wastes", null, wastes);
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
			pcs.firePropertyChange("wastes", null, wastes);
		return ret;
	}

	/**
	 *  Get the capacity of this Wastebin.
	 * @return capacity
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
		pcs.firePropertyChange("capacity", oldc, capacity);
	}

	/**
	 *  Get the name of this Wastebin.
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name of this Wastebin.
	 * @param name the value to be set
	 */
	public void setName(String name)
	{
		String oldn = this.name;
		this.name = name;
		pcs.firePropertyChange("name", oldn, name);
	}
	
	//-------- object methods --------

	/**
	 *  Get a string representation of this Wastebin.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Wastebin(" + "id=" + getId() + ", location=" + getLocation() + ", name=" + getName() + ")";
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
	public boolean contains(Waste waste)
	{
		return wastes.contains(waste);
	}

	/**
	 *  Clone the object.
	 */
	public Object clone()
	{
		Wastebin clone = (Wastebin)super.clone();
		clone.wastes = new ArrayList();
		for(int i = 0; i < wastes.size(); i++)
			clone.wastes.add(((Waste)wastes.get(i)).clone());
		return clone;
	}
	
	/**
	 *  Update this wastebin.
	 */
	public void update(Wastebin wb)
	{
		assert this.getId().equals(wb.getId());
		
		Waste[] newwastes = wb.getWastes();
		
		Set toremove = new HashSet();
		for(int i=0; i<wastes.size(); i++)
			toremove.add(wastes.get(i));
		
		// Add new wastes and mark old existing ones.
		for(int i=0; i<newwastes.length; i++)
		{
			if(toremove.contains(newwastes[i]))
			{
				toremove.remove(newwastes[i]);
			}
			else
			{
				addWaste(newwastes[i]);
			}
		}
		
		// Delete old non-existing ones.
		for(Iterator it=toremove.iterator(); it.hasNext(); )
		{
			removeWaste((Waste)it.next());
		}
	}
}
