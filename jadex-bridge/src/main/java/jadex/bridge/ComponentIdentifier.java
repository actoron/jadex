package jadex.bridge;

import jadex.commons.SUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * An component identifier (AID), see FIPASC00023.
 */
public class ComponentIdentifier implements IComponentIdentifier, Cloneable, Serializable
{
	//-------- attributes ----------

	/** The component name. */
	protected String name;
	
	/** Attribute for slot addresses. */
	protected List addresses;

	/** Attribute for slot resolvers. */
	protected List resolvers;

	//-------- constructors --------

	/**
	 *  Create a new component identifier.
	 *  Bean constructor
	 */
	public ComponentIdentifier()
	{
		this(null, null, null);
	}

	/**
	 *  Create a new component identifier with a given global name.
	 *  @param name A global name (e.g. "cms@lars").
	 */
	public ComponentIdentifier(String name)
	{
		this(name, null, null);
	}

	/**
	 *  Create a new component identifier with a global name and given addresses.
	 *  @param name A global name (e.g. "cms@lars").
	 *  @param addresses A list of transport addresses.
	 */
	public ComponentIdentifier(String name, String[] addresses)
	{
		this(name, addresses, null);
	}

	/**
	 *  Create a new component identifier.
	 *  @param name A local or global name.
	 *  @param addresses A list of transport addresses.
	 *  @param resolvers A list of resolvers, which may provide additional transport adresses.
	 */
	public ComponentIdentifier(String name, String[] addresses, IComponentIdentifier[] resolvers)
	{
		this.name = name;

		for(int i = 0; addresses != null && i < addresses.length; i++)
			addAddress(addresses[i]);
		for(int i = 0; resolvers != null && i < resolvers.length; i++)
			addResolver(resolvers[i]);
	}

	//-------- accessor methods --------

	/**
	 *  Get the component name.
	 */
	public String	getName()
	{
		return  this.name;
	}

	/**
	 *  Set the component name.
	 *  @param name	The component name.
	 */
	public void	setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Get the addresses of this ComponentIdentifier.
	 * @return addresses
	 */
	public String[] getAddresses()
	{
		if(addresses!=null)
			return (String[])addresses.toArray(new String[addresses.size()]);
		else
			return new String[0];
	}

	/**
	 *  Set the addresses of this ComponentIdentifier.
	 * @param addresses the value to be set
	 */
	public void setAddresses(String[] addresses)
	{
		if(this.addresses!=null)
			this.addresses.clear();
		else
			this.addresses = new ArrayList();

		for(int i = 0; i < addresses.length; i++)
		{
			if(addresses[i]==null)
				throw new IllegalArgumentException("Addresses must not null: "+this);
			this.addresses.add(addresses[i]);
		}
	}

	/**
	 *  Get an addresses of this ComponentIdentifier.
	 *  @param idx The index.
	 *  @return addresses
	 */
	public String getAddress(int idx)
	{
		if(addresses!=null)
			return (String)this.addresses.get(idx);
		else
			throw new ArrayIndexOutOfBoundsException(idx);
	}

	/**
	 *  Set a address to this ComponentIdentifier.
	 *  @param idx The index.
	 *  @param address a value to be added
	 */
	public void setAddress(int idx, String address)
	{
		if(addresses!=null)
			this.addresses.set(idx, address);
		else
			throw new ArrayIndexOutOfBoundsException(idx);
	}

	/**
	 *  Add a address to this ComponentIdentifier.
	 *  @param address a value to be removed
	 */
	public void addAddress(String address)
	{
		if(addresses==null)
			this.addresses = new ArrayList();			
		this.addresses.add(address);
	}

	/**
	 *  Remove a address from this ComponentIdentifier.
	 *  @param address a value to be removed
	 *  @return  True when the addresses have changed.
	 */
	public boolean removeAddress(String address)
	{
		if(addresses!=null)
			return this.addresses.remove(address);
		else
			return false;
	}


	/**
	 *  Get the resolvers of this ComponentIdentifier.
	 * @return resolvers
	 */
	public ComponentIdentifier[] getResolvers()
	{
		if(resolvers!=null)
			return (ComponentIdentifier[])resolvers.toArray(new ComponentIdentifier[resolvers.size()]);
		else
			return new ComponentIdentifier[0];
	}

	/**
	 *  Set the resolvers of this ComponentIdentifier.
	 * @param resolvers the value to be set
	 */
	public void setResolvers(ComponentIdentifier[] resolvers)
	{
		if(this.resolvers!=null)
			this.resolvers.clear();
		else
			this.resolvers = new ArrayList();
			
		for(int i = 0; i < resolvers.length; i++)
			this.resolvers.add(resolvers[i]);
	}

	/**
	 *  Get an resolvers of this ComponentIdentifier.
	 *  @param idx The index.
	 *  @return resolvers
	 */
	public ComponentIdentifier getResolver(int idx)
	{
		if(resolvers!=null)
			return (ComponentIdentifier)this.resolvers.get(idx);
		else
			throw new ArrayIndexOutOfBoundsException(idx);
	}

	/**
	 *  Set a resolver to this ComponentIdentifier.
	 *  @param idx The index.
	 *  @param resolver a value to be added
	 */
	public void setResolver(int idx, ComponentIdentifier resolver)
	{
		if(resolvers!=null)
			this.resolvers.set(idx, resolver);
		else
			throw new ArrayIndexOutOfBoundsException(idx);
	}

	/**
	 *  Add a resolver to this ComponentIdentifier.
	 *  @param resolver a value to be removed
	 */
	public void addResolver(IComponentIdentifier resolver)
	{
		if(resolvers==null)
			this.resolvers = new ArrayList();
		this.resolvers.add(resolver);
	}

	/**
	 *  Remove a resolver from this ComponentIdentifier.
	 *  @param resolver a value to be removed
	 *  @return  True when the resolvers have changed.
	 */
	public boolean removeResolver(IComponentIdentifier resolver)
	{
		if(resolvers==null)
			return this.resolvers.remove(resolver);
		else
			return false;
	}

	//--------- methods --------

	/**
	 * Clone this component identifier.
	 * Does a deep copy.
	 */
	public Object clone()
	{
		ComponentIdentifier clone = new ComponentIdentifier(getName(), getAddresses(), null);

		// Deep copy of resolvers.
		ComponentIdentifier[] res = getResolvers();
		for(int i = 0; i < res.length; i++)
			clone.addResolver((ComponentIdentifier)res[i].clone());

		return clone;
	}

	/**
	 * Checks if this adress equals one or more addresses in the identifier
	 * @param address
	 * @return true
	 */
	public boolean hasAddress(String address)
	{
		boolean ret = false;
		for(int i = 0; !ret && addresses!=null && i < addresses.size(); i++)
			ret = address.equals(addresses.get(i));

		return ret;
	}

	/**
	 * @return the local name of a component
	 */
	public String getLocalName()
	{
		String ret = getName();
		int idx;
		if((idx = ret.indexOf('@')) != -1)
			ret = ret.substring(0, idx);
		return ret;
	}

	/**
	 *  Get the platform name.
	 *  @return The platform name.
	 */
	public String getPlatformName()
	{
		String ret = getName();
		int idx;
		if((idx = ret.indexOf('@')) != -1)
			ret = ret.substring(idx + 1);
		return ret;
	}

	/**
	 *  The hash code of the object.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return 31 + ((name == null) ? 0 : name.hashCode());
	}

	/**
	 *  Test if two component identifiers are equal.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		return this==obj
			|| obj instanceof IComponentIdentifier
				&& SUtil.equals(name, ((IComponentIdentifier)obj).getName());
	}
	
	/**
	 *  Return a string representation.
	 *  @return The string representation.
	 */
	public String	toString()
	{
		return name;
	}
}
