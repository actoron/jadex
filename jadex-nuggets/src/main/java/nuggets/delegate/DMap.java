/*
 * DMap.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 5, 2005.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import java.util.Iterator;
import java.util.Map;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IDelayedOperation;
import nuggets.InstanceNotAvailableException;
import nuggets.PersistenceException;


/** DMap 
 * @author walczak
 * @since  Dec 5, 2005
 */
public class DMap extends ADelegate
{
	
	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ADelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{
		Map map=(Map)o;
		declareReferences(map, mill, classloader);
		  mill.startConcept(o);
		addReferences(map, mill);
	}


	/** 
	 * @param map
	 * @param mill
	 */
	protected void addReferences(Map map, ICruncher mill)
	{
		final Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
			Object key = it.next();
			mill.add(key);
			Object value=map.get(key);
			mill.add(value);
		}
	}


	/** 
	 * @param map
	 * @param mill
	 */
	protected void declareReferences(Map map, ICruncher mill, ClassLoader classloader)
	{
		final Iterator it = map.keySet().iterator();
		while(it.hasNext())
		{
			Object key = it.next();
			mill.declare(key, classloader);
			Object value=map.get(key);
			mill.declare(value, classloader);
		}
	}


	/** 
	 * @param obj
	 * @param asm
	 * @throws Exception
	 * @see nuggets.delegate.ADelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object obj, IAssembler asm) throws Exception
	{
		Map c = (Map)obj;
		String key;
		String value;
		while((key = asm.nextToken()) != null)
		{
			value = asm.nextToken();
			if(value == null) throw new PersistenceException("No value for a key found");
			try {
				c.put(asm.getValue(key), asm.getValue(value));
			} catch(InstanceNotAvailableException inae) {
				asm.delay(new MapDelayedAdd(c, key, value));
			}
		}
	}

	/** ArrayDelayedSet 
	 * @author walczak
	 * @since  Jan 19, 2006
	 */
	public static final class MapDelayedAdd implements IDelayedOperation
	{
		private final String	key;

		private final Map	col;

		private final String	value;

		/** 
		 * Constructor for ArrayDelayedSet.
		 * @param col
		 * @param key
		 * @param value 
		 */
		public MapDelayedAdd(Map col, String key, String value)
		{
			this.col = col;
			this.key = key;
			this.value = value;
		}

		/** 
		 * @param asm
		 * @throws Exception
		 * @see nuggets.IDelayedOperation#perform(nuggets.IAssembler)
		 */
		public void perform(IAssembler asm) throws Exception
		{
			col.put(asm.getValue(key), asm.getValue(value));
		}
	}

}
