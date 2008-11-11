/*
 * DObjectArray.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 18, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import java.lang.reflect.Array;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IDelayedOperation;
import nuggets.IDelegate;
import nuggets.InstanceNotAvailableException;

/** DObjectArray 
 * @author walczak
 * @since  Jan 18, 2006
 */
public class DObjectArray extends ADelegate implements IDelegate
{
	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ADelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{
		final int l = Array.getLength(o);
		int i=l;
		while(i>0) {
			mill.declare(Array.get(o, --i), classloader);
		}
		  mill.startConcept(o);
		mill.put("type", o.getClass().getName());
		mill.put("length", Integer.toString(l));
		while(i<l) {
			mill.add(Array.get(o, i++));
		}
	}

	/** 
	 * @param clazz
	 * @param asm
	 * @return clazz.newInstance();
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		int l= Integer.parseInt((String)asm.getAttributeValue("length"));
		return Array.newInstance(clazz.getComponentType(), l);
	}
	
	/** 
	 * @param obj
	 * @param asm
	 * @throws Exception
	 * @see nuggets.delegate.ADelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object obj, IAssembler asm) throws Exception
	{
		
		int i=0;
		String id;
		while((id=asm.nextToken())!=null) {
			try {
				Array.set(obj, i, asm.getValue(id));
			} catch(InstanceNotAvailableException nae) {
				asm.delay(new ArrayDelayedSet(obj, i, id));
			}
			i++;
		}
		
	}

	/** ArrayDelayedSet 
	 * @author walczak
	 * @since  Jan 19, 2006
	 */
	public static final class ArrayDelayedSet implements IDelayedOperation
	{
		private final int	i;
		private final String	id;
		private final Object	obj;

		/** 
		 * Constructor for ArrayDelayedSet.
		 * @param obj
		 * @param i
		 * @param id
		 */
		public ArrayDelayedSet(Object obj, int i, String id)
		{
			this.obj = obj;
			this.i = i;
			this.id = id;
		}

		/** 
		 * @param asm
		 * @throws Exception
		 * @see nuggets.IDelayedOperation#perform(nuggets.IAssembler)
		 */
		public void perform(IAssembler asm) throws Exception
		{
			Array.set(obj, i, asm.getValue(id));
		}
	}
		
}
