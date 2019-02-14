package jadex.bridge;

import java.io.Serializable;

import jadex.commons.SUtil;


/**
 * A component identifier. Name is unique and has the form <name>@<platform>
 */
// Called basic to avoid incompatibilities with older releases due to new transport identifier
public class ComponentIdentifier implements IComponentIdentifier, Cloneable, Serializable
{
	//-------- attributes ----------

	/** The component name. */
	protected String name;
	
	/** Cache for platform name for getRoot() calls. */
	protected String root;
	
	//-------- constructors --------

	/**
	 *  Create a new component identifier.
	 *  Bean constructor
	 */
	public ComponentIdentifier()
	{
	}

	/**
	 *  Create a new component identifier with a global name and given addresses.
	 *  @param name A global name (e.g. "cms@lars").
	 */
	public ComponentIdentifier(String name)
	{
		if(name==null)
			throw new IllegalArgumentException("Name must not null.");
		
		if(name!=null && (name.indexOf("@")!=name.lastIndexOf("@")))
		{
			throw new IllegalArgumentException("Invalid component identifier: "+name);			
		}
		this.name = SUtil.intern(name);
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The local name.
	 *  @param parent The parent.
	 *  @param addresses The addresses.
	 */
	public ComponentIdentifier(String name, IComponentIdentifier parent)//, String[] addresses)
	{
		this(name+"@"+parent.getName().replace('@', ':'));//, addresses);
	}
	
	/**
	 *  Copy a component identifier.
	 *  @param cid	The id to copy from. 
	 */
	public ComponentIdentifier(IComponentIdentifier cid)
	{
		this(cid.getName());//, cid.getAddresses());
	}
	
	/**
	 *  Creates an identifier with known root (used by getRoot()).
	 *  
	 *  @param name The global name.
	 *  @param root The root name.
	 */
	private ComponentIdentifier(String name, String root)
	{
		this.name = name;
		this.root = root;
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
		if(name==null)
		{
			throw new NullPointerException();
		}
		this.name = SUtil.intern(name);
	}
	
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
			ret = new ComponentIdentifier(paname+"@"+pfname);//, getAddresses());
		}
		else if(at!=-1)
		{
			String paname = name.substring(at+1);
			ret = new ComponentIdentifier(paname);//, getAddresses());
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
		return new ComponentIdentifier(getPlatformName(), getPlatformName());
	}

	//--------- methods --------

	/**
	 * Clone this component identifier.
	 * Does a deep copy.
	 */
	public Object clone()
	{
		ComponentIdentifier clone = new ComponentIdentifier(getName());
		return clone;
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
		if (root == null)
		{
			String rootname = getName();
			int idx;
			if((idx = rootname.indexOf('@')) != -1)
				rootname = rootname.substring(idx + 1);
			if((idx = rootname.lastIndexOf(':')) != -1)
				rootname = rootname.substring(idx + 1);
			root = SUtil.intern(rootname);
		}
		return root;
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
			|| (obj instanceof ComponentIdentifier && (((ComponentIdentifier) obj).name == name))
			|| (obj instanceof IComponentIdentifier
				&& SUtil.equals(name, ((IComponentIdentifier)obj).getName()));
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
	
//	public static void main(String[] args)
//	{
//		for (int j = 0; j < 5; ++j)
//		{
//			long ts = System.currentTimeMillis();
//			for (int i = 0; i < 1000000; ++i)
//			{
//				new String("TestTestTestTest0").intern();
//				new String("TestTestTestTest1").intern();
//				new String("TestTestTestTest2").intern();
//				new String("TestTestTestTest3").intern();
//				new String("TestTestTestTest4").intern();
//				new String("TestTestTestTest5").intern();
//				new String("TestTestTestTest6").intern();
//				new String("TestTestTestTest7").intern();
//				new String("TestTestTestTest8").intern();
//				new String("TestTestTestTest9").intern();
//			}
//			ts = System.currentTimeMillis() - ts;
//			System.out.println("Java intern took: " + ts);
//		}
//		for (int j = 0; j < 5; ++j)
//		{
//			long ts = System.currentTimeMillis();
//			for (int i = 0; i < 1000000; ++i)
//			{
//				SUtil.intern(new String("TestTestTestTest0"));
//				SUtil.intern(new String("TestTestTestTest1"));
//				SUtil.intern(new String("TestTestTestTest2"));
//				SUtil.intern(new String("TestTestTestTest3"));
//				SUtil.intern(new String("TestTestTestTest4"));
//				SUtil.intern(new String("TestTestTestTest5"));
//				SUtil.intern(new String("TestTestTestTest6"));
//				SUtil.intern(new String("TestTestTestTest7"));
//				SUtil.intern(new String("TestTestTestTest8"));
//				SUtil.intern(new String("TestTestTestTest9"));
//			}
//			ts = System.currentTimeMillis() - ts;
//			System.out.println("Jadex intern took: " + ts);
//		}
//	}
	
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
