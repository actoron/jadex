package jadex.bridge;

import java.io.Serializable;

import jadex.commons.SUtil;


/**
 * An component identifier (AID), see FIPASC00023.
 */
// Called basic to avoid incompatibilities with older releases due to new transport identifier
public class BasicComponentIdentifier implements IComponentIdentifier, Cloneable, Serializable
{
	//-------- attributes ----------

	/** The component name. */
	protected String name;
	
//	/** Attribute for slot resolvers. */
//	protected IComponentIdentifier[]	resolvers;

	//-------- constructors --------

	/**
	 *  Create a new component identifier.
	 *  Bean constructor
	 */
	public BasicComponentIdentifier()
	{
//		this(null, (String[])null);
	}

//	/**
//	 *  Create a new component identifier with a given global name.
//	 *  @param name A global name (e.g. "cms@lars").
//	 */
//	public ComponentIdentifier(String name)
//	{
//		this(name, (String[])null);
//	}

	/**
	 *  Create a new component identifier with a global name and given addresses.
	 *  @param name A global name (e.g. "cms@lars").
	 *  @param addresses A list of transport addresses.
	 */
	public BasicComponentIdentifier(String name)//, String[] addresses)
	{
		if(name==null)
			throw new IllegalArgumentException("Name must not null.");
		
//		this(name, addresses, null);
		if(name!=null && (name.indexOf("@")!=name.lastIndexOf("@")))
		{
			throw new IllegalArgumentException("Invalid component identifier: "+name);			
		}
		this.name = name;
//		this.addresses	= addresses;
	}
	
//	/**
//	 *  Create component identifier.
//	 *  @param name The local name.
//	 *  @param parent The parent.
//	 *  @param addresses The addresses.
//	 */
//	public ComponentIdentifier(String name, IComponentIdentifier parent)
//	{
//		this(name, parent, parent.getAddresses());
//	}
	
	/**
	 *  Create component identifier.
	 *  @param name The local name.
	 *  @param parent The parent.
	 *  @param addresses The addresses.
	 */
	public BasicComponentIdentifier(String name, IComponentIdentifier parent)//, String[] addresses)
	{
		this(name+"@"+parent.getName().replace('@', ':'));//, addresses);
	}
	
	/**
	 *  Copy a component identifier.
	 *  @param cid	The id to copy from. 
	 */
	public BasicComponentIdentifier(IComponentIdentifier cid)
	{
		this(cid.getName());//, cid.getAddresses());
	}
	
//	/**
//	 *  Create a new component identifier.
//	 *  @param name A local or global name.
//	 *  @param addresses A list of transport addresses.
//	 *  @param resolvers A list of resolvers, which may provide additional transport adresses.
//	 */
//	public ComponentIdentifier(String name, String[] addresses, IComponentIdentifier[] resolvers)
//	{
//		this.name = name;
//
////		System.out.println("created: "+name);
//		
//		this.addresses	= addresses;
//		this.resolvers	= resolvers;
//	}

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
		if(name==null)
		{
			throw new NullPointerException();
		}
		this.name = name;
	}
	
//	/**
//	 *  Get an addresses of this ComponentIdentifier.
//	 *  @param idx The index.
//	 *  @return addresses
//	 */
//	public String getAddress(int idx)
//	{
//		if(addresses!=null)
//			return (String)this.addresses.get(idx);
//		else
//			throw new ArrayIndexOutOfBoundsException(idx);
//	}

//	/**
//	 *  Set a address to this ComponentIdentifier.
//	 *  @param idx The index.
//	 *  @param address a value to be added
//	 */
//	public void setAddress(int idx, String address)
//	{
//		if(addresses!=null)
//			this.addresses.set(idx, address);
//		else
//			throw new ArrayIndexOutOfBoundsException(idx);
//	}

//	/**
//	 *  Add a address to this ComponentIdentifier.
//	 *  @param address a value to be removed
//	 */
//	public void addAddress(String address)
//	{
//		if(addresses==null)
//			this.addresses = new ArrayList();			
//		this.addresses.add(address);
//	}

//	/**
//	 *  Remove a address from this ComponentIdentifier.
//	 *  @param address a value to be removed
//	 *  @return  True when the addresses have changed.
//	 */
//	public boolean removeAddress(String address)
//	{
//		if(addresses!=null)
//			return this.addresses.remove(address);
//		else
//			return false;
//	}


//	/**
//	 *  Get the resolvers of this ComponentIdentifier.
//	 * @return resolvers
//	 */
//	public IComponentIdentifier[] getResolvers()
//	{
//		return resolvers;
//	}

//	/**
//	 *  Set the resolvers of this ComponentIdentifier.
//	 * @param resolvers the value to be set
//	 */
//	public void setResolvers(IComponentIdentifier[] resolvers)
//	{
//		this.resolvers	= resolvers;
//	}

//	/**
//	 *  Get an resolvers of this ComponentIdentifier.
//	 *  @param idx The index.
//	 *  @return resolvers
//	 */
//	public ComponentIdentifier getResolver(int idx)
//	{
//		if(resolvers!=null)
//			return (ComponentIdentifier)this.resolvers.get(idx);
//		else
//			throw new ArrayIndexOutOfBoundsException(idx);
//	}

//	/**
//	 *  Set a resolver to this ComponentIdentifier.
//	 *  @param idx The index.
//	 *  @param resolver a value to be added
//	 */
//	public void setResolver(int idx, ComponentIdentifier resolver)
//	{
//		if(resolvers!=null)
//			this.resolvers.set(idx, resolver);
//		else
//			throw new ArrayIndexOutOfBoundsException(idx);
//	}

//	/**
//	 *  Add a resolver to this ComponentIdentifier.
//	 *  @param resolver a value to be removed
//	 */
//	public void addResolver(IComponentIdentifier resolver)
//	{
//		if(resolvers==null)
//			this.resolvers = new ArrayList();
//		this.resolvers.add(resolver);
//	}

//	/**
//	 *  Remove a resolver from this ComponentIdentifier.
//	 *  @param resolver a value to be removed
//	 *  @return  True when the resolvers have changed.
//	 */
//	public boolean removeResolver(IComponentIdentifier resolver)
//	{
//		if(resolvers==null)
//			return this.resolvers.remove(resolver);
//		else
//			return false;
//	}
	
	/**
	 *  Get the parent identifier.
	 *  @return The parent identifier (if any).
	 */
	public IComponentIdentifier getParent()
	{
		IComponentIdentifier ret = null;
		int	at = name.indexOf("@");
		int idx = name.indexOf(":", at);
		if(idx!=-1)
		{
			String paname = name.substring(at+1, idx);
			String pfname = name.substring(idx+1);
//			ret = new ComponentIdentifier(paname+"@"+pfname, getAddresses(), getResolvers());
			ret = new BasicComponentIdentifier(paname+"@"+pfname);//, getAddresses());
		}
		else if(at!=-1)
		{
			String paname = name.substring(at+1);
//			ret = new ComponentIdentifier(paname, getAddresses(), getResolvers());
			ret = new BasicComponentIdentifier(paname);//, getAddresses());
		}
		// else at root.
		return ret;
	}
	
	/**
	 *  Get the root identifier.
	 *  @return The root identifier.
	 */
	public IComponentIdentifier getRoot()
	{
//		return new ComponentIdentifier(getPlatformName(), getAddresses(), getResolvers());
		return new BasicComponentIdentifier(getPlatformName());//, getAddresses());
	}

	//--------- methods --------

	/**
	 * Clone this component identifier.
	 * Does a deep copy.
	 */
	public Object clone()
	{
		BasicComponentIdentifier clone = new BasicComponentIdentifier(getName());//, getAddresses());

//		// Deep copy of resolvers.
//		ComponentIdentifier[] res = getResolvers();
//		for(int i = 0; i < res.length; i++)
//			clone.addResolver((ComponentIdentifier)res[i].clone());

		return clone;
	}

//	/**
//	 * Checks if this adress equals one or more addresses in the identifier
//	 * @param address
//	 * @return true
//	 */
//	public boolean hasAddress(String address)
//	{
//		boolean ret = false;
//		for(int i = 0; !ret && addresses!=null && i < addresses.size(); i++)
//			ret = address.equals(addresses.get(i));
//
//		return ret;
//	}

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
		if((idx = ret.lastIndexOf(':')) != -1)
			ret = ret.substring(idx + 1);
		return ret;
	}
	
	/**
	 *  Get the name without @ replaced by dot.
	 */
	public String getDotName()
	{
		return getName().replace('@', ':');
//		return cid.getParent()==null? cid.getName(): cid.getLocalName()+":"+getSubcomponentName(cid);
	}
	
//	/**
//	 *  Get the application name. Equals the local component name in case it is a child of the platform.
//	 *  broadcast@awa.plat1 -> awa
//	 *  @return The application name.
//	 */
//	public String getApplicationName()
//	{
//		String ret = getName();
//		int idx;
//		// If it is a direct subcomponent
//		if((idx = ret.lastIndexOf(':')) != -1)
//		{
//			// cut off platform name
//			ret = ret.substring(0, idx);
//			// cut off local name 
//			if((idx = ret.indexOf('@'))!=-1)
//				ret = ret.substring(idx + 1);
//			if((idx = ret.indexOf(':'))!=-1)
//				ret = ret.substring(idx + 1);
//		}
//		else
//		{
//			ret = getLocalName();
//		}
//		return ret;
//	}
	
	/**
	 *  Get the platform name without the suffix for name uniqueness.
	 *  @return The platform name without suffix.
	 */
	public String getPlatformPrefix()
	{
		return getPlatformPrefix(getPlatformName());
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
	
	//-------- static part --------
	
	/**
	 *  Get the stripped platform name.
	 *  @param name	The platform name.
	 *  @return the stripped platform name.
	 */
	public static String	getPlatformPrefix(String name)
	{
		// Strip auto-generated platform suffix.
		if(name.indexOf('_')!=-1)
		{
			name	= name.substring(0, name.lastIndexOf('_'));
		}
		return name;
	}
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		ComponentIdentifier cid = new ComponentIdentifier("broadcast@awa.plat1");
//		System.out.println(cid.getApplicationName());
//		cid = new ComponentIdentifier("broadcast@plat1");
//		System.out.println(cid.getApplicationName());
//		cid = new ComponentIdentifier("broadcast@a.b.plat1");
//		System.out.println(cid.getApplicationName());
//	}
}
