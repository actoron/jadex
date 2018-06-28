package jadex.commons;

import java.net.URL;


/**
 *  A tuple is a list of entities.
 *  The entities of a tuple may be null.
 *  The equals and hashCode methods are overridden,
 *  such that two tuples are equal and have the same hashcode,
 *  if they contain the same entities.
 *  Therefore tuples can be used for multipart keys in hashtables.
 *
 *  @see java.util.Hashtable
 *  @see Object#hashCode()
 *  @see Object#equals(Object)
 */
public class Tuple	implements Cloneable, java.io.Serializable
{
	//-------- attributes --------

	/** The entities of the tuple. */
	protected Object[]	entities;

	//-------- constructors --------

	/**
	 *  Convenience constructor for binary tuples.
	 *  @param entity1	The first object in the tuple.
	 *  @param entity2	The second object in the tuple.
	 */
	public Tuple(Object entity1, Object entity2)
	{
		this(new Object[]{entity1, entity2});
	}
	
	/**
	 *  Convenience constructor for binary tuples.
	 *  @param entity1	The first object in the tuple.
	 *  @param entity2	The second object in the tuple.
	 *  @param entity3	The third object in the tuple.
	 */
	public Tuple(Object entity1, Object entity2, Object entity3)
	{
		this(new Object[]{entity1, entity2, entity3});
	}

	/**
	 *  Create a new tuple.
	 *  @param entities	The objects in the tuple.
	 */
	public Tuple(Object[] entities)
	{
		this.entities	= entities;//.clone(); // does not work with clone?!
	}

	//-------- Accessors --------

	/**
	 *  Get an entity.
	 *  @param n	The entities position.
	 *  @return The entity.
	 */
	public Object	getEntity(int n)
	{
		return entities[n];
	}

	/**
	 *  Get entities
	 *  @return The entities.
	 */
	public Object[]	getEntities()
	{
		return entities;
	}

	/**
	 *  Get an entity.
	 *  @param n	The entities position.
	 *  @return The entity.
	 */
	public Object	get(int n)
	{
		return entities[n];
	}

	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public int	size()
	{
		return entities.length;
	}

	//-------- Object overridings --------

	/**
	 *  Compute the hashcode of the tuple.
	 */
	public int	hashCode()
	{
		int	hash	= entities.length;
		for(int i=0; i<entities.length; i++)
		{
			if(entities[i]!=null)
			{
				hash	= hash ^ (entities[i].hashCode()<<i);
			}
		}
		return hash;
	}

	/**
	 *  Test two tuples for equality.
	 */
	public boolean	equals(Object o)
	{
		if(o instanceof Tuple)
		{
			Tuple	tuple	= (Tuple)o;
			if(tuple.entities.length==entities.length)
			{
				boolean	equals	= true;
				for(int i=0; i<entities.length; i++)
				{
					equals	= equals && SUtil.equals(entities[i], tuple.entities[i]);
				}
				return equals;
			}
		}
		return false;
	}

	/**
	 *  Convert this tuple to a string representation.
	 *  @return	A string representation of this tuple.
	 */
	public String toString()
	{
		return "Tuple"+SUtil.arrayToString(entities);
	}

	//-------- Cloneable interface --------

	/**
	 *  Clone this tuple.
	 *  @return A shallow copy of this tuple.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		Tuple ret = (Tuple) super.clone();
		ret.entities = entities.clone();
		return ret;
//		return new Tuple((Object[])entities.clone());
	}
	
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		String s1 = "file:/C:/projects/jadex/jadex-applications-micro/target/classes/";
		String s2 = "file:/C:/projects/jadex/jadex-applications-micro/target/test-classes/";
		
		URL u1 = new URL(s1);
		URL u2 = new URL(s2);
		
		String sufc = "classes";
		String suftc = "test-classes";
		if(s1.endsWith(sufc))
			s1 = s1 + "/";
		if(s2.endsWith(suftc))
			s2 = s2 + "/";
		sufc = "classes/";
		suftc = "test-classes/";
		
		if(s2.endsWith(suftc))
		{
			if(s1.endsWith(sufc) && u1.getProtocol().equals("file") && u2.getProtocol().equals("file"))
			{
				String st1 = s1.substring(0, s1.lastIndexOf(sufc));
				String st2 = s1.substring(0, s2.lastIndexOf(suftc));
				if(st1.equals(st2))
				{
					System.out.println("url: "+u1.getPath());
				}
			}
		}
	}
}

