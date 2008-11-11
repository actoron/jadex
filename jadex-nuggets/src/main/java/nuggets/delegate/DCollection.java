/*
 * DCollection.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 5, 2005.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import java.util.Collection;
import java.util.Iterator;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IDelayedOperation;
import nuggets.InstanceNotAvailableException;


/** DCollection 
 * @author walczak
 * @since  Dec 5, 2005
 */
public class DCollection extends ADelegate
{
	
	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ADelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{

		Iterator it = ((Collection)o).iterator();
		while(it.hasNext())			mill.declare(it.next(), classloader);

		  mill.startConcept(o);
		it = ((Collection)o).iterator();
		while(it.hasNext())			mill.add(it.next());
	}

	/** 
	 * @param clazz
	 * @param asm
	 * @return clazz.newInstance();
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		return clazz.newInstance();
	}


	/** 
	 * @param obj
	 * @param asm
	 * @throws Exception
	 * @see nuggets.delegate.ADelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object obj, IAssembler asm) throws Exception
	{
		Collection c = (Collection)obj;
		String token;
		while((token = asm.nextToken()) != null)
		{
			try {
				c.add(asm.getValue(token));
			} catch(InstanceNotAvailableException nae) {
				asm.delay(new CollectionDelayedAdd(c, token));
			}
		}
	}

	/** ArrayDelayedSet 
	 * @author walczak
	 * @since  Jan 19, 2006
	 */
	public static final class CollectionDelayedAdd implements IDelayedOperation
	{
		private final String			id;
		private final Collection	col;

		/** 
		 * Constructor for ArrayDelayedSet.
		 * @param col
		 * @param id
		 */
		public CollectionDelayedAdd(Collection col, String id)
		{
			this.col = col;
			this.id = id;
		}

		/** 
		 * @param asm
		 * @throws Exception
		 * @see nuggets.IDelayedOperation#perform(nuggets.IAssembler)
		 */
		public void perform(IAssembler asm) throws Exception
		{
			col.add(asm.getValue(id));
		}
	}


}

/* 
 * $Log$
 * Revision 1.4  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.3  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */