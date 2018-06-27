package jadex.rules.test.rulesystem;

import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.beans.PropertyChangeSupport;

/**
 *  The test bean has an attribute that fires a
 *  bean property event when the attribute value
 *  is changed.
 */
// Copy of TestBean for second type node
// to test left/right behavior of rete tree independently.
public class TestBean2
{
	//-------- attributes --------

	/** The beans name. */
	public String name;

	/** The helper object for bean events. */
	public PropertyChangeSupport pcs;

	//-------- constructor --------

	/**
	 *  Create a new test bean.
	 */
	public TestBean2(String name)
	{
		this.name = name;
		this.pcs = new PropertyChangeSupport(this);
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		String old = this.name;
		this.name = name;
//		System.out.println("bean fires: "+name);
		this.pcs.firePropertyChange("name", old, this.name);
//		System.out.println("bean has fired: "+name);
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return name;
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }

    //-------- spurious methods for reflection testing --------

	/**
	 *  Spurious method to test reflective access.
	 */
	public void addPropertyChangeListener()
	{
		throw new RuntimeException("Method should not be called");
	}
	
	/**
	 *  Spurious method to test reflective access.
	 */
	public void removePropertyChangeListener()
	{
		throw new RuntimeException("Method should not be called");
	}

	/**
	 *  Spurious method to test reflective access.
	 */
	public void addPropertyChangeListener(Object listener)
	{
		throw new RuntimeException("Method should not be called");
	}
	
	/**
	 *  Spurious method to test reflective access.
	 */
	public void removePropertyChangeListener(Object listener)
	{
		throw new RuntimeException("Method should not be called");
	}

	/**
	 *  Spurious method to test reflective access.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener, Object arg2)
	{
		throw new RuntimeException("Method should not be called");
	}
	
	/**
	 *  Spurious method to test reflective access.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener, Object arg2)
	{
		throw new RuntimeException("Method should not be called");
	}

	/**
	 *  Spurious method to test reflective access.
	 */
	public void addPropertyChangeListener(Object arg0, PropertyChangeListener listener)
	{
		throw new RuntimeException("Method should not be called");
	}
	
	/**
	 *  Spurious method to test reflective access.
	 */
	public void removePropertyChangeListener(Object arg0, PropertyChangeListener listener)
	{
		throw new RuntimeException("Method should not be called");
	}
}
