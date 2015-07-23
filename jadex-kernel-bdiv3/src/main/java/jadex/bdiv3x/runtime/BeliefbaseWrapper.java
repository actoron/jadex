package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;
import jadex.javaparser.IMapAccess;

import java.util.ArrayList;
import java.util.List;

/**
 *  Prepend capability prefix to belief names.
 */
public class BeliefbaseWrapper implements IMapAccess, IBeliefbase
{
	//-------- attributes --------
	
	/** The flat belief base. */
	protected IBeliefbase	beliefbase;
	
	/** The full capability prefix. */
	protected String	prefix;
	
	/** The local belief names (cached on first access). */
	protected String[]	names;
	
	/** The local belief set names (cached on first access). */
	protected String[]	setnames;
	
	//-------- constructors --------
	
	/**
	 *  Create a belief base wrapper.
	 */
	public BeliefbaseWrapper(IBeliefbase beliefbase, String prefix)
	{
		this.beliefbase	= beliefbase;
		this.prefix	= prefix;
	}
	
	//-------- IMapAccess methods --------
	
	/**
	 *  Get an object from the map.
	 *  @param name The name
	 *  @return The value.
	 */
	public Object get(Object name)
	{
		return ((IMapAccess)beliefbase).get(prefix+name);
	}
	
	//-------- element methods ---------

	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public MElement getModelElement()
	{
		return beliefbase.getModelElement();
	}
	
	//-------- IBeliefbase methods --------
	
   /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IBelief getBelief(String name)
	{
		return beliefbase.getBelief(prefix+name);
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IBeliefSet getBeliefSet(String name)
	{
		return beliefbase.getBeliefSet(prefix+name);
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief with the
	 *  specified name.
	 *  @param name the name of a belief.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief set.
	 *  @see #containsBeliefSet(java.lang.String)
	 */
	public boolean containsBelief(String name)
	{
		return beliefbase.containsBelief(prefix+name);
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief set with the
	 *  specified name.
	 *  @param name the name of a belief set.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief.
	 *  @see #containsBelief(java.lang.String)
	 */
	public boolean containsBeliefSet(String name)
	{
		return beliefbase.containsBeliefSet(prefix+name);
	}

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public String[] getBeliefNames()
	{
		if(names==null)
		{
			List<String>	lnames	= new ArrayList<String>();
			for(String name: beliefbase.getBeliefNames())
			{
				if(name.startsWith(prefix))
				{
					name	= name.substring(prefix.length());
					if(name.indexOf(MElement.CAPABILITY_SEPARATOR)==-1)
					{
						lnames.add(name);
					}
				}
			}
			names	= lnames.toArray(new String[lnames.size()]);
		}
		
		return names;
	}

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public String[] getBeliefSetNames()
	{
		if(setnames==null)
		{
			List<String>	lnames	= new ArrayList<String>();
			for(String name: beliefbase.getBeliefSetNames())
			{
				if(name.startsWith(prefix))
				{
					name	= name.substring(prefix.length());
					if(name.indexOf(MElement.CAPABILITY_SEPARATOR)==-1)
					{
						lnames.add(name);
					}
				}
			}
			setnames	= lnames.toArray(new String[lnames.size()]);
		}
		
		return setnames;
	}
}